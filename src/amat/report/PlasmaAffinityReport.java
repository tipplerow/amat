
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.math.StatSummary;
import jam.util.ListMap;
import jam.util.ListView;

import amat.bcell.BCell;
import amat.driver.AmatDriver;
import amat.germinal.GerminalCenter;

/**
 * Tabulates the affinity of BCRs to the epiopes present during
 * affinity maturation among the population of plasma cells.
 */
public final class PlasmaAffinityReport extends AmatReport {
    private final AffinitySummary plasmaSummary;

    private final ListView<PlasmaAffinityCycle> cycleView;
    private final ListView<PlasmaAffinityDetail> detailView;

    private static PlasmaAffinityReport instance = null;

    private PlasmaAffinityReport(AffinitySummary plasmaSummary,
                                 ListView<PlasmaAffinityCycle> cycleView,
                                 ListView<PlasmaAffinityDetail> detailView) {
        this.plasmaSummary = plasmaSummary;
        this.cycleView  = cycleView;
        this.detailView = detailView;
    }

    /**
     * The system property with this name must be {@code true} to
     * run the affinity detail report.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.PlasmaAffinityReport.runDetail";

    /**
     * Base name of the detail report file.
     */
    public static final String DETAIL_REPORT_NAME = "plasma-affinity-detail.csv";

    /**
     * The system property with this name must be {@code false} to
     * omit the affinity-by-cycle report (it runs by default).
     */
    public static final String RUN_BY_CYCLE_PROPERTY = "amat.PlasmaAffinityReport.runByCycle";

    /**
     * Base name of the by-cycle report file.
     */
    public static final String BY_CYCLE_REPORT_NAME = "plasma-affinity-by-cycle.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static PlasmaAffinityReport instance() {
        if (instance == null)
            instance = makeInstance();

        return instance;
    }

    private static PlasmaAffinityReport makeInstance() {
        JamLogger.info("Generating plasma affinity report...");

        List<AffinityRecord> records = new ArrayList<AffinityRecord>();
        ListMap<Integer, AffinityRecord> byCycle = ListMap.tree();
        List<PlasmaAffinityDetail> details = new ArrayList<PlasmaAffinityDetail>();

        for (GerminalCenter gc : AmatDriver.instance().viewGerminalCenters()) {
            for (BCell bcell : gc.viewPlasmaCells()) {
                //
                // The "footprint" panel contains only those epitopes
                // that were present for the selection and competition
                // in the germinal center at the cycle when the B cell
                // exited...
                //
                int cycle = bcell.getGcCycle();
                AffinityRecord affinity = AffinityRecord.compute(bcell);

                records.add(affinity);
                byCycle.list(cycle).add(affinity);
                details.add(new PlasmaAffinityDetail(bcell, affinity));
            }
        }

        AffinitySummary plasmaSummary = AffinitySummary.compute(records);

        ListView<PlasmaAffinityCycle> cycleView = aggregateCycles(byCycle);
        ListView<PlasmaAffinityDetail> detailView = ListView.create(details);

        return new PlasmaAffinityReport(plasmaSummary, cycleView, detailView);
    }

    private static ListView<PlasmaAffinityCycle> aggregateCycles(ListMap<Integer, AffinityRecord> map) {
        // Ensure that the cycles are in ascending order...
        List<Integer> cycles = new ArrayList<Integer>(map.keySet());
        Collections.sort(cycles);

        List<PlasmaAffinityCycle> aggregates = new ArrayList<PlasmaAffinityCycle>();

        for (Integer cycle : cycles) {
            List<AffinityRecord> cycleRecords = map.get(cycle);

            if (cycleRecords.size() > 1)
                aggregates.add(new PlasmaAffinityCycle(cycle, AffinitySummary.compute(cycleRecords)));
        }
        
        return ListView.create(aggregates);
    }

    /**
     * Runs the report if it is requested in the driver configuration.
     */
    public static void run() {
        if (detailRequested())
            instance().reportDetail();

        if (byCycleRequested())
            instance().reportByCycle();
    }

    private static boolean detailRequested() {
        return JamProperties.getOptionalBoolean(RUN_DETAIL_PROPERTY, false);
    }

    private void reportDetail() {
        PrintWriter writer = openWriter(DETAIL_REPORT_NAME);
        writer.println(PlasmaAffinityDetail.header());

        for (PlasmaAffinityDetail record : viewDetailRecords())
            writer.println(record.format());

        writer.close();
    }

    private static boolean byCycleRequested() {
        return JamProperties.getOptionalBoolean(RUN_BY_CYCLE_PROPERTY, true);
    }

    private void reportByCycle() {
        PrintWriter writer = openWriter(BY_CYCLE_REPORT_NAME);
        writer.println(PlasmaAffinityCycle.header());

        for (PlasmaAffinityCycle record : viewCycleRecords())
            writer.println(record.format());

        writer.close();
    }

    /**
     * Returns the summary statistics for the population of plasma
     * cells.
     *
     * @return the summary statistics for the population of plasma
     * cells.
     */
    public AffinitySummary getPlasmaSummary() {
        return plasmaSummary;
    }

    /**
     * Returns a read-only view of the by-cycle records.
     *
     * @return a read-only view of the by-cycle records.
     */
    public ListView<PlasmaAffinityCycle> viewCycleRecords() {
        return cycleView;
    }

    /**
     * Returns a read-only view of the affinity detail records.
     *
     * @return a read-only view of the affinity detail records.
     */
    public ListView<PlasmaAffinityDetail> viewDetailRecords() {
        return detailView;
    }
}
