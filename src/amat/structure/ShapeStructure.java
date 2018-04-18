
package amat.structure;

import java.util.Arrays;

import jam.math.Discretization;
import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Represents structures as points in a Euclidean shape space.
 *
 * <p>To facilitate the identification of equivalent shapes, the shape
 * space is actually discretized and mapped onto a fine lattice with a
 * lattice spacing equal to {@link ShapeStructure#RESOLUTION}.  All
 * continuous coordinates are mapped to the nearest lattice point.
 */
public final class ShapeStructure extends NumericStructure {
    private final int       hashCode;     // Pre-computed hash code...
    private final long[]    latticeCoord; // The discrete coordinates...
    private final JamVector doubleCoord;  // The continuous coordinates...

    /**
     * The lattice spacing for the discretization of the shape space.
     */
    public static final double RESOLUTION = 0.001;

    private static final Discretization DISCRETIZATION =
        Discretization.withResolution(RESOLUTION);

    private ShapeStructure(long[] latticeCoord) {
        this.hashCode     = Arrays.hashCode(latticeCoord);
        this.latticeCoord = latticeCoord;
        this.doubleCoord  = JamVector.copyOf(DISCRETIZATION.asContinuous(latticeCoord));
    }

    /**
     * The constant shape-space coordinate comprising conserved
     * regions.
     */
    public static final double CONSERVED = 0.0;

    /**
     * Creates a new shape structure with fixed coordinates.
     *
     * @param shape the fixed shape-space coordinates.
     */
    public ShapeStructure(VectorView shape) {
        this(DISCRETIZATION.asDiscrete(shape.toNumeric()));
    }

    /**
     * Creates a purely conserved shape structure.
     *
     * @param length the number of conserved dimensions.
     *
     * @return a purely conserved shape structure with the given
     * dimension.
     */
    public static ShapeStructure conserved(int length) {
        return new ShapeStructure(new JamVector(length, CONSERVED));
    }

    /**
     * Returns the shape-space coordinate for a specified dimension.
     *
     * @param index the index of the shape-space dimension.
     *
     * @return the shape-space coordinate in the specified dimension.
     *
     * @throws IllegalArgumentException unless the index is in bounds.
     */
    public double get(int index) {
        return doubleCoord.getDouble(index);
    }

    @Override public String format() {
	StringBuilder builder = new StringBuilder();

	builder.append("ShapeStructure(");
	builder.append(doubleCoord.get(0));

	for (int index = 1; index < doubleCoord.length(); index++) {
	    builder.append(", ");
	    builder.append(doubleCoord.get(index));
	}
	
	builder.append(")");
	return builder.toString();
    }

    @Override public StructureType getType() {
        return StructureType.SHAPE;
    }

    @Override public int length() {
        return doubleCoord.length();
    }

    @Override public double mutationalDistance(Structure that) {
	if (that instanceof ShapeStructure)
	    return this.euclideanDistance((ShapeStructure) that);
	else
	    throw new IllegalArgumentException("ShapeStructure required.");
    }

    @Override public double asNumeric(int index) {
        return doubleCoord.get(index);
    }

    @Override public VectorView asNumeric() {
        return doubleCoord;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof ShapeStructure) && equalsShapeStructure((ShapeStructure) that);
    }

    private boolean equalsShapeStructure(ShapeStructure that) {
        return Arrays.equals(this.latticeCoord, that.latticeCoord);
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public String toString() {
        return format();
    }
}
