
package amat.receptor;

import amat.structure.PottsStructure;

/**
 * Produces receptors with binary {@code 0/1} states as the structural
 * elements.
 */
public final class PottsReceptorGenerator extends ReceptorGenerator {
    private final int cardinality;
    private final int length;

    /**
     * Creates a new generator to produce receptors with {@code 0/1}
     * elements.
     *
     * @param cardinality the cardinality of the Potts states.
     *
     * @param length the number of elements in each receptor.
     */
    public PottsReceptorGenerator(int cardinality, int length) {
        this.cardinality = cardinality;
        this.length = length;
    }

    /**
     * Returns the cardinality of the Potts states produced by this
     * generator.
     *
     * @return the cardinality of the Potts states produced by this
     * generator.
     */
    public int getCardinality() {
        return cardinality;
    }

    /**
     * Returns the number of elements in the receptors produced by
     * this generator.
     *
     * @return the number of elements in the receptors produced by
     * this generator.
     */
    public int getLength() {
        return length;
    }

    @Override public Receptor generate() {
        return new Receptor(PottsStructure.variable(cardinality, length));
    }
}
