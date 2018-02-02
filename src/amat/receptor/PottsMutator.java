
package amat.receptor;

import jam.math.JamRandom;

import amat.structure.PottsStructure;

/**
 * Generates mutations in {@code PottsStructure} receptors.
 */
public final class PottsMutator extends Mutator {
    private int cardinality;
    private int[] elements; // The Potts elements being mutated...

    /**
     * Creates a new mutator for {@code PottsStructure} receptors.
     */
    public PottsMutator() {
        super();
    }

    @Override public boolean isValidParent(Receptor parent) {
        return parent.getStructure() instanceof PottsStructure;
    }

    @Override protected void copyElements(Receptor parent) {
        PottsStructure structure = (PottsStructure) parent.getStructure();

        cardinality = structure.cardinality();
        elements = structure.asOrdinal();
    }

    @Override protected void mutateElement(int index) {
        //
        // Let C be the cardinality. Add a random integer on the
        // interval [1, C), then take the modulo C, to guarantee
        // a different element...
        //
        int original = elements[index];
        int mutation = (original + JamRandom.global().nextInt(1, cardinality)) % cardinality;

        assert mutation != original;
        assert 0 <= mutation && mutation < cardinality;

        elements[index] = mutation;
    }

    @Override protected PottsStructure newStructure() {
        return new PottsStructure(cardinality, elements);
    }
}
