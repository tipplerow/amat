
package amat.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.dist.EmpiricalDiscreteDistribution;
import jam.math.StatSummary;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;

/**
 * Computes the fractional distribution of the number of antigens
 * presented during FDC visitation trials.
 */
public final class VisitationReport extends AmatReport {
    private final List<VisitationRecord> records = new ArrayList<VisitationRecord>();

    private StatSummary affinitySummary = null;
    private StatSummary quantitySummary = null;

    private EmpiricalDiscreteDistribution totalVisitDist = null;
    private EmpiricalDiscreteDistribution uniqueVisitDist = null;
    private EmpiricalDiscreteDistribution uniqueRevisitDist = null;

    private VisitationReport() {
    }

    /**
     * The single report instance.
     */
    public static final VisitationReport INSTANCE = new VisitationReport();

    /**
     * The system property with this name must be {@code false} to omit
     * the visitation detail report; it runs by default.
     */
    public static final String RUN_DETAIL_PROPERTY = "amat.VisitationReport.runDetail";

    /**
     * Base name of the visitation detail file.
     */
    public static final String DETAIL_REPORT_NAME = "visit-detail.csv";

    /**
     * Runs the visitation reports.
     */
    public static void run() {
        if (runDetailRequested())
            INSTANCE.reportDetail();
    }

    public double getMeanAffinity() {
        return getAffinitySummary().getMean();
    }

    public double getMeanQuantity() {
        return getQuantitySummary().getMean();
    }

    public double getMeanTotalAgEncounter() {
        return getTotalVisitDist().mean();
    }

    public double getMeanUniqueAgEncounter() {
        return getUniqueVisitDist().mean();
    }

    public double getMeanUniqueAgRevisited() {
        return getUniqueRevisitDist().mean();
    }

    private StatSummary getAffinitySummary() {
        if (affinitySummary == null)
            affinitySummary = StatSummary.compute(BCell.viewAffinityList());

        return affinitySummary;
    }

    private StatSummary getQuantitySummary() {
        if (quantitySummary == null)
            quantitySummary = StatSummary.compute(BCell.viewQuantityList());

        return quantitySummary;
    }

    private EmpiricalDiscreteDistribution getTotalVisitDist() {
        if (totalVisitDist == null)
            totalVisitDist = EmpiricalDiscreteDistribution.compute(BCell.viewTotalEncounters());

        return totalVisitDist;
    }

    private EmpiricalDiscreteDistribution getUniqueVisitDist() {
        if (uniqueVisitDist == null)
            uniqueVisitDist = EmpiricalDiscreteDistribution.compute(BCell.viewUniqueEncounters());

        return uniqueVisitDist;
    }

    private EmpiricalDiscreteDistribution getUniqueRevisitDist() {
        if (uniqueRevisitDist == null)
            uniqueRevisitDist = EmpiricalDiscreteDistribution.compute(BCell.viewUniqueRevisits());

        return uniqueRevisitDist;
    }

    private static boolean runDetailRequested() {
        return JamProperties.getOptionalBoolean(RUN_DETAIL_PROPERTY, true);
    }

    private void reportDetail() {
        PrintWriter writer = openWriter(DETAIL_REPORT_NAME);
        writer.println(VisitationRecord.header());

        for (VisitationRecord record : viewRecords())
            writer.println(record.format());

        writer.close();
    }

    private List<VisitationRecord> viewRecords() {
        if (records.isEmpty())
            generateRecords();

        return Collections.unmodifiableList(records);
    }

    private void generateRecords() {
        JamLogger.info("Generating visitation detail records...");
        int gcCycle = GerminalCenter.REPLICATION_CYCLE + 1;

        while (true) {
            VisitationRecord record = VisitationRecord.compute(gcCycle);

            if (record == null)
                break;

            records.add(record);
            ++gcCycle;
        }
    }
}
