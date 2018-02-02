
package amat.plasma;

import jam.app.JamProperties;
import jam.math.Probability;

import amat.bcell.BCell;
import amat.bcell.IndependentSelectionModel;

/**
 * Encodes a model for selecting surviving B cells into the plasma
 * cell compartment.
 *
 * <p>The global model ({@link PlasmaSelectionModel#global()}) is
 * defined by the following system properties:
 *
 * <p><b>{@code amat.PlasmaSelectionModel.affinityThreshold:}</b> The
 * minimum affinity with which a B cell must bind an epitope in order
 * to be selected into the plasma cell compartment.
 *
 * <p><b>{@code amat.PlasmaSelectionModel.selectionProbability:}</b>
 * The probability that a B cell that exceeds the affinity threshold
 * is selected into the plasma cell compartment.
 */
public final class PlasmaSelectionModel extends IndependentSelectionModel {
    private final double threshold;
    private final Probability probability;

    private static PlasmaSelectionModel global = null;

    /**
     * Name of the system property which defines the minimum epitope
     * binding affinity required for selection into the plasma cell
     * compartment.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = 
        "amat.PlasmaSelectionModel.affinityThreshold";

    /**
     * Default value for the minimum epitope binding affinity required
     * for selection into the plasma cell compartment.
     */
    public static final double DEFAULT_AFFINITY_THRESHOLD = 0.0;

    /**
     * Name of the system property which defines the probability of
     * selection to the plasma cell compartment.
     */
    public static final String SELECTION_PROBABILITY_PROPERTY = 
        "amat.PlasmaSelectionModel.selectionProbability";

    /**
     * Default value for the probability of selection to the plasma
     * cell compartment.
     */
    public static final double DEFAULT_SELECTION_PROBABILITY = 0.05;

    /**
     * Creates a new plasma selection model with a fixed selection
     * probability.
     *
     * @param threshold the minimum epitope binding affinity required
     * for selection into the plasma cell compartment.
     *
     * @param probability the probability that a surviving B cell will
     * be selected into the plasma cell compartment.
     */
    public PlasmaSelectionModel(double threshold, Probability probability) {
        this.threshold = threshold;
        this.probability = probability;
    }

    /**
     * Returns the global plasma selection model defined by system
     * properties.
     *
     * @return the global plasma selection model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the plasma selection model are properly defined.
     */
    public static PlasmaSelectionModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static PlasmaSelectionModel createGlobal() {
        return new PlasmaSelectionModel(resolveAffinityThreshold(),
                                        resolveSelectionProbability());
    }

    private static double resolveAffinityThreshold() {
        return JamProperties.getOptionalDouble(AFFINITY_THRESHOLD_PROPERTY, 
                                               DEFAULT_AFFINITY_THRESHOLD);
    }

    private static Probability resolveSelectionProbability() {
        return Probability.valueOf(JamProperties.getOptionalDouble(SELECTION_PROBABILITY_PROPERTY,
                                                                   DEFAULT_SELECTION_PROBABILITY));
    }

    /**
     * Returns the minimum epitope binding affinity required for
     * selection into the plasma cell compartment.
     *
     * @return the minimum epitope binding affinity required for
     * selection into the plasma cell compartment.
     */
    public double getAffinityThreshold() {
        return threshold;
    }

    /**
     * Returns the probability that a surviving B cell will be
     * selected into the plasma cell compartment.
     *
     * @return the probability that a surviving B cell will be
     * selected into the plasma cell compartment.
     */
    public Probability getSelectionProbability() {
        return probability;
    }

    /**
     * Determines whether a surviving B cell is selected into the
     * plasma cell compartment.
     *
     * @param cell a B cell that has survived the competition for
     * antigen and T cell help.
     *
     * @return {@code true} iff the B cell is selected into the plasma
     * cell compartment.
     */
    @Override public boolean select(BCell cell) {
        return cell.getMaxAffinity() >= threshold && probability.accept();
    }
}
