
package amat.binding;

import jam.vector.VectorAggregator;

import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Defines the free energy of epitope-receptor binding as the
 * Manhattan (1-norm) distance between their numerical vector
 * structures of equal length, normalized by their length, and
 * then multiplied by a scalar pre-factor.
 *
 * <p>Manhattan models are typically used with {@code BitStructure}
 * epitopes and receptors.  For bit structures, the free energy is
 * equal to {@code preFactor * (1.0 - f)}, where {@code f} is the
 * fraction of matching bits and {@code preFactor} is the scalar
 * pre-factor.  The free energy ranges from {@code 0.0} for exactly
 * matching epitopes and receptor pairs to {@code 1.0} for completely
 * mismatched pairs.
 */
public final class ManhattanAffinity extends NumericAffinity {
    /**
     * Creates a new Manhattan binding model with a fixed scaling
     * factor.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar multiple applied to the normalized
     * Manhattan distance between epitopes and receptors.
     */
    public ManhattanAffinity(double actEnergy, double preFactor) {
        super(actEnergy, preFactor);
    }

    @Override public double computeFreeEnergy(Epitope epitope, Receptor receptor) {
        return getPreFactor() * normalizedAggregateDifference(epitope, receptor, VectorAggregator.NORM1);
    };

    @Override public AffinityType getType() {
        return AffinityType.MANHATTAN;
    }
}
