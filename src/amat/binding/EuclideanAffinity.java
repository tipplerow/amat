
package amat.binding;

import jam.vector.VectorAggregator;

import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Defines the free energy of epitope-receptor binding as the
 * <em>square</em> of the Euclidean (2-norm) distance between 
 * their numerical vector structures of equal length, divided
 * (normalized) by their length, and multiplied by a scalar
 * pre-factor.
 *
 * <p>This model typically applies to {@code ShapeStructure} 
 * epitopes and receptors.
 */
public final class EuclideanAffinity extends NumericAffinity {
    /**
     * Creates a new Euclidean binding model with a fixed scaling
     * factor.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar multiple applied to the normalized
     * Euclidean distance between epitopes and receptors.
     */
    public EuclideanAffinity(double actEnergy, double preFactor) {
        super(actEnergy, preFactor);
    }

    @Override public double computeFreeEnergy(Epitope epitope, Receptor receptor) {
        return getPreFactor() * normalizedAggregateDifference(epitope, receptor, VectorAggregator.SUMSQR);
    };

    @Override public AffinityType getType() {
        return AffinityType.EUCLIDEAN;
    }
}
