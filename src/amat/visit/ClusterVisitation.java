
package amat.visit;

import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import jam.app.JamProperties;
import jam.chem.Langmuir;
import jam.markov.MarkovProcess;
import jam.markov.StochasticMatrix;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.math.Probability;
import jam.matrix.JamMatrix;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Implements a visitation model where B cells encounter antigens in
 * clusters, which are quantified by the probability that a B cell
 * will revisit the same antigen at the next site.
 *
 * <p><b>This implementation is restricted to antigens administered at
 * equal concentrations.</b>
 *
 * <p>In this model, B cells visit a fixed number of sites on each
 * cycle, where the number of visits is specified by the system
 * property <b>{@code amat.ClusterVisitation.visitCount}</b>.
 *
 * <p>The probability that a B cell will revisit the same antigen at
 * the next site is specified by the system property
 * <b>{@code amat.ClusterVisitation.revisitProb}</b>.
 */
public class ClusterVisitation extends VisitationModel {
    private final int visitCount;
    private final JamRandom randomSource;
    private final Probability revisitProb;

    // The stochastic matrix is fixed throughout one germinal center
    // cycle (because we assume that the antigen concentration remains
    // fixed).  Here we maintain the matrix in a cache until the cycle
    // changes...
    private int cacheCycle;
    private AntigenPool cacheAgPool;
    private Probability cacheSeeOne;
    private List<Antigen> cacheAgList;
    private StochasticMatrix cacheMatrix;

    // The global model defined by system properties...
    private static ClusterVisitation global = null;

    /**
     * Name of the system property which defines the number of sites
     * visited by each B cell.
     */
    public static final String VISIT_COUNT_PROPERTY = "amat.ClusterVisitation.visitCount";

    /**
     * Name of the system property which defines the probability that
     * a B cell revisits the same antigen at the next site.
     */
    public static final String REVISIT_PROB_PROPERTY = "amat.ClusterVisitation.revisitProb";

    /**
     * Valid range for the number of antigen or FDC sites visited.
     */
    public static final IntRange VISIT_COUNT_RANGE = IntRange.NON_NEGATIVE;

    /**
     * Creates a new cluster visitation model.
     *
     * @param visitCount the number of antigen sites visited by each B
     * cell.
     *
     * @param revisitProb the probability that a B cell will revisit
     * the same antigen at the next site.
     *
     * @throws IllegalArgumentException unless the visit count is
     * positive.
     */
    public ClusterVisitation(int visitCount, Probability revisitProb) {
        validateVisitCount(visitCount);
        
        this.visitCount = visitCount;
        this.revisitProb = trimRevisitProb(revisitProb);
        this.randomSource = JamRandom.global();

        // Assign a negative index so that the cache will be rebuilt
        // on the first visit (with a non-negative cycle)...
        this.cacheCycle = -1;
    }

    private static void validateVisitCount(int visitCount) {
        VISIT_COUNT_RANGE.validate(visitCount);
    }

    private static Probability trimRevisitProb(Probability revisitProb) {
        //
        // A revisit probability of exactly one generates a transition
        // matrix with all eigenvalues exactly equal to one, which the
        // StochasticMatrix class will not allow, so we trim the value
        // to 0.999999, which will be practically indistinguishable in
        // the simulation results...
        //
        return Probability.min(revisitProb, MAX_REVISIT_PROB);
    }

    private static final Probability MAX_REVISIT_PROB = Probability.valueOf(0.999999);

    /**
     * Returns the global visitation model defined by system properties.
     *
     * @return the global visitation model defined by system properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the visitation model are properly defined.
     */
    public static ClusterVisitation global() {
        if (global == null)
            createGlobal();

        return global;
    }

    private static void createGlobal() {
        int    visitCount  = JamProperties.getRequiredInt(VISIT_COUNT_PROPERTY);
        double revisitProb = JamProperties.getRequiredDouble(REVISIT_PROB_PROPERTY);

        global = new ClusterVisitation(visitCount, Probability.valueOf(revisitProb));
    }

    /**
     * Creates the stochastic matrix that describes antigen visitation.
     *
     * <p>The matrix indexes correspond to the indexes of the antigens
     * in the list view returned by {@code agPool.listAntigens()}.
     *
     * @param agPool the pool of all available antigens.
     *
     * @param revisitProb the probability that a B cell will revisit
     * the same antigen at the next site.
     *
     * @return the Markov process that simulates antigen visitation
     * for the specified antigen pool and revisitProb parameter.
     *
     * @throws IllegalArgumentException unless the antigens in the
     * pool are present at equal concentrations and the revisitProb
     * parameter is valid for the number of antigens present.
     */
    public static StochasticMatrix createStochasticMatrix(AntigenPool agPool, Probability revisitProb) {
        if (!agPool.isUniform())
            throw new IllegalArgumentException("Antigen pool must be uniform.");

        int agCount = agPool.size();
        JamMatrix visitMat = new JamMatrix(agCount, agCount);

        double diagonal = revisitProb.doubleValue();
        double offDiagonal = computeOffDiagonal(revisitProb, agCount);

        for (int ii = 0; ii < agCount; ++ii)
            for (int jj = 0; jj < agCount; ++jj)
                visitMat.set(ii, jj, ii == jj ? diagonal : offDiagonal);

        return new StochasticMatrix(visitMat);
    }

    private static double computeOffDiagonal(Probability revisitProb, int agCount) {
        //
        // The probability of NOT revisiting the same antigen 
        // is equally distributed among the other (agCount - 1)
        // antigens, which are assumed to be present in equal
        // concentrations...
        //
        return (1.0 - revisitProb.doubleValue()) / (agCount - 1);
    }

    /**
     * Returns the number of antigen sites visited by each B cell.
     *
     * @return the number of antigen sites visited by each B cell.
     */
    public int getVisitCount() {
        return visitCount;
    }

    /**
     * Returns the probability that a B cell will revisit the same
     * antigen at the next site.
     *
     * @return the probability that a B cell will revisit the same
     * antigen at the next site.
     */
    public Probability getRevisitProb() {
        return revisitProb;
    }

    @Override public Multiset<Antigen> visit(int gcCycle, AntigenPool agPool) {
        maintainCache(gcCycle, agPool);

        Multiset<Antigen> visited = HashMultiset.create();
        MarkovProcess process = new MarkovProcess(randomSource, cacheMatrix);

        for (int index = 0; index < visitCount; index++)
            if (cacheSeeOne.accept(randomSource))
                visited.add(cacheAgList.get(process.next()));

        return visited;
    }

    private void maintainCache(int gcCycle, AntigenPool agPool) {
        if (needUpdate(gcCycle, agPool))
            updateCache(gcCycle, agPool);
    }

    private boolean needUpdate(int gcCycle, AntigenPool agPool) {
        return gcCycle != cacheCycle || agPool != cacheAgPool;
    }

    private void updateCache(int gcCycle, AntigenPool agPool) {
        cacheCycle  = gcCycle;
        cacheAgPool = agPool;
        cacheAgList = agPool.listAntigens();
        cacheMatrix = createStochasticMatrix(agPool, revisitProb);
        cacheSeeOne = Langmuir.probability(agPool.getTotalConc());
    }
}
