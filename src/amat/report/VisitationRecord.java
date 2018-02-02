
package amat.report;

import com.google.common.collect.Multiset;

import jam.dist.EmpiricalDiscreteDistribution;
import jam.math.StatSummary;

import amat.bcell.BCell;

/**
 * Encapsulates summary statistics for the number of epitopes
 * encountered by B cells on a single germinal center cycle.
 */
public final class VisitationRecord {
    private final int gcCycle;
    private final StatSummary affinitySummary;
    private final StatSummary quantitySummary;
    private final EmpiricalDiscreteDistribution totalVisitDist;
    private final EmpiricalDiscreteDistribution uniqueVisitDist;
    private final EmpiricalDiscreteDistribution uniqueRevisitDist;

    private VisitationRecord(int gcCycle,
                             StatSummary affinitySummary,
                             StatSummary quantitySummary,
                             EmpiricalDiscreteDistribution totalVisitDist,
                             EmpiricalDiscreteDistribution uniqueVisitDist,
                             EmpiricalDiscreteDistribution uniqueRevisitDist) {
        this.gcCycle        = gcCycle;
        this.affinitySummary   = affinitySummary;
        this.quantitySummary   = quantitySummary;
        this.totalVisitDist    = totalVisitDist;
        this.uniqueVisitDist   = uniqueVisitDist;
        this.uniqueRevisitDist = uniqueRevisitDist;
    }

    /**
     * The minimum number of observations required to compute a
     * visitation record.
     */
    public static final int MIN_OBS = 10;

    /**
     * Creates a new visitation record for a particular GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return the visitation record for the specified GC cycle, or
     * {@code null} if there are fewer than the minimum number of
     * observations for that GC cycle.
     */
    public static VisitationRecord compute(int gcCycle) {
        Multiset<Integer> totalVisits = BCell.viewTotalEncounters(gcCycle);
        Multiset<Integer> uniqueVisits = BCell.viewUniqueEncounters(gcCycle);
        Multiset<Integer> uniqueRevisits = BCell.viewUniqueRevisits(gcCycle);

        if (totalVisits.size() < MIN_OBS || uniqueVisits.size() < MIN_OBS || uniqueRevisits.size() < MIN_OBS)
            return null;

        StatSummary affinitySummary = StatSummary.compute(BCell.viewAffinityList(gcCycle));
        StatSummary quantitySummary = StatSummary.compute(BCell.viewQuantityList(gcCycle));

        EmpiricalDiscreteDistribution totalVisitDist = EmpiricalDiscreteDistribution.compute(totalVisits);
        EmpiricalDiscreteDistribution uniqueVisitDist = EmpiricalDiscreteDistribution.compute(uniqueVisits);
        EmpiricalDiscreteDistribution uniqueRevisitDist = EmpiricalDiscreteDistribution.compute(uniqueRevisits);

        return new VisitationRecord(gcCycle, 
                                    affinitySummary,
                                    quantitySummary,
                                    totalVisitDist,
                                    uniqueVisitDist,
                                    uniqueRevisitDist);
    }

    /**
     * Returns the header line for report files.
     *
     * @return the header line for report files.
     */
    public static String header() {
        return "gcCycle"
            + ",totalVisitMean"
            + ",totalVisitErr"
            + ",uniqueVisitMean"
            + ",uniqueVisitErr"
            + ",uniqueRevisitMean"
            + ",uniqueRevisitErr"
            + ",affinityMean"
            + ",affinityErr"
            + ",quantityMean"
            + ",quantityErr";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f",
                             gcCycle, 
                             totalVisitDist.mean(),
                             totalVisitDist.sterr(),
                             uniqueVisitDist.mean(),
                             uniqueVisitDist.sterr(),
                             uniqueRevisitDist.mean(),
                             uniqueRevisitDist.sterr(),
                             affinitySummary.getMean(),
                             affinitySummary.getError(),
                             quantitySummary.getMean(),
                             quantitySummary.getError());
    }
}
