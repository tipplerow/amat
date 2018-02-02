
package amat.report;

import java.io.PrintWriter;

import jam.app.JamProperties;
import jam.io.IOUtil;
import jam.math.DoubleUtil;

import amat.driver.AmatDriver;
import amat.germinal.GerminalCenter;
import amat.germinal.GerminalCenterState;

/**
 * Reports the occurrence of the final germinal center state.
 */
public final class GCFateReport extends AmatReport {
    private final GerminalCenterState[] stateValues = GerminalCenterState.values();
    private double[] stateFraction;

    private GCFateReport() {}

    private static GCFateReport instance = null;

    /**
     * The system property with this name must be {@code false} to
     * suppress execution.
     */
    public static final String RUN_PROPERTY = "amat.GCFateReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "gc-fate.txt";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static GCFateReport instance() {
        if (instance == null)
            instance = new GCFateReport();

        return instance;
    }

    /**
     * Runs the germinal center fate report unless the system property
     * {@link GCFateReport#RUN_PROPERTY} is {@code false}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, true);
    }

    private void report() {
        reportConsole();
        reportFile();
    }

    private void reportConsole() {
        System.out.println();
        displayBreak();
        report(new PrintWriter(System.out), false);
        displayBreak();
    }

    private void reportFile() {
        report(openWriter(REPORT_NAME), true);
    }

    private void report(PrintWriter writer, boolean close) {
        for (GerminalCenterState state : stateValues)
            writer.println(String.format("%-20s %6.4f", state.name() + ":", getStateFraction(state)));

        writer.flush();

        if (close)
            IOUtil.close(writer);
    }

    /**
     * Returns the fraction of germinal centers that terminate in a
     * given state.
     *
     * @param state the germinal center state to query.
     *
     * @return the fraction of germinal centers that terminated in the
     * specified state.
     */
    public double getStateFraction(GerminalCenterState state) {
        if (stateFraction == null)
            computeStateFraction();

        return stateFraction[state.ordinal()];
    }

    private void computeStateFraction() {
        int[] stateCount = new int[stateValues.length];

        for (GerminalCenter gc : driver.viewGerminalCenters())
            ++stateCount[gc.getFinalState().ordinal()];

        stateFraction = new double[stateValues.length];

        for (int index = 0; index < stateFraction.length; index++)
            stateFraction[index] = DoubleUtil.ratio(stateCount[index], driver.countGerminalCenters());
    }
}
