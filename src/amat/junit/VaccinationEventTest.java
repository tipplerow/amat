
package amat.junit;

import amat.vaccine.VaccinationEvent;
import amat.vaccine.Vaccine;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccinationEventTest extends VaccineTestBase {
    //
    // Antigens are defined in VaccineTestBase...
    //
    @Test public void testParseOne() {
        VaccinationEvent event = VaccinationEvent.parse("10: AG1, 1.0");
	Vaccine vaccine = event.getVaccine();

	assertEquals(10, event.getInjectionCycle());
	assertEquals(1, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);
    }

    @Test public void testParseMulti() {
        VaccinationEvent event = VaccinationEvent.parse("22: AG1, 1.0; AG2, 2.0");
	Vaccine vaccine = event.getVaccine();

	assertEquals(22, event.getInjectionCycle());
	assertEquals(2, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 1.0);
	assertComponent(vaccine, 1, AG2, 2.0);
    }

    @Test public void testParseShortHand() {
        VaccinationEvent event = VaccinationEvent.parse("33: AG1, AG2; 5.5");
	Vaccine vaccine = event.getVaccine();

	assertEquals(33, event.getInjectionCycle());
	assertEquals(2, vaccine.countComponents());
	assertComponent(vaccine, 0, AG1, 5.5);
	assertComponent(vaccine, 1, AG2, 5.5);
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoColon() {
        VaccinationEvent.parse("10, AG1, 1.0");
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoCycle() {
        VaccinationEvent.parse(": AG1, 1.0");
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoVaccine() {
        VaccinationEvent.parse("88: ");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccinationEventTest");
    }
}
