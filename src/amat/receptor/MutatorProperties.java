
package amat.receptor;

import java.util.EnumMap;

import jam.app.JamProperties;
import jam.math.EventSet;
import jam.math.Probability;

import amat.epitope.Epitope;

/**
 * Manages receptor mutation characteristics defined through global
 * properties.
 *
 * <p>Mutation probabilities may be specified for point mutations of
 * single elements or as joint probabilities of one or more mutation
 * for the entire receptor.  At most one specification is allowed; the
 * other may be computed as a function of the receptor length.  If no
 * mutation probabilities are specified through system properties, the
 * joint probabilities will default to 0.3 for lethal mutations, 0.2
 * for somatic, and 0.5 for silent.  The specification type must be
 * the same for lethal and somatic mutations.
 *
 * <p><b>{@code amat.Mutator.elementLethalProbability:}</b> The
 * probability that a point (single-element) mutation is lethal
 *  to the containing entity (B cell or T cell).
 *
 * <p><b>{@code amat.Mutator.elementSomaticProbability:}</b> The
 * probability that a point (single-element) mutation is a somatic
 * (non-lethal, affinity-affecting) mutation.
 *
 * <p><b>{@code amat.Mutator.receptorLethalProbability:}</b> The joint
 * probability that one or more lethal point mutations occur.  When
 * specifying the joint probability for the receptor as a whole, all
 * receptors must have the same length.
 *
 * <p><b>{@code amat.Mutator.receptorSomaticProbability:}</b> The
 * joint probability that one or more somatic point mutations occur
 * (and no lethal point mutations occur). When specifying the joint
 * probability for the receptor as a whole, all receptors must have
 * the same length.
 */
public final class MutatorProperties {
    private static EventSet<MutationType> elementEventSet = null;
    private static EventSet<MutationType> receptorEventSet = null;

    private MutatorProperties() {}

    /**
     * Name of the global property which defines the probability that
     * a point mutation is lethal to the containing entity (B cell or
     * T cell).
     */
    public static final String ELEMENT_LETHAL_PROBABILITY_PROPERTY = 
        "amat.Mutator.elementLethalProbability";

    /**
     * Name of the global property which defines the probability that
     * a point mutation is a non-lethal affinity-affecting mutation.
     */
    public static final String ELEMENT_SOMATIC_PROBABILITY_PROPERTY = 
        "amat.Mutator.elementSomaticProbability";

    /**
     * Name of the global property which defines the joint probability
     * that one or more lethal point mutations occur.
     */
    public static final String RECEPTOR_LETHAL_PROBABILITY_PROPERTY = 
        "amat.Mutator.receptorLethalProbability";

    /**
     * Name of the global property which defines the joint probability
     * that one or more somatic point mutations occur (and no lethal
     * point mutations occur).
     */
    public static final String RECEPTOR_SOMATIC_PROBABILITY_PROPERTY = 
        "amat.Mutator.receptorSomaticProbability";

    /**
     * Default value for the joint probability that one or more lethal
     * point mutations occur.
     */
    public static final Probability DEFAULT_RECEPTOR_LETHAL_PROBABILITY = 
        Probability.valueOf(0.3);

    /**
     * Default value for the joint probability that one or more somatic
     * point mutations occur.
     */
    public static final Probability DEFAULT_RECEPTOR_SOMATIC_PROBABILITY = 
        Probability.valueOf(0.2);

    /**
     * Default value for the joint probability that all point
     * mutations are silent.
     */
    public static final Probability DEFAULT_RECEPTOR_SILENT_PROBABILITY = 
        Probability.other(DEFAULT_RECEPTOR_LETHAL_PROBABILITY,
                          DEFAULT_RECEPTOR_SOMATIC_PROBABILITY);

    /**
     * Returns an event set encapsulating the probabilities for each
     * possible single-element (point) mutation event.
     *
     * @return an event set encapsulating the probabilities for each
     * possible single-element (point) mutation event.
     */
    public static EventSet<MutationType> getElementEventSet() {
        if (elementEventSet == null)
            resolveEvents();

        return elementEventSet;
    }

    /**
     * Returns an event set encapsulating the probabilities for each
     * possible receptor-aggregate (joint) mutation event.
     *
     * @return an event set encapsulating the probabilities for each
     * possible receptor-aggregate (joint) mutation event.
     */
    public static EventSet<MutationType> getReceptorEventSet() {
        if (receptorEventSet == null)
            resolveEvents();

        return receptorEventSet;
    }

    private static void resolveEvents() {
        if (areNoPropertiesSet())
            assignDefaultProperties();
        else if (areReceptorPropertiesSet())
            assignReceptorProperties();
        else if (areElementPropertiesSet())
            assignElementProperties();
        else
            throw new IllegalStateException("Conflicting mutator properties are set.");
    }

