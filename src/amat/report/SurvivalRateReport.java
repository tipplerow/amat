
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;

import jam.app.JamProperties;
import jam.io.IOUtil;

import amat.germinal.GerminalCenter;

/**
 * Reports survival rates of the B cell population for at each cycle
 * of affinity maturation for each germinal center simulated.
 */
public final class SurvivalRateReport extends AmatReport {
    private SurvivalRateSummary headline = null;
    private final Collection<SurvivalRateDetail> details = new ArrayList<SurvivalRateDetail>();
    private final Collection<SurvivalRateSummary> summaries = new ArrayList<SurvivalRateSummary>();

    private SurvivalRateReport() {}

    private static SurvivalRateReport instance = null;

    /**
     * The system property with this name must be {@code true} to
     * schedule the detail report for execution.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.SurvivalRateReport.detail";

    /**
     * The system property with this name must be {@code true} to
     * schedule the summary report for execution.
     */
    public static final String RUN_SUMMARY_PROPERTY = "amat.SurvivalRateReport.summary";

    /**
     * Base name of the detail report file.
     */
    public static final String DETAIL_REPORT_NAME = "survival-rate-detail.csv";

    /**
     * Base name of the summary report file.
     */
    public static final String SUMMARY_REPORT_NAME = "survival-rate-summary.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static SurvivalRateReport instance() {
        if (instance == null)
            instance = new SurvivalRateReport();

        return instance;
    }

    /**
     * Generates the overall headline summary record.
     *
     * @return the overall headline summary record.
     */
    public SurvivalRateSummary headline() {
        if (headline == null)
            headline = SurvivalRateSummary.compute(-1, viewDetailRecords());

        return headline;
    }

    /**
     * Runs the survival rate report if requested in the driver
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
        writer.println(SurvivalRateDetail.header());

        for (SurvivalRateDetail detail : viewDetailRecords())
            writer.println(detail.format());

        IOUtil.close(writer);
    }

    private Collection<SurvivalRateDetail> viewDetailRecords() {
        if (details.isEmpty())
            generateDetailRecords();

        return Collections.unmodifiableCollection(details);
    }

    private void generateDetailRecords() {
        for (GerminalCenter gc : driver.viewGerminalCenters())
            for (int cycle = GerminalCenter.REPLICATION_CYCLE; cycle < gc.countCycles(); ++cycle)
                details.add(SurvivalRateDetail.compute(gc, cycle));
    }

    private void reportSummary() {
        PrintWriter writer = openWriter(SUMMARY_REPORT_NAME);
        writer.println(SurvivalRateSummary.header());

        for (SurvivalRateSummary summary : viewSummaryRecords())
            writer.println(summary.format());

        IOUtil.close(writer);
    }

    private Collection<SurvivalRateSummary> viewSummaryRecords() {
        if (summaries.isEmpty())
            generateSummaryRecords();

        return Collections.unmodifiableCollection(summaries);
    }

    private void generateSummaryRecords() {
        SortedMap<Integer, Collection<SurvivalRateDetail>> grouped = 
            SurvivalRateDetail.groupByGeneration(viewDetailRecords());

        for (Integer generation : grouped.keySet()) {
            Collection<SurvivalRateDetail> details = grouped.get(generation);

            if (details.size() > 1)
                summaries.add(SurvivalRateSummary.compute(generation, details));
        }
    }
}
