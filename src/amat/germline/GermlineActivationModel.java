
package amat.germline;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jam.app.JamProperties;
import jam.math.IntRange;

import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.binding.AffinityModel;
import amat.epitope.Epitope;

/**
 * Encodes a model for generating germline B cells and recruiting them
 * into the germinal center.
 *
 * <p>The global model ({@link GermlineActivationModel#global()}) is
 * defined by the following system properties:
 *
 * <p><b>{@code amat.GermlineActivationModel.affinityThreshold:}</b>
 * The minimum antigen affinity required for germline cell activation.
 *
 * <p><b>{@code amat.GermlineActivationModel.germlineCount:}</b> The
 * number of unique germline cells present at the start of affinity
 * maturation.
 *
 * <p><b>{@code amat.GermlineActivationModel.replicationFactor:}</b>
 * The number of times each germline cell is replicated prior to the
 * start of affinity maturation.
 */
public final class GermlineActivationModel {
    private final int attemptLimit;
    private final int germlineCount;
    private final int replicationFactor;
    private final double affinityThreshold;

    private static GermlineActivationModel global = null;

    /**
     * Name of the system property which defines the minimum antigen
     * affinity required for germline cell activation.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = 
        "amat.GermlineActivationModel.affinityThreshold";

    /**
     * Default value for the minimum antigen affinity required for
     * germline cell activation.
     */
    public static final double DEFAULT_AFFINITY_THRESHOLD = 0.0;

    /**
     * Name of the system property which defines the number of unique
     * germline cells present at the start of affinity maturation.
     */
    public static final String GERMLINE_COUNT_PROPERTY = 
        "amat.GermlineActivationModel.germlineCount";

    /**
     * Default value for the number of unique germline cells present
     * at the start of affinity maturation.
     */
    public static final int DEFAULT_GERMLINE_COUNT = 50;

    /**
     * Name of the system property which defines the number of times
     * each germline cell is replicated prior to the start of affinity
     * maturation.
     */
    public static final String REPLICATION_FACTOR_PROPERTY = 
        "amat.GermlineActivationModel.replicationFactor";

    /**
     * Default value for the number of times each germline cell is
     * replicated prior to the start of affinity maturation.
     */
    public static final int DEFAULT_REPLICATION_FACTOR = 40;

    /**
     * Creates a new memory selection model with a fixed selection
     * threshold and germline count.
     *
     * @param affinityThreshold the minimum antigen affinity required
     * for germline cell activation.
     *
     * @param germlineCount the number of unique germline cells
     * present at the start of affinity maturation.
     *
     * @param replicationFactor the number of times each germline cell
     * is replicated prior to the start of affinity maturation.
     */
    public GermlineActivationModel(double affinityThreshold, 
                                   int    germlineCount,
                                   int    replicationFactor) {
        this.attemptLimit      = 10000 * germlineCount;
        this.germlineCount     = germlineCount;
        this.replicationFactor = replicationFactor;
        this.affinityThreshold = affinityThreshold;
    }

    /**
     * Returns the global memory selection model defined by system
     * properties.
     *
     * @return the global memory selection model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the memory selection model are properly defined.
     */
    public static GermlineActivationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static GermlineActivationModel createGlobal() {
        return new GermlineActivationModel(resolveAffinityThreshold(),
                                           resolveGermlineCount(),
                                           resolveReplicationFactor());
    }

    private static double resolveAffinityThreshold() {
        return JamProperties.getOptionalDouble(AFFINITY_THRESHOLD_PROPERTY, DEFAULT_AFFINITY_THRESHOLD);
    }

    private static int resolveGermlineCount() {
        return JamProperties.getOptionalInt(GERMLINE_COUNT_PROPERTY, IntRange.POSITIVE, DEFAULT_GERMLINE_COUNT);
    }

    private static int resolveReplicationFactor() {
        return JamProperties.getOptionalInt(REPLICATION_FACTOR_PROPERTY, IntRange.POSITIVE, DEFAULT_REPLICATION_FACTOR);
    }

    /**
     * Returns the minimum antigen affinity required for germline cell
     * activation.
     *
     * @return the minimum antigen affinity required for germline cell
     * activation.
     */
    public double getAffinityThreshold() {
        return affinityThreshold;
    }

    /**
     * Returns the number of germline cells present at the start of
     * affinity maturation.
     *
     * @return the number of germline cells present at the start of
     * affinity maturation.
     */
    public int getGermlineCount() {
        return germlineCount;
    }

    /**
     * Returns the number of times each germline cell is replicated
     * prior to the start of affinity maturation.
     *
     * @return the number of times each germline cell is replicated
     * prior to the start of affinity maturation.
     */
    public int getReplicationFactor() {
        return replicationFactor;
    }

    /**
     * Generates activated germline cells.
     *
     * @param antigenPool the pool of available antigens.
     *
     * @return a set containing the activated germline cells.
     *
     * @throws IllegalStateException if the requested number of
     * germline cells cannot be generated in the allowed number
     * of attempts (e.g., the activation threshold is too high).
     */
    public Set<BCell> activate(AntigenPool antigenPool) {
        int attemptIndex = 0;
        Set<BCell> germlines = new HashSet<BCell>();
        Set<Epitope> epitopes = antigenPool.viewEpitopes();

        while (germlines.size() < germlineCount && attemptIndex < attemptLimit) {
            BCell germline = BCell.germline();

            if (activate(germline, epitopes))
                germlines.add(germline);

            ++attemptIndex;
        }

        if (germlines.size() != germlineCount)
            throw new IllegalStateException("Failed to generate germline cells.");

        return germlines;
    }

    private boolean activate(BCell germline, Collection<Epitope> epitopes) {
        for (Epitope epitope : epitopes)
            if (activate(germline, epitope))
                return true;

        return false;
    }

    private boolean activate(BCell germline, Epitope epitope) {
        return AffinityModel.global().computeAffinity(epitope, germline.getReceptor()) >= affinityThreshold;
    }

    /**
     * Replicates activated germline cells.
     *
     * @param germlines activated germline cells generated by 
     * {@link GermlineActivationModel#activate(AntigenPool)}.
     *
     * @return a set containing {@link GermlineActivationModel#getReplicationFactor()}
     * copies of each germline cell.
     */
    public Set<BCell> replicate(Collection<BCell> germlines) {
        Set<BCell> replicants = new HashSet<BCell>();

        for (BCell germline : germlines)
            for (int copyNumber = 0; copyNumber < replicationFactor; copyNumber++)
                replicants.add(germline.replicate());

        return replicants;
    }
}
