
package amat.binding;

import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Defines the free energy of epitope-receptor binding as the
 * <em>opposite</em> of the sum of the element-by-element products
 * between numeric structures of equal length, normalized by their
 * length, and multiplied by a scaling parameter.
 *
 * <p>Quadratic models are typically used with {@code SpinStructure}
 * epitopes and receptors.  For spin structures, the free energy is
 * equal to {@code preFactor * (1.0 - 2.0 * f)}, where {@code f} is
 * the fraction of matching spins and {@code preFactor} is the scalar
 * factor. The free energy ranges from {@code -preFactor} for exactly
 * matching epitopes and receptors to {@code +preFactor} for complete
 * mismatches.
 */
public final class QuadraticAffinity extends NumericAffinity {
    /**
     * Creates a new quadratic binding model with a fixed scaling
     * factor.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar multiple applied to the normalized
     * quadratic distance between epitopes and receptors.
     */
    public QuadraticAffinity(double actEnergy, double preFactor) {
        super(actEnergy, preFactor);
    }

    @Override public double computeFreeEnergy(Epitope epitope, Receptor receptor) {
        int length = commonLength(epitope, receptor);

	VectorView view1   = asNumeric(epitope);
	VectorView view2   = asNumeric(receptor);
	JamVector  product = JamVector.multiplyEBE(view1, view2);

	return -getPreFactor() * VectorAggregator.sum(product) / length;
    };

    @Override public AffinityType getType() {
        return AffinityType.QUADRATIC;
    }
}
