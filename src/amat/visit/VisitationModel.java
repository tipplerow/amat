
package amat.visit;

import com.google.common.collect.Multiset;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Encodes the manner by which B cells visit follicular dendritic
 * cells (FDCs) in the light zone of a germinal center.
 *
 * <p>The global model ({@link VisitationModel#global()}) is defined by
 * the system property <b>{@code amat.VisitationModel.modelType}</b>.
 */
public abstract class VisitationModel {
    private static VisitationModel global = null;

    /**
     * Name of the system property which defines the type of FDC
     * visitation model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.VisitationModel.modelType";

    /**
     * Returns the global FDC visitation model defined by system
     * properties.
     *
     * @return the global FDC visitation model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the FDC visitation model are properly defined.
     */
    public static VisitationModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static VisitationModel createGlobal() {
        VisitationType modelType = resolveModelType();

        switch (modelType) {
        case CLUSTER:
            return ClusterVisitation.global();

        case FIXED_COUNT:
            return FixedCountVisitation.global();

        default:
            throw JamException.runtime("Unknown FDC visitation type [%s].", modelType);
        }
    }

    private static VisitationType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, VisitationType.class);
    }

    /**
     * Simulates the visitation of FDCs by a B cell in the light zone
     * of a germinal center.
     *
     * <p>The visitation is encoded in a multiset which contains the
     * antigens encountered in the light zone.  An antigen will have
     * multiple occurrences in the multiset if the B cell encountered
     * it more than once.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of all available antigens.
     *
     * @return a multiset containing the antigens encountered on the
     * FDCs.
     */
    public abstract Multiset<Antigen> visit(int cycle, AntigenPool pool);
}
