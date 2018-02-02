
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
 * Reports clonal diversity of the B cell population for at each cycle
 * of affinity maturation for each germinal center simulated.
 */
public final class ClonalDiversityReport extends AmatReport {
    private final Collection<ClonalDiversityDetail> details = new ArrayList<ClonalDiversityDetail>();
    private final Collection<ClonalDiversitySummary> summaries = new ArrayList<ClonalDiversitySummary>();

    private ClonalDiversityReport() {}

    private static ClonalDiversityReport instance = null;

    /**
     * The system property with this name must be {@code true} to
     * schedule the detail report for execution.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.ClonalDiversityReport.detail";

    /**
     * The system property with this name must be {@code true} to
     * schedule the summary report for execution.
     */
    public static final String RUN_SUMMARY_PROPERTY = "amat.ClonalDiversityReport.summary";

    /**
     * Base name of the detail report file.
     */
    public static final String DETAIL_REPORT_NAME = "clonal-diversity-detail.csv";

    /**
     * Base name of the summary report file.
     */
    public static final String SUMMARY_REPORT_NAME = "clonal-diversity-summary.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static ClonalDiversityReport instance() {
        if (instance == null)
            instance = new ClonalDiversityReport();

        return instance;
    }

    /**
     * Runs the clonal diversity report if requested in the driver
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
        writer.println(ClonalDiversityDetail.header());

        for (ClonalDiversityDetail detail : viewDetailRecords())
            writer.println(detail.format());

        IOUtil.close(writer);
    }

    private Collection<ClonalDiversityDetail> viewDetailRecords() {
        if (details.isEmpty())
            generateDetailRecords();

        return Collections.unmodifiableCollection(details);
    }

    private void generateDetailRecords() {
        for (GerminalCenter gc : driver.viewGerminalCenters())
            for (int cycle = GerminalCenter.REPLICATION_CYCLE; cycle < gc.countCycles(); ++cycle)
                if (gc.getPopulation(cycle).ending() > 1)
                    details.add(ClonalDiversityDetail.compute(gc, cycle));
    }

    private void reportSummary() {
        PrintWriter writer = openWriter(SUMMARY_REPORT_NAME);
        writer.println(ClonalDiversitySummary.header());

        for (ClonalDiversitySummary summary : viewSummaryRecords())
            writer.println(summary.format());

        IOUtil.close(writer);
    }

    private Collection<ClonalDiversitySummary> viewSummaryRecords() {
        if (summaries.isEmpty())
            generateSummaryRecords();

        return Collections.unmodifiableCollection(summaries);
    }

    private void generateSummaryRecords() {
        SortedMap<Integer, Collection<ClonalDiversityDetail>> grouped = 
            ClonalDiversityDetail.groupByGeneration(viewDetailRecords());

        for (Integer generation : grouped.keySet()) {
            Collection<ClonalDiversityDetail> details = grouped.get(generation);

            if (details.size() > 1)
                summaries.add(ClonalDiversitySummary.compute(generation, details));
        }
    }
}
