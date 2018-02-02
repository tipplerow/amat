
package amat.structure;

import jam.lang.JamBoolean;
import jam.math.JamRandom;
import jam.vector.BitVector;

/**
 * Represents structures composed of boolean bit states.
 */
public final class BitStructure extends DiscreteStructure {
    private final BitVector bits;

    private BitStructure(BitVector bits, boolean copy) {
        if (bits == null)
            throw new NullPointerException("Missing bit vector.");

        this.bits = copy ? bits.copy() : bits;
    }

    /**
     * Returns the constant bit value comprising conserved regions.
     */
    public static final boolean CONSERVED = false;

    /**
     * Creates a new bit structure with fixed boolean states.
     *
     * @param bits the boolean states.
     */
    public BitStructure(BitVector bits) {
	this(bits, true);
    }

    /**
     * Creates a purely conserved bit structure.
     *
     * @param length the number of conserved bits.
     *
     * @return a purely conserved bit structure with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static BitStructure conserved(int length) {
        return generate(length, 0);
    }

    /**
     * Creates a purely variable bit structure.
     *
     * @param length the number of variable bits.
     *
     * @return a purely variable bit structure with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static BitStructure variable(int length) {
        return generate(0, length);
    }

    /**
     * Generates a bit structure containing conserved and variable
     * regions.
     *
     * @param conservedLength the number of bits in the conserved
     * region; may be zero if the variable length is positive.
     *
     * @param variableLength the number of bits in the variable
     * region; may be zero if the conserved length is positive.
     *
     * @return a new bit structure containing a conserved region and
     * a randomly-generated variable region with the specified lengths.
     *
     * @throws IllegalArgumentException unless the total length is
     * positive and each component length is non-negative.
     */
    public static BitStructure generate(int conservedLength, int variableLength) {
	validateLengths(conservedLength, variableLength);

        JamRandom random = JamRandom.global();
	BitVector vector = new BitVector(computeLength(conservedLength, variableLength));

	for (int index = 0; index < conservedLength; index++)
	    vector.set(index, CONSERVED);

	for (int index = conservedLength; index < vector.length(); index++)
	    vector.set(index, random.nextBoolean());

        return new BitStructure(vector, false);
    }

    /**
     * Creates a copy of the underlying bit vector.
     
     * @return a copy of the underlying bit vector.
     */
    public BitVector copyBits() {
        return bits.copy();
    }

    /**
     * Returns the bit at a given location within this structure.
     *
     * @param index the index of the bit to return.
     *
     * @return the bit at the given location within this structure.
     *
     * @throws IllegalArgumentException unless the index is in bounds.
     */
    public boolean get(int index) {
        return bits.get(index);
    }

    @Override public String format() {
        return String.format("BitStructure(%s)", bits.format());
    }

    @Override public StructureType getType() {
        return StructureType.BIT;
    }

    @Override public int length() {
        return bits.length();
    }

    /**
     * Defines the mutational distance between this structure and
     * another bit structure as the Hamming distance between them.
     *
     * @param struct the reference structure.
     *
     * @return the Hamming distance between this structure and
     * the reference bit structure.
     *
     * @throws IllegalArgumentException unless the input structure is
     * a bit structure.
     */
    @Override public double mutationalDistance(Structure struct) {
	validateType(struct);
	return hammingDistance((BitStructure) struct);
    }

    @Override public int cardinality() {
	return 2;
    }

    @Override public int asOrdinal(int index) {
	return JamBoolean.intValue(bits.get(index));
    }

    @Override public boolean equals(Object that) {
        return (that instanceof BitStructure) && equalsBitStructure((BitStructure) that);
    }

    private boolean equalsBitStructure(BitStructure that) {
        return this.bits.equals(that.bits);
    }

    @Override public int hashCode() {
        return bits.hashCode();
    }

    @Override public String toString() {
        return format();
    }
}
