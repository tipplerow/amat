
package amat.junit;

import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class StructureTest extends StructureTestBase {
    @Test public void testLengthComparator() {
        Structure s1 = Structure.parse("BitStructure(0)");
        Structure s2 = Structure.parse("SpinStructure(+-)");
        Structure s3 = Structure.parse("BitStructure(11)");
        Structure s4 = Structure.parse("SpinStructure(--+)");

        assertTrue(Structure.LENGTH_COMPARATOR.compare(s2, s1) > 0);
        assertTrue(Structure.LENGTH_COMPARATOR.compare(s2, s3) == 0);
        assertTrue(Structure.LENGTH_COMPARATOR.compare(s2, s4) < 0);

        assertTrue(Structure.LENGTH_COMPARATOR.compare(s1, s2) < 0);
        assertTrue(Structure.LENGTH_COMPARATOR.compare(s3, s2) == 0);
        assertTrue(Structure.LENGTH_COMPARATOR.compare(s4, s2) > 0);
    }

    @Test public void testIsMatch() {
        Structure bit1 = Structure.parse("BitStructure(00)");
        Structure bit2 = Structure.parse("BitStructure(011)");

        Structure spin1 = Structure.parse("SpinStructure(--)");
        Structure spin2 = Structure.parse("SpinStructure(-++)");

        assertTrue( bit1.isMatch(bit2, 0));
        assertFalse(bit1.isMatch(bit2, 1));

        assertFalse(bit1.isMatch(spin1, 0));
        assertFalse(bit1.isMatch(spin1, 1));

        assertFalse(bit1.isMatch(spin2, 0));
        assertFalse(bit1.isMatch(spin2, 1));

        assertTrue( bit2.isMatch(bit1, 0));
        assertFalse(bit2.isMatch(bit1, 1));
        assertFalse(bit2.isMatch(bit1, 2));

        assertFalse(bit2.isMatch(spin1, 0));
        assertFalse(bit2.isMatch(spin1, 1));
        assertFalse(bit2.isMatch(spin1, 2));

        assertFalse(bit2.isMatch(spin2, 0));
        assertFalse(bit2.isMatch(spin2, 1));
        assertFalse(bit2.isMatch(spin2, 2));
    }

    @Test public void testBitMutationalDistance() {
	Structure bit00 = Structure.parse("BitStructure(00)");
	Structure bit01 = Structure.parse("BitStructure(01)");
	Structure bit10 = Structure.parse("BitStructure(10)");
	Structure bit11 = Structure.parse("BitStructure(11)");

	assertDouble(0.0, bit00.mutationalDistance(bit00));
	assertDouble(1.0, bit00.mutationalDistance(bit01));
	assertDouble(1.0, bit00.mutationalDistance(bit10));
	assertDouble(2.0, bit00.mutationalDistance(bit11));
    }

    @Test public void testSpinMutationalDistance() {
	Structure spin00 = Structure.parse("SpinStructure(--)");
	Structure spin01 = Structure.parse("SpinStructure(-+)");
	Structure spin10 = Structure.parse("SpinStructure(+-)");
	Structure spin11 = Structure.parse("SpinStructure(++)");

	assertDouble(0.0, spin00.mutationalDistance(spin00));
	assertDouble(1.0, spin00.mutationalDistance(spin01));
	assertDouble(1.0, spin00.mutationalDistance(spin10));
	assertDouble(2.0, spin00.mutationalDistance(spin11));
    }

    @Test public void testShapeMutationalDistance() {
	Structure shape12 = Structure.parse("ShapeStructure(1.0, 2.0)");
	Structure shape14 = Structure.parse("ShapeStructure(1.0, 4.0)");
	Structure shape42 = Structure.parse("ShapeStructure(4.0, 2.0)");
	Structure shape46 = Structure.parse("ShapeStructure(4.0, 6.0)");

	assertDouble(0.0, shape12.mutationalDistance(shape12));
	assertDouble(2.0, shape12.mutationalDistance(shape14));
	assertDouble(3.0, shape12.mutationalDistance(shape42));
	assertDouble(5.0, shape12.mutationalDistance(shape46));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.StructureTest");
    }
}
