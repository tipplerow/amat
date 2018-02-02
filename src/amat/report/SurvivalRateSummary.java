
package amat.report;

import java.util.Collection;

import jam.math.StatSummary;

/**
 * Copmutes and stores summary statistics for the survival rates at
 * a particular generation over all germinal centers in a simulation.
 */
public final class SurvivalRateSummary {
    private final int generation;
    private final StatSummary growthSummary;
    private final StatSummary mutationSummary;
    private final StatSummary signalingSummary;
    private final StatSummary competitionSummary;

    private SurvivalRateSummary(int generation,
                                StatSummary growthSummary,
                                StatSummary mutationSummary,
                                StatSummary signalingSummary,
                                StatSummary competitionSummary) {
        this.generation = generation;
        this.growthSummary = growthSummary;
        this.mutationSummary = mutationSummary;
        this.signalingSummary = signalingSummary;
        this.competitionSummary = competitionSummary;
    }

    /**
     * Creates a new summary record for a particular generation.
     *
     * @param generation the generation for which detail records were
     * collected.
     *
     * @param details the detail records for the given generation.
     *
     * @return a summary of the specified detail records.
     */
    public static SurvivalRateSummary compute(int generation, Collection<SurvivalRateDetail> details) {
        StatSummary growthSummary      = StatSummary.compute(details, x -> x.getGrowthRate());
        StatSummary mutationSummary    = StatSummary.compute(details, x -> x.getMutationSurvival());
        StatSummary signalingSummary   = StatSummary.compute(details, x -> x.getSignalingSurvival());
        StatSummary competitionSummary = StatSummary.compute(details, x -> x.getCompetitionSurvival());

        return new SurvivalRateSummary(generation, growthSummary, mutationSummary, signalingSummary, competitionSummary);
    }

    /**
     * Returns the header line for summary report files.
     *
     * @return the header line for summary report files.
     */
    public static String header() {
        return "generation"
            + ",growthMean"
            + ",growthErr"
            + ",mutationMean"
            + ",mutationErr"
            + ",signalingMean"
            + ",signalingErr"
            + ",competitionMean"
            + ",competitionErr";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f",
                             generation,
                             growthSummary.getMean(),
                             growthSummary.getError(),
                             mutationSummary.getMean(),
                             mutationSummary.getError(),
                             signalingSummary.getMean(),
                             signalingSummary.getError(),
                             competitionSummary.getMean(),
                             competitionSummary.getError());
    }

    /**
     * Returns the generation for which the diversity was recorded.
     *
     * @return the generation for which the diversity was recorded.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the summary for the growth rate.
     *
     * @return the summary for the growth rate.
     */
    public StatSummary getGrowthSummary() {
        return growthSummary;
    }

    /**
     * Returns the summary for the mutation survival rate.
     *
     * @return the summary for the mutation survival rate.
     */
    public StatSummary getMutationSummary() {
        return mutationSummary;
    }

    /**
     * Returns the summary for the BCR signaling survival rate.
     *
     * @return the summary for the BCR signaling survival rate.
     */
    public StatSummary getSignalingSummary() {
        return signalingSummary;
    }

    /**
     * Returns the summary for the T cell competition survival rate.
     *
     * @return the summary for the T cell competition survival rate.
     */
    public StatSummary getCompetitionSummary() {
        return competitionSummary;
    }
}
