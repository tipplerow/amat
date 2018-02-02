
package amat.report;

import java.io.PrintWriter;

import jam.app.JamProperties;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;

/**
 * Writes detail records for each founder cell produced in the
 * simulation.
 */
public final class FounderDetailReport extends AmatReport {
    private static FounderDetailReport instance = null;

    private FounderDetailReport() {
    }

    /**
     * The system property with this name must be {@code false} to
     * omit the report.  (The report runs by default.)
     */
    public static final String RUN_PROPERTY = "amat.FounderDetailReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "founder-detail.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static FounderDetailReport instance() {
        if (instance == null)
            instance = new FounderDetailReport();

        return instance;
    }

    /**
     * Runs the production rate report unless the system property
     * {@link FounderDetailReport#RUN_PROPERTY} is {@code false}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, true);
    }

    private void report() {
        PrintWriter writer = openWriter(REPORT_NAME);
        writer.println(FounderDetailRecord.header());

        for (GerminalCenter gc : driver.viewGerminalCenters()) {
            for (BCell cell : gc.viewFounderCells()) {
                FounderDetailRecord record = new FounderDetailRecord(gc.getTrialIndex(), cell);
                writer.println(record.format());
            }
        }

        writer.close();
    }
}
