
package amat.receptor;

import jam.spin.SpinVector;
import amat.structure.SpinStructure;

/**
 * Generates mutations in {@code SpinStructure} receptors.
 */
public final class SpinMutator extends Mutator {
    //
    // This variable holds the structural elements being mutated...
    //
    private SpinVector spins;

    /**
     * Creates a new mutator for {@code SpinStructure} receptors.
     */
    public SpinMutator() {
        super();
    }

    @Override public boolean isValidParent(Receptor parent) {
        return parent.getStructure() instanceof SpinStructure;
    }

    @Override protected void copyElements(Receptor parent) {
        spins = ((SpinStructure) parent.getStructure()).copySpins();
    }

    @Override protected void mutateElement(int index) {
        spins.flip(index);
    }

    @Override protected SpinStructure newStructure() {
        return new SpinStructure(spins);
    }
}
