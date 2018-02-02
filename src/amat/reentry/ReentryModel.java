
package amat.reentry;

import jam.app.JamProperties;
import jam.math.Probability;

import amat.bcell.BCell;
import amat.bcell.IndependentSelectionModel;

/**
 * Encodes a model for selecting B cells previously exported from the
 * germinal center for reentry into the GC.
 *
 * <p>The global model ({@link ReentryModel#global()}) is defined by
 * the following system properties:
 *
 * <p><b>{@code amat.ReentryModel.selectionProbability:}</b> The
 * probability that a previously exported B cell will be selected 
 * for reentry into the germinal center.
 */
public final class ReentryModel extends IndependentSelectionModel {
    private final Probability probability;

    private static ReentryModel global = null;

    /**
     * Name of the system property which defines the probability of
     * reentry.
     */
    public static final String SELECTION_PROBABILITY_PROPERTY = 
        "amat.ReentryModel.selectionProbability";

    /**
     * Default value for the probability of reentry.
     */
    public static final double DEFAULT_SELECTION_PROBABILITY = 0.0;

    /**
     * Creates a new reentry selection model with a fixed selection
     * probability.
     *
     * @param probability the probability that a previously exported B
     * cell will be selected for reentry into the germinal center.
     */
    public ReentryModel(Probability probability) {
        this.probability = probability;
    }

    /**
     * Returns the global reentry selection model defined by system
     * properties.
     *
     * @return the global reentry selection model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the reentry selection model are properly defined.
     */
    public static ReentryModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ReentryModel createGlobal() {
        return new ReentryModel(resolveSelectionProbability());
    }

    private static Probability resolveSelectionProbability() {
        return Probability.valueOf(JamProperties.getOptionalDouble(SELECTION_PROBABILITY_PROPERTY, 
                                                                   DEFAULT_SELECTION_PROBABILITY));
    }

    /**
     * Returns the probability that a previously exported B cell will
     * be selected for reentry into the germinal center.
     *
     * @return the probability that a previously exported B cell will
     * be selected for reentry into the germinal center.
     */
    public Probability getSelectionProbability() {
        return probability;
    }

    /**
     * Determines whether a previously exported B cell will be
     * selected for reentry into the germinal center.
     *
     * @param cell a B cell that was previously exported from the
     * germinal center.
     *
     * @return {@code true} iff the B cell is selected for reentry
     * into the germinal center.
     */
    @Override public boolean select(BCell cell) {
        return probability.accept();
    }
}
