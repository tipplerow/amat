
package amat.tcell;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import jam.app.JamProperties;
import jam.lang.JamException;
import jam.math.DoubleRange;

import amat.bcell.ApoptosisModel;
import amat.bcell.BCell;

/**
 * Encodes the manner by which B cells in the light zone compete for T
 * cell help.
 *
 * <p>The global model ({@link TCellCompetitionModel#global()}) is
 * defined by the following system properties:
 *
 * <p><b>{@code amat.TCellCompetitionModel.modelType:}</b> The
 * enumerated competition model type.
 *
 * <p><b>{@code amat.TCellCompetitionModel.survivalRate:}</b> The
 * fraction of B cells that will receive T cell help and survive.
 */
public final class TCellCompetitionModel {
    private static ApoptosisModel global = null;

    /**
     * Name of the system property which defines the type of T cell
     * competition model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.TCellCompetitionModel.modelType";

    /**
     * Name of the system property which defines the fraction of B
     * cells receiving T cell help.
     */
    public static final String SURVIVAL_RATE_PROPERTY = "amat.TCellCompetitionModel.survivalRate";

    /**
     * Returns the global T cell competition model defined by system
     * properties.
     *
     * @return the global T cell competition model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the T cell competition model are properly defined.
     */
    public static ApoptosisModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ApoptosisModel createGlobal() {
        TCellCompetitionType modelType = resolveModelType();

        switch (modelType) {
        case MAX_AFFINITY_RANK:
            return createRankCompetition(BCell.MAX_AFFINITY_COMPARATOR);

        case ANTIGEN_QTY_RANK:
            return createRankCompetition(BCell.ANTIGEN_QTY_COMPARATOR);

        case ANTIGEN_QTY_MEAN_RATIO:
            return new MeanRatioCompetition(bcell -> bcell.getAntigenQty());

        case WANG_CELL:
            return WangCellCompetition.INSTANCE;

        default:
            throw JamException.runtime("Unknown T cell competition type [%s].", modelType);
        }
    }

    private static TCellCompetitionType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, TCellCompetitionType.class);
    }

    private static ApoptosisModel createRankCompetition(Comparator<BCell> comparator) {
        double survivalRate = JamProperties.getRequiredDouble(SURVIVAL_RATE_PROPERTY, DoubleRange.FRACTIONAL);
        return new RankCompetition(survivalRate, comparator);
    }
}
