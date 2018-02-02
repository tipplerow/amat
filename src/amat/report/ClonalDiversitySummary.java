
package amat.report;

import java.util.Collection;

import jam.math.StatSummary;

/**
 * Copmutes and stores summary statistics for the clonal diversity at
 * a particular generation over all germinal centers in a simulation.
 */
public final class ClonalDiversitySummary {
    private final int generation;
    private final StatSummary countSummary;
    private final StatSummary entropySummary;

    private ClonalDiversitySummary(int generation,
                                   StatSummary countSummary,
                                   StatSummary entropySummary) {
        this.generation = generation;
        this.countSummary = countSummary;
        this.entropySummary = entropySummary;
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
    public static ClonalDiversitySummary compute(int generation, Collection<ClonalDiversityDetail> details) {
        StatSummary countSummary   = StatSummary.compute(details, x -> Double.valueOf(x.getGermlineCount()));
        StatSummary entropySummary = StatSummary.compute(details, x -> x.getGermlineEntropy());

        return new ClonalDiversitySummary(generation, countSummary, entropySummary);
    }

    /**
     * Returns the header line for summary report files.
     *
     * @return the header line for summary report files.
     */
    public static String header() {
        return "generation"
            + ",countMean"
            + ",countErr"
            + ",countMedian"
            + ",countQ1"
            + ",countQ3"
            + ",entropyMean"
            + ",entropyErr"
            + ",entropyMedian"
            + ",entropyQ1"
            + ",entropyQ3";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",
                             generation,
                             countSummary.getMean(),
                             countSummary.getError(),
                             countSummary.getMedian(),
                             countSummary.getQuartile1(),
                             countSummary.getQuartile3(),
                             entropySummary.getMean(),
                             entropySummary.getError(),
                             entropySummary.getMedian(),
                             entropySummary.getQuartile1(),
                             entropySummary.getQuartile3());
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
     * Returns the summary for the number of unique germlines.
     *
     * @return the summary for the number of unique germlines.
     */
    public StatSummary getCountSummary() {
        return countSummary;
    }

    /**
     * Returns the summary for the germline entropy.
     *
     * @return the summary for the germline entropy.
     */
    public StatSummary getEntropySummary() {
        return entropySummary;
    }
}
