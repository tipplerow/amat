
package amat.receptor;

import java.util.EnumMap;

import jam.math.EventSet;
import jam.math.Probability;

/**
 * Converts between two specifications of mutation frequency: (1)
 * probabilities of point mutations occurring independently in
 * individual receptor elements, and (2) joint probabilities of
 * one or more mutations of a given type occurring over the whole
 * receptor.
 */
public final class MutationCalculator {
    /**
     * Computes the receptor aggregate (joint) probabilities of (1)
     * one or more lethal mutations, (2) all silent mutations, or (3)
     * one or more somatic mutations for a receptor with a fixed
     * length.
     *
     * @param elementProbs the point probabilities of lethal, silent,
     * and somatic mutations for invidual elements of the receptor.
     *
     * @param receptorLen the number of elements in the receptor.
     *
     * @return an event set containing receptor aggregate (joint)
     * probabilities for (1) one or more lethal mutations, (2) all
     * silent mutations, or (3) one or more somatic mutations for a
     * receptor with the specified length.
     */
    public static EventSet<MutationType> computeReceptorProb(EventSet<MutationType> elementProbs, int receptorLen) {
        Probability elementLethal  = elementProbs.getEventProbability(MutationType.LETHAL);
        Probability elementSilent  = elementProbs.getEventProbability(MutationType.SILENT);
        Probability elementSomatic = elementProbs.getEventProbability(MutationType.SOMATIC);

        // The outcome for the entire receptor is LETHAL if one or
        // more point mutations are LETHAL...
        Probability receptorLethal = elementLethal.oneOrMoreOccur(receptorLen);

        // The outcome for the entire receptor is SILENT if and only
        // if all point mutations are SILENT...
        Probability receptorSilent = elementSilent.allOccur(receptorLen);

        // And the other outcome is one or more SOMATIC mutations...
        Probability receptorSomatic = Probability.other(receptorLethal, receptorSilent);

        EnumMap<MutationType, Probability> receptorProb = 
            new EnumMap<MutationType, Probability>(MutationType.class);

        receptorProb.put(MutationType.LETHAL,  receptorLethal);
        receptorProb.put(MutationType.SILENT,  receptorSilent);
        receptorProb.put(MutationType.SOMATIC, receptorSomatic);

        return EventSet.create(receptorProb);
    }

    /**
     * Computes the point probabilities of lethal, silent, and somatic
     * mutations (in individual receptor elements).
     *
     * @param receptorProbs receptor aggregate (joint) probabilities
     * for (1) one or more lethal mutations, (2) all silent mutations,
     * and (3) one or more somatic mutations in a receptor with the
     * specified length.
     *
     * @param receptorLen the number of elements in the receptor.
     *
     * @return an event set containing point probabilities for lethal,
     * silent, and somatic mutations (in individual receptor elements).
     */
    public static EventSet<MutationType> computeElementProb(EventSet<MutationType> receptorProbs, int receptorLen) {
        Probability receptorLethal  = receptorProbs.getEventProbability(MutationType.LETHAL);
        Probability receptorSilent  = receptorProbs.getEventProbability(MutationType.SILENT);
        Probability receptorSomatic = receptorProbs.getEventProbability(MutationType.SOMATIC);

        // The outcome for the entire receptor was LETHAL if one or
        // more point mutations were LETHAL...
        Probability elementLethal = receptorLethal.ifOneOrMoreOccurred(receptorLen);

        // The outcome for the entire receptor was SILENT if and only
        // if all point mutations were SILENT...
        Probability elementSilent = receptorSilent.ifAllOccurred(receptorLen);

        // And the other outcome is a SOMATIC mutation...
        Probability elementSomatic = Probability.other(elementLethal, elementSilent);

        EnumMap<MutationType, Probability> elementProb = 
            new EnumMap<MutationType, Probability>(MutationType.class);

        elementProb.put(MutationType.LETHAL,  elementLethal);
        elementProb.put(MutationType.SILENT,  elementSilent);
        elementProb.put(MutationType.SOMATIC, elementSomatic);

        return EventSet.create(elementProb);
    }
}

