
package amat.report;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import jam.math.DoubleUtil;

import amat.bcell.BCell;
import amat.epitope.Epitope;
import amat.structure.CV;
import amat.structure.CVClassifier;
import amat.structure.Structure;
import amat.vaccine.VaccinationSchedule;

/**
 * Stores the fraction of B cell receptor elements that exactly match
 * the corresponding elements in the conserved and variable regions of
 * the epitopes presented during affinity maturation.
 */
public final class MatchingRecord {
    private final double total;
    private final double variable;
    private final double conserved;

    private static Map<CV, int[]> indexMap = null;

    private MatchingRecord(double total, double variable, double conserved) {
        this.total = total;
        this.variable = variable;
        this.conserved = conserved;
    }

    /**
     * Generates a matching record averaged over the epitope footprint
     * (the epitopes administered on or before the specified germinal
     * center cycle).
     *
     * @param bcell the bcell to be analyzed.
     *
     * @param cycle the germinal center cycle from which the footprint
     * will be taken.
     *
     * @return a record containing the fraction of matching elements
     * averaged over the epitope footprint at the specified germinal
     * center cycle.
     */
    public static MatchingRecord compute(BCell bcell, int cycle) {
        return compute(bcell, VaccinationSchedule.global().getEpitopeFootprint(cycle));
    }

    /**
     * Generates a matching record for a single epitope.
     *
     * @param bcell the bcell to be analyzed.
     *
     * @param epitope the epitope to match.
     *
     * @return a record containing the fraction of matching elements
     * between the specified B cell and epitope.
     */
    public static MatchingRecord compute(BCell bcell, Epitope epitope) {
        return compute(bcell, Arrays.asList(epitope));
    }

    /**
     * Generates a matching record averaged across a collection of
     * epitopes.
     *
     * @param bcell the bcell to be analyzed.
     *
     * @param epitopes the epitopes to match.
     *
     * @return a record containing the fraction of matching elements
     * averaged across the specified epitopes.
     */
    public static MatchingRecord compute(BCell bcell, Collection<Epitope> epitopes) {
        int variableMatch = 0;
        int conservedMatch = 0;

        int variableElements = 0;
        int conservedElements = 0;

        int[] indexVariable = getIndexMap().get(CV.VARIABLE);
        int[] indexConserved = getIndexMap().get(CV.CONSERVED);

        for (Epitope epitope : epitopes) {
            Structure structEpi = epitope.getStructure();
            Structure structBCR = bcell.getReceptor().getStructure();

            for (int index : indexVariable) {
                ++variableElements;

                if (structBCR.isMatch(structEpi, index))
                    ++variableMatch;
            }

            for (int index : indexConserved) {
                ++conservedElements;

                if (structBCR.isMatch(structEpi, index))
                    ++conservedMatch;
            }
        }

        int totalMatch = variableMatch + conservedMatch;
        int totalElements = variableElements + conservedElements;

        double totalFrac = DoubleUtil.ratio(totalMatch, totalElements);
        double variableFrac = DoubleUtil.ratio(variableMatch, variableElements);
        double conservedFrac = DoubleUtil.ratio(conservedMatch, conservedElements);

        return new MatchingRecord(totalFrac, variableFrac, conservedFrac);
    }

    private static Map<CV, int[]> getIndexMap() {
        if (indexMap == null)
            indexMap = Epitope.classify().mapIndexes();

        return indexMap;
    }

    /**
     * Returns the fraction of B cell receptor elements that match
     * epitope elements in a given region.
     *
     * @param cv the enumerated epitope region.
     *
     * @return the fraction of B cell receptor elements that match
     * epitope elements in the specified region.
     */
    public double get(CV cv) {
        switch (cv) {
        case CONSERVED:
            return getConserved();

        case VARIABLE:
            return getVariable();

        default:
            throw new IllegalStateException("Unknown epitope region.");
        }
    }

    /**
     * Returns the fraction of B cell receptor elements that match
     * conserved epitope elements.
     *
     * @return the fraction of B cell receptor elements that match
     * conserved epitope elements.
     */
    public double getConserved() {
        return conserved;
    }

    /**
     * Returns the fraction of B cell receptor elements that match
     * variable epitope elements.
     *
     * @return the fraction of B cell receptor elements that match
     * variable epitope elements.
     */
    public double getVariable() {
        return variable;
    }

    /**
     * Returns the total fraction of matching B cell receptor
     * elements.
     *
     * @return the total fraction of matching B cell receptor
     * elements.
     */
    public double getTotal() {
        return total;
    }
}
