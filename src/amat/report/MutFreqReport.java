
package amat.report;

import java.io.PrintWriter;

import jam.app.JamProperties;
import jam.io.IOUtil;

import amat.driver.AmatDriver;
import amat.receptor.MutationType;
import amat.receptor.Mutator;
import amat.receptor.MutatorProperties;

/**
 * Reports the expected and actual frequencies for all mutation types.
 */
public final class MutFreqReport extends AmatReport {
    private final Mutator mutator = Mutator.global();

    private MutationType[] types;
    private int receptorLen;
    private double[] actual;
    private double[] expected;

    private MutFreqReport() {}

    private static MutFreqReport instance = null;

    /**
     * The system property with this name must be {@code true} to
     * schedule the report for execution.
     */
    public static final String RUN_PROPERTY = "amat.MutFreqReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "mut-freq.txt";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static MutFreqReport instance() {
        if (instance == null)
            instance = new MutFreqReport();

        return instance;
    }

    /**
     * Runs the mutation frequency report if the system property
     * {@link MutFreqReport#RUN_PROPERTY} is {@code true}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, false);
    }

    private void report() {
        assemble();
        reportConsole();
        reportFile();
    }

    private void assemble() {
        types = MutationType.values();

        actual   = new double[types.length];
        expected = new double[types.length];

        for (int index = 0; index < types.length; index++) {
            actual[index]   = mutator.getFrequency(types[index]);
            expected[index] = MutatorProperties.getReceptorProbability(types[index]).doubleValue();
        }
    }

    private void reportConsole() {
        report(new PrintWriter(System.out), false);
    }

    private void reportFile() {
        report(openWriter(REPORT_NAME), true);
    }

    private void report(PrintWriter writer, boolean close) {
        String format = "%-8s  %8.4f    %6.4f\n";

        writer.println();
        writer.println("          EXPECTED    ACTUAL");
        writer.println("          --------    ------");

        for (int index = 0; index < types.length; index++)
            writer.printf(format, types[index] + ":", expected[index], actual[index]);

        writer.flush();

        if (close)
            IOUtil.close(writer);
    }
}
