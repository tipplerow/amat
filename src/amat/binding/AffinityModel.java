
package amat.binding;

import jam.app.JamProperties;
import jam.lang.JamException;
import jam.math.DoubleRange;

import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Encodes models of epitope-receptor interactions and computes the
 * free energy, affinity, and equilibrium constant for interactions.
 *
 * <p>A global affinity model (accessible via the static method
 * {@link AffinityModel#global()}) is defined by the following
 * system properties:
 *
 * <p><b>{@code amat.AffinityModel.modelType:}</b> Enumerated type for
 * the affinity model.
 *
 * <p><b>{@code amat.AffinityModel.actEnergy:}</b> The activation energy
 * required to convert free energy into affinity (in units of kT).
 *
 * <p><b>{@code amat.AffinityModel.preFactor:}</b> A scalar pre-factor
 * that rescales the model-specific interaction function into a free
 * energy in units of kT; must be positive.
 */
public abstract class AffinityModel {
    private final double actEnergy;
    private final double preFactor;

    private static AffinityModel global = null;

    /**
     * Name of the system property which defines the type of free-energy
     * model.
     */
    public static final String MODEL_TYPE_PROPERTY = "amat.AffinityModel.modelType";

    /**
     * Name of the system property which defines the activation energy.
     */
    public static final String ACT_ENERGY_PROPERTY = "amat.AffinityModel.actEnergy";

    /**
     * Name of the system property which defines the scalar pre-factor.
     */
    public static final String PRE_FACTOR_PROPERTY = "amat.AffinityModel.preFactor";

    /**
     * Creates a new affinity model.
     *
     * @param actEnergy the activation energy required to convert free
     * energy into <em>affinity</em> (in units of kT).
     *
     * @param preFactor the scalar pre-factor required to rescale the
     * model-specific interaction function into a free energy in units
     * of kT.
     */
    protected AffinityModel(double actEnergy, double preFactor) {
        validatePreFactor(preFactor);

        this.actEnergy = actEnergy;
        this.preFactor = preFactor;
    }

    private void validatePreFactor(double preFactor) {
        if (preFactor <= 0.0)
            throw new IllegalArgumentException("Pre-factor must be positive.");
    }

    /**
     * Returns the global affinity model defined by system properties.
     *
     * @return the global affinity model defined by system properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the affinity model are properly defined.
     */
    public static AffinityModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static AffinityModel createGlobal() {
        AffinityType modelType = resolveModelType();

        switch (modelType) {
        case EUCLIDEAN:
            return new EuclideanAffinity(resolveActEnergy(), resolvePreFactor());

        case HAMMING:
            return HammingAffinity.global();

        case MANHATTAN:
            return new ManhattanAffinity(resolveActEnergy(), resolvePreFactor());

        case QUADRATIC:
            return new QuadraticAffinity(resolveActEnergy(), resolvePreFactor());

        default:
            throw JamException.runtime("Unknown affinity type [%s].", modelType);
        }
    }

    private static AffinityType resolveModelType() {
        return JamProperties.getRequiredEnum(MODEL_TYPE_PROPERTY, AffinityType.class);
    }

    private static double resolveActEnergy() {
        return JamProperties.getRequiredDouble(ACT_ENERGY_PROPERTY);
    }

    private static double resolvePreFactor() {
        return JamProperties.getRequiredDouble(PRE_FACTOR_PROPERTY, DoubleRange.POSITIVE);
    }

    /**
     * Returns the length shared by an epitope and receptor.
     *
     * @param epitope the epitope to examine.
     *
     * @param receptor the receptor to examine.
     *
     * @return the length shared by the given epitope and receptor.
     *
     * @throws RuntimeException unless the epitope and receptor have
     * the same length.
     */
    public int commonLength(Epitope epitope, Receptor receptor) {
        int epitopeLength  = epitope.getStructure().length();
        int receptorLength = receptor.getStructure().length();

        if (epitopeLength == receptorLength)
            return epitopeLength;
        else
            throw JamException.runtime("Epitope and receptor have different lengths.");
    }

    /**
     * Computes the <em>affinity</em> of a receptor for an epitope.
     *
     * @param epitope the epitope partner in the binding interaction.
     *
     * @param receptor the receptor partner in the binding interaction.
     *
     * @return the binding affinity between the epitope and receptor
     * (in units of kT).
     *
     * @throws RuntimeException unless the epitope and receptor are
     * compatible.
     */
    public double computeAffinity(Epitope epitope, Receptor receptor) {
        return computeAffinity(computeFreeEnergy(epitope, receptor));
    }

    /**
     * Computes the <em>affinity</em> given the binding free energy.
     *
     * @param freeEnergy the free energy of epitope-receptor binding.
     *
     * @return the binding affinity (in units of kT).
     */
    public double computeAffinity(double freeEnergy) {
        return getActivationEnergy() - freeEnergy;
    }

    /**
     * Computes the equilibrium constant for receptor-epitope binding.
     *
     * @param epitope the epitope partner in the binding interaction.
     *
     * @param receptor the receptor partner in the binding interaction.
     *
     * @return the dimensionless equilibrium constant for binding of
     * the specified epitope and receptor.
     *
     * @throws RuntimeException unless the epitope and receptor are
     * compatible.
     */
    public double computeEquilConst(Epitope epitope, Receptor receptor) {
        return computeEquilConst(computeAffinity(epitope, receptor));
    }

    /**
     * Computes the equilibrium constant for receptor-epitope binding.
     *
     * @param affinity the affinity of receptor-epitope binding (in
     * units of kT).
     *
     * @return the dimensionless equilibrium constant.
     */
    public static double computeEquilConst(double affinity) {
        return Math.exp(affinity);
    }

    /**
     * Computes the free energy of binding between an epitope and a
     * receptor (in units of kT).
     *
     * @param epitope the epitope partner in the binding interaction.
     *
     * @param receptor the receptor partner in the binding interaction.
     *
     * @return the free energy of binding between the epitope and
     * receptor.
     *
     * @throws RuntimeException unless the epitope and receptor are
     * compatible.
     */
    public abstract double computeFreeEnergy(Epitope epitope, Receptor receptor);

    /**
     * Returns the enumerated type for this affinity model.
     *
     * @return the enumerated type for this affinity model.
     */
    public abstract AffinityType getType();

    /**
     * Returns the activation energy required to convert the free
     * energy of binding into a binding <em>affinity</em>.
     *
     * @return the activation energy in this affinity model (in units
     * of kT).
     */
    public double getActivationEnergy() {
        return actEnergy;
    }

    /**
     * Returns the scalar pre-factor that rescales the model-specific
     * interaction function into a free energy in units of kT.
     *
     * @return the scalar pre-factor.
     */
    public double getPreFactor() {
        return preFactor;
    }

    /**
     * Ensures that the epitope and receptor structures are compatible
     * with each other and with the interaction model.
     *
     * @param epitope the epitope to validate.
     *
     * @param receptor the receptor to validate.
     *
     * @throws RuntimeException unless the epitope and receptor are
     * compatible.
     */
    public abstract void validate(Epitope epitope, Receptor receptor);
}
