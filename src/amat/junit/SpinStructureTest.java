
package amat.junit;

import java.util.ArrayList;
import java.util.Arrays;

import jam.spin.SpinVector;
import jam.vector.JamVector;

import amat.structure.SpinStructure;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class SpinStructureTest extends StructureTestBase {
    @Test public void testEquals() {
        SpinVector v1 = SpinVector.parse("+-+-");
        SpinVector v2 = SpinVector.parse("--++");

        SpinStructure s1a = new SpinStructure(v1);
        SpinStructure s1b = new SpinStructure(v1);
        SpinStructure s2  = new SpinStructure(v2);

        assertTrue(s1a.equals(s1b));
        assertFalse(s1a.equals(s2));
        assertTrue(s2.equals(s2));
    }

    @Test public void testFormatParse() {
        SpinStructure s1 = new SpinStructure(SpinVector.parse("--++--++"));
        SpinStructure s2 = (SpinStructure) Structure.parse("SpinStructure(- - + + - - + +)");
        SpinStructure s3 = (SpinStructure) Structure.parse(s1.format());

        assertEquals(s1, s2);
        assertEquals(s1, s3);
    }

    @Test public void testHashCode() {
        SpinVector v1 = SpinVector.parse("+-+-");
        SpinVector v2 = SpinVector.parse("--++");

        SpinStructure s1a = new SpinStructure(v1);
        SpinStructure s1b = new SpinStructure(v1);
        SpinStructure s2  = new SpinStructure(v2);

        assertTrue(s1a.hashCode() == s1b.hashCode());
        assertTrue(s1a.hashCode() != s2.hashCode());
    }

    @Test public void testMutationalDistance() {
        SpinStructure s1 = (SpinStructure) Structure.parse("SpinStructure(---- ++++)");
        SpinStructure s2 = (SpinStructure) Structure.parse("SpinStructure(+--- +++-)");

	assertDouble(0.0, s1.mutationalDistance(s1));
	assertDouble(0.0, s2.mutationalDistance(s2));

	assertDouble(2.0, s1.mutationalDistance(s2));
	assertDouble(2.0, s2.mutationalDistance(s1));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new SpinStructure(null);
    }

    @Test public void testNumeric() {
	SpinStructure struct = (SpinStructure) Structure.parse("SpinStructure(- + - +)");

	assertDouble(-1.0, struct.asNumeric(0));
	assertDouble( 1.0, struct.asNumeric(1));
	assertDouble(-1.0, struct.asNumeric(2));
	assertDouble( 1.0, struct.asNumeric(3));

	assertTrue(JamVector.valueOf(-1.0, 1.0, -1.0, 1.0).equals(struct.asNumeric()));
    }

    @Test public void testOrdinal() {
	SpinStructure struct = (SpinStructure) Structure.parse("SpinStructure(- + - +)");

	assertEquals(0, struct.asOrdinal(0));
	assertEquals(1, struct.asOrdinal(1));
	assertEquals(0, struct.asOrdinal(2));
	assertEquals(1, struct.asOrdinal(3));

	assertTrue(Arrays.equals(new int[] { 0, 1, 0, 1 }, struct.asOrdinal()));
    }

    @Test public void testRandom() {
        int conservedLength = 2;
        int variableLength  = 3;
        int structureCount  = 1000000;

        double[] expectedMean  = new double[] { -1.0, -1.0,  0.0,  0.0,  0.0 };
        double[] expectedStDev = new double[] {  0.0,  0.0,  1.0,  1.0,  1.0 };

        double meanTolerance  = 0.002;
        double stDevTolerance = 0.00001;

        ArrayList<Structure> structures = new ArrayList<Structure>(structureCount);

        for (int structureIndex = 0; structureIndex < structureCount; structureIndex++)
            structures.add(SpinStructure.generate(conservedLength, variableLength));

        assertSummary(structures, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.SpinStructureTest");
    }
}
