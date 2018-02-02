
package amat.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.app.JamProperties;
import jam.math.StatSummary;

import amat.driver.AmatDriver;
import amat.germinal.GerminalCenter;

/**
 * Generates statistical summaries of the antibody and plasma-cell
 * production rates for each affinity maturation trial.
 *
 * <p>The <em>antibody production rate</em> is defined as the number
 * of unique antibodies produced during affinity maturation divided by
 * the number of cells present at the start of the germinal center
 * reaction (after proliferation of the germline cells).
 *
 * <p>The <em>plasma-cell production rate</em> is defined as the total
 * number of plasma cells produced during affinity maturation divided
 * by the number of cells present at the start of the germinal center
 * reaction (after proliferation of the germline cells).
 *
 * <p>The <em>diversity</em> is the ratio of the number of unique
 * antibodies to the total number of plasma cells produced.
 */
public final class ProdRateReport extends AmatReport {
    private List<Double> abProdRates = new ArrayList<Double>();
    private List<Double> pcProdRates = new ArrayList<Double>();
    private List<Double> pcDiversity = new ArrayList<Double>();

    private StatSummary abSummary  = null;
    private StatSummary pcSummary  = null;
    private StatSummary divSummary = null;

    private static ProdRateReport instance = null;

    private ProdRateReport() {}

    /**
     * The system property with this name must be {@code true} to
     * schedule the report for execution.
     */
    public static final String RUN_PROPERTY = "amat.ProdRateReport.run";

    /**
     * Base name of the antibody report file.
     */
    public static final String ANTIBODY_REPORT_NAME = "prod-rate-ab.txt";

    /**
     * Base name of the plasma-cell report file.
     */
    public static final String PLASMA_CELL_REPORT_NAME = "prod-rate-pc.txt";

    /**
     * Base name of the diversity report file.
     */
    public static final String DIVERSITY_REPORT_NAME = "pc-diversity.txt";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static ProdRateReport instance() {
        if (instance == null)
            instance = new ProdRateReport();

        return instance;
    }

    /**
     * Runs the production rate report if the system property
     * {@link ProdRateReport#RUN_PROPERTY} is {@code true}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, false);
    }

    private void report() {
        display();
        writeFile(ANTIBODY_REPORT_NAME, "", getAbSummary());
        writeFile(PLASMA_CELL_REPORT_NAME, "", getPcSummary());
        writeFile(DIVERSITY_REPORT_NAME, "", getDivSummary());
    }

    private void display() {
        System.out.println();
        displayBreak();
        System.out.println(String.format("Mean antibody production rate:    %10.6f", getAbSummary().getMean()));
        System.out.println(String.format("Mean plasma-cell production rate: %10.6f", getPcSummary().getMean()));
        System.out.println(String.format("Mean plasma-cell diversity:       %10.6f", getDivSummary().getMean()));
        displayBreak();
    }

    /**
     * Returns a statistical summary of the antibody production rates
     * observed during the simulation.
     *
     * @return a statistical summary of the antibody production rates
     * observed during the simulation.
     */
    public StatSummary getAbSummary() {
        if (abSummary == null)
            abSummary = StatSummary.compute(getAbProdRates());

        return abSummary;
    }

    /**
     * Returns a statistical summary of the plasma-cell production
     * rates observed during the simulation.
     *
     * @return a statistical summary of the plasma-cell production
     * rates observed during the simulation.
     */
    public StatSummary getPcSummary() {
        if (pcSummary == null)
            pcSummary = StatSummary.compute(getPcProdRates());

        return pcSummary;
    }

    /**
     * Returns a statistical summary of the plasma-cell diversity.
     *
     * @return a statistical summary of the plasma-cell diversity.
     */
    public StatSummary getDivSummary() {
        if (divSummary == null)
            divSummary = StatSummary.compute(getPcDiversity());

        return divSummary;
    }

    /**
     * Returns the antibody production rates observed during the
     * simulation.
     *
     * @return the antibody production rates observed during the
     * simulation (in an unmodifiable list).
     */
    public List<Double> getAbProdRates() {
        if (abProdRates.isEmpty())
            generateAbProdRates();

        return Collections.unmodifiableList(abProdRates);
    }

    private void generateAbProdRates() {
	for (GerminalCenter gc : driver.viewGerminalCenters())
	    abProdRates.add(gc.computeAntibodyProdRate());
    }

    /**
     * Returns the plasma-cell production rates observed during the
     * simulation.
     *
     * @return the plasma-cell production rates observed during the
     * simulation (in an unmodifiable list).
     */
    public List<Double> getPcProdRates() {
        if (pcProdRates.isEmpty())
            generatePcProdRates();

        return Collections.unmodifiableList(pcProdRates);
    }

    private void generatePcProdRates() {
	for (GerminalCenter gc : driver.viewGerminalCenters())
	    pcProdRates.add(gc.computePlasmaCellProdRate());
    }

    /**
     * Returns the plasma-cell diversity for each trial.
     *
     * @return the plasma-cell diversity for each trial (in an
     * unmodifiable list).
     */
    public List<Double> getPcDiversity() {
        if (pcDiversity.isEmpty())
            generatePcDiversity();

        return Collections.unmodifiableList(pcDiversity);
    }

    private void generatePcDiversity() {
	for (GerminalCenter gc : driver.viewGerminalCenters())
	    pcDiversity.add(gc.computePlasmaCellDiversity());
    }
}
