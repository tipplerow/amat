
package amat.occupy;

import java.util.Collection;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Encodes the manner in which antigens occupy sites on the surface of
 * follicular dendritic cells (FDCs) in the light zone of a germinal
 * center.
 *
 * <p>The global model ({@link OccupationModel#global()}) is defined by
 * the system property <b>{@code amat.OccupationModel.modelType}</b>.
 *
 * <p>The {@link AllOneOccupationModel} (type {@link OccupationType#ALL_ONE})
 * switches from "all" to "one" behavior on the germinal center cycle given by 
 * the system property <b>{@code amat.OccupationModel.allOneTransition}</b>.
 */
public abstract class OccupationModel {
    private static OccupationModel global = null;

    /**
     * Name of the system property which defines the type of FDC
     * occupation model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.OccupationModel.modelType";

    /**
     * Name of the system property which defines the germinal center
     * cycle when the FDC occupation model switches from ALL to ONE.
     */
    public static final String ALL_ONE_TRANSITION_PROPERTY = "amat.OccupationModel.allOneTransition";

    /**
     * Returns the global FDC occupation model defined by system
     * properties.
     *
     * @return the global FDC occupation model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the FDC occupation model are properly defined.
     */
    public static OccupationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static OccupationModel createGlobal() {
        OccupationType modelType = resolveModelType();

        switch (modelType) {
        case ALL:
            return AllOccupationModel.INSTANCE;

        case ONE:
            return OneOccupationModel.INSTANCE;

        case ALL_ONE:
            return new AllOneOccupationModel(resolveAllOneTransition());

        case LANGMUIR:
            return LangmuirOccupationModel.INSTANCE;

        default:
            throw JamException.runtime("Unknown FDC occupation type [%s].", modelType);
        }
    }

    private static OccupationType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, OccupationType.class);
    }

    private static int resolveAllOneTransition() {
        return JamProperties.getRequiredInt(ALL_ONE_TRANSITION_PROPERTY, AllOneOccupationModel.TRANSITION_RANGE);
    }

    /**
     * Simulates a visit to a single FDC surface site and presents the
     * antigens occupying that site.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigens present at the FDC site.
     */
    public abstract Collection<Antigen> visit(int cycle, AntigenPool pool);
}
