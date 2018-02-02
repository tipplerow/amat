
package amat.report;

import amat.bcell.BCell;
import amat.panel.EncounterPanel;
import amat.panel.NeutralizationPanel;
import amat.receptor.Receptor;

/**
 * Records the affinity and breadth of a B cell for the immunogens
 * that it encountered during affinity maturation (the "footprint")
 * and for the epitopes in the full global neutralization panel.
 */
public final class AffinityRecord {
    private final long footprintSize;

    private final double footprintAffinity;
    private final double neutPanelAffinity;

    private final double footprintBreadth;
    private final double neutPanelBreadth;

    private AffinityRecord(long   footprintSize,
                           double footprintAffinity,
                           double neutPanelAffinity,
                           double footprintBreadth,
                           double neutPanelBreadth) {
        this.footprintSize = footprintSize;

        this.footprintAffinity = footprintAffinity;
        this.neutPanelAffinity = neutPanelAffinity;

        this.footprintBreadth = footprintBreadth;
        this.neutPanelBreadth = neutPanelBreadth;
    }

    /**
     * Computes the affinity record for a specific B cell.
     *
     * @param bcell the B cell to examine.
     *
     * @return a new affinity record for the specified B cell.
     */
    public static AffinityRecord compute(BCell bcell) {
        Receptor receptor = bcell.getReceptor();

        NeutralizationPanel footPanel = new EncounterPanel(bcell);
        NeutralizationPanel neutPanel = NeutralizationPanel.global();

        long footprintSize = footPanel.countEpitopes();

        double footprintAffinity = footPanel.computeMeanAffinity(receptor);
        double neutPanelAffinity = neutPanel.computeMeanAffinity(receptor);

        double footprintBreadth = footPanel.computeBreadth(receptor);
        double neutPanelBreadth = neutPanel.computeBreadth(receptor);

        return new AffinityRecord(footprintSize,
                                  footprintAffinity, neutPanelAffinity,
                                  footprintBreadth,  neutPanelBreadth);
    }

    /**
     * Returns the number of epitopes encountered by the B cell.
     *
     * @return the number of epitopes encountered by the B cell.
     */
    public long getFootprintSize() {
        return footprintSize;
    }

    /**
     * Returns the mean affinity of the B cell for the epitopes that
     * it encountered during maturation.
     *
     * @return the mean affinity of the B cell for the epitopes that
     * it encountered during maturation.
     */
    public double getFootprintAffinity() {
        return footprintAffinity;
    }

    /**
     * Returns the breadth of the B cell with respect to the epitopes
     * that it encountered during maturation.
     *
     * @return the breadth of the B cell with respect to the epitopes
     * that it encountered during maturation.
     */
    public double getFootprintBreadth() {
        return footprintBreadth;
    }

    /**
     * Returns the mean affinity of the B cell for the epitopes in the
     * full global neutralization panel.
     *
     * @return the mean affinity of the B cell for the epitopes in the
     * full global neutralization panel.
     */
    public double getNeutPanelAffinity() {
        return neutPanelAffinity;
    }

    /**
     * Returns the breadth of the B cell with respect to the epitopes
     * in the full global neutralization panel.
     *
     * @return the breadth of the B cell with respect to the epitopes
     * in the full global neutralization panel.
     */
    public double getNeutPanelBreadth() {
        return neutPanelBreadth;
    }
}
