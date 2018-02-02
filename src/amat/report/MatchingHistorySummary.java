
package amat.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.math.DoubleUtil;
import jam.math.StatSummary;

/**
 * Copmutes and stores summary statistics for the average fraction of
 * plasma cell receptor elements that exactly match the conserved and
 * variable regions of the epitopes presented for affinity maturation
 * (as a function of the generation in the plasma cell lineage).
 */
public final class MatchingHistorySummary {
    private final int generation;
    private final double population;
    private final StatSummary totalSummary;
    private final StatSummary variableSummary;
    private final StatSummary conservedSummary;

    private MatchingHistorySummary(int generation,
                                   double population,
                                   StatSummary totalSummary,
                                   StatSummary variableSummary,
                                   StatSummary conservedSummary) {
        this.generation = generation;
        this.population = population;
        this.totalSummary = totalSummary;
        this.variableSummary = variableSummary;
        this.conservedSummary = conservedSummary;
    }

    /**
     * Require at least this many plasma cells to summarize a generation.
     */
    public static final int MINIMUM_SAMPLE = 5;

    /**
     * Creates all new summary records from a collection of details.
     *
     * @param details the history detail records.
     *
     * @return a list containing summary records for each generation
     * represented in the detail records.
     */
    public static List<MatchingHistorySummary> compute(Collection<MatchingHistoryDetail> details) {
        //
        // The list generations.get(k) will contain all matching records for generation "k"...
        //
        List<List<MatchingRecord>> generations = 
            MatchingHistoryDetail.groupByGeneration(details);

        List<MatchingHistorySummary> summaries =
            new ArrayList<MatchingHistorySummary>();

        for (int generation = 0; generation < generations.size(); ++generation) {
            List<MatchingRecord> records = generations.get(generation);

            if (records.size() < MINIMUM_SAMPLE)
                break;

            double population = computePopulation(generation, generations, details);
            StatSummary totalSummary = StatSummary.compute(records, x -> x.getTotal());
            StatSummary variableSummary = StatSummary.compute(records, x -> x.getVariable());
            StatSummary conservedSummary = StatSummary.compute(records, x -> x.getConserved());

            summaries.add(new MatchingHistorySummary(generation, population, totalSummary, variableSummary, conservedSummary));
        }

        return summaries;
    }

    private static double computePopulation(int generation, 
                                            List<List<MatchingRecord>> generations, 
                                            Collection<MatchingHistoryDetail> details) {
        int totalSize = details.size();
        int generationSize = generations.get(generation).size();

        if (generation == generations.size() - 1) {
            return DoubleUtil.ratio(generationSize, totalSize);
        }
        else {
            //
            // The population of this generation is the difference in
            // size between the B cells present at this generation and
            // those present at the next generation...
            //
            int nextGenSize = generations.get(generation + 1).size();
            return DoubleUtil.ratio(generationSize - nextGenSize, totalSize);
        }
    }

    /**
     * Returns the header line for summary report files.
     *
     * @return the header line for summary report files.
     */
    public static String header() {
        return "generation"
            + ",population"
            + ",matchConservedMean"
            + ",matchConservedErr"
            + ",matchVariableMean"
            + ",matchVariableErr"
            + ",matchTotalMean"
            + ",matchTotalErr";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d,%f,%f,%f,%f,%f,%f,%f",
                             generation,
                             population,
                             conservedSummary.getMean(),
                             conservedSummary.getError(),
                             variableSummary.getMean(),
                             variableSummary.getError(),
                             totalSummary.getMean(),
                             totalSummary.getError());
    }

    /**
     * Returns the generation for which the history was recorded.
     *
     * @return the generation for which the history was recorded.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the fraction of plasma cells produced in this generation.
     *
     * @return the fraction of plasma cells produced in this generation.
     */
    public double getPopulation() {
        return population;
    }

    /**
     * Returns the summary for the fraction of plasma cell receptor
     * elements matching the conserved epitope region.
     *
     * @return the summary for the fraction of plasma cell receptor
     * elements matching the conserved epitope region.
     */
    public StatSummary getConservedSummary() {
        return conservedSummary;
    }

    /**
     * Returns the summary for the fraction of plasma cell receptor
     * elements matching the variable epitope region.
     *
     * @return the summary for the fraction of plasma cell receptor
     * elements matching the variable epitope region.
     */
    public StatSummary getVariableSummary() {
        return variableSummary;
    }

    /**
     * Returns the summary for the fraction of plasma cell receptor
     * elements matching the entire epitope.
     *
     * @return the summary for the fraction of plasma cell receptor
     * elements matching the entire epitope.
     */
    public StatSummary getTotalSummary() {
        return totalSummary;
    }
}
