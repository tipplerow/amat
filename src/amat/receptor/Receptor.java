
package amat.receptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import amat.structure.Structure;

/**
 * Represents a B cell or T cell receptor that can recognize epitopes
 * and undergo somatic mutations.
 */
public final class Receptor {
    private final Structure structure;

    /**
     * Creates a new receptor with a fixed structure.
     *
     * @param structure the fixed structure to assign.
     */
    public Receptor(Structure structure) {
        this.structure = structure;
    }

    /**
     * Creates a mutated variant of this receptor; this receptor is
     * unchanged.
     *
     * @return either (1) a new mutated variant of this receptor, if
     * the mutation was affinity-affecting, or (2) this receptor, if
     * the mutation was silent; (3) {@code null} if the mutation was
     * lethal.
     */
    public Receptor mutate() {
        return Mutator.global().mutate(this);
    }

    /**
     * Returns the fixed structure of this receptor.
     *
     * @return the fixed structure of this receptor.
     */
    public Structure getStructure() {
        return structure;
    }

    /**
     * Extracts the structures from a collection of receptors.
     *
     * @param receptors the receptors to process.
     *
     * @return a list containing the structure of each receptor, in the
     * order returned by the collection iterator.
     */
    public static List<Structure> getStructures(Collection<Receptor> receptors) {
        ArrayList<Structure> structures = new ArrayList<Structure>(receptors.size());

        for (Receptor receptor : receptors)
            structures.add(receptor.getStructure());

        return structures;
    }

    @Override public int hashCode() {
        return structure.hashCode();
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Receptor) && equalsReceptor((Receptor) that);
    }

    private boolean equalsReceptor(Receptor that) {
        return this.structure.equals(that.structure);
    }

    @Override public String toString() {
        return String.format("Receptor(%s)", getStructure());
    }
}
