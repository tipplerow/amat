
package amat.divide;

import java.util.Collection;

import jam.math.IntRange;
import jam.math.JamRandom;
import jam.util.CollectionUtil;

import amat.bcell.BCell;

/**
 * Implements a dark-zone division model where the number of divisions
 * for B cell {@code k} is a function of {@code Q(k) / mean(Q)}, where
 * {@code Q(k)} is the amount of antigen captured by B cell {@code k}
 * and {@code mean(Q)} is the amount of antigen captured averaged over
 * all B cells that survive the BCR signaling test.  Let {@code R} be
 * the ratio {@code Q(k) / mean(Q)}, then the (continuous) expected
 * number of divisions is:
 *
 * <pre>

       n(R) = 2 + tanh[(N - 2) * (R - 1)], for R &le; 1
       n(R) = 2 + (N - 2) * tanh(R - 1),   for R &gt; 1
 * </pre>
 *
 * where {@code N} is the maximum number of cell divisions allowed.
 * The continuous expected value {@code n(R)} is then discretized
 * probabilistically by {@link JamRandom#discretize(double)}.
 *
 * <p>The maximum number of cell divisions is given by the system
 * property <b>{@code amat.DZDivisionModel.maxCount}</b>.
 */
public final class MeanCaptureRatioDivision extends DZDivisionModel {
    private final int maxCount;

    /**
     * Valid range for the maximum number of B cell divisions.
     */
    public static final IntRange MAX_RANGE = new IntRange(3, 6);

    /**
     * Creates a new mean capture ratio division model with a
     * specified maximum number of divisions.
     *
     * @param maxCount the maximum number of divisions.
     *
     * @throws IllegalArgumentException if the maximum count is
     * outside of its allowed range.
     */
    public MeanCaptureRatioDivision(int maxCount) {
        MAX_RANGE.validate(maxCount);
        this.maxCount = maxCount;
    }

    /**
     * Computes the number of cell divisions for a given antigen
     * capture ratio.
     *
     * @param qtyRatio the ratio of antigen captured to the mean value.
     *
     * @return the number of cell divisions for the specified antigen
     * quantity capture ratio.
     */
    public int computeDivisionCount(double qtyRatio) {
        return JamRandom.global().discretize(computeExpectedDivisionCount(qtyRatio));
    }

    /**
     * Computes the expected number of cell divisions for a given
     * antigen capture ratio.
     *
     * @param qtyRatio the ratio of antigen captured to the mean value.
     *
     * @return the expected number of cell divisions for the specified
     * antigen quantity capture ratio.
     *
     * @throws IllegalArgumentException if the ratio is negative.
     */
    public double computeExpectedDivisionCount(double qtyRatio) {
        if (qtyRatio < 0.0)
            throw new IllegalArgumentException("Negative antigen capture ratio.");
        else if (qtyRatio <= 1.0)
            return 2.0 + Math.tanh(3.0 * (qtyRatio - 1.0));
        else
            return 2.0 + 3.0 * Math.tanh(qtyRatio - 1.0);
    }

    @Override public void assignDivisionCount(Collection<BCell> bcells) {
        double meanQty = CollectionUtil.average(bcells, bcell -> bcell.getAntigenQty());

        for (BCell bcell : bcells)
            bcell.setDivisionCount(computeDivisionCount(bcell.getAntigenQty() / meanQty));
    }
}
