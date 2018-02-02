
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
 * Reports the generation at which plasma cells exited their germinal
 * center, the number of mutations they accumulated during affinity
 * maturation, and the mutational distance between their receptor and
 * the receptor of their germline (founder) cell.
 */
public final class GermDistReport extends AmatReport {
    // One detail record for each plasma cell...
    private final List<GermDistDetail> detailRecords = new ArrayList<GermDistDetail>();

    private StatSummary generationSummary = null;
    private StatSummary mutationCountSummary = null;
    private StatSummary mutationalDistSummary = null;

    private static GermDistReport instance = null;

    private GermDistReport() {}

    /**
     * The system property with this name must be {@code true} to
     * schedule the detail report for execution.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.GermDistReport.detail";

    /**
     * The system property with this name must be {@code true} to
     * schedule the summary report for execution.
     */
    public static final String RUN_SUMMARY_PROPERTY = "amat.GermDistReport.summary";

    /**
     * Base name of the file containing the mutational distance detail.
     */
    public static final String DETAIL_REPORT_NAME = "germ-dist-detail.csv";

    /**
     * Base name of the file containing the generation summary.
     */
    public static final String GENERATION_REPORT_NAME = "generation-summary.txt";

    /**
     * Base name of the file containing the mutation count summary.
     */
    public static final String MUTATION_COUNT_REPORT_NAME = "mutation-count-summary.txt";

    /**
     * Base name of the file containing the mutational distance summary.
     */
    public static final String MUTATIONAL_DIST_REPORT_NAME = "mutational-dist-summary.txt";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static GermDistReport instance() {
        if (instance == null)
            instance = new GermDistReport();

        return instance;
    }

    /**
     * Runs the mutational distance report (if it has been requested
     * in the simulation configuration file).
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

    private void reportDetail() {
        PrintWriter writer = openWriter(DETAIL_REPORT_NAME);
        writer.println(GermDistDetail.header());

        for (GermDistDetail detailRecord : getDetailRecords())
            writer.println(detailRecord.format());

        writer.close();
    }

    /**
     * Returns a list of germline distance detail records, one for
     * each plasma cell pair in the simulation.
     *
     * @return an unmodifiable list of matching detail records.
     */
    public List<GermDistDetail> getDetailRecords() {
        if (detailRecords.isEmpty())
            generateDetailRecords();

        return Collections.unmodifiableList(detailRecords);
    }

    private void generateDetailRecords() {
        for (GerminalCenter gc : driver.viewGerminalCenters())
            generateDetailRecords(gc);
    }

    private void generateDetailRecords(GerminalCenter gc) {
        for (BCell plasmaCell : gc.viewPlasmaCells())
            generateDetailRecord(plasmaCell);
    }

    private void generateDetailRecord(BCell plasmaCell) {
        detailRecords.add(GermDistDetail.compute(plasmaCell));
    }

    private static boolean summaryRequested() {
        return JamProperties.getOptionalBoolean(RUN_SUMMARY_PROPERTY, false);
    }

    private void reportSummary() {
        display();
        writeFile(GENERATION_REPORT_NAME,      "", getGenerationSummary());
        writeFile(MUTATION_COUNT_REPORT_NAME,  "", getMutationCountSummary());
        writeFile(MUTATIONAL_DIST_REPORT_NAME, "", getMutationalDistSummary());
    }

    private void display() {
        System.out.println();
        displayBreak();
        System.out.println(String.format("Mean generation:          %6.2f", getGenerationSummary().getMean()));
        System.out.println(String.format("Mean mutation count:      %6.2f", getMutationCountSummary().getMean()));
        System.out.println(String.format("Mean mutational distance: %6.2f", getMutationalDistSummary().getMean()));
        displayBreak();
    }

    /**
     * Returns a statistical summary for the generation at which
     * plasma cells exited their germinal center.
     * 
     * @return a statistical summary for the generation at which
     * plasma cells exited their germinal center.
     */
    public StatSummary getGenerationSummary() {
        if (generationSummary == null)
            generationSummary = StatSummary.compute(getGeneration());

        return generationSummary;
    }

    /**
     * Returns a statistical summary for the number of mutations
     * accumulated by plasma cells during affinity maturation.
     * 
     * @return a statistical summary for the number of mutations
     * accumulated by plasma cells during affinity maturation.
     */
    public StatSummary getMutationCountSummary() {
        if (mutationCountSummary == null)
            mutationCountSummary = StatSummary.compute(getMutationCount());

        return mutationCountSummary;
    }

    /**
     * Returns a statistical summary for the mutational distance
     * between the plasma cell receptors and the receptors of their
     * germline (founder) cells.
     * 
     * @return a statistical summary for the mutational distance
     * between the plasma cell receptors and the receptors of their
     * germline (founder) cells.
     */
    public StatSummary getMutationalDistSummary() {
        if (mutationalDistSummary == null)
            mutationalDistSummary = StatSummary.compute(getMutationalDist());

        return mutationalDistSummary;
    }

    private List<Double> getGeneration() {
        List<Double> result = new ArrayList<Double>();

        for (GermDistDetail detailRecord : getDetailRecords())
            result.add(Double.valueOf(detailRecord.getGeneration()));

        return result;
    }

    private List<Double> getMutationCount() {
        List<Double> result = new ArrayList<Double>();

        for (GermDistDetail detailRecord : getDetailRecords())
            result.add(Double.valueOf(detailRecord.getMutationCount()));

        return result;
    }

    private List<Double> getMutationalDist() {
        List<Double> result = new ArrayList<Double>();

        for (GermDistDetail detailRecord : getDetailRecords())
            result.add(detailRecord.getMutationalDist());

        return result;
    }
}
