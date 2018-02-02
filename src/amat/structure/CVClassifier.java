
package amat.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jam.math.IntUtil;

/**
 * Finds structural elements that are indentical across a collection
 * of structures and classifies each element as <em>conserved</em> or
 * <em>variable</em>.
 *
 * <p>Individual structural elements are compared for equality by the
 * {@link Structure#isMatch(Structure, int)} method.
 */
public final class CVClassifier {
    private final Structure[] structures;

    private final Set<Integer> conserved = new TreeSet<Integer>(); // Indexes of conserved elements
    private final Set<Integer> variable  = new TreeSet<Integer>(); // Indexes of variable elements

    // Classification for each element...
    private final List<CV> classification = new ArrayList<CV>();

    private CVClassifier(Collection<Structure> structures) {
        this.structures = structures.toArray(new Structure[0]);
    }

    /**
     * Creates a new classifier for a collection of structures.
     *
     * @param structures the structures to examine.
     *
     * @return a classifier that has classified each element in the
     * collection of structures.
     */
    public static CVClassifier classify(Collection<Structure> structures) {
        CVClassifier classifier = new CVClassifier(structures);
        classifier.classify();

        return classifier;
    }

    private void classify() {
        //
        // Sort structures by length; the shortest structure (in the
        // first position) then becomes the reference structure.  No
        // elements in positions beyond the length of the reference
        // structure can be conserved.
        //
        Arrays.sort(structures, Structure.LENGTH_COMPARATOR);
        Structure reference = structures[0];

        int refLength = reference.length();
        int maxLength = getMaximumLength();

        for (int elementIndex = 0; elementIndex < refLength; elementIndex++) {
            if (isConserved(reference, elementIndex))
                setConserved(elementIndex);
            else
                setVariable(elementIndex);
        }

        for (int elementIndex = refLength; elementIndex < maxLength; elementIndex++)
            setVariable(elementIndex);

        assert classification.size() == maxLength;
        assert conserved.size() + variable.size() == maxLength;
    }

    private int getMaximumLength() {
        return structures[structures.length - 1].length();
    }

    private boolean isConserved(Structure reference, int elementIndex) {
        for (int structureIndex = 1; structureIndex < structures.length; structureIndex++)
            if (!reference.isMatch(structures[structureIndex], elementIndex))
                return false;

        return true;
    }

    private void setConserved(int elementIndex) {
        conserved.add(elementIndex);
        classification.add(CV.CONSERVED);
    }
        
    private void setVariable(int elementIndex) {
        variable.add(elementIndex);
        classification.add(CV.VARIABLE);
    }

    /**
     * Returns the number of conserved elements.
     *
     * @return the number of conserved elements.
     */
    public int countConserved() {
        return conserved.size();
    }

    /**
     * Returns the number of variable elements.
     *
     * @return the number of variable elements.
     */
    public int countVariable() {
        return variable.size();
    }

    /**
     * Counts the number of matching elements in the conserved region
     * of two structures.
     *
     * @param s1 the first structure to examine.
     *
     * @param s2 the second structure to examine.
     *
     * @return the number of matching elements in the conserved region
     * of the two structures.
     */
    public int countMatchingConserved(Structure s1, Structure s2) {
        return countMatching(conserved, s1, s2);
    }

    /**
     * Counts the number of matching elements in the variable region
     * of two structures.
     *
     * @param s1 the first structure to examine.
     *
     * @param s2 the second structure to examine.
     *
     * @return the number of matching elements in the variable region
     * of the two structures.
     */
    public int countMatchingVariable(Structure s1, Structure s2) {
        return countMatching(variable, s1, s2);
    }

    private static int countMatching(Set<Integer> indexes, Structure s1, Structure s2) {
        int total = 0;

        for (int index : indexes)
            if (s1.isMatch(s2, index))
                ++total;

        return total;
    }

    /**
     * Returns the classification for a specific structural element.
     *
     * @param index the zero-offset index of the element to examine.
     *
     * @return the classification for the specified structural element.
     */
    public CV getClassification(int index) {
        return classification.get(index);
    }
        
    /**
     * Creates a map from index type to index components.
     *
     * @return a map whose values are the epitope indexes with types
     * equal to the key value.
     */
    public Map<CV, int[]> mapIndexes() {
        Map<CV, int[]> map = new EnumMap<CV, int[]>(CV.class);

        map.put(CV.CONSERVED, getConserved());
        map.put(CV.VARIABLE,  getVariable());

        return map;
    }

    /**
     * Returns the indexes of elements of a given type.
     *
     * @param cv the type of indexes to return (conserved or
     * variable).
     *
     * @return an array containing the indexes of elements having the
     * specified type.
     */
    public int[] getIndexes(CV cv) {
        return mapIndexes().get(cv);
    }

    /**
     * Returns the indexes of elements that are identical across all
     * structures.
     *
     * @return an array containing the indexes of elements that are
     * identical across all structures.
     */
    public int[] getConserved() {
        return IntUtil.toArray(conserved);
    }

    /**
     * Returns the indexes of elements that differ between structures.
     *
     * @return an array containing the indexes of elements that differ
     * between structures.
     */
    public int[] getVariable() {
        return IntUtil.toArray(variable);
    }

    /**
     * Identifies conserved structural elements.
     *
     * @param index the zero-offset index of the element to examine.
     *
     * @return {@code true} iff the specified element is conserved
     * across all structures.
     */
    public boolean isConserved(int index) {
        return classification.get(index).equals(CV.CONSERVED);
    }

    /**
     * Identifies variable structural elements.
     *
     * @param index the zero-offset index of the element to examine.
     *
     * @return {@code true} iff the specified element is variable
     * between structures.
     */
    public boolean isVariable(int index) {
        return classification.get(index).equals(CV.VARIABLE);
    }
}
