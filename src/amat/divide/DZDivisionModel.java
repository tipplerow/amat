
package amat.divide;

import java.util.Collection;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.bcell.BCell;

/**
 * Determines the number of times a parent B cell will divide in the
 * dark zone (DZ) following the competition for antigen binding and T
 * cell help in the light zone (LZ).
 *
 * <p>The global model ({@link DZDivisionModel#global()}) is defined by
 * the system property <b>{@code amat.DZDivisionModel.modelType}</b>.  
 *
 * <p>In the {@link FixedCountDivision} model (type {@link DZDivisionType#FIXED_COUNT}), all
 * B cells divide a fixed number of times, regardless of antigen capture, where the number of
 * divisions specified by the system property <b>{@code amat.DZDivisionModel.fixedCount}</b>.
 *
 * <p>In the {@link MeanCaptureRatioDivision} model (type {@link DZDivisionType#MEAN_CAPTURE_RATIO}), 
 * the number of divisions by a particular B cell is a function of the ratio of the amount of antigen 
 * captured by that cell to the average amount captured (averaged over the B cells that survive the 
 * BCR signaling test).  The maximum number of cell divisions in that model is specified by the system 
 * property <b>{@code amat.DZDivisionModel.maxCount}</b>.
 */
public abstract class DZDivisionModel {
    private static DZDivisionModel global = null;

    /**
     * Name of the system property which defines the type of dark-zone
     * division model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.DZDivisionModel.modelType";

    /**
     * Name of the system property which defines the fixed number of
     * divisions in the {@link FixedCountDivision} model.
     */
    public static final String FIXED_COUNT_PROPERTY = "amat.DZDivisionModel.fixedCount";

    /**
     * Name of the system property which defines the maximum number of
     * divisions in the {@link MeanCaptureRatioDivision} model.
     */
    public static final String MAX_COUNT_PROPERTY = "amat.DZDivisionModel.maxCount";

    /**
     * Returns the global dark-zone division model defined by system
     * properties.
     *
     * @return the global dark-zone division model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the dark-zone division model are properly defined.
     */
    public static DZDivisionModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static DZDivisionModel createGlobal() {
        DZDivisionType modelType = resolveModelType();

        switch (modelType) {
        case FIXED_COUNT:
            return new FixedCountDivision(resolveFixedCount());

        case MEAN_CAPTURE_RATIO:
            return new MeanCaptureRatioDivision(resolveMaxCount());

        default:
            throw JamException.runtime("Unknown dark-zone division type [%s].", modelType);
        }
    }

    private static DZDivisionType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, DZDivisionType.class);
    }

    private static int resolveFixedCount() {
        return JamProperties.getRequiredInt(FIXED_COUNT_PROPERTY, FixedCountDivision.COUNT_RANGE);
    }

    private static int resolveMaxCount() {
        return JamProperties.getRequiredInt(MAX_COUNT_PROPERTY, MeanCaptureRatioDivision.MAX_RANGE);
    }

    /**
     * Assigns the number of divisions for each member in a population
     * of light-zone B cells.
     *
     * @param bcells the population of light-zone B cells.
     *
     * @throws IllegalStateException if any B cells have already been
     * assigned a division count.
     */
    public abstract void assignDivisionCount(Collection<BCell> bcells);
}
