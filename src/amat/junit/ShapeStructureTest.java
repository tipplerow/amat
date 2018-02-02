
package amat.junit;

import java.util.ArrayList;

import jam.vector.JamVector;

import amat.structure.ShapeStructure;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class ShapeStructureTest extends StructureTestBase {

    @Test public void testEquals() {
        JamVector v1 = JamVector.parseCSV("1.0, 2.0, 3.0");
        JamVector v2 = JamVector.parseCSV("1.1, 2.1, 3.1");

        ShapeStructure s1a = new ShapeStructure(v1);
        ShapeStructure s1b = new ShapeStructure(v1);
        ShapeStructure s2  = new ShapeStructure(v2);

        assertTrue(s1a.equals(s1b));
        assertFalse(s1a.equals(s2));
        assertTrue(s2.equals(s2));

        // The resolution of the underlying discrete lattice is 0.001...
        JamVector v3 = JamVector.parseCSV("1.0001, 2.0001, 3.0001");
        ShapeStructure s3 = new ShapeStructure(v3);

        assertFalse(v1.equals(v3));
        assertTrue(s1a.equals(s3));
    }

    @Test public void testFormatParse() {
        ShapeStructure s1 = (ShapeStructure) Structure.parse("ShapeStructure(1.0, 2.0, 3.0)");
        ShapeStructure s2 = (ShapeStructure) Structure.parse(s1.format());

	assertEquals(3, s2.length());
	assertDouble(1.0, s2.asNumeric().getDouble(0));
	assertDouble(2.0, s2.asNumeric().getDouble(1));
	assertDouble(3.0, s2.asNumeric().getDouble(2));
    }

    @Test public void testHashCode() {
        JamVector v1 = JamVector.parseCSV("1.0,    2.0,    3.0");
        JamVector v2 = JamVector.parseCSV("1.0001, 2.0001, 3.0001");
        JamVector v3 = JamVector.parseCSV("1.1,    2.1,    3.1");

        ShapeStructure s1 = new ShapeStructure(v1);
        ShapeStructure s2 = new ShapeStructure(v2);
        ShapeStructure s3 = new ShapeStructure(v3);

        assertTrue(s1.hashCode() == s2.hashCode());
        assertTrue(s1.hashCode() != s3.hashCode());
    }

    @Test public void testMutationalDistance() {
        ShapeStructure s1 = (ShapeStructure) Structure.parse("ShapeStructure(1.0, 2.0, 3.0)");
        ShapeStructure s2 = (ShapeStructure) Structure.parse("ShapeStructure(1.5, 1.0, 6.0)");

	assertDouble(0.0, s1.mutationalDistance(s1));
	assertDouble(0.0, s2.mutationalDistance(s2));

	assertDouble(Math.sqrt(10.25), s1.mutationalDistance(s2));
	assertDouble(Math.sqrt(10.25), s2.mutationalDistance(s1));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new ShapeStructure(null);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.ShapeStructureTest");
    }
}
