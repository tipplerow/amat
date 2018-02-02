
package amat.tcell;

import java.util.Collections;
import java.util.Set;

import jam.math.Probability;

import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.bcell.SequentialApoptosisModel;

/**
 * Implements the T cell competition model defined by Wang et al.,
 * Cell 160, 785--797 (2015).  
 *
 * <p>Germinal center B cells receive T cell help with probability
 * {@code Q / (alpha + beta * Q)} where {@code Q} is the amount of
 * antigen captured, {@code alpha = R * N * <Q> / (N - 1)}, and 
 * {@code beta = 1 - R / (N - 1)}.  The constant {@code R} is the 
 * inverse of the total antigen concentration, {@code N} is the 
 * number of B cells competing for T cell help, and {@code <Q>} 
 * is the mean amount of antigen captured (computed over all 
 * competing B cells).
 */
public final class WangCellCompetition extends SequentialApoptosisModel {
    private double alpha;
    private double beta;

    private WangCellCompetition() {}

    /**
     * The global competition model instance.
     */
    public static final WangCellCompetition INSTANCE = new WangCellCompetition();

    @Override public boolean apoptose(BCell cell) {
        Probability survivalProb = computeSurvivalProb(cell);
        Probability apoptosisProb = survivalProb.not();

        return apoptosisProb.accept();
    }

    private Probability computeSurvivalProb(BCell cell) {
        return Probability.valueOf(cell.getAntigenQty() / (alpha + beta * cell.getAntigenQty()));
    }

    @Override public void initialize(Set<BCell> cells, AntigenPool pool) {
        alpha = computeAlpha(cells, pool);
        beta  = computeBeta(cells, pool);
    }

    private static double computeAlpha(Set<BCell> cells, AntigenPool pool) {
        double R = computeR(pool);
        double N = computeN(cells);
        double meanQ = computeMeanQ(cells);

        return R * meanQ * (N / (N - 1.0));
    }

    private static double computeBeta(Set<BCell> cells, AntigenPool pool) {
        double R = computeR(pool);
        double N = computeN(cells);

        return 1.0 - R / (N - 1.0);
    }

    private static double computeMeanQ(Set<BCell> cells) {
        double meanQ = 
            cells.stream().mapToDouble(bcell -> bcell.getAntigenQty()).average().orElse(Double.NaN);

        if (Double.isNaN(meanQ))
            throw new IllegalStateException("Missing average antigen quantity.");

        return meanQ;
    }

    private static double computeN(Set<BCell> cells) {
        return cells.size();
    }

    private static double computeR(AntigenPool pool) {
        return 1.0 / pool.getTotalConc().doubleValue();
    }

    @Override public synchronized Set<BCell> apoptose(Set<BCell> cells, AntigenPool pool) {
        //
        // A single B cell always receives help...
        //
        if (cells.size() <= 1)
            return Collections.emptySet();
        else
            return super.apoptose(cells, pool);
    }
}
