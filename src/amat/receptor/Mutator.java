
package amat.receptor;

import com.google.common.collect.EnumMultiset;

import jam.lang.JamException;
import jam.math.DoubleUtil;
import jam.math.EventSet;
import jam.math.Probability;

import amat.structure.Structure;
import amat.structure.StructureType;

/**
 * Defines an interface for managing the mutation of receptors.
 */
public abstract class Mutator {
    private final EventSet<MutationType> elementEventSet;

    // Running totals for mutation outcomes, used to ensure that the
    // actual outcome frequencies are near their expected values...
    private final EnumMultiset<MutationType> mutationCounter =
        EnumMultiset.create(MutationType.class);

    // The single global mutator...
    private static Mutator global = null;

    /**
     * Creates a new mutator with fixed event probabilities given by
     * the global system properties.
     */
    protected Mutator() {
        this.elementEventSet = MutatorProperties.getElementEventSet();
    }

    /**
     * Returns a mutator that will produce a sequence of mutations
     * defined by the global mutator properties.
     *
     * @return a mutator that will produce a sequence of mutations
     * defined by the global mutator properties.
     *
     * @throws RuntimeException unless the global mutator properties
     * are properly defined.
     */
    public static Mutator global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static Mutator createGlobal() {
        StructureType structureType = ReceptorProperties.getStructureType();

        switch (structureType) {
        case BIT:
            return new BitMutator();

        case POTTS:
            return new PottsMutator();

        case SPIN:
            return new SpinMutator();

        default:
            throw JamException.runtime("No receptor mutator for structure type [%s].", structureType);
        }
    }

    /**
     * Identifies parent receptors that are compatible with (can be
     * mutated by) this mutator.
     *
     * @param parent the receptor to validate.
     *
     * @return {@code true} iff the specified parent is compatible
     * with this mutator.
     */
    public abstract boolean isValidParent(Receptor parent);

    /**
     * Returns the actual frequency of mutations of a given type
     * produced by this mutator.
     *
     * @param mutationType the type of mutation outcome.
     *
     * @return the actual frequency of mutations of the specified type
     * produced by this mutator.
     */
    public double getFrequency(MutationType mutationType) {
        return DoubleUtil.ratio(mutationCounter.count(mutationType), mutationCounter.size());
    }

    /**
     * Mutates a parent receptor.
     *
     * @param parent the receptor to mutate.
     *
     * @return either (1) {@code null} if any element mutations were
     * lethal; (2) the parent itself, if no element mutations were
     * lethal or affinity-affecting; or (3) a new mutated receptor, if
     * one or more element mutations were affinity-affecting and none
     * were lethal.
     *
     * @throws RuntimeException unless the parent is compatible with
     * this mutator.
     */
    public synchronized Receptor mutate(Receptor parent) {
        //
        // This method must be synchronized because the structure
        // being mutated (in the local workspace) would be shared
        // among mulitple threads...
        //
        int length = parent.getStructure().length();
        boolean somatic = false;

        validateParent(parent);
        copyElements(parent);

        for (int elementIndex = 0; elementIndex < length; elementIndex++) {
            MutationType mutationType = elementEventSet.select();

            switch (mutationType) {
            case LETHAL:
                return lethalMutation();

            case SILENT:
                // Do nothing for a silent mutation...
                break;

            case SOMATIC:
                somatic = true;
                mutateElement(elementIndex);
                break;

            default:
                throw new IllegalStateException("Unknown mutation type.");
            }
        }

        if (somatic)
            return somaticMutation();
        else
            return silentMutation(parent);
    }

    private Receptor lethalMutation() {
        mutationCounter.add(MutationType.LETHAL);
        return null;
    }

    private Receptor silentMutation(Receptor parent) {
        mutationCounter.add(MutationType.SILENT);
        return parent;
    }

    private Receptor somaticMutation() {
        mutationCounter.add(MutationType.SOMATIC);
        return new Receptor(newStructure());
    }

    /**
     * Copies the underlying elements of the parent receptor structure
     * into a local workspace.
     *
     * @param parent the parent to mutate.
     */
    protected abstract void copyElements(Receptor parent);

    /**
     * Mutates an element of the receptor structure.
     *
     * @param index the index of the element to mutate.
     */
    protected abstract void mutateElement(int index);

    /**
     * Creates a new structure to hold the mutated elements.
     *
     * @return the new structure containing the mutated elements.
     */
    protected abstract Structure newStructure();

    /**
     * Ensures that a parent receptor is compatible with (can be
     * mutated by) this mutator.
     *
     * @param parent the receptor to mutate.
     *
     * @throws RuntimeException unless the parent is compatible with
     * this mutator.
     */
    public void validateParent(Receptor parent) {
        if (!isValidParent(parent))
            throw new IllegalArgumentException("Invalid parent type.");
    }
}

