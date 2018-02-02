
package amat.structure;

import java.util.Comparator;

import jam.lang.Formatted;
import jam.lang.JamException;

/**
 * Defines a structural represenatation for lymphocyte receptors and
 * the epitopes to which they bind.
 */
public abstract class Structure implements Formatted {
    private static final class LengthComparator implements Comparator<Structure> {
        @Override public int compare(Structure s1, Structure s2) {
            return Integer.compare(s1.length(), s2.length());
        }
    }

    /**
     * A comparator to sort structures in ascending order by length.
     */
    public static final Comparator<Structure> LENGTH_COMPARATOR = new LengthComparator();

    /**
     * Creates a new structure from a string representation.
     *
     * <p>Examples of valid string representations are:
     *
     * <pre>
       SpinStructure(+--+--++)
       ShapeStructure(1.0, 2.0, 3.0)
       BitStructure(0000 1111 0011 1100)
     * </pre>
     *
     * The string must contain the class name followed by the
     * class-specific structural representation in parentheses.
     * Note that the structural representation may contain white
     * space to aid in reading; all white space will be ignored.
     *
     * @param str the string to parse.
     *
     * @return the structure defined by the given string.
     *
     * @throws RuntimeException unless the string is a properly
     * formatted representation of a structure.
     */
    public static Structure parse(String str) {
        return StructureParser.parse(str);
    }

    /**
     * Returns the enumerated type of this structure.
     *
     * @return the enumerated type of this structure.
     */
    public abstract StructureType getType();

    /**
     * Returns the number of discrete elements or dimensions in this
     * structure.
     *
     * @return the number of discrete elements or dimensions in this
     * structure.
     */
    public abstract int length();

    /**
     * Computes the mutational distance between this structure and
     * another reference structure of the same runtime type.
     *
     * @param struct the reference structure.
     *
     * @return the mutational distance between this structure and
     * the input structure.
     *
     * @throws IllegalArgumentException if the input structure is a
     * different runtime type.
     */
    public abstract double mutationalDistance(Structure struct);

    /**
     * Identifies elements in another structure that match the
     * corresponding element in this structure. 
     *
     * @param struct the other structure to examine.
     *
     * @param index the (zero-offset) index of the element to examine.
     *
     * @return {@code true} iff this structure has an element with the
     * specified index (is at least that long) and the element matches
     * the corresponding element of the input structure.
     */
    public abstract boolean isMatch(Structure struct, int index);

    /**
     * Identifies structures composed of discrete elements.
     *
     * @return {@code true} iff this structure is composed of discrete
     * elements.
     */
    public abstract boolean isDiscrete();

    /**
     * Identifies structures that may be mapped to numeric vectors.
     *
     * @return {@code true} iff this structure may be mapped to a
     * numeric vector.
     */
    public abstract boolean isNumeric();

    /**
     * Computes the total length of a structure composed of conserved
     * and variable regions.
     *
     * @param conservedLength the number of conserved elements or
     * dimensions.
     *
     * @param variableLength the number of variable elements or
     * dimensions.
     *
     * @return the total length.
     */
    public static int computeLength(int conservedLength, int variableLength) {
        return conservedLength + variableLength;
    }

    /**
     * Ensures that another structure has the same length as this
     * structure.
     *
     * @param that the structure to validate.
     *
     * @throws IllegalArgumentException unless the input structure has
     * the same length as this structure.
     */
    public void validateLength(Structure that) {
	if (this.length() != that.length())
	    throw new IllegalArgumentException("Length mismatch.");
    }

    /**
     * Ensures that another structure has the same runtime type as
     * this structure.
     *
     * @param that the structure to validate.
     *
     * @throws IllegalArgumentException unless the input structure has
     * the same runtime type as this structure.
     */
    public void validateType(Structure that) {
	if (!this.getClass().equals(that.getClass()))
	    throw new IllegalArgumentException("Runtime type mismatch.");
    }

    /**
     * Validates the length of a structure.
     *
     * @param length the number of structural elements or dimensions.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static void validateLength(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");
    }

    /**
     * Validates the lengths of the conserved and variable regions in
     * a structure.
     *
     * @param conservedLength the number of conserved elements or
     * dimensions.
     *
     * @param variableLength the number of variable elements or
     * dimensions.
     *
     * @throws IllegalArgumentException if either length is negative
     * or if the total length (their sum) is not positive.
     */
    public static void validateLengths(int conservedLength, int variableLength) {
        if (conservedLength < 0)
            throw new IllegalArgumentException("Negative conserved length.");

        if (variableLength < 0)
            throw new IllegalArgumentException("Negative variable length.");

        validateLength(computeLength(conservedLength, variableLength));
    }

    /**
     * Ensures that this structure contains discrete structural elements.
     *
     * @throws RuntimeException unless this is a discrete structure.
     */
    public void validateDiscrete() {
        if (!isDiscrete())
            throw JamException.runtime("%s structure does not contain discrete elements.", getClass().getName());
    }

    /**
     * Ensures that this structure has a valid numeric representation.
     *
     * @throws RuntimeException unless this is a numeric structure.
     */
    public void validateNumeric() {
        if (!isNumeric())
            throw JamException.runtime("%s structure has no numeric representation.", getClass().getName());
    }

    @Override public String toString() {
        return format();
    }
}
