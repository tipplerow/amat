
package amat.report;

import java.util.List;

import jam.app.JamLogger;
import jam.util.CollectionUtil;

import amat.bcell.BCell;
import amat.driver.AmatDriver;
import amat.germinal.GerminalCenter;
import amat.germinal.GerminalCenterState;

/**
 * Encapsulates the information contained in a "headline" report.
 */
public final class HeadlineRecord {
    private final double meanTotalAgEncounter;
    private final double meanUniqueAgEncounter;
    private final double meanUniqueAgRevisited;
    private final double meanVisitAffinity;
    private final double meanQuantity;

    private final double meanLineageAgEncounter;
    private final double meanLineageAgRevisited;
    private final double meanLineageAntigenQty;

    private final double meanGrowthRate;
    private final double meanMutationSurvivalRate;
    private final double meanSignalingSurvivalRate;
    private final double meanCompetitionSurivalRate;

    private final double meanAbProdRate;
    private final double meanPcProdRate;
    private final double meanGeneration;
    private final double meanMutationCount;
    private final double meanMutationalDist;

    private final double meanMatchingConserved;
    private final double meanMatchingVariable;
    private final double meanMatchingTotal;

    private final int    aboveThresholdNumber;
    private final double aboveThresholdFraction;
    private final double aboveThresholdRate;

    private final double footprintAffinityMean;
    private final double footprintAffinitySD;
    private final double footprintAffinityErr;

    private final double footprintBreadthMean;
    private final double footprintBreadthSD;
    private final double footprintBreadthErr;

    private final double neutPanelAffinityMean;
    private final double neutPanelAffinitySD;
    private final double neutPanelAffinityErr;

    private final double neutPanelBreadthMean;
    private final double neutPanelBreadthSD;
    private final double neutPanelBreadthErr;

    private final double fractionAgConsumed;
    private final double fractionExtinguished;
    private final double fractionSizeLimit;
    private final double fractionTimeLimit;

    private final double gcCycleMean;
    private final double gcCycleSD;
    private final double gcCycleErr;

    private static HeadlineRecord instance = null;

