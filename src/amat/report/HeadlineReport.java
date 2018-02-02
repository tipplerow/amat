
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jam.app.JamProperties;
import jam.math.StatSummary;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;

/**
 * Reports the most important "headline" statistics for an affinity
 * maturation simulation.
 */
public final class HeadlineReport extends AmatReport {
    private static HeadlineReport instance = null;

    private HeadlineReport() {}

    /**
     * The system property with this name must be {@code false} to
     * stop the headline report from running. (It runs by default.)
     */
    public static final String RUN_PROPERTY = "amat.HeadlineReport.run";

    /**
     * Base name of the file containing the headline report.
     */
    public static final String REPORT_NAME = "headline.csv";

    /**
     * The system property with this name is prepended to the header
     * line to allow additional simulation parameters (e.g., number
     * and concentration of antigens) to appear in the header report.
     */
    public static final String CASE_HEADER_PROPERTY = "amat.HeadlineReport.caseHeader";

    /**
     * The system property with this name is prepended to the data
     * line to allow additional simulation parameters (e.g., number
     * and concentration of antigens) to appear in the header report.
     */
    public static final String CASE_DATA_PROPERTY = "amat.HeadlineReport.caseData";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static HeadlineReport instance() {
        if (instance == null)
            instance = new HeadlineReport();

        return instance;
    }

    /**
     * Runs the headline report (unless the configuration file
     * specifies otherwise).
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

	String caseHeader = JamProperties.getOptional(CASE_HEADER_PROPERTY, "");
	String caseData   = JamProperties.getOptional(CASE_DATA_PROPERTY, "");

	if (!caseHeader.endsWith(","))
	    caseHeader = caseHeader + ",";

	if (!caseData.endsWith(","))
	    caseData = caseData + ",";

        writer.println(caseHeader + HeadlineRecord.header());
	writer.println(caseData   + HeadlineRecord.instance().format());

        writer.close();
    }
}
