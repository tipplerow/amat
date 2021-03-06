
package amat.binding;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;

import amat.epitope.Epitope;
import amat.receptor.Receptor;
import amat.structure.DiscreteStructure;

/**
 * Defines the free energy of epitope-receptor binding as the Hamming
 * distance between their discrete structures multiplied by a scalar
 * pre-factor (the affinity gain generated by the matching of a single
 * receptor-epitope element pair).
 *
 * <p>This model applies only to epitopes and receptors with discrete
 * structural elements.
 *
 * <p>The model parameters are defined by the following system
 * properties:
 *
 * <p><b>{@code amat.HammingAffinity.matchGain:}</b> The affinity gain
 * generated by matching a single receptor-epitope element pair (in
 * units of kT), or equivalently, the free-energy penalty associated
 * with a single mismatched receptor-epitope element pair.
 *
 * <p><b>{@code amat.HammingAffinity.actEnergy:}</b> The free energy
 * at which the affinity is zero (in units of kT).  If this property
 * is unspecified, the default value with generate zero affinity for
 * the number of matching elements expected simply by chance.
 */
public final class HammingAffinity extends DiscreteAffinity {
    private static HammingAffinity global = null;

    /**
     * Name of the system property which defines the affinity gain for
     * matching a single element-receptor pair.
     */
    public final static String MATCH_GAIN_PROPERTY = "amat.HammingAffinity.matchGain";

    /**
     * Name of the system property which defines the activation energy.
     */
    public final static String ACT_ENERGY_PROPERTY = "amat.HammingAffinity.actEnergy";

    /**
     * Creates a new Hamming binding model with a fixed gain and
     * the default activation energy.
     *
     * @param matchGain the affinity gain generated by matching a
     * single receptor-epitope element pair (in units of kT), or
     * equivalently, free-energy penalty associated with a single
     * mismatched receptor-epitope element pair.
     */
    public HammingAffinity(double matchGain) {
        this(matchGain, defaultActEnergy(matchGain));
    }

    /**
     * Creates a new Hamming binding model with fixed gain and
     * activation energy parameters.
     *
     * @param matchGain the affinity gain generated by matching a
     * single receptor-epitope element pair (in units of kT), or
     * equivalently, free-energy penalty associated with a single
     * mismatched receptor-epitope element pair.
     *
     * @param actEnergy the free energy at which the affinity is zero
     * (in units of kT).
     */
    public HammingAffinity(double matchGain, double actEnergy) {
        super(actEnergy, matchGain); // Different order in the super class...
    }

    /**
     * Returns the global affinity model defined by system properties.
     *
     * @return the global affinity model defined by system properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the affinity model are properly defined.
     */
    public static HammingAffinity global() {
        if (global == null)
            createGlobal();

        return global;
    }

    private static void createGlobal() {
        double actEnergy;
        double matchGain = JamProperties.getRequiredDouble(MATCH_GAIN_PROPERTY, DoubleRange.POSITIVE);

        if (JamProperties.isSet(ACT_ENERGY_PROPERTY))
            actEnergy = JamProperties.getRequiredDouble(ACT_ENERGY_PROPERTY, DoubleRange.POSITIVE);
        else
            actEnergy = defaultActEnergy(matchGain);
        
        global = new HammingAffinity(matchGain, actEnergy);
    }

    /**
     * Computes the free energy of binding between an epitope and a
     * receptor (in units of kT) given the Hamming distance between
     * them.
     *
     * @param hammingDistance the Hamming distance between epitope and
     * receptor.
     *
     * @return the free energy of binding at the given distance.
     *
     * @throws IllegalArgumentException if the distance is negative.
     */
    public double computeFreeEnergy(double hammingDistance) {
        return computeFreeEnergy(getMatchGain(), hammingDistance);
    }

    private static double computeFreeEnergy(double matchGain, double hammingDistance) {
        validateDistance(hammingDistance);
        return matchGain * hammingDistance;
    }

    /**
     * Computes the activation energy that will generate zero affinity
     * for a receptor-epitope pair with the mean Hamming distance for
     * structures having the same length and cardinality as those in
     * the global epitope registry.
     *
     * @param matchGain the affinity gain generated by matching a
     * single receptor-epitope element pair (in units of kT), or
     * equivalently, free-energy penalty associated with a single
     * mismatched receptor-epitope element pair.
     *
     * @return the activation energy that will generate zero affinity
     * for a receptor-epitope pair with the mean Hamming distance for
     * structures having the same length and cardinality as those in
     * the global epitope registry.
     */
    public static double defaultActEnergy(double matchGain) {
        return computeFreeEnergy(matchGain, meanDistance());
    }

    /**
     * Computes the mean Hamming distance between discrete structures
     * having the same length and cardinality as those in the global
     * epitope registry.
     *
     * @return the mean Hamming distance between discrete structures
     * having the same length and cardinality as those in the global
     * epitope registry.
     *
     * @throws IllegalStateException unless the global epitope
     * registry is populated with epitopes having a common length and
     * cardinality.
     */
    public static double meanDistance() {
        return meanDistance(Epitope.resolveLength(), Epitope.resolveCardinality());
    }

    /**
     * Computes the mean Hamming distance between discrete structures
     * having a fixed length and cardinality.
     *
     * <p>For any single element in an epitope, the probability that
     * the corresponding receptor element matches is {@code 1 / C},
     * where {@code C} is the cardinality.  The expected number of
     * matches for structures of length {@code L} is therefore {@code
     * L / C}, and the mean Hamming distance is {@code L - (L / C) = L * (C - 1) / C}.
     *
     * @param length the length of all structures.
     *
     * @param cardinality the number of unique structural elements.
     *
     * @return the mean Hamming distance between discrete structures
     * having the specified length and cardinality.
     *
     * @throws IllegalArgumentException unless the length and
     * cardinality are positive.
     */
    public static double meanDistance(int length, int cardinality) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");

        if (cardinality < 1)
            throw new IllegalArgumentException("Cardinality must be positive.");

        return DoubleUtil.ratio(length * (cardinality - 1), cardinality);
    }

    /**
     * Validates the Hamming distance between an epitope and receptor.
     *
     * @param hammingDistance the Hamming distance between epitope and
     * receptor.
     *
     * @throws IllegalArgumentException if the distance is negative.
     */
    public static void validateDistance(double hammingDistance) {
        if (hammingDistance < 0.0)
            throw new IllegalArgumentException("Negative Hamming distance.");
    }

    /**
     * Returns the affinity gain generated by matching a single
     * receptor-epitope element pair (in units of kT), or
     * equivalently, free-energy penalty associated with a single
     * mismatched receptor-epitope element pair.
     *
     * @return the affinity gain generated by matching a single
     * receptor-epitope element pair (in units of kT), or
     * equivalently, free-energy penalty associated with a single
     * mismatched receptor-epitope element pair.
     */
    public double getMatchGain() {
        //
        // It is just the pre-factor, but the name "matchGain" is more
        // meaningful...
        //
        return getPreFactor();
    }

    @Override public double computeFreeEnergy(Epitope epitope, Receptor receptor) {
        validate(epitope, receptor);

        DiscreteStructure epiStruct = asDiscrete(epitope);
        DiscreteStructure recStruct = asDiscrete(receptor);

        return computeFreeEnergy(epiStruct.hammingDistance(recStruct));
    };

    @Override public void validate(Epitope epitope, Receptor receptor) {
        super.validate(epitope, receptor);
        commonLength(epitope, receptor);
    }

    @Override public AffinityType getType() {
        return AffinityType.HAMMING;
    }
}