    private HeadlineRecord(double meanTotalAgEncounter,
                           double meanUniqueAgEncounter,
                           double meanUniqueAgRevisited,
                           double meanVisitAffinity,
                           double meanQuantity,
                           double meanLineageAgEncounter,
                           double meanLineageAgRevisited,
                           double meanLineageAntigenQty,
                           double meanGrowthRate,
                           double meanMutationSurvivalRate,
                           double meanSignalingSurvivalRate,
                           double meanCompetitionSurivalRate,
                           double meanAbProdRate,
                           double meanPcProdRate,
                           double meanGeneration,
                           double meanMutationCount,
                           double meanMutationalDist,
                           double meanMatchingConserved,
                           double meanMatchingVariable,
                           double meanMatchingTotal,
			   int    aboveThresholdNumber,
			   double aboveThresholdFraction,
			   double aboveThresholdRate,
                           double footprintAffinityMean,
                           double footprintAffinitySD,
                           double footprintAffinityErr,
                           double footprintBreadthMean,
                           double footprintBreadthSD,
                           double footprintBreadthErr,
                           double neutPanelAffinityMean,
                           double neutPanelAffinitySD,
                           double neutPanelAffinityErr,
                           double neutPanelBreadthMean,
                           double neutPanelBreadthSD,
                           double neutPanelBreadthErr,
                           double fractionAgConsumed,
                           double fractionExtinguished,
                           double fractionSizeLimit,
                           double fractionTimeLimit,
                           double gcCycleMean,
                           double gcCycleSD,
                           double gcCycleErr) {
        this.meanTotalAgEncounter  = meanTotalAgEncounter;
        this.meanUniqueAgEncounter = meanUniqueAgEncounter;
        this.meanUniqueAgRevisited = meanUniqueAgRevisited;
        this.meanVisitAffinity     = meanVisitAffinity;
        this.meanQuantity          = meanQuantity;

        this.meanLineageAgEncounter = meanLineageAgEncounter;
        this.meanLineageAgRevisited = meanLineageAgRevisited;
        this.meanLineageAntigenQty  = meanLineageAntigenQty;

        this.meanGrowthRate             = meanGrowthRate;
        this.meanMutationSurvivalRate   = meanMutationSurvivalRate;
        this.meanSignalingSurvivalRate  = meanSignalingSurvivalRate;
        this.meanCompetitionSurivalRate = meanCompetitionSurivalRate;

        this.meanAbProdRate     = meanAbProdRate;
        this.meanPcProdRate     = meanPcProdRate;
        this.meanGeneration     = meanGeneration;
        this.meanMutationCount  = meanMutationCount;
        this.meanMutationalDist = meanMutationalDist;

        this.meanMatchingConserved = meanMatchingConserved;
        this.meanMatchingVariable  = meanMatchingVariable;
        this.meanMatchingTotal     = meanMatchingTotal;

	this.aboveThresholdNumber   = aboveThresholdNumber;
	this.aboveThresholdFraction = aboveThresholdFraction;
	this.aboveThresholdRate     = aboveThresholdRate;

        this.footprintAffinityMean = footprintAffinityMean;
        this.footprintAffinitySD   = footprintAffinitySD;
        this.footprintAffinityErr  = footprintAffinityErr;

        this.footprintBreadthMean = footprintBreadthMean;
        this.footprintBreadthSD   = footprintBreadthSD;
        this.footprintBreadthErr  = footprintBreadthErr;

        this.neutPanelAffinityMean = neutPanelAffinityMean;
        this.neutPanelAffinitySD   = neutPanelAffinitySD;
        this.neutPanelAffinityErr  = neutPanelAffinityErr;

        this.neutPanelBreadthMean = neutPanelBreadthMean;
        this.neutPanelBreadthSD   = neutPanelBreadthSD;
        this.neutPanelBreadthErr  = neutPanelBreadthErr;

        this.fractionAgConsumed   = fractionAgConsumed;
        this.fractionExtinguished = fractionExtinguished;
        this.fractionSizeLimit    = fractionSizeLimit;
        this.fractionTimeLimit    = fractionTimeLimit;

        this.gcCycleMean = gcCycleMean;
        this.gcCycleSD   = gcCycleSD;
        this.gcCycleErr  = gcCycleErr;
    }

    /**
     * Returns the single headline record for the affinity maturation
     * simulation.
     *
     * @return the single headline record for the affinity maturation
     * simulation.
     */
    public static HeadlineRecord instance() {
	if (instance == null)
	    instance = create();

	return instance;
    }

