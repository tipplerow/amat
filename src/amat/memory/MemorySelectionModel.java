
package amat.memory;

import jam.app.JamProperties;
import jam.math.Probability;

import amat.bcell.BCell;
import amat.bcell.IndependentSelectionModel;

/**
 * Encodes a model for selecting surviving B cells into the memory
 * cell compartment.
 *
 * <p>The global model ({@link MemorySelectionModel#global()}) is
 * defined by the following system properties:
 *
 * <p><b>{@code amat.MemorySelectionModel.selectionProbability:}</b>
 * The probability that a surviving B cell is selected into the memory
 * cell compartment.
 */
public final class MemorySelectionModel extends IndependentSelectionModel {
    private final Probability probability;

    private static MemorySelectionModel global = null;

    /**
     * Name of the system property which defines the probability of
     * selection to the memory cell compartment.
     */
    public static final String SELECTION_PROBABILITY_PROPERTY = 
        "amat.MemorySelectionModel.selectionProbability";

    /**
     * Default value for the probability of selection to the memory
     * cell compartment.
     */
    public static final double DEFAULT_SELECTION_PROBABILITY = 0.05;

    /**
     * Creates a new memory selection model with a fixed selection
     * probability.
     *
     * @param probability the probability that a surviving B cell will
     * be selected into the memory cell compartment.
     */
    public MemorySelectionModel(Probability probability) {
        this.probability = probability;
    }

    /**
     * Returns the global memory selection model defined by system
     * properties.
     *
     * @return the global memory selection model defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the memory selection model are properly defined.
     */
    public static MemorySelectionModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static MemorySelectionModel createGlobal() {
        return new MemorySelectionModel(resolveSelectionProbability());
    }

    private static Probability resolveSelectionProbability() {
        return Probability.valueOf(JamProperties.getOptionalDouble(SELECTION_PROBABILITY_PROPERTY,
                                                                   DEFAULT_SELECTION_PROBABILITY));
    }                                                                   

    /**
     * Returns the probability that a surviving B cell will be
     * selected into the memory cell compartment.
     *
     * @return the probability that a surviving B cell will be
     * selected into the memory cell compartment.
     */
    public Probability getSelectionProbability() {
        return probability;
    }

    /**
     * Determines whether a surviving B cell is selected into the
     * memory cell compartment.
     *
     * @param cell a B cell that has survived the competition for
     * antigen and T cell help.
     *
     * @return {@code true} iff the B cell is selected into the memory
     * cell compartment.
     */
    @Override public boolean select(BCell cell) {
        return probability.accept();
    }
}
