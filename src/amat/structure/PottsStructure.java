
package amat.structure;

import java.util.Arrays;

import jam.lang.JamException;
import jam.math.JamRandom;
import jam.util.RegexUtil;

/**
 * Represents structures composed of discrete states (identified by an
 * ordinal index) with an arbitrary cardinality, like those in a Potts
 * model.
 *
 * <p><b>String representation.</b> Potts structures are represented
 * by the following alphabetic code: {@code 'A' = 0}, {@code 'B' = 1},
 * {@code 'C' = 2}, ...
 */
public final class PottsStructure extends DiscreteStructure {
    private final int cardinality;
    private final int hashCode;
    private final int[] elements;

    private PottsStructure(int cardinality, int[] elements, boolean copy) {
	validateElements(cardinality, elements);

	this.cardinality = cardinality;
	this.hashCode = Arrays.hashCode(elements);
        this.elements = copy ? Arrays.copyOf(elements, elements.length) : elements;
    }

    private static void validateCardinality(int cardinality) {
	if (cardinality < 2)
	    throw new IllegalArgumentException("Cardinality must be two or greater.");
    }

    private static void validateElements(int cardinality, int[] elements) {
	validateCardinality(cardinality);

        if (elements == null)
            throw new NullPointerException("Missing elements.");

	if (elements.length < 1)
	    throw new IllegalArgumentException("Empty elements.");

	for (int index = 0; index < elements.length; index++)
	    validateElement(cardinality, elements[index]);
    }

    private static void validateElement(int cardinality, int element) {
	if (element < 0)
	    throw new IllegalArgumentException("Negative element.");

	if (element >= cardinality)
	    throw new IllegalArgumentException("Element exceeds cardinality.");
    }

    /**
     * Returns the constant ordinal value comprising conserved regions.
     */
    public static final int CONSERVED = 0;

    /**
     * Creates a new Potts structure with fixed states.
     *
     * @param cardinality the number of unique element states.
     *
     * @param elements the ordinal element values.
     *
     * @throws IllegalArgumentException unless the cardinality is two
     * or greater and the elements satisfy the cardinality conditions
     * (are in the range {@code [0, cardinality - 1]}.
     */
    public PottsStructure(int cardinality, int[] elements) {
	this(cardinality, elements, true);
    }

    /**
     * Creates a purely conserved Potts structure.
     *
     * @param cardinality the number of unique element states.
     *
     * @param length the number of conserved bits.
     *
     * @return a purely conserved Potts structure with the specified
     * length and cardinality.
     *
     * @throws IllegalArgumentException unless the cardinality is two
     * or greater and the length is positive.
     */
    public static PottsStructure conserved(int cardinality, int length) {
        return generate(cardinality, length, 0);
    }

    /**
     * Creates a purely variable Potts structure.
     *
     * @param cardinality the number of unique element states.
     *
     * @param length the number of variable bits.
     *
     * @return a purely variable Potts structure with the specified
     * length and cardinality.
     *
     * @throws IllegalArgumentException unless the cardinality is two
     * or greater and the length is positive.
     */
    public static PottsStructure variable(int cardinality, int length) {
        return generate(cardinality, 0, length);
    }

    /**
     * Generates a Potts structure containing conserved and variable
     * regions.
     *
     * @param cardinality the number of unique element states.
     *
     * @param conservedLength the number of bits in the conserved
     * region; may be zero if the variable length is positive.
     *
     * @param variableLength the number of bits in the variable
     * region; may be zero if the conserved length is positive.
     *
     * @return a new Potts structure containing a conserved region and
     * a randomly-generated variable region with the specified lengths.
     *
     * @throws IllegalArgumentException unless the cardinality is two
     * or greater, the total length is positive, and each component
     * length is non-negative.
     */
    public static PottsStructure generate(int cardinality, int conservedLength, int variableLength) {
	validateCardinality(cardinality);
	validateLengths(conservedLength, variableLength);

	int length = computeLength(conservedLength, variableLength);
	int[] elements = new int[length];
        JamRandom random = JamRandom.global();

	for (int index = 0; index < conservedLength; index++)
	    elements[index] = CONSERVED;

	for (int index = conservedLength; index < elements.length; index++)
	    elements[index] = random.nextInt(cardinality);

        return new PottsStructure(cardinality, elements, false);
    }

    /**
     * Creates a new Potts structure from its string representation.
     *
     * <p>The string representation must contain the cardinality, a
     * semicolon, and the alphabetic code for each discrete element.
     * The element specification may contain white space to aid in
     * reading.  For example: {@code 4; AAAA ABCD}.
     *
     * @param structRep the structural representation as described
     * above.
     *
     * @return a new Potts structure corresponding to the input
     * representation.
     *
     * @throws RuntimeException unless the structural representation
     * is properly formatted.
     */
    public static PottsStructure parse(String structRep) {
	String[] split1 = RegexUtil.SEMICOLON.split(structRep);

	if (split1.length != 2)
	    throw JamException.runtime("Invalid structure: [%s].", structRep);

	String cardField = split1[0].trim();
	String elemField = split1[1].trim();

	int   cardinality = Integer.parseInt(cardField);
	int[] elements    = new int[elemField.length()];

	for (int index = 0; index < elements.length; index++)
	    elements[index] = intValue(elemField.charAt(index));

	return new PottsStructure(cardinality, elements, false);
    }

    private static int intValue(char element) {
        return element - 'A';
    }

    private static char charValue(int element) {
        return (char) ('A' + element);
    }

    @Override public String format() {
        return String.format("PottsStructure(%s)", formatStructure());
    }

    private String formatStructure() {
	StringBuilder builder = new StringBuilder();

	builder.append(cardinality);
	builder.append(";");

	for (int element : elements)
	    builder.append(charValue(element));

	return builder.toString();
    }

    @Override public StructureType getType() {
        return StructureType.POTTS;
    }

    @Override public int length() {
        return elements.length;
    }

    /**
     * Defines the mutational distance between this structure and
     * another Potts structure as the Hamming distance between them.
     *
     * @param struct the reference structure.
     *
     * @return the Hamming distance between this structure and
     * the reference Potts structure.
     *
     * @throws IllegalArgumentException unless the input structure is
     * a Potts structure.
     */
    @Override public double mutationalDistance(Structure struct) {
	validateType(struct);
	return hammingDistance((PottsStructure) struct);
    }

    @Override public int cardinality() {
	return cardinality;
    }

    @Override public int asOrdinal(int index) {
	return elements[index];
    }

    @Override public int[] asOrdinal() {
	return Arrays.copyOf(elements, elements.length);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof PottsStructure) && equalsPottsStructure((PottsStructure) that);
    }

    private boolean equalsPottsStructure(PottsStructure that) {
        return this.cardinality == that.cardinality && Arrays.equals(this.elements, that.elements);
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public String toString() {
        return format();
    }
}
