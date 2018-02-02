
package amat.report;

import java.io.File;
import java.io.PrintWriter;

import jam.io.IOUtil;

import amat.driver.AmatDriver;

/**
 * Provides a framework for reporting on affinity maturation.
 */
public abstract class AmatReport {
    /**
     * The single driver instance.
     */
    protected final AmatDriver driver = AmatDriver.instance();

    /**
     * Runs all known reports.
     */
    public static void runAll() {
        ClonalDiversityReport.run();
        CycleSummaryReport.run();
        FounderDetailReport.run();
        GCFateReport.run();
        GermDistReport.run();
	HeadlineReport.run();
        MatchingReport.run();
        MatchingHistoryReport.run();
        MutFreqReport.run();
        PlasmaAffinityReport.run();
        PlasmaDetailReport.run();
        ProdRateReport.run();
        PropertyReport.run();
        SurvivalRateReport.run();
        VisitationReport.run();
    }

    /**
     * Writes a visual separator (a display break) to standard output.
     */
    protected void displayBreak() {
        System.out.println("------------------------------------------------------------------------");
    }

    /**
     * Opens a report writer in the appropriate report directory.
     *
     * @param baseName the base name of the file to write.
     *
     * @return a report writer in the appropriate report directory.
     *
     * @throws RuntimeException unless the file is open for writing.
     */
    protected PrintWriter openWriter(String baseName) {
        File reportDir  = driver.getReportDir();
        File reportFile = new File(reportDir, baseName);

        return IOUtil.openWriter(reportFile, false);
    }

    /**
     * Resolves the full report file path for a given base name (using
     * the report directory from the driver application).
     *
     * @param baseName the base name of the report file.
     *
     * @return the full report file path.
     */
    protected File resolveReportFile(String baseName) {
        return new File(driver.getReportDir(), baseName);
    }

    /**
     * Writes objects to a report file.
     *
     * @param baseName the base name of the report file.
     *
     * @param objects the objects to write.
     */
    protected void writeFile(String baseName, Object... objects) {
        IOUtil.writeFile(resolveReportFile(baseName), false, objects);
    }
}
