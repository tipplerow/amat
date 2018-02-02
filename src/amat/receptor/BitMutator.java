
package amat.receptor;

import jam.vector.BitVector;
import amat.structure.BitStructure;

/**
 * Generates mutations in {@code BitStructure} receptors.
 */
public final class BitMutator extends Mutator {
    //
    // This variable holds the structural elements being mutated...
    //
    private BitVector bits;

    /**
     * Creates a new mutator for {@code BitStructure} receptors.
     */
    public BitMutator() {
        super();
    }

    @Override public boolean isValidParent(Receptor parent) {
        return parent.getStructure() instanceof BitStructure;
    }

    @Override protected void copyElements(Receptor parent) {
        bits = ((BitStructure) parent.getStructure()).copyBits();
    }

    @Override protected void mutateElement(int index) {
        bits.flip(index);
    }

    @Override protected BitStructure newStructure() {
        return new BitStructure(bits);
    }
}
