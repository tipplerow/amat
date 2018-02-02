
package amat.structure;

import jam.math.Distance;
import jam.math.DoubleComparator;
import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Marks structures as numeric.
 */
public abstract class NumericStructure extends Structure {
    /**
     * Returns a numeric representation of one structural element.
     *
     * @param index the (zero-offset) index of the desired element.
     *
     * @return the numeric representation of the specified structural
     * element.
     */
    public abstract double asNumeric(int index);

    /**
     * Returns a read-only view of the numeric representation for this
     * structure.
     *
     * @return a read-only view of the numeric representation for this
     * structure.
     */
    public VectorView asNumeric() {
	JamVector result = new JamVector(length());

	for (int index = 0; index < result.length(); index++)
	    result.set(index, asNumeric(index));

	return result;
    }

    /**
     * Successfully validates this structure as numeric.
     */
    public final void validateNumeric() {
        //
        // This instance is numeric, so this is a no-op...
        //
    }

    /**
     * Computes the Euclidean distance between this structure and
     * another numeric structure.
     *
     * @param that the reference structure.
     *
     * @return the Euclidean distance between this structure and
     * the reference structure.
     */
    public double euclideanDistance(NumericStructure that) {
        return Distance.euclidean(this.asNumeric(), that.asNumeric());
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
     * specified index (is at least that long), the input structure is
     * another numeric structure, and the numeric representation of
     * its indexed element is equal to the numeric representation of
     * the corresponding element in this structure within the default
     * floating-point tolerance.
     */
    @Override public boolean isMatch(Structure struct, int index) {
        return (struct instanceof NumericStructure) && isNumericMatch((NumericStructure) struct, index);
    }

    private boolean isNumericMatch(NumericStructure that, int index) {
        if (index >= this.length())
            return false;

        if (index >= that.length())
            return false;

        double thisElement = this.asNumeric(index);
        double thatElement = that.asNumeric(index);

        return DoubleComparator.DEFAULT.equals(thisElement, thatElement);
    }

    @Override public final boolean isNumeric() {
        return true;
    }

    @Override public boolean isDiscrete() {
        return false;
    }
}
