
package amat.binding;

import amat.epitope.Epitope;
import amat.receptor.Receptor;
import amat.structure.DiscreteStructure;

/**
 * Base class for models of interactions between epitopes and
 * receptors composed of discrete structural elements.
 */
public abstract class DiscreteAffinity extends AffinityModel {
    /**
     * Creates a free energy model for discrete structures.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar pre-factor required to rescale the
     * model-specific interaction function into a free energy in units
     * of kT.
     */
    protected DiscreteAffinity(double actEnergy, double preFactor) {
        super(actEnergy, preFactor);
    }

    /**
     * Returns the discrete epitope structure.
     *
     * @param epitope the epitope to examine.
     *
     * @return the discrete structure of the given epitope.
     *
     * @throws RuntimeException unless the epitope has a discrete
     * structure.
     */
    protected DiscreteStructure asDiscrete(Epitope epitope) {
	return (DiscreteStructure) epitope.getStructure();
    }

    /**
     * Returns the discrete receptor structure.
     *
     * @param receptor the receptor to examine.
     *
     * @return the discrete structure of the given receptor.
     *
     * @throws RuntimeException unless the receptor has a discrete
     * structure.
     */
    protected DiscreteStructure asDiscrete(Receptor receptor) {
	return (DiscreteStructure) receptor.getStructure();
    }

    @Override public void validate(Epitope epitope, Receptor receptor) {
        epitope.getStructure().validateDiscrete();
        receptor.getStructure().validateDiscrete();
    }
}