    private static HeadlineRecord create() {
        JamLogger.info("Assembling the plasma cell lineage...");
        List<BCell> lineage = AmatDriver.instance().viewPlasmaLineage(GerminalCenter.REPLICATION_CYCLE + 1);

        double meanLineageAgEncounter = CollectionUtil.average(lineage, bcell -> (double) bcell.countUniqueEpitopesEncountered());
        double meanLineageAgRevisited = CollectionUtil.average(lineage, bcell -> (double) bcell.countUniqueEpitopesRevisited());
        double meanLineageAntigenQty  = CollectionUtil.average(lineage, bcell -> bcell.getAntigenQty());

        SurvivalRateSummary survivalHeadline = SurvivalRateReport.instance().headline();

        double meanGrowthRate             = survivalHeadline.getGrowthSummary().getMean();
        double meanMutationSurvivalRate   = survivalHeadline.getMutationSummary().getMean();
        double meanSignalingSurvivalRate  = survivalHeadline.getSignalingSummary().getMean();
        double meanCompetitionSurivalRate = survivalHeadline.getCompetitionSummary().getMean();

        double meanTotalAgEncounter  = VisitationReport.INSTANCE.getMeanTotalAgEncounter();
        double meanUniqueAgEncounter = VisitationReport.INSTANCE.getMeanUniqueAgEncounter();
        double meanUniqueAgRevisited = VisitationReport.INSTANCE.getMeanUniqueAgRevisited();

        double meanVisitAffinity = VisitationReport.INSTANCE.getMeanAffinity();
        double meanQuantity = VisitationReport.INSTANCE.getMeanQuantity();

	double meanAbProdRate = ProdRateReport.instance().getAbSummary().getMean();
	double meanPcProdRate = ProdRateReport.instance().getPcSummary().getMean();
          
	double meanGeneration     = GermDistReport.instance().getGenerationSummary().getMean();
	double meanMutationCount  = GermDistReport.instance().getMutationCountSummary().getMean();
	double meanMutationalDist = GermDistReport.instance().getMutationalDistSummary().getMean();

	double meanMatchingConserved  = MatchingReport.instance().getConservedSummary().getMean();
	double meanMatchingVariable   = MatchingReport.instance().getVariableSummary().getMean();
	double meanMatchingTotal      = MatchingReport.instance().getTotalSummary().getMean();
	int    aboveThresholdNumber   = MatchingReport.instance().getAboveThresholdNumber();
	double aboveThresholdFraction = MatchingReport.instance().getAboveThresholdFraction();
	double aboveThresholdRate     = meanPcProdRate * aboveThresholdFraction;

        AffinitySummary plasmaSummary = PlasmaAffinityReport.instance().getPlasmaSummary();

        double footprintAffinityMean = plasmaSummary.getFootprintAffinitySummary().getMean(); 
        double footprintAffinitySD   = plasmaSummary.getFootprintAffinitySummary().getSD();
        double footprintAffinityErr  = plasmaSummary.getFootprintAffinitySummary().getError();

        double footprintBreadthMean = plasmaSummary.getFootprintBreadthSummary().getMean();
        double footprintBreadthSD   = plasmaSummary.getFootprintBreadthSummary().getSD();
        double footprintBreadthErr  = plasmaSummary.getFootprintBreadthSummary().getError();

        double neutPanelAffinityMean = plasmaSummary.getNeutPanelAffinitySummary().getMean();
        double neutPanelAffinitySD   = plasmaSummary.getNeutPanelAffinitySummary().getSD();
        double neutPanelAffinityErr  = plasmaSummary.getNeutPanelAffinitySummary().getError();

        double neutPanelBreadthMean = plasmaSummary.getNeutPanelBreadthSummary().getMean();
        double neutPanelBreadthSD   = plasmaSummary.getNeutPanelBreadthSummary().getSD();
        double neutPanelBreadthErr  = plasmaSummary.getNeutPanelBreadthSummary().getError();

        GCFateReport fate = GCFateReport.instance();

        double fractionAgConsumed   = fate.getStateFraction(GerminalCenterState.ANTIGEN_CONSUMED);
        double fractionExtinguished = fate.getStateFraction(GerminalCenterState.EXTINGUISHED);
        double fractionSizeLimit    = fate.getStateFraction(GerminalCenterState.EXCEEDED_CAPACITY);
        double fractionTimeLimit    = fate.getStateFraction(GerminalCenterState.EXCEEDED_TIME);

        double gcCycleMean = fate.getGcCycleSummary().getMean();
        double gcCycleSD   = fate.getGcCycleSummary().getSD();
        double gcCycleErr  = fate.getGcCycleSummary().getError();

	return new HeadlineRecord(meanTotalAgEncounter,
                                  meanUniqueAgEncounter,
                                  meanUniqueAgRevisited,
                                  meanVisitAffinity,
                                  meanQuantity,
                                  meanLineageAgEncounter,
                                  meanLineageAgRevisited,
                                  meanLineageAntigenQty,
                                  meanGrowthRate,
                                  meanMutationSurvivalRate,
                                  meanSignalingSurvivalRate,
                                  meanCompetitionSurivalRate,
                                  meanAbProdRate,
                                  meanPcProdRate,
				  meanGeneration,
				  meanMutationCount,
				  meanMutationalDist,
				  meanMatchingConserved,
				  meanMatchingVariable,
				  meanMatchingTotal,
				  aboveThresholdNumber,
				  aboveThresholdFraction,
				  aboveThresholdRate,
                                  footprintAffinityMean,
                                  footprintAffinitySD,
                                  footprintAffinityErr,
                                  footprintBreadthMean,
                                  footprintBreadthSD,
                                  footprintBreadthErr,
                                  neutPanelAffinityMean,
                                  neutPanelAffinitySD,
                                  neutPanelAffinityErr,
                                  neutPanelBreadthMean,
                                  neutPanelBreadthSD,
                                  neutPanelBreadthErr,
                                  fractionAgConsumed,
                                  fractionExtinguished,
                                  fractionSizeLimit,
                                  fractionTimeLimit,
                                  gcCycleMean,
                                  gcCycleSD,
                                  gcCycleErr);
    }

