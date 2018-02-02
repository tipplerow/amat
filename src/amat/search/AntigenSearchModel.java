
package amat.search;

import java.util.Collection;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.bcell.BCell;

/**
 * Encodes the manner by which B cells search for and locate antigens
 * in the light zone of a germinal center.
 *
 * <p>The global model ({@link AntigenSearchModel#global()}) is defined by
 * the system property <b>{@code amat.AntigenSearchModel.modelType}</b>.
 *
 * <p>The {@link AllOneAntigenSearch} model (type {@link AntigenSearchType#ALL_ONE})
 * switches from "see all" to "see one" behavior on the germinal center cycle given
 * by the system property <b>{@code amat.AntigenSearchModel.allOneTransition}</b>.
 */
public abstract class AntigenSearchModel {
    private long visitCount   = 0; // Number of visitation trials
    private long antigenCount = 0; // Number of antigens selected in all trials

    private static AntigenSearchModel global = null;

    /**
     * Name of the system property which defines the type of antigen
     * visitation model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.AntigenSearchModel.modelType";

    /**
     * Name of the system property which defines the germinal center
     * cycle when the antigen search model switches from ALL to ONE.
     */
    public static final String ALL_ONE_TRANSITION_PROPERTY = "amat.AntigenSearchModel.allOneTransition";

    /**
     * Returns the global antigen visitation model defined by system
     * properties.
     *
     * @return the global antigen visitation model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the antigen visitation model are properly defined.
     */
    public static AntigenSearchModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static AntigenSearchModel createGlobal() {
        AntigenSearchType modelType = resolveModelType();

        switch (modelType) {
        case ONE:
            return OneAntigenSearch.INSTANCE;

        case ALL:
            return AllAntigenSearch.INSTANCE;

        case ALL_ONE:
            return new AllOneAntigenSearch(resolveAllOneTransition());

        case LANGMUIR:
            return LangmuirAntigenSearch.INSTANCE;

        default:
            throw JamException.runtime("Unknown antigen visitation type [%s].", modelType);
        }
    }

    private static AntigenSearchType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, AntigenSearchType.class);
    }

    private static int resolveAllOneTransition() {
        return JamProperties.getRequiredInt(ALL_ONE_TRANSITION_PROPERTY, AllOneAntigenSearch.TRANSITION_RANGE);
    }

    /**
     * Simulates the search for and binding of antigens by a
     * collection of B cells.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of available antigens.
     *
     * @param cells the B cells present in the light zone of a
     * germinal center.
     */
    public void bind(int cycle, AntigenPool pool, Collection<BCell> cells) {
        for (BCell cell : cells)
            cell.bind(visit(cycle, pool));
    }

    /**
     * Creates an antigen pool for visitation by a B cell.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of all available antigens.
     *
     * @return a new pool of antigens selected for visitation.
     */
    public AntigenPool visit(int cycle, AntigenPool pool) {
        Collection<Antigen> antigens = selectAntigens(cycle, pool);

        // Keep a running total of the visitation attempts and the
        // number of antigens selected...
        ++visitCount;
        antigenCount += antigens.size();

        return pool.subset(antigens);
    }

    /**
     * Selects antigens for visitation by a B cell.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigens to be visited.
     */
    public abstract Collection<Antigen> selectAntigens(int cycle, AntigenPool pool);

    /**
     * Returns the total number of visitation trials.
     *
     * @return the total number of visitation trials.
     */
    public long getVisitCount() {
        return visitCount;
    }

    /**
     * Returns the total number of antigens selected in all trials.
     *
     * @return the total number of antigens selected in all trials.
     */
    public long getAntigenCount() {
        return antigenCount;
    }

    /**
     * Returns the average number of antigens selected (per trial).
     *
     * @return the average number of antigens selected (per trial).
     */
    public double getMeanAntigenCount() {
        return ((double) antigenCount) / ((double) visitCount);
    }
}
