
package amat.signal;

import java.util.function.ToDoubleFunction;

import amat.bcell.BCell;
import amat.bcell.IndependentApoptosisModel;

/**
 * Represents a manner of BCR signaling by which B cells receive
 * survival signals when a characteristic value exceeds a threshold.
 * The characteristic value is defined by a function extracting the
 * quantity from B cells.
 */
public final class ThresholdSignaling extends IndependentApoptosisModel {
    private final double threshold;
    private final ToDoubleFunction<BCell> valueFunc;

    /**
     * Creates a new threshold signaling model.
     *
     * @param valueFunc the function that extracts the characteristic
     * value from each B cell.
     *
     * @param threshold the minimum characteristic value required to
     * avoid apoptosis.
     */
    public ThresholdSignaling(ToDoubleFunction<BCell> valueFunc, double threshold) {
        this.valueFunc = valueFunc;
        this.threshold = threshold;
    }

    /**
     * Returns the function that extracts the characteristic value
     * from each B cell.
     *
     * @return the function that extracts the characteristic value
     * from each B cell.
     */
    public ToDoubleFunction<BCell> getValueFunc() {
        return valueFunc;
    }

    /**
     * Returns the minimum characteristic value required to avoid
     * apoptosis.
     *
     * @return the minimum characteristic value required to avoid
     * apoptosis.
     */
    public double getThreshold() {
        return threshold;
    }

    @Override public boolean apoptose(BCell cell) {
        return valueFunc.applyAsDouble(cell) < threshold;
    }
}
