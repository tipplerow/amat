
package amat.junit;

import java.util.ArrayList;
import java.util.Arrays;

import jam.vector.BitVector;
import jam.vector.JamVector;

import amat.structure.BitStructure;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class BitStructureTest extends StructureTestBase {
    @Test public void testEquals() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = BitVector.parse("0011");

        BitStructure s1a = new BitStructure(v1);
        BitStructure s1b = new BitStructure(v1);
        BitStructure s2  = new BitStructure(v2);

        assertTrue(s1a.equals(s1b));
        assertFalse(s1a.equals(s2));
        assertTrue(s2.equals(s2));
    }

    @Test public void testFormatParse() {
        BitStructure s1 = new BitStructure(BitVector.parse("01010101"));
        BitStructure s2 = (BitStructure) Structure.parse("BitStructure(0101 0101)");
        BitStructure s3 = (BitStructure) Structure.parse(s1.format());

        assertEquals(s1, s2);
        assertEquals(s1, s3);
    }

    @Test public void testHashCode() {
        BitVector v1 = BitVector.parse("0101");
        BitVector v2 = BitVector.parse("0011");

        BitStructure s1a = new BitStructure(v1);
        BitStructure s1b = new BitStructure(v1);
        BitStructure s2  = new BitStructure(v2);

        assertTrue(s1a.hashCode() == s1b.hashCode());
        assertTrue(s1a.hashCode() != s2.hashCode());
    }

    @Test public void testMutationalDistance() {
        Structure s1 = Structure.parse("BitStructure(0001 1111)");
        Structure s2 = Structure.parse("BitStructure(1111 1000)");

	assertDouble(0.0, s1.mutationalDistance(s1));
	assertDouble(0.0, s2.mutationalDistance(s2));

	assertDouble(6.0, s1.mutationalDistance(s2));
	assertDouble(6.0, s2.mutationalDistance(s1));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new BitStructure(null);
    }

    @Test public void testNumeric() {
	BitStructure struct = (BitStructure) Structure.parse("BitStructure(0101)");

	assertDouble(0.0, struct.asNumeric(0));
	assertDouble(1.0, struct.asNumeric(1));
	assertDouble(0.0, struct.asNumeric(2));
	assertDouble(1.0, struct.asNumeric(3));

	assertTrue(JamVector.valueOf(0.0, 1.0, 0.0, 1.0).equals(struct.asNumeric()));
    }

    @Test public void testOrdinal() {
	BitStructure struct = (BitStructure) Structure.parse("BitStructure(0101)");

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

        double[] expectedMean  = new double[] { 0.0, 0.0, 0.5, 0.5, 0.5 };
        double[] expectedStDev = new double[] { 0.0, 0.0, 0.5, 0.5, 0.5 };

        double meanTolerance  = 0.002;
        double stDevTolerance = 0.00001;

        ArrayList<Structure> structures = new ArrayList<Structure>(structureCount);

        for (int structureIndex = 0; structureIndex < structureCount; structureIndex++)
            structures.add(BitStructure.generate(conservedLength, variableLength));

        assertSummary(structures, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.BitStructureTest");
    }
}