    private static boolean areNoPropertiesSet() {
        return JamProperties.isUnset(ELEMENT_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(ELEMENT_SOMATIC_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(RECEPTOR_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(RECEPTOR_SOMATIC_PROBABILITY_PROPERTY);
    }

    private static boolean areReceptorPropertiesSet() {
        return JamProperties.isUnset(ELEMENT_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(ELEMENT_SOMATIC_PROBABILITY_PROPERTY)
            && JamProperties.isSet(RECEPTOR_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isSet(RECEPTOR_SOMATIC_PROBABILITY_PROPERTY);
    }

    private static boolean areElementPropertiesSet() {
        return JamProperties.isSet(ELEMENT_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isSet(ELEMENT_SOMATIC_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(RECEPTOR_LETHAL_PROBABILITY_PROPERTY)
            && JamProperties.isUnset(RECEPTOR_SOMATIC_PROBABILITY_PROPERTY);
    }

    private static void assignDefaultProperties() {
        EnumMap<MutationType, Probability> eventProb = 
            new EnumMap<MutationType, Probability>(MutationType.class);

        eventProb.put(MutationType.LETHAL,  DEFAULT_RECEPTOR_LETHAL_PROBABILITY);
        eventProb.put(MutationType.SILENT,  DEFAULT_RECEPTOR_SILENT_PROBABILITY);
        eventProb.put(MutationType.SOMATIC, DEFAULT_RECEPTOR_SOMATIC_PROBABILITY);

        receptorEventSet = EventSet.create(eventProb);
        elementEventSet  = MutationCalculator.computeElementProb(receptorEventSet, Epitope.resolveLength());
    }

    private static void assignReceptorProperties() {
        EnumMap<MutationType, Probability> eventProb = 
            new EnumMap<MutationType, Probability>(MutationType.class);

        Probability lethalProb  = Probability.valueOf(JamProperties.getRequiredDouble(RECEPTOR_LETHAL_PROBABILITY_PROPERTY));
        Probability somaticProb = Probability.valueOf(JamProperties.getRequiredDouble(RECEPTOR_SOMATIC_PROBABILITY_PROPERTY));
        Probability silentProb  = Probability.other(lethalProb, somaticProb);

        eventProb.put(MutationType.LETHAL,  lethalProb);
        eventProb.put(MutationType.SILENT,  silentProb);
        eventProb.put(MutationType.SOMATIC, somaticProb);

        receptorEventSet = EventSet.create(eventProb);
        elementEventSet  = MutationCalculator.computeElementProb(receptorEventSet, Epitope.resolveLength());
    }

    private static void assignElementProperties() {
        EnumMap<MutationType, Probability> eventProb = 
            new EnumMap<MutationType, Probability>(MutationType.class);

        Probability lethalProb  = Probability.valueOf(JamProperties.getRequiredDouble(ELEMENT_LETHAL_PROBABILITY_PROPERTY));
        Probability somaticProb = Probability.valueOf(JamProperties.getRequiredDouble(ELEMENT_SOMATIC_PROBABILITY_PROPERTY));
        Probability silentProb  = Probability.other(lethalProb, somaticProb);

        eventProb.put(MutationType.LETHAL,  lethalProb);
        eventProb.put(MutationType.SILENT,  silentProb);
        eventProb.put(MutationType.SOMATIC, somaticProb);

        elementEventSet  = EventSet.create(eventProb);
        receptorEventSet = MutationCalculator.computeReceptorProb(elementEventSet, Epitope.resolveLength());
    }

    /**
     * Returns the probability of a single-element (point) mutation of
     * a particular type.
     *
     * @param mutationType the enumerated mutation type.
     *
     * @return the probability that a single-element (point) mutation
     * has the specified type.
     */
    public static Probability getElementProbability(MutationType mutationType) {
        return getElementEventSet().getEventProbability(mutationType);
    }

    /**
     * Returns the probability that a single-element (point) mutation
     * is lethal.
     *
     * @return the probability that a single-element (point) mutation
     * is lethal.
     */
    public static Probability getElementLethalProbability() {
        return getElementProbability(MutationType.LETHAL);
    }

    /**
     * Returns the probability that a single-element (point) mutation
     * is silent.
     *
     * @return the probability that a single-element (point) mutation
     * is silent.
     */
    public static Probability getElementSilentProbability() {
        return getElementProbability(MutationType.SILENT);
    }

    /**
     * Returns the probability that a single-element (point) mutation
     * is an affinity-affecting somatic mutation.
     *
     * @return the probability that a single-element (point) mutation
     * is an affinity-affecting somatic mutation.
     */
    public static Probability getElementSomaticProbability() {
        return getElementProbability(MutationType.SOMATIC);
    }

    /**
     * Returns the receptor-aggregate (joint) probability of a 
     * mutation of a particular type.
     *
     * @param mutationType the enumerated mutation type.
     *
     * @return the receptor-aggregate (joint) probability of a 
     * mutation has the specified type.
     */
    public static Probability getReceptorProbability(MutationType mutationType) {
        return getReceptorEventSet().getEventProbability(mutationType);
    }

    /**
     * Returns the receptor-aggregate (joint) probability that one or
     * more point mutations are lethal.
     *
     * @return the receptor-aggregate (joint) probability that one or
     * more point mutations are lethal.
     */
    public static Probability getReceptorLethalProbability() {
        return getReceptorProbability(MutationType.LETHAL);
    }

    /**
     * Returns the receptor-aggregate (joint) probability that all
     * point mutations are silent.
     *
     * @return the receptor-aggregate (joint) probability that all
     * point mutations are silent.
     */
    public static Probability getReceptorSilentProbability() {
        return getReceptorProbability(MutationType.SILENT);
    }

    /**
     * Returns the receptor-aggregate (joint) probability that one or
     * more point mutations are affinity-affecting somatic mutations
     * (and none are lethal).
     *
     * @return the receptor-aggregate (joint) probability that one or
     * more point mutations are affinity-affecting somatic mutations
     * (and none are lethal).
     */
    public static Probability getReceptorSomaticProbability() {
        return getReceptorProbability(MutationType.SOMATIC);
    }

    /**
     * Returns the receptor-aggregate (joint) probability that the
     * lymphocyte survives all point mutations (none are lethal).
     *
     * @return the receptor-aggregate (joint) probability that the
     * lymphocyte survives all point mutations (none are lethal).
     */
    public static Probability getReceptorSurvivalProbability() {
        return getReceptorLethalProbability().not();
    }
}
