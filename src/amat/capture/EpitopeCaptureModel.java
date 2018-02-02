
package amat.capture;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.lang.JamException;

/**
 * Encodes quantitative models for the amount of antigen captured
 * and internalized when B cells encounter antigen presented on the
 * surface of follicular dendritic cells (FDCs) in the light zone of
 * a germinal center.
 *
 * <p>The global model ({@link EpitopeCaptureModel#global()}) is defined by
 * the system property <b>{@code amat.EpitopeCaptureModel.modelType}</b>.
 */
public abstract class EpitopeCaptureModel {
    private static EpitopeCaptureModel global = null;

    /**
     * Name of the system property which defines the type of epitope
     * capture model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.EpitopeCaptureModel.modelType";

    /**
     * Returns the global epitope capture model defined by system
     * properties.
     *
     * @return the global epitope capture model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the epitope capture model are properly defined.
     */
    public static EpitopeCaptureModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static EpitopeCaptureModel createGlobal() {
        EpitopeCaptureType modelType = resolveModelType();

        switch (modelType) {
        case CK:
            return CKCaptureModel.INSTANCE;

        case LANGMUIR:
            return LangmuirCaptureModel.INSTANCE;

        default:
            throw JamException.runtime("Unknown epitope capture type [%s].", modelType);
        }
    }

    private static EpitopeCaptureType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, EpitopeCaptureType.class);
    }

    /**
     * Computes the amount of epitope captured (internalized) during
     * a single encounter with a B cell.
     *
     * @param affinity the affinity of BCR-epitope binding (in units
     * of kT).
     *
     * @param concentration the concentration of the antigen in the
     * germinal center.
     *
     * @return the amount of antigen captured (bound and internalized)
     * by the B cell.
     */
    public abstract double capture(double affinity, Concentration concentration);
}
