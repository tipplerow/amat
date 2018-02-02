
package amat.report;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import jam.util.MapUtil;

import amat.bcell.ClonalDiversity;
import amat.germinal.GerminalCenter;

/**
 * Records the clonal diversity for a particular generation for a
 * given germinal center.
 */
public final class ClonalDiversityDetail {
    private final int trialIndex;
    private final int generation;
    private final ClonalDiversity diversity;

    private ClonalDiversityDetail(int trialIndex,
                                  int generation,
                                  ClonalDiversity diversity) {
        this.trialIndex = trialIndex;
        this.generation = generation;
        this.diversity  = diversity;
    }

    /**
     * Creates a new diversity record for a particular generation of a
     * given germinal center.
     *
     * @param gc the germinal center of interest.
     *
     * @param generation the generation of interest.
     *
     * @return the diversity record for the specified germinal center
     * and generation.
     */
    public static ClonalDiversityDetail compute(GerminalCenter gc, int generation) {
        return new ClonalDiversityDetail(gc.getTrialIndex(), generation, 
                                         ClonalDiversity.compute(gc.viewActiveCells(generation)));
    }

    /**
     * Returns the header line for detail report files.
     *
     * @return the header line for detail report files.
     */
    public static String header() {
        return "trialIndex,generation,founderCount,founderEntropy";
    }

    /**
     * Formats this record for output to a report file.
     *
     * @return the string representation of this record to write to
     * report files.
     */
    public String format() {
        return String.format("%d,%d,%d,%f", trialIndex, generation, diversity.getCount(), diversity.getEntropy());
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
     * Returns the generation for which the diversity was recorded.
     *
     * @return the generation for which the diversity was recorded.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the clonal diversity (quantified by the germline count)
     * at the recorded generation.
     *
     * @return the clonal diversity (quantified by the germline count)
     * at the recorded generation.
     */
    public int getGermlineCount() {
        return diversity.getCount();
    }

    /**
     * Returns the clonal diversity (quantified by the germline entropy)
     * at the recorded generation.
     *
     * @return the clonal diversity (quantified by the germline entropy)
     * at the recorded generation.
     */
    public double getGermlineEntropy() {
        return diversity.getEntropy();
    }

    /**
     * Groups a collection of diversity records by generation.
     *
     * @param records the records to group.
     *
     * @return a map containing the records grouped by generation.
     */
    public static SortedMap<Integer, Collection<ClonalDiversityDetail>> 
        groupByGeneration(Collection<ClonalDiversityDetail> records) {

        SortedMap<Integer, Collection<ClonalDiversityDetail>> grouped =
            new TreeMap<Integer, Collection<ClonalDiversityDetail>>();

        MapUtil.group(grouped, records, x -> x.getGeneration());
        return grouped;
    }
}
