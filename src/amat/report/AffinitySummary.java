
package amat.report;

import java.util.Collection;

import jam.math.StatSummary;

/**
 * Summarizes the affinity and breadth of a B cell for the epitopes
 * that it encountered during affinity maturation (the "footprint")
 * and for the epitopes in the full global neutralization panel.
 */
public final class AffinitySummary {
    private final StatSummary footprintSizeSummary;

    private final StatSummary footprintAffinitySummary;
    private final StatSummary neutPanelAffinitySummary;

    private final StatSummary footprintBreadthSummary;
    private final StatSummary neutPanelBreadthSummary;

    private AffinitySummary(StatSummary footprintSizeSummary,
                            StatSummary footprintAffinitySummary,
                            StatSummary neutPanelAffinitySummary,
                            StatSummary footprintBreadthSummary,
                            StatSummary neutPanelBreadthSummary) {
        this.footprintSizeSummary = footprintSizeSummary;

        this.footprintAffinitySummary = footprintAffinitySummary;
        this.neutPanelAffinitySummary = neutPanelAffinitySummary;

        this.footprintBreadthSummary = footprintBreadthSummary;
        this.neutPanelBreadthSummary = neutPanelBreadthSummary;
    }

    /**
     * Computes the affinity summary for a collection of affinity
     * records.
     *
     * @param records the affinity records to summarize.
     *
     * @return the affinity summary for the specified records.
     */
    public static AffinitySummary compute(Collection<AffinityRecord> records) {
        StatSummary footprintSizeSummary = StatSummary.compute(records, record -> (double) record.getFootprintSize());

        StatSummary footprintAffinitySummary = StatSummary.compute(records, record -> record.getFootprintAffinity());
        StatSummary neutPanelAffinitySummary = StatSummary.compute(records, record -> record.getNeutPanelAffinity());

        StatSummary footprintBreadthSummary = StatSummary.compute(records, record -> record.getFootprintBreadth());
        StatSummary neutPanelBreadthSummary = StatSummary.compute(records, record -> record.getNeutPanelBreadth());

        return new AffinitySummary(footprintSizeSummary,
                                   footprintAffinitySummary, neutPanelAffinitySummary,
                                   footprintBreadthSummary,  neutPanelBreadthSummary);
    }

    /**
     * Summarizes the number of epitopes encountered by the B cell
     * population during maturation.
     *
     * @return a summary of the number of epitopes encountered by the
     * B cell population during maturation.
     */
    public StatSummary getFootprintSizeSummary() {
        return footprintSizeSummary;
    }

    /**
     * Summarizes the affinity of the B cell population for the
     * epitopes that they encountered during maturation (the 
     * vaccine "footprint").
     *
     * @return a summary of the affinity for epitopes encountered
     * during maturation.
     */
    public StatSummary getFootprintAffinitySummary() {
        return footprintAffinitySummary;
    }

    /**
     * Summarizes the breadth of the B cell population with respect to
     * the epitopes that they encountered during maturation (the vaccine
     * "footprint").
     *
     * @return a summary of the breadth with respect to the epitopes
     * encountered during maturation.
     */
    public StatSummary getFootprintBreadthSummary() {
        return footprintBreadthSummary;
    }

    /**
     * Summarizes the affinity of the B cell population for the
     * epitopes in the full global neutralization panel.
     *
     * @return a summary of the affinity for the epitopes in the full
     * global neutralization panel.
     */
    public StatSummary getNeutPanelAffinitySummary() {
        return neutPanelAffinitySummary;
    }

    /**
     * Summarizes the breadth of the B cell population with respect to
     * the epitopes in the full global neutralization panel.
     *
     * @return the breadth of the B cell with respect to the epitopes
     * in the full global neutralization panel.
     */
    public StatSummary getNeutPanelBreadthSummary() {
        return neutPanelBreadthSummary;
    }
}
