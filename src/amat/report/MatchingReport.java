
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.StatSummary;

import amat.bcell.BCell;
import amat.epitope.Epitope;
import amat.germinal.GerminalCenter;
import amat.vaccine.VaccinationSchedule;

/**
 * Reports the fraction of plasma cell receptor elements that exactly
 * match the corresponding elements in the epitopes presented for
 * immunization, with conserved and variable regions considered
 * separately.
 */
public final class MatchingReport extends AmatReport {
    // For each plasma cell index, a map from epitope key to matching record...
    private final Map<Long, Map<String, MatchingRecord>> recordMap = 
        new TreeMap<Long, Map<String, MatchingRecord>>();

    // All matching records "unrolled" for easier processing...
    private final List<MatchingRecord> recordList = new ArrayList<MatchingRecord>();

    private final StatSummary totalSummary;
    private final StatSummary variableSummary;
    private final StatSummary conservedSummary;

    private final int aboveThresholdNumber;
    private final double aboveThresholdFraction;

    private static MatchingReport instance = null;

    private MatchingReport() {
        generateRecords();

        totalSummary     = StatSummary.compute(recordList, x -> x.getTotal());
        variableSummary  = StatSummary.compute(recordList, x -> x.getVariable());
        conservedSummary = StatSummary.compute(recordList, x -> x.getConserved());

	aboveThresholdNumber = 
            (int) recordList.stream().filter(x -> x.getConserved() > getConservedThreshold()).count();

        aboveThresholdFraction =
            DoubleUtil.ratio(aboveThresholdNumber, recordList.size());
    }

    private void generateRecords() {
        for (GerminalCenter gc : driver.viewGerminalCenters())
            generateRecords(gc);
    }

    private void generateRecords(GerminalCenter gc) {
        for (BCell plasmaCell : gc.viewPlasmaCells())
            generateRecords(plasmaCell);
    }

    private void generateRecords(BCell plasmaCell) {
        // Create a new empty map for this plasma cell...
        recordMap.put(plasmaCell.getIndex(), new HashMap<String, MatchingRecord>());

        // Restrict the matching test to epitopes that were present
        // for the selection and competition in the germinal center...
        int exitCycle = plasmaCell.getGeneration();
        Set<Epitope> epitopes = VaccinationSchedule.global().getEpitopeFootprint(exitCycle);

        for (Epitope epitope : epitopes)
            addRecord(plasmaCell, epitope);
    }

    private void addRecord(BCell plasmaCell, Epitope epitope) {
        MatchingRecord record = 
            MatchingRecord.compute(plasmaCell, epitope);

        recordList.add(record);
        recordMap.get(plasmaCell.getIndex()).put(epitope.getKey(), record);
    }

    /**
     * The system property with this name must be {@code true} to
     * schedule the detail report for execution.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.MatchingReport.detail";

    /**
     * The system property with this name must be {@code true} to
     * schedule the summary report for execution.
     */
    public static final String RUN_SUMMARY_PROPERTY = "amat.MatchingReport.summary";

    /**
     * The system property with this name must be assigned a fractional 
     * value to schedule the threshold matching report for execution.
     */
    public static final String THRESHOLD_PROPERTY = "amat.MatchingReport.threshold";

    /**
     * Base name of the file containing the founder detail report.
     */
    public static final String FOUNDER_DETAIL_NAME = "matching-founder-detail.csv";

    /**
     * Base name of the file containing the founder summary report.
     */
    public static final String FOUNDER_SUMMARY_NAME = "matching-founder-summary.csv";

    /**
     * Base name of the file containing the matching detail.
     */
    public static final String DETAIL_REPORT_NAME = "matching-detail.csv";

    /**
     * Base name of the file containing the conserved matching summary.
     */
    public static final String CONSERVED_REPORT_NAME = "matching-conserved.txt";

    /**
     * Base name of the file containing the variable matching summary.
     */
    public static final String VARIABLE_REPORT_NAME = "matching-variable.txt";

