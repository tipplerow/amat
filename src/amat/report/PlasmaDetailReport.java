
package amat.report;

import java.io.PrintWriter;

import jam.app.JamProperties;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;

/**
 * Writes detail records for each plasma cell produced in the
 * simulation.
 */
public final class PlasmaDetailReport extends AmatReport {
    private static PlasmaDetailReport instance = null;

    private PlasmaDetailReport() {
    }

    /**
     * The system property with this name must be {@code false} to
     * omit the report.  (The report runs by default.)
     */
    public static final String RUN_PROPERTY = "amat.PlasmaDetailReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "plasma-detail.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static PlasmaDetailReport instance() {
        if (instance == null)
            instance = new PlasmaDetailReport();

        return instance;
    }

    /**
     * Runs the production rate report if the system property 
     * {@link PlasmaDetailReport#RUN_PROPERTY} is {@code true}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, false);
    }

    private void report() {
        PrintWriter writer = openWriter(REPORT_NAME);
        writer.println(PlasmaDetailRecord.header());

        for (GerminalCenter gc : driver.viewGerminalCenters()) {
            for (BCell cell : gc.viewPlasmaCells()) {
                PlasmaDetailRecord record = new PlasmaDetailRecord(gc.getTrialIndex(), cell);
                writer.println(record.format());
            }
        }

        writer.close();
    }
}
