
package amat.junit;

import amat.antigen.Antigen;
import amat.vaccine.Vaccine;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccineTest extends VaccineTestBase {
    //
    // Antigens are defined in VaccineTestBase...
    //
    @Test public void testParseOne() {
        Vaccine vaccine = Vaccine.parse("AG1, 1.0");

	assertEquals(1, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);

        vaccine = new Vaccine("AG1", 1.0);

	assertEquals(1, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);
    }

    @Test public void testParseMulti() {
        Vaccine vaccine = Vaccine.parse("AG1, 1.0; AG2, 2.0");

	assertEquals(2, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);
	assertComponent(vaccine, 1, AG2, 2.0);

        vaccine = Vaccine.parse("AG1, 1.0; AG2, 2.0; AG3, 3.0");

	assertEquals(3, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);
	assertComponent(vaccine, 1, AG2, 2.0);
	assertComponent(vaccine, 2, AG3, 3.0);
    }

    @Test public void testParseShortHand() {
        Vaccine vaccine = Vaccine.parse("AG1, AG2; 5.5");

	assertEquals(2, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 5.5);
	assertComponent(vaccine, 1, AG2, 5.5);

        vaccine = Vaccine.parse("AG1, AG2, AG3; 8.8");

	assertEquals(3, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 8.8);
	assertComponent(vaccine, 1, AG2, 8.8);
	assertComponent(vaccine, 2, AG3, 8.8);
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoAntigens() {
	Vaccine.parse("; 5.0");
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoConcentration() {
	Vaccine.parse("AG1, AG2, 5.0");
    }

    @Test(expected = RuntimeException.class)
    public void testParseDuplicateAntigen() {
        Vaccine.parse("AG1, 1.0; AG1, 2.0");
    }

    @Test public void testShortcut() {
        Vaccine vaccine = Vaccine.shortcut(3, 1.23);

        assertEquals(3, vaccine.countComponents());

        assertComponent(vaccine, 0, Antigen.lookup("E1"), 1.23);
        assertComponent(vaccine, 1, Antigen.lookup("E2"), 1.23);
        assertComponent(vaccine, 2, Antigen.lookup("E3"), 1.23);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccineTest");
    }
}
