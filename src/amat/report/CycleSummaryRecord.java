
package amat.report;

import java.util.Collection;

import jam.math.DoubleUtil;
import jam.util.CollectionUtil;

import amat.bcell.BCell;
import amat.bcell.ClonalDiversity;
import amat.driver.AmatDriver;
import amat.germinal.GerminalCenter;
import amat.germinal.PopulationRecord;

/**
 * Summarizes the state of the germinal centers after a particular
 * cycle.
 */
public final class CycleSummaryRecord {
    private final int cycleIndex;
    private final double meanPopulation;
    private final double meanClonalEntropy;
    private final double meanTotalEncounter;
    private final double meanUniqueEncounter;
    private final double meanAntigenQty;
    private final double meanGeneration;
    private final double meanMutationCount;
    private final double meanMutationalDist;
    private final double meanMatchingConserved;
    private final double meanMatchingVariable;
    private final double aboveThresholdFraction;

    private CycleSummaryRecord(int cycleIndex,
                               double meanPopulation,
                               double meanClonalEntropy,
                               double meanTotalEncounter,
                               double meanUniqueEncounter,
                               double meanAntigenQty,
                               double meanGeneration,
                               double meanMutationCount,
                               double meanMutationalDist,
                               double meanMatchingConserved,
                               double meanMatchingVariable,
                               double aboveThresholdFraction) {
        this.cycleIndex             = cycleIndex;
        this.meanPopulation         = meanPopulation;
        this.meanClonalEntropy      = meanClonalEntropy;
        this.meanTotalEncounter     = meanTotalEncounter;
        this.meanUniqueEncounter    = meanUniqueEncounter;
        this.meanAntigenQty         = meanAntigenQty;
        this.meanGeneration         = meanGeneration;
        this.meanMutationCount      = meanMutationCount;
        this.meanMutationalDist     = meanMutationalDist;
        this.meanMatchingConserved  = meanMatchingConserved;
        this.meanMatchingVariable   = meanMatchingVariable;
	this.aboveThresholdFraction = aboveThresholdFraction;
    }

    private static final int MINIMUM_ACTIVE_SIZE = 500;

    /**
     * Computes the cycle summary record for a given cycle index.
     *
     * @param cycleIndex the GC cycle of interest.
     *
     * @return the cycle summary record for the given cycle index, or
     * {@code null} if no germinal centers survived to the specified
     * cycle.
     */
    public static CycleSummaryRecord compute(int cycleIndex) {
        Collection<BCell> activeCells = 
            AmatDriver.instance().getActiveCells(cycleIndex);

        if (activeCells.size() < MINIMUM_ACTIVE_SIZE)
            return null;

        Collection<GerminalCenter> germinalCenters =
            AmatDriver.instance().viewGerminalCenters();

        Collection<PopulationRecord> populationRecords =
            AmatDriver.instance().getPopulationRecords(cycleIndex);

        double meanPopulation = 
            CollectionUtil.average(populationRecords, record -> record.beginning());

        double meanClonalEntropy =
            CollectionUtil.average(germinalCenters, gc -> computeClonalEntropy(gc, cycleIndex));

        double meanTotalEncounter =
            CollectionUtil.average(activeCells, bcell -> bcell.countTotalEpitopesEncountered());

        double meanUniqueEncounter =
            CollectionUtil.average(activeCells, bcell -> bcell.countUniqueEpitopesEncountered());

        double meanAntigenQty =
            CollectionUtil.average(activeCells, bcell -> bcell.getAntigenQty());
        
        double meanGeneration =
            CollectionUtil.average(activeCells, bcell -> bcell.getGeneration());

        double meanMutationCount =
            CollectionUtil.average(activeCells, bcell -> bcell.getMutationCount());

        double meanMutationalDist =
            CollectionUtil.average(activeCells, bcell -> bcell.getFounderDistance());

        double meanMatchingConserved =
            CollectionUtil.average(activeCells, bcell -> bcell.getFractionMatchingConserved());

        double meanMatchingVariable =
            CollectionUtil.average(activeCells, bcell -> bcell.getFractionMatchingVariable());

        double aboveThresholdFraction =
            CollectionUtil.average(activeCells, bcell -> isAboveThreshold(bcell) ? 1.0 : 0.0);

        return new CycleSummaryRecord(cycleIndex,
                                      meanPopulation,
                                      meanClonalEntropy,
                                      meanTotalEncounter,
                                      meanUniqueEncounter,
                                      meanAntigenQty,
                                      meanGeneration,
                                      meanMutationCount,
                                      meanMutationalDist,
                                      meanMatchingConserved,
                                      meanMatchingVariable,
                                      aboveThresholdFraction);
    }

    private static double computeClonalEntropy(GerminalCenter gc, int cycleIndex) {
        if (gc.countActiveCells(cycleIndex) > 0)
            return gc.computeClonalDiversity(cycleIndex).getEntropy();
        else
            return 0.0;
    }

    private static boolean isAboveThreshold(BCell bcell) {
        return bcell.getFractionMatchingConserved() > MatchingReport.getConservedThreshold();
    }


    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the headline report file.
     */
    public String format() {
        return String.format("%d,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f,%.8f",
                             cycleIndex,
                             meanPopulation,
                             meanClonalEntropy,
                             meanTotalEncounter,
                             meanUniqueEncounter,
                             meanAntigenQty,
                             meanGeneration,
                             meanMutationCount,
                             meanMutationalDist,
                             meanMatchingConserved,
                             meanMatchingVariable,
                             aboveThresholdFraction);
    }

    /**
     * Returns a string suitable for the header line in the headline
     * report file.
     *
     * @return a string suitable for the header line in the headline
     * report file.
     */
    public static String header() {
        return "cycleIndex"
            + ",meanPopulation"
            + ",meanClonalEntropy"
            + ",meanTotalEncounter"
            + ",meanUniqueEncounter"
            + ",meanAntigenQty"
            + ",meanGeneration"
            + ",meanMutationCount"
            + ",meanMutationalDist"
            + ",meanMatchingConserved"
            + ",meanMatchingVariable"
            + ",aboveThresholdFraction";
    }
}
