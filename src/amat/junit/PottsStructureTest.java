
package amat.junit;

import java.util.ArrayList;
import java.util.Arrays;

import jam.vector.JamVector;

import amat.structure.PottsStructure;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class PottsStructureTest extends StructureTestBase {
    @Test(expected = IllegalArgumentException.class)
    public void testCardinality1() {
        new PottsStructure(1, new int[] { 0, 0 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCardinality2() {
        new PottsStructure(3, new int[] { 0, 1, 2, 3 });
    }

    @Test public void testEquals() {
        int[] v1 = new int[] { 0, 1, 2, 3 };
        int[] v2 = new int[] { 1, 0, 3, 2 };

        PottsStructure s1a = new PottsStructure(4, v1);
        PottsStructure s1b = new PottsStructure(4, v1);
        PottsStructure s2  = new PottsStructure(5, v1);
        PottsStructure s3  = new PottsStructure(4, v2);

        assertTrue(s1a.equals(s1b));
        assertFalse(s1a.equals(s2));
        assertFalse(s1a.equals(s3));
    }

    @Test public void testFormatParse() {
        PottsStructure s1 = new PottsStructure(4, new int[] { 0, 1, 2, 3 });
        PottsStructure s2 = (PottsStructure) Structure.parse("PottsStructure(4; ABCD)");
        PottsStructure s3 = (PottsStructure) Structure.parse(s1.format());

        assertEquals(s1, s2);
        assertEquals(s1, s3);
    }

    @Test public void testHashCode() {
        PottsStructure s1a = new PottsStructure(4, new int[] { 0, 1, 2, 3 });
        PottsStructure s1b = new PottsStructure(4, new int[] { 0, 1, 2, 3 });
        PottsStructure s2  = new PottsStructure(4, new int[] { 1, 0, 3, 2 });

        assertTrue(s1a.hashCode() == s1b.hashCode());
        assertTrue(s1a.hashCode() != s2.hashCode());
    }

    @Test public void testMutationalDistance() {
        Structure s1 = Structure.parse("PottsStructure(4; AAAA AAAA)");
        Structure s2 = Structure.parse("PottsStructure(4; AAAA ABCD)");
        Structure s3 = Structure.parse("PottsStructure(4; BACA AAAA)");

	assertDouble(0.0, s1.mutationalDistance(s1));
	assertDouble(0.0, s2.mutationalDistance(s2));
	assertDouble(0.0, s3.mutationalDistance(s3));

	assertDouble(3.0, s1.mutationalDistance(s2));
	assertDouble(2.0, s1.mutationalDistance(s3));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new PottsStructure(6, null);
    }

    @Test public void testNumeric() {
	PottsStructure struct = new PottsStructure(4, new int[] { 3, 2, 1, 0 });

	assertDouble(3.0, struct.asNumeric(0));
	assertDouble(2.0, struct.asNumeric(1));
	assertDouble(1.0, struct.asNumeric(2));
	assertDouble(0.0, struct.asNumeric(3));

	assertTrue(JamVector.valueOf(3.0, 2.0, 1.0, 0.0).equals(struct.asNumeric()));
    }

    @Test public void testPotts() {
	PottsStructure struct = new PottsStructure(4, new int[] { 3, 2, 1, 0 });

	assertEquals(3, struct.asOrdinal(0));
	assertEquals(2, struct.asOrdinal(1));
	assertEquals(1, struct.asOrdinal(2));
	assertEquals(0, struct.asOrdinal(3));

	assertTrue(Arrays.equals(new int[] { 3, 2, 1, 0 }, struct.asOrdinal()));
    }

    @Test public void testRandom() {
	int cardinality     = 6;
        int conservedLength = 2;
        int variableLength  = 3;
        int structureCount  = 1000000;

        double[] expectedMean  = new double[] { 0.0, 0.0, 2.5, 2.5, 2.5 };
        double[] expectedStDev = new double[] { 0.0, 0.0, 1.7, 1.7, 1.7 };

        double meanTolerance  = 0.002;
        double stDevTolerance = 0.01;

        ArrayList<Structure> structures = new ArrayList<Structure>(structureCount);

        for (int structureIndex = 0; structureIndex < structureCount; structureIndex++)
            structures.add(PottsStructure.generate(cardinality, conservedLength, variableLength));

        assertSummary(structures, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.PottsStructureTest");
    }
}
