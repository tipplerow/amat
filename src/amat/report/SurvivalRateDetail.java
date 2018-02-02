
package amat.report;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import jam.util.MapUtil;

import amat.germinal.GerminalCenter;
import amat.germinal.GerminalCenterEvent;
import amat.germinal.PopulationRecord;

/**
 * Records the survival rate for a particular generation for a
 * given germinal center.
 */
public final class SurvivalRateDetail {
    private final int trialIndex;
    private final int generation;
    private final PopulationRecord population;

    private SurvivalRateDetail(int trialIndex,
                               int generation,
                               PopulationRecord population) {
        this.trialIndex = trialIndex;
        this.generation = generation;
        this.population = population;
    }

    /**
     * Creates a new survival rate record for a particular generation
     * of a given germinal center.
     *
     * @param gc the germinal center of interest.
     *
     * @param generation the generation of interest.
     *
     * @return the survival rate record for the specified germinal
     * center and generation.
     */
    public static SurvivalRateDetail compute(GerminalCenter gc, int generation) {
        return new SurvivalRateDetail(gc.getTrialIndex(), generation, gc.getPopulation(generation));
    }

    /**
     * Returns the header line for detail report files.
     *
     * @return the header line for detail report files.
     */
    public static String header() {
        return "trialIndex,generation,growth,mutation,signaling,competition";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d,%d,%f,%f,%f,%f", 
                             trialIndex, generation, 
                             getGrowthRate(),
                             getMutationSurvival(),
                             getSignalingSurvival(),
                             getCompetitionSurvival());
    }

    /**
     * Returns the trial index for the germinal center.
     *
     * @return the trial index for the germinal center.
     */
    public int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Returns the generation for which the survival rate was recorded.
     *
     * @return the generation for which the survival rate was recorded.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the overall growth rate for the B cell population for
     * the generation when the survival rates were recorded.
     *
     * @return the overall growth rate for the B cell population for
     * the generation when the survival rates were recorded.
     */
    public double getGrowthRate() {
        return population.computeGrowthRate();
    }

    /**
     * Returns the fraction of B cells surviving the division and
     * mutation step.
     *
     * @return the fraction of B cells surviving the division and
     * mutation step.
     */
    public double getMutationSurvival() {
        return population.computeSurvivalRate(GerminalCenterEvent.DIVISION_MUTATION);
    }

    /**
     * Returns the fraction of B cells surviving the BCR signaling
     * step.
     *
     * @return the fraction of B cells surviving the BCR signaling
     * step.
     */
    public double getSignalingSurvival() {
        return population.computeSurvivalRate(GerminalCenterEvent.BCR_SIGNALING);
    }

    /**
     * Returns the fraction of B cells surviving the competition for T
     * cell help.
     *
     * @return the fraction of B cells surviving the competition for T
     * cell help.
     */
    public double getCompetitionSurvival() {
        return population.computeSurvivalRate(GerminalCenterEvent.TCELL_COMPETITION);
    }

    /**
     * Groups a collection of survival rate records by generation.
     *
     * @param records the records to group.
     *
     * @return a map containing the records grouped by generation.
     */
    public static SortedMap<Integer, Collection<SurvivalRateDetail>> 
        groupByGeneration(Collection<SurvivalRateDetail> records) {

        SortedMap<Integer, Collection<SurvivalRateDetail>> grouped =
            new TreeMap<Integer, Collection<SurvivalRateDetail>>();

        MapUtil.group(grouped, records, x -> x.getGeneration());
        return grouped;
    }
}