    /**
     * Returns the mean number of total antigen encounters in the
     * light zone.
     *
     * @return the mean number of total antigen encounters in the
     * light zone.
     */
    public double getMeanTotalAgEncounter() {
        return meanTotalAgEncounter;
    }

    /**
     * Returns the mean number of unique antigens encountered in the
     * light zone.
     *
     * @return the mean number of unique antigens encountered in the
     * light zone.
     */
    public double getMeanUniqueAgEncounter() {
        return meanUniqueAgEncounter;
    }

    /**
     * Returns the mean number of antigens revisited (encountered on
     * two sequential trips) in the light zone.
     *
     * @return the mean number of antigens revisited (encountered on
     * two sequential trips) in the light zone.
     */
    public double getMeanUniqueRevisited() {
        return meanUniqueAgRevisited;
    }

    /**
     * Returns the mean number of antigens encountered by plasma cells
     * and their entire lineage.
     *
     * @return the mean number of antigens encountered by plasma cells
     * and their entire lineage.
     */
    public double getMeanLineageAgEncounter() {
        return meanLineageAgEncounter;
    }

    /**
     * Returns the mean number of antigens revisited (on sequential
     * trips to the light zone) by plasma cells and their entire
     * lineage.
     *
     * @return the mean number of antigens encountered (on sequential
     * trips to the light zone) by plasma cells and their entire
     * lineage.
     */
    public double getMeanLineageAgRevisited() {
        return meanLineageAgRevisited;
    }

    /**
     * Returns the mean number of antigens revisited (on sequential
     * trips to the light zone) by plasma cells and their entire
     * lineage.
     *
     * @return the mean number of antigens encountered (on sequential
     * trips to the light zone) by plasma cells and their entire
     * lineage.
     */
    public double getMeanLineageAntigenQty() {
        return meanLineageAntigenQty;
    }

    /**
     * Returns the mean antibody production rate.
     *
     * @return the mean antibody production rate.
     */
    public double getMeanAbProdRate() {
	return meanAbProdRate;
    }

    /**
     * Returns the mean plasma-cell production rate.
     *
     * @return the mean plasma-cell production rate.
     */
    public double getMeanPcProdRate() {
	return meanPcProdRate;
    }

    /**
     * Returns the mean generation at which plasma cells exited their
     * germinal center.
     *
     * @return the meangeneration at which plasma cells exited their
     * germinal center. 
     */
    public double getMeanGeneration() {
	return meanGeneration;
    }

    /**
     * Returns the mean number of mutations accumulated by the plasma
     * cells.
     *
     * @return the mean number of mutations accumulated by the plasma
     * cells.
     */
    public double getMeanMutationCount() {
	return meanMutationCount;
    }

