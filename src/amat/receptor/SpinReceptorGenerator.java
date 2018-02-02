
package amat.receptor;

import amat.structure.SpinStructure;

/**
 * Produces receptors with binary spin states as the structural
 * elements.
 */
public final class SpinReceptorGenerator extends ReceptorGenerator {
    private final int length;

    /**
     * Creates a new generator to produce receptors with spin
     * elements.
     *
     * @param length the number of spins in each receptor.
     */
    public SpinReceptorGenerator(int length) {
        this.length = length;
    }

    /**
     * Returns the number of spins in the receptors produced by this
     * generator.
     *
     * @return the number of spins in the receptors produced by this
     * generator.
     */
    public int getLength() {
        return length;
    }

    @Override public Receptor generate() {
        return new Receptor(SpinStructure.variable(length));
    }
}
