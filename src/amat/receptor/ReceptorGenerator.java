
package amat.receptor;

import jam.lang.JamException;

import amat.structure.StructureType;

/**
 * Defines an interface for managing the creation of new receptors.
 */
public abstract class ReceptorGenerator {
    private static ReceptorGenerator global = null;

    /**
     * Returns a generator that will produce a sequence of receptors
     * defined by the global receptor properties.
     *
     * @return a generator that will produce a sequence of receptors
     * defined by the global receptor properties.
     *
     * @throws RuntimeException unless the global receptor properties
     * are properly defined.
     */
    public static ReceptorGenerator global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ReceptorGenerator createGlobal() {
        StructureType structureType = ReceptorProperties.getStructureType();

        switch (structureType) {
        case BIT:
            return createGlobalBitReceptorGenerator();

        case POTTS:
            return createGlobalPottsReceptorGenerator();

        case SPIN:
            return createGlobalSpinReceptorGenerator();

        default:
            throw JamException.runtime("No receptor generator for structure type [%s].", structureType);
        }
    }

    private static ReceptorGenerator createGlobalBitReceptorGenerator() {
        return new BitReceptorGenerator(ReceptorProperties.getLength());
    }

    private static ReceptorGenerator createGlobalPottsReceptorGenerator() {
        return new PottsReceptorGenerator(ReceptorProperties.getCardinality(), ReceptorProperties.getLength());

    }

    private static ReceptorGenerator createGlobalSpinReceptorGenerator() {
        return new SpinReceptorGenerator(ReceptorProperties.getLength());
    }

    /**
     * Generates the next receptor in the random sequence defined by
     * this generator.
     *
     * @return the next receptor in the random sequence defined by this
     * generator.
     */
    public abstract Receptor generate();
}