    /**
     * Returns the mean mutational distance between plasma cells and
     * their germline (founder) cells.
     *
     * @return the mean mutational distance between plasma cells and
     * their germline (founder) cells.
     */
    public double getMeanMutationalDist() {
        return meanMutationalDist;
    }

    /**
     * Returns the mean fraction of plasma cell receptor elements that
     * match conserved elements of the immunization epitopes.
     *
     * @return the mean fraction of plasma cell receptor elements that
     * match conserved elements of the immunization epitopes.
     */
    public double getMeanMatchingConserved() {
	return meanMatchingConserved;
    }

    /**
     * Returns the mean fraction of plasma cell receptor elements that
     * match variable elements of the immunization epitopes.
     *
     * @return the mean fraction of plasma cell receptor elements that
     * match variable elements of the immunization epitopes.
     */
    public double getMeanMatchingVariable() {
	return meanMatchingVariable;
    }

    /**
     * Returns the mean fraction of plasma cell receptor elements that
     * match elements of the immunization epitopes.
     *
     * @return the mean fraction of plasma cell receptor elements that
     * match elements of the immunization epitopes.
     */
    public double getMeanMatchingTotal() {
	return meanMatchingTotal;
    }

    /**
     * Returns the number of plasma cell receptors with a conserved
     * matching fraction above the reporting threshold.
     *
     * @return the number of plasma cell receptors with a conserved
     * matching fraction above the reporting threshold.
     */
    public int getAboveThresholdNumber() {
	return aboveThresholdNumber;
    }

    /**
     * Returns the fraction of plasma cell receptors with a conserved
     * matching fraction above the reporting threshold.
     *
     * @return the fraction of plasma cell receptors with a conserved
     * matching fraction above the reporting threshold.
     */
    public double getAboveThresholdFraction() {
	return aboveThresholdFraction;
    }

