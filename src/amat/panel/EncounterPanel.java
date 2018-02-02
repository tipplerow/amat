
package amat.panel;

import amat.bcell.BCell;

/**
 * Defines a neutralization panel containing the epitopes encountered
 * by a B cell <em>and every preceding cell in its lineage</em> during
 * affinity maturation.
 */
public final class EncounterPanel extends CustomPanel {
    private final BCell bcell;

    /**
     * Creates a new panel for a specified B cell.
     *
     * <p>The affinity threshold is defined by the system property
     * {@code amat.NeutralizationPanel.affinityThreshold}.
     *
     * @param bcell the B cell of interest.
     */
    public EncounterPanel(BCell bcell) {
        super(bcell.getEpitopeFootprint());
        this.bcell = bcell;
    }

    /**
     * Returns the B cell for which this panel was generated.
     *
     * @return the B cell for which this panel was generated.
     */
    public BCell getBCell() {
        return bcell;
    }

    @Override public PanelType getType() {
        return PanelType.ENCOUNTER;
    }
}
