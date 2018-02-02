
package amat.report;

/**
 * Summarizes the affinity and breadth of plasma cells drawn from the
 * same GC cycle.
 */
public final class PlasmaAffinityCycle {
    private final int gcCycle;
    private final AffinitySummary summary;

    /**
     * Creates a new affinity summary record for a given GC cycle.
     *
     * @param gcCycle the cycle at which the plasma cells exited the
     * germinal center.
     *
     * @param summary the summary record for the plasma cells produced
     * in the cycle.
     */
    public PlasmaAffinityCycle(int gcCycle, AffinitySummary summary) {
        this.gcCycle = gcCycle;
        this.summary = summary;
    }

    /**
     * Generates a string suitable for the header line in a report.
     *
     * @return the header string.
     */
    public static String header() {
        return "gcCycle"
            + ",footprintSizeMean"
            + ",footprintSizeSD"
            + ",footprintSizeErr"
            + ",footprintAffinityMean"
            + ",footprintAffinitySD"
            + ",footprintAffinityErr"
            + ",footprintBreadthMean"
            + ",footprintBreadthSD"
            + ",footprintBreadthErr"
            + ",neutPanelAffinityMean"
            + ",neutPanelAffinitySD"
            + ",neutPanelAffinityErr"
            + ",neutPanelBreadthMean"
            + ",neutPanelBreadthSD"
            + ",neutPanelBreadthErr";
    }

    /**
     * Formats this record to be written to a report file.
     *
     * @return the formatted string.
     */
    public String format() {
        return String.format("%d"
                             + ",%.4f,%.4f,%.6f"
                             + ",%.4f,%.4f,%.6f"
                             + ",%.6f,%.6f,%.8f"
                             + ",%.4f,%.4f,%.6f"
                             + ",%.6f,%.6f,%.8f",
                             gcCycle,
                             summary.getFootprintSizeSummary().getMean(),
                             summary.getFootprintSizeSummary().getSD(),
                             summary.getFootprintSizeSummary().getError(),
                             summary.getFootprintAffinitySummary().getMean(),
                             summary.getFootprintAffinitySummary().getSD(),
                             summary.getFootprintAffinitySummary().getError(),
                             summary.getFootprintBreadthSummary().getMean(),
                             summary.getFootprintBreadthSummary().getSD(),
                             summary.getFootprintBreadthSummary().getError(),
                             summary.getNeutPanelAffinitySummary().getMean(),
                             summary.getNeutPanelAffinitySummary().getSD(),
                             summary.getNeutPanelAffinitySummary().getError(),
                             summary.getNeutPanelBreadthSummary().getMean(),
                             summary.getNeutPanelBreadthSummary().getSD(),
                             summary.getNeutPanelBreadthSummary().getError());
                             
    }

    /**
     * Returns the GC cycle at which the plasma cells exited the
     * germinal center.
     *
     * @return the GC cycle at which the plasma cells exited the
     * germinal center.
     */
    public int getGcCycle() {
        return gcCycle;
    }

    /**
     * Returns the summary record for the plasma cells produced in the
     * cycle.
     *
     * @return the summary record for the plasma cells produced in the
     * cycle.
     */
    public AffinitySummary getSummary() {
        return summary;
    }
}
