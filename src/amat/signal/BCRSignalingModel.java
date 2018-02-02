
package amat.signal;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.bcell.ApoptosisModel;

/**
 * Encodes the manner by which BCR signaling after antigen capture
 * allows B cells in the light zone to avoid apoptosis (their default
 * fate).
 *
 * <p>The global model ({@link BCRSignalingModel#global()}) is defined
 * by the following system properties:
 *
 * <p><b>{@code amat.BCRSignalingModel.modelType:}</b> The enumerated
 * signaling model type.
 *
 * <p><b>{@code amat.BCRSignalingModel.affinityThreshold:}</b> For
 * models of the {@code AFFINITY_THRESHOLD} type, B cells must bind at
 * least one epitope with this affinity or greater to avoid apoptosis;
 *
 * <p><b>{@code amat.BCRSignalingModel.quantityThreshold:}</b> For
 * models of the {@code QUANTITY_THRESHOLD} type, B cells must capture
 * this quantity of antigen or greater to avoid apoptosis.
 */
public final class BCRSignalingModel {
    private static ApoptosisModel global = null;

    /**
     * Name of the system property which defines the type of BCR
     * signaling model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.BCRSignalingModel.modelType";

    /**
     * Name of the system property which defines the minimum binding
     * affinity required to avoid apoptosis.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = "amat.BCRSignalingModel.affinityThreshold";

    /**
     * Name of the system property which defines the minimum quantity
     * of antigen captured to avoid apoptosis.
     */
    public static final String QUANTITY_THRESHOLD_PROPERTY = "amat.BCRSignalingModel.quantityThreshold";

    /**
     * Returns the global BCR signaling model defined by system
     * properties.
     *
     * @return the global BCR signaling model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the BCR signaling model are properly defined.
     */
    public static ApoptosisModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ApoptosisModel createGlobal() {
        BCRSignalingType modelType = resolveModelType();

        switch (modelType) {
        case AFFINITY_THRESHOLD:
            return new ThresholdSignaling(bcell -> bcell.getMaxAffinity(), resolveAffinityThreshold());

        case QUANTITY_THRESHOLD:
            return new ThresholdSignaling(bcell -> bcell.getAntigenQty(), resolveQuantityThreshold());

        case QUANTITY_LANGMUIR:
            return QuantityLangmuirSignaling.INSTANCE;

        default:
            throw JamException.runtime("Unknown BCR signaling type [%s].", modelType);
        }
    }

    private static BCRSignalingType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, BCRSignalingType.class);
    }

    private static double resolveAffinityThreshold() {
        return JamProperties.getRequiredDouble(AFFINITY_THRESHOLD_PROPERTY);
    }

    private static double resolveQuantityThreshold() {
        return JamProperties.getRequiredDouble(QUANTITY_THRESHOLD_PROPERTY);
    }
}
