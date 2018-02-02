
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.IOUtil;

/**
 * Reports clonal diversity of the B cell population for at each cycle
 * of affinity maturation for each germinal center simulated.
 */
public final class CycleSummaryReport extends AmatReport {
    private final Collection<CycleSummaryRecord> records = new ArrayList<CycleSummaryRecord>();

    private CycleSummaryReport() {}

    private static CycleSummaryReport instance = null;

    /**
     * The system property with this name must be {@code false} to
     * omit the report: it runs by default.
     */
    public static final String RUN_PROPERTY = "amat.CycleSummaryReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "cycle-summary.csv";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static CycleSummaryReport instance() {
        if (instance == null)
            instance = new CycleSummaryReport();

        return instance;
    }

    /**
     * Runs the clonal diversity report if requested in the driver
     * configuration.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, true);
    }

    private void report() {
        JamLogger.info("Preparing the cycle summary report...");

        PrintWriter writer = openWriter(REPORT_NAME);
        writer.println(CycleSummaryRecord.header());

        for (CycleSummaryRecord record : viewRecords())
            writer.println(record.format());

        IOUtil.close(writer);
    }

    private Collection<CycleSummaryRecord> viewRecords() {
        if (records.isEmpty())
            generateRecords();

        return Collections.unmodifiableCollection(records);
    }

    private void generateRecords() {
        while (true) {
            int cycle = records.size();
            CycleSummaryRecord record = CycleSummaryRecord.compute(cycle);

            if (record != null)
                records.add(record);
            else
                return;
        }
    }
}