    /**
     * Returns the rate of BNAb production.
     *
     * @return the rate of BNAb production.
     */
    public double getAboveThresholdRate() {
	return aboveThresholdRate;
    }

    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the headline report file.
     */
    public String format() {
        return String.format("%.6f"     // meanTotalAgEncounter
                             + ",%.6f"  // meanUniqueAgEncounter
                             + ",%.6f"  // meanUniqueAgRevisited
                             + ",%.6f"  // meanVisitAffinity
                             + ",%.6f"  // meanQuantity
                             + ",%.6f"  // meanLineageAgEncounter
                             + ",%.6f"  // meanLineageAgRevisited
                             + ",%.6f"  // meanLineageAntigenQty
                             + ",%.6f"  // meanGrowthRate
                             + ",%.6f"  // meanMutationSurvivalRate
                             + ",%.6f"  // meanSignalingSurvivalRate
                             + ",%.6f"  // meanCompetitionSurivalRate
                             + ",%.6f"  // meanAbProdRate
                             + ",%.6f"  // meanPcProdRate
                             + ",%.6f"  // meanGeneration
                             + ",%.6f"  // meanMutationCount
                             + ",%.6f"  // meanMutationalDist
                             + ",%.6f"  // meanMatchingConserved
                             + ",%.6f"  // meanMatchingVariable
                             + ",%.6f"  // meanMatchingTotal
                             + ",%d"    // aboveThresholdNumber
                             + ",%.6f"  // aboveThresholdFraction
                             + ",%.8f"  // aboveThresholdRate
                             + ",%.8f"  // footprintAffinityMean
                             + ",%.8f"  // footprintAffinitySD
                             + ",%.8f"  // footprintAffinityErr
                             + ",%.8f"  // footprintBreadthMean
                             + ",%.8f"  // footprintBreadthSD
                             + ",%.8f"  // footprintBreadthErr
                             + ",%.8f"  // neutPanelAffinityMean
                             + ",%.8f"  // neutPanelAffinitySD
                             + ",%.8f"  // neutPanelAffinityErr
                             + ",%.8f"  // neutPanelBreadthMean
                             + ",%.8f"  // neutPanelBreadthSD
                             + ",%.8f"  // neutPanelBreadthErr
                             + ",%.4f"  // fractionAgConsumed
                             + ",%.4f"  // fractionExtinguished
                             + ",%.4f"  // fractionSizeLimit
                             + ",%.4f"  // fractionTimeLimit
                             + ",%.8f"  // gcCycleMean
                             + ",%.8f"  // gcCycleSD
                             + ",%.8f", // gcCycleErr
                             meanTotalAgEncounter,
                             meanUniqueAgEncounter,
                             meanUniqueAgRevisited,
                             meanVisitAffinity,
                             meanQuantity,
                             meanLineageAgEncounter,
                             meanLineageAgRevisited,
                             meanLineageAntigenQty,
                             meanGrowthRate,
                             meanMutationSurvivalRate,
                             meanSignalingSurvivalRate,
                             meanCompetitionSurivalRate,
			     meanAbProdRate,
			     meanPcProdRate,
			     meanGeneration,
			     meanMutationCount,
			     meanMutationalDist,
			     meanMatchingConserved,
			     meanMatchingVariable,
			     meanMatchingTotal,
			     aboveThresholdNumber,
			     aboveThresholdFraction,
			     aboveThresholdRate,
                             footprintAffinityMean,
                             footprintAffinitySD,
                             footprintAffinityErr,
                             footprintBreadthMean,
                             footprintBreadthSD,
                             footprintBreadthErr,
                             neutPanelAffinityMean,
                             neutPanelAffinitySD,
                             neutPanelAffinityErr,
                             neutPanelBreadthMean,
                             neutPanelBreadthSD,
                             neutPanelBreadthErr,
                             fractionAgConsumed,
                             fractionExtinguished,
                             fractionSizeLimit,
                             fractionTimeLimit,
                             gcCycleMean,
                             gcCycleSD,
                             gcCycleErr);
    }

    /**
     * Returns a string suitable for the header line in the headline
     * report file.
     *
     * @return a string suitable for the header line in the headline
     * report file.
     */
    public static String header() {
        return "meanTotalAgEncounter"
            + ",meanUniqueAgEncounter"
            + ",meanUniqueAgRevisited"
            + ",meanVisitAffinity"
            + ",meanQuantity"
            + ",meanLineageAgEncounter"
            + ",meanLineageAgRevisited"
            + ",meanLineageAntigenQty"
            + ",meanGrowthRate"
            + ",meanMutationSurvivalRate"
            + ",meanSignalingSurvivalRate"
            + ",meanCompetitionSurivalRate"
            + ",meanAbProdRate"
            + ",meanPcProdRate"
	    + ",meanGeneration"
	    + ",meanMutationCount"
	    + ",meanMutationalDist"
	    + ",meanMatchingConserved"
	    + ",meanMatchingVariable"
	    + ",meanMatchingTotal"
	    + ",aboveThresholdNumber"
	    + ",aboveThresholdFraction"
	    + ",aboveThresholdRate"
            + ",footprintAffinityMean"
            + ",footprintAffinitySD"
            + ",footprintAffinityErr"
            + ",footprintBreadthMean"
            + ",footprintBreadthSD"
            + ",footprintBreadthErr"
            + ",neutPanelAffinityMean"
            + ",neutPanelAffinitySD"
            + ",neutPanelAffinityErr"
            + ",neutPanelBreadthMean"
            + ",neutPanelBreadthSD"
            + ",neutPanelBreadthErr"
            + ",fractionAgConsumed"
            + ",fractionExtinguished"
            + ",fractionSizeLimit"
            + ",fractionTimeLimit"
            + ",gcCycleMean"
            + ",gcCycleSD"
            + ",gcCycleErr";
    }
}
