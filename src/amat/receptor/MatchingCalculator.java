
package amat.receptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jam.math.DoubleUtil;
import jam.util.CollectionUtil;

import amat.epitope.Epitope;
import amat.structure.CV;
import amat.structure.CVClassifier;
import amat.structure.Structure;

/**
 * Computes the fraction of receptor elements that match the
 * corresponding conserved or variable elements in the global 
 * epitope registry.
 */
public final class MatchingCalculator {
    private final Map<CV, int[]> indexMap;

    private static MatchingCalculator instance = null;

    private MatchingCalculator() {
        this.indexMap = Epitope.classify().mapIndexes();
    }

    /**
     * Returns the single calculator instance.
     *
     * @return the single calculator instance.
     */
    public static MatchingCalculator instance() {
        if (instance == null)
            instance = new MatchingCalculator();

        return instance;
    }

    /**
     * Computes the average fraction of receptor elements that exactly
     * match those in the conserved or variable region of the epitopes
     * present in the global epitope registry.
     *
     * @param receptor the receptor to analyze.
     *
     * @param cv the epitope region to analyze.
     *
     * @return the average fraction of receptor elements that exactly
     * match those in the specified region of the epitopes present in
     * the global epitope registry.
     */
    public double compute(Receptor receptor, CV cv) {
        return compute(receptor, Epitope.all(), cv);
    }

    /**
     * Computes the average fraction of receptor elements that exactly
     * match those in the conserved or variable region in a collection
     * of epitopes.
     *
     * @param receptor the receptor to analyze.
     *
     * @param epitopes the eptiopes to which fit will be measured.
     *
     * @param cv the epitope region to analyze.
     *
     * @return the average fraction of receptor elements that exactly
     * match those in the specified region of the specified epitopes.
     */
    public double compute(Receptor receptor, Collection<Epitope> epitopes, CV cv) {
        switch (cv) {
        case CONSERVED:
            // No need to average over epitopes, since the conserved region is identical...
            return compute(receptor, CollectionUtil.peek(epitopes), cv);

        case VARIABLE:
            return CollectionUtil.average(epitopes, epitope -> compute(receptor, epitope, cv));

        default:
            throw new IllegalStateException("Unknown element type.");
        }
    }

    /**
     * Computes the fraction of B cell receptor elements that exactly
     * match those in the conserved or variable region in an epitope.
     *
     * @param receptor the receptor to analyze.
     *
     * @param epitope the eptiope to which fit will be measured.
     *
     * @param cv the epitope region to analyze.
     *
     * @return the fraction of receptor elements that exactly match
     * the element of the epitope in the specified region.
     */
    public double compute(Receptor receptor, Epitope epitope, CV cv) {
        return compute(receptor, epitope, indexMap.get(cv));
    }

    private static double compute(Receptor receptor, Epitope epitope, int[] elements) {
        int matchCount = 0;

        Structure structEpi = epitope.getStructure();
        Structure structRec = receptor.getStructure();

        for (int element : elements)
            if (structRec.isMatch(structEpi, element))
                ++matchCount;

        return DoubleUtil.ratio(matchCount, elements.length);
    }
}
