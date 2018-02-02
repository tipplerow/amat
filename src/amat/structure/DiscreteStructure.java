
package amat.structure;

/**
 * Base class for structures composed of discrete elements.
 *
 * <p>Discrete elements are uniquely identified by an ordinal index
 * ranging from {@code 0} to {@code N - 1}, where {@code N} is the
 * cardinality of the structure.
 *
 * <p>All discrete elements have a valid integer representation, and
 * integers are valid floating-point values, so discrete structures
 * are also numeric structures.
 */
public abstract class DiscreteStructure extends NumericStructure {
    /**
     * Returns the number of unique discrete structural elements.
     *
     * @return the number of unique discrete structural elements.
     */
    public abstract int cardinality();

    /**
     * Returns the ordinal value of one structural element.
     *
     * @param index the (zero-offset) index of the desired element.
     *
     * @return the ordinal value of the specified structural element.
     */
    public abstract int asOrdinal(int index);

    /**
     * Returns the ordinal values of the structural elements.
     *
     * @return an array containing the ordinal value of each
     * structural element.
     */
    public int[] asOrdinal() {
	int[] result = new int[length()];

	for (int index = 0; index < result.length; index++)
	    result[index] = asOrdinal(index);

	return result;
    }

    /**
     * Computes the Hamming distance between this structure and another 
     * discrete structure: the number of structural elements that differ 
     * between the two structures.
     *
     * @param struct the reference structure.
     *
     * @return the Hamming distance between this structure and the
     * reference structure.
     *
     * @throws IllegalArgumentException unless the input structure is
     * a discrete structure with the same length as this structure.
     */
    public int hammingDistance(DiscreteStructure struct) {
	validateLength(struct);
	int result = 0;

	for (int index = 0; index < length(); index++)
	    if (!isMatch(struct, index))
		++result;

	return result;
    }

    /**
     * Identifies elements in another structure that match the
     * corresponding element in this structure. 
     *
     * @param struct the other structure to examine.
     *
     * @param index the (zero-offset) index of the element to examine.
     *
     * @return {@code true} iff this structure has an element with the
     * specified index (is at least that long), the input structure
     * has the same runtime type as this structure, and the ordinal
     * value of its indexed element is equal to the ordinal value of
     * the corresponding element in this structure.
     */
    @Override public boolean isMatch(Structure struct, int index) {
        return getClass().equals(struct.getClass()) && isDiscreteMatch((DiscreteStructure) struct, index);
    }

    private boolean isDiscreteMatch(DiscreteStructure that, int index) {
        if (index >= this.length())
            return false;

        if (index >= that.length())
            return false;

	return this.asOrdinal(index) == that.asOrdinal(index);
    }

    @Override public final boolean isDiscrete() {
        return true;
    }

    @Override public double asNumeric(int index) {
	return asOrdinal(index);
    }
}