    /**
     * Base name of the file containing the total matching summary.
     */
    public static final String TOTAL_REPORT_NAME = "matching-total.txt";

    /**
     * Base name of the file containing the threshold matching summary.
     */
    public static final String THRESHOLD_REPORT_NAME = "matching-threshold.txt";

    /**
     * Default value for the threshold matching fraction (the fraction
     * of conserved element matches beyond which "breadth" would be
     * conferred).
     */
    public static final double DEFAULT_THRESHOLD = 0.9;

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static MatchingReport instance() {
        if (instance == null)
            instance = new MatchingReport();

        return instance;
    }

    /**
     * Runs the matching report (if it has been requested in the
     * simulation configuration file).
     */
    public static void run() {
        if (detailRequested())
            instance().reportDetail();

        if (summaryRequested())
            instance().reportSummary();

        if (thresholdRequested())
            instance().reportThreshold();
    }

    private static boolean detailRequested() {
        return JamProperties.getOptionalBoolean(RUN_DETAIL_PROPERTY, false);
    }

    private void reportDetail() {
        PrintWriter writer = openWriter(DETAIL_REPORT_NAME);
        writer.println("plasmaIndex,epitopeKey,conservedFit,variableFit,totalFit");

        for (Map.Entry<Long, Map<String, MatchingRecord>> plasmaEntry : recordMap.entrySet())
            for (Map.Entry<String, MatchingRecord> epitopeEntry : plasmaEntry.getValue().entrySet())
                writer.println(String.format("%d,%s,%6.4f,%6.4f,%6.4f",
                                             plasmaEntry.getKey().longValue(),
                                             epitopeEntry.getKey(),
                                             epitopeEntry.getValue().getConserved(),
                                             epitopeEntry.getValue().getVariable(),
                                             epitopeEntry.getValue().getTotal()));

        writer.close();
    }

    private static boolean summaryRequested() {
        return JamProperties.getOptionalBoolean(RUN_SUMMARY_PROPERTY, false);
    }

    private void reportSummary() {
        display();
        writeFile(CONSERVED_REPORT_NAME, "", conservedSummary);
        writeFile(VARIABLE_REPORT_NAME,  "", variableSummary);
        writeFile(TOTAL_REPORT_NAME,     "", totalSummary);
    }

    private static boolean thresholdRequested() {
        return JamProperties.isSet(THRESHOLD_PROPERTY);
    }

    private void reportThreshold() {
        PrintWriter writer = openWriter(THRESHOLD_REPORT_NAME);

        writer.println(String.format("Conserved matching threshold: %8.4f",  getConservedThreshold()));
	writer.println(String.format("Number above threshold:         %d",   aboveThresholdNumber));
	writer.println(String.format("Fraction above threshold:     %10.6f", aboveThresholdFraction));
        writer.close();
    }

    private void display() {
        System.out.println();
        displayBreak();
        System.out.println(String.format("Fraction matching CONSERVED: %8.4f", conservedSummary.getMean()));
        System.out.println(String.format("Fraction matching VARIABLE:  %8.4f", variableSummary.getMean()));
        System.out.println(String.format("Fraction matching TOTAL:     %8.4f", totalSummary.getMean()));
        System.out.println(String.format("Fraction above threshold:    %8.4f", aboveThresholdFraction));
        displayBreak();
    }

    public StatSummary getConservedSummary() {
        return conservedSummary;
    }

    public StatSummary getVariableSummary() {
        return variableSummary;
    }

    public StatSummary getTotalSummary() {
        return totalSummary;
    }

    public int getAboveThresholdNumber() {
        return aboveThresholdNumber;
    }

    public double getAboveThresholdFraction() {
        return aboveThresholdFraction;
    }

    public static double getConservedThreshold() {
	return JamProperties.getOptionalDouble(THRESHOLD_PROPERTY, DoubleRange.FRACTIONAL, DEFAULT_THRESHOLD);
    }
}
