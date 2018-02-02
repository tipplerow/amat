
package amat.binding;

import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

import amat.epitope.Epitope;
import amat.receptor.Receptor;
import amat.structure.NumericStructure;

/**
 * Base class for models of interactions between epitopes and
 * receptors that have representations as numeric vectors.
 */
public abstract class NumericAffinity extends AffinityModel {
    /**
     * Creates a numeric free energy model.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar pre-factor required to rescale the
     * model-specific interaction function into a free energy in units
     * of kT.
     */
    protected NumericAffinity(double actEnergy, double preFactor) {
        super(actEnergy, preFactor);
    }

    /**
     * Returns a numeric view of an epitope structure.
     *
     * @param epitope the epitope to examine.
     *
     * @return a numeric view of the structure of the given epitope.
     *
     * @throws RuntimeException unless the epitope has a valid numeric
     * representation.
     */
    protected VectorView asNumeric(Epitope epitope) {
	return ((NumericStructure) epitope.getStructure()).asNumeric();
    }

    /**
     * Returns a numeric view of a receptor structure.
     *
     * @param receptor the receptor to examine.
     *
     * @return a numeric view of the structure of the given receptor.
     *
     * @throws RuntimeException unless the receptor has a valid numeric
     * representation.
     */
    protected VectorView asNumeric(Receptor receptor) {
	return ((NumericStructure) receptor.getStructure()).asNumeric();
    }

    /**
     * Computes the vector difference between the numeric representations
     * of an epitope and receptor.
     *
     * @param epitope the epitope partner in the binding interaction.
     *
     * @param receptor the receptor partner in the binding interaction.
     *
     * @return the vector difference between the numeric representations
     * of the specified epitope and receptor.
     *
     * @throws RuntimeException unless the epitope and receptor have
     * valid numeric representations of equal length.
     */
    protected JamVector difference(Epitope epitope, Receptor receptor) {
        return JamVector.minus(asNumeric(epitope), asNumeric(receptor));
    }

    /**
     * Computes the difference between the numeric representations of
     * an epitope and receptor, applies a vector aggregator, and then
     * normalizes by the (equal) length.
     *
     * @param epitope the epitope partner in the binding interaction.
     *
     * @param receptor the receptor partner in the binding interaction.
     *
     * @param aggregator the aggregator to apply to the difference in
     * epitope and receptor structures.
     *
     * @return the normalized aggregated difference between the epitope 
     * and receptor.
     *
     * @throws RuntimeException unless the epitope and receptor have
     * valid numeric representations of equal length.
     */
    protected double normalizedAggregateDifference(Epitope epitope,
                                                   Receptor receptor,
                                                   VectorAggregator aggregator) {
        return aggregator.compute(difference(epitope, receptor)) / commonLength(epitope, receptor);
    }

    @Override public void validate(Epitope epitope, Receptor receptor) {
        epitope.getStructure().validateNumeric();
        receptor.getStructure().validateNumeric();
    }
}
