
package amat.panel;

import jam.util.AutoMap;

import amat.vaccine.VaccinationSchedule;

/**
 * Defines a neutralization panel containing the epitopes presented by
 * vaccination on or before a specified germinal center cycle.
 */
public final class FootprintPanel extends CustomPanel {
    private final int gcCycle;

    // Instances indexed by GC cycle, created on demand...
    private static final AutoMap<Integer, FootprintPanel> instances = 
        AutoMap.hash(cycle -> new FootprintPanel(cycle));

    private FootprintPanel(int gcCycle) {
        super(VaccinationSchedule.global().getEpitopeFootprint(gcCycle));
        this.gcCycle = gcCycle;
    }

    /**
     * Returns the footprint panel for a specific germinal center
     * cycle.
     *
     * <p>The affinity threshold is defined by the system property
     * {@code amat.NeutralizationPanel.affinityThreshold}.
     *
     * @param gcCycle the germinal center cycle of interest.
     *
     * @return the footprint panel for the specified germinal center
     * cycle.
     *
     * @throws IllegalArgumentException if the cycle index is
     * negative.
     */
    public static FootprintPanel instance(int gcCycle) {
        if (gcCycle < 0)
            throw new IllegalArgumentException("Germinal center cycle must be non-negative.");
        else
            return instances.get(gcCycle);
    }

    /**
     * Returns the germinal center cycle for this panel.
     *
     * @return the germinal center cycle for this panel.
     */
    public int getGCCycle() {
        return gcCycle;
    }

    @Override public PanelType getType() {
        return PanelType.FOOTPRINT;
    }
}
