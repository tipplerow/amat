
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;

import jam.app.JamProperties;
import jam.app.JamLogger;
import jam.io.IOUtil;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;
import amat.structure.CV;

/**
 * Reports survival rates of the B cell population for at each cycle
 * of affinity maturation for each germinal center simulated.
 */
public final class MatchingHistoryReport extends AmatReport {
    private final Collection<MatchingHistoryDetail> details = new ArrayList<MatchingHistoryDetail>();
    private final Collection<MatchingHistorySummary> summaries;

    private static MatchingHistoryReport instance = null;

    private MatchingHistoryReport() {
        generateDetails();
        this.summaries = MatchingHistorySummary.compute(details);
    }

    private void generateDetails() {
        JamLogger.info("Generating matching history detail records...");

        for (GerminalCenter gc : driver.viewGerminalCenters())
            for (BCell plasmaCell : gc.viewPlasmaCells())
                details.add(new MatchingHistoryDetail(gc.getTrialIndex(), plasmaCell));
    }

    /**
     * The system property with this name must be {@code true} to
     * schedule the detail report for execution.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.MatchingHistoryReport.detail";

    /**
     * The system property with this name must be {@code true} to
     * schedule the summary report for execution.
     */
    public static final String RUN_SUMMARY_PROPERTY = "amat.MatchingHistoryReport.summary";

    /**
     * Base name of the detail report file.
     */
    public static final String DETAIL_REPORT_NAME = "matching-history-detail.csv";

    /**
     * Base name of the summary report file.
     */
    public static final String SUMMARY_REPORT_NAME = "matching-history-summary.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static MatchingHistoryReport instance() {
        if (instance == null)
            instance = new MatchingHistoryReport();

        return instance;
    }

    /**
     * Runs the matching history report if requested in the driver
     * configuration.
     */
    public static void run() {
        if (detailRequested())
            instance().reportDetail();

        if (summaryRequested())
            instance().reportSummary();
    }

    private static boolean detailRequested() {
        return JamProperties.getOptionalBoolean(RUN_DETAIL_PROPERTY, false);
    }

    private static boolean summaryRequested() {
        return JamProperties.getOptionalBoolean(RUN_SUMMARY_PROPERTY, true);
    }

    private void reportDetail() {
        PrintWriter writer = openWriter(DETAIL_REPORT_NAME);
        writer.println(MatchingHistoryDetail.header());

        for (MatchingHistoryDetail detail : details) {
            writer.println(detail.format(CV.CONSERVED));
            writer.println(detail.format(CV.VARIABLE));
        }

        IOUtil.close(writer);
    }

    private void reportSummary() {
        PrintWriter writer = openWriter(SUMMARY_REPORT_NAME);
        writer.println(MatchingHistorySummary.header());

        for (MatchingHistorySummary summary : summaries)
            writer.println(summary.format());

        IOUtil.close(writer);
    }
}
