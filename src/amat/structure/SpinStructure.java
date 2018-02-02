
package amat.structure;

import jam.math.JamRandom;
import jam.spin.Spin;
import jam.spin.SpinVector;

/**
 * Represents structures composed of binary spin states.
 */
public final class SpinStructure extends DiscreteStructure {
    private final SpinVector spins;

    private SpinStructure(SpinVector spins, boolean copy) {
        if (spins == null)
            throw new NullPointerException("Missing spin vector.");

        this.spins = copy ? spins.copy() : spins;
    }

    /**
     * Returns the constant spin value comprising conserved regions.
     */
    public static final Spin CONSERVED = Spin.DOWN;

    /**
     * Creates a new spin structure with fixed spin states.
     *
     * @param spins the boolean states.
     */
    public SpinStructure(SpinVector spins) {
	this(spins, true);
    }

    /**
     * Creates a purely conserved spin structure.
     *
     * @param length the number of conserved spins.
     *
     * @return a purely conserved spin structure with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static SpinStructure conserved(int length) {
        return generate(length, 0);
    }

    /**
     * Creates a purely variable spin structure.
     *
     * @param length the number of variable spins.
     *
     * @return a purely variable spin structure with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static SpinStructure variable(int length) {
        return generate(0, length);
    }

    /**
     * Creates a spin structure containing conserved and variable
     * regions.
     *
     * @param conservedLength the number of spins in the conserved
     * region; may be zero if the variable length is positive.
     *
     * @param variableLength the number of spins in the variable
     * region; may be zero if the conserved length is positive.
     *
     * @return a new spin structure containing a conserved region and
     * a randomly-generated variable region with the specified lengths.
     *
     * @throws IllegalArgumentException unless the total length is
     * positive and each component length is non-negative.
     */
    public static SpinStructure generate(int conservedLength, int variableLength) {
	validateLengths(conservedLength, variableLength);

        JamRandom  random = JamRandom.global();
	SpinVector vector = new SpinVector(computeLength(conservedLength, variableLength));

	for (int index = 0; index < conservedLength; index++)
	    vector.set(index, CONSERVED);

	for (int index = conservedLength; index < vector.length(); index++)
	    vector.set(index, Spin.next(random));

        return new SpinStructure(vector, false);
    }

    /**
     * Creates a copy of the underlying spin vector.
     *
     * @return a copy of the underlying spin vector.
     */
    public SpinVector copySpins() {
        return spins.copy();
    }

    /**
     * Returns the spin at a given location within this structure.
     *
     * @param index the index of the spin to return.
     *
     * @return the spin at the given location within this structure.
     *
     * @throws IllegalArgumentException unless the index is in bounds.
     */
    public Spin get(int index) {
        return spins.get(index);
    }

    @Override public String format() {
        return String.format("SpinStructure(%s)", spins.format());
    }

    @Override public StructureType getType() {
        return StructureType.SPIN;
    }

    @Override public int length() {
        return spins.length();
    }

    /**
     * Defines the mutational distance between this structure and
     * another spin structure as the Hamming distance between them.
     *
     * @param struct the reference structure.
     *
     * @return the Hamming distance between this structure and
     * the reference spin structure.
     *
     * @throws IllegalArgumentException unless the input structure is
     * a spin structure.
     */
    @Override public double mutationalDistance(Structure struct) {
	validateType(struct);
	return hammingDistance((SpinStructure) struct);
    }

    @Override public int cardinality() {
	return 2;
    }

    @Override public int asOrdinal(int index) {
	return get(index).ordinal();
    }

    @Override public double asNumeric(int index) {
	return get(index).doubleValue();
    }

    @Override public boolean equals(Object that) {
        return (that instanceof SpinStructure) && equalsSpinStructure((SpinStructure) that);
    }

    private boolean equalsSpinStructure(SpinStructure that) {
        return this.spins.equals(that.spins);
    }

    @Override public int hashCode() {
        return spins.hashCode();
    }

    @Override public String toString() {
        return format();
    }
}
