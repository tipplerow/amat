
package amat.panel;

import java.util.Collection;

import jam.app.JamProperties;
import jam.lang.JamException;
import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.util.CollectionUtil;

import amat.binding.AffinityModel;
import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Defines a representative sample of immunogenic epitopes for a given
 * pathogen species, with the purpose of computing the neutralization
 * breadth.
 *
 * <p>A global neutralization panel (accessible via the static method
 * {@link NeutralizationPanel#global()}) is defined by the following
 * system properties:
 *
 * <p><b>{@code amat.NeutralizationPanel.panelType:}</b> Enumerated
 * type for the neutralization panel.
 *
 * <p><b>{@code amat.NeutralizationPanel.affinityThreshold:}</b>
 * Minimum receptor-epitope binding affinity (in units of kT) required
 * to neutralize a member of the panel.
 */
public abstract class NeutralizationPanel {
    private final double affinityThreshold;

    private static final AffinityModel AFFINITY_MODEL = AffinityModel.global();
    private static final DoubleComparator COMPARATOR  = DoubleComparator.DEFAULT;
    private static final DoubleRange THRESHOLD_RANGE  = DoubleRange.POSITIVE;

    private static NeutralizationPanel global = null;

    /**
     * Creates a new neutralization panel with the affinity threshold
     * given by the {@code amat.NeutralizationPanel.affinityThreshold}
     * system property.
     *
     * @throws RuntimeException unless a valid affinity threshold is
     * defined by the system properties.
     */
    protected NeutralizationPanel() {
        this(resolveAffinityThreshold());
    }

    /**
     * Creates a new neutralization panel with a fixed affinity
     * threshold.
     *
     * @param affinityThreshold the minimum receptor-epitope binding
     * affinity (in units of kT) required to neutralize a member of
     * the panel.
     *
     * @throws IllegalArgumentException unless the threshold is
     * positive.
     */
    protected NeutralizationPanel(double affinityThreshold) {
        validateThreshold(affinityThreshold);
        this.affinityThreshold = affinityThreshold;
    }

    private static void validateThreshold(double affinityThreshold) {
        THRESHOLD_RANGE.validate("Affinity threshold", affinityThreshold);
    }

    /**
     * Name of the system property which defines the type of
     * neutralization panel.
     */
    public static final String PANEL_TYPE_PROPERTY = "amat.NeutralizationPanel.panelType";

    /**
     * Name of the system property which defines the neutralization
     * affinity threshold.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = "amat.NeutralizationPanel.affinityThreshold";

    /**
     * Returns the global neutralization panel defined by system
     * properties.
     *
     * @return the global neutralization panel defined by system
     * properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the neutralization panel are properly defined.
     */
    public static NeutralizationPanel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static NeutralizationPanel createGlobal() {
        PanelType panelType = resolvePanelType();

        switch (panelType) {
        case UNIFORM_DISCRETE:
            return new UniformDiscretePanel(resolveAffinityThreshold());

        default:
            throw JamException.runtime("Unknown affinity type [%s].", panelType);
        }
    }

    private static PanelType resolvePanelType() {
        return JamProperties.getRequiredEnum(PANEL_TYPE_PROPERTY, PanelType.class);
    }

    private static double resolveAffinityThreshold() {
        return JamProperties.getRequiredDouble(AFFINITY_THRESHOLD_PROPERTY, THRESHOLD_RANGE);
    }

    /**
     * Computes the fraction of epitopes in this panel that are
     * neutralized by a given B cell receptor.
     *
     * @param receptor the B cell receptor to test.
     *
     * @return the fraction of epitopes in this panel that are
     * neutralized by the given B cell receptor.
     */
    public abstract double computeBreadth(Receptor receptor);

    /**
     * Computes the fraction of epitopes from an arbitrary collection
     * that are neutralized by a given B cell receptor.
     *
     * @param receptor the B cell receptor to test.
     *
     * @param epitopes the epitopes to test.
     *
     * @return the fraction of epitopes from the specified collection
     * that are neutralized by the given B cell receptor.
     *
     * @throws IllegalArgumentException if the epitope collection is
     * empty.
     */
    public double computeBreadth(Receptor receptor, Collection<Epitope> epitopes) {
        if (epitopes.isEmpty())
            throw new IllegalArgumentException("At least one epitope is required.");

        int neutralized = 0;

        for (Epitope epitope : epitopes)
            if (isNeutralized(epitope, receptor))
                ++neutralized;

        return DoubleUtil.ratio(neutralized, epitopes.size());
    }

    /**
     * Computes the mean affinity of a given B cell receptor for the
     * epitopes in this panel.
     *
     * @param receptor the B cell receptor to test.
     *
     * @return the mean affinity of the given B cell receptor for the
     * epitopes in this panel.
     */
    public abstract double computeMeanAffinity(Receptor receptor);

    /**
     * Computes the mean affinity of a given B cell receptor for the
     * epitopes in an arbitrary collection.
     *
     * @param receptor the B cell receptor to test.
     *
     * @param epitopes the epitopes to test.
     *
     * @return the mean affinity of the given B cell receptor for the
     * epitopes in the specified collection.
     */
    public static double computeMeanAffinity(Receptor receptor, Collection<Epitope> epitopes) {
        return CollectionUtil.average(epitopes, epitope -> AFFINITY_MODEL.computeAffinity(epitope, receptor));
    }

    /**
     * Returns the number of unique epitopes in this panel.
     *
     * @return the number of unique epitopes in this panel.
     */
    public abstract long countEpitopes();

    /**
     * Returns the enumerated type for this neutralization panel.
     *
     * @return the enumerated type for this neutralization panel.
     */
    public abstract PanelType getType();

    /**
     * Returns the affinity threshold for this neutralization panel
     * (in units of kT).
     *
     * @return the affinity threshold for this neutralization panel
     * (in units of kT).
     */
    public double getAffinityThreshold() {
        return affinityThreshold;
    }

    /**
     * Determines whether an epitope is neutralized by a receptor.
     *
     * @param epitope the epitope to examine.
     *
     * @param receptor the receptor to examine.
     *
     * @return {@code true} iff the binding affinity (computed by the
     * global affinity model) of the receptor to the epitope meets or
     * exceeds the affinity threshold.
     */
    public boolean isNeutralized(Epitope epitope, Receptor receptor) {
        return COMPARATOR.GE(AFFINITY_MODEL.computeAffinity(epitope, receptor), affinityThreshold);
    }
}
