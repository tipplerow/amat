
package amat.receptor;

import amat.structure.BitStructure;

/**
 * Produces receptors with binary {@code 0/1} states as the structural
 * elements.
 */
public final class BitReceptorGenerator extends ReceptorGenerator {
    private final int length;

    /**
     * Creates a new generator to produce receptors with {@code 0/1}
     * elements.
     *
     * @param length the number of bits in each receptor.
     */
    public BitReceptorGenerator(int length) {
        this.length = length;
    }

    /**
     * Returns the number of bits in the receptors produced by this
     * generator.
     *
     * @return the number of bits in the receptors produced by this
     * generator.
     */
    public int getLength() {
        return length;
    }

    @Override public Receptor generate() {
        return new Receptor(BitStructure.variable(length));
    }
}
