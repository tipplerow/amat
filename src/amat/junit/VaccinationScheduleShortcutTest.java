
package amat.junit;

import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.epitope.Epitope;
import amat.vaccine.VaccinationEvent;
import amat.vaccine.VaccinationSchedule;
import amat.vaccine.Vaccine;
import amat.vaccine.VaccineComponent;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccinationScheduleShortcutTest extends NumericTestBase {
    static {
        System.setProperty(Epitope.CONFIG_FILE_PROPERTY, "test/epitope_sample.conf");
        System.setProperty(VaccinationSchedule.ANTIGEN_COUNT_PROPERTY, "4");
        System.setProperty(VaccinationSchedule.TOTAL_CONC_PROPERTY, "2.0");
    }

    @Test public void testShortcut() {
        assertSchedule(VaccinationSchedule.global(), 
                       new String[] { "E1", "E2", "E3", "E4" }, 
                       new double[] {  0.5,  0.5,  0.5,  0.5 });

        assertSchedule(VaccinationSchedule.shortcut(3, 15.0),
                       new String[] { "E1", "E2", "E3" }, 
                       new double[] {  5.0,  5.0,  5.0 });
    }

    private void assertSchedule(VaccinationSchedule schedule, String[] keys, double[] conc) {
        assertEquals(1, schedule.countEvents());
        assertKeys(schedule.eventOn(0).getVaccine(), keys);
        assertConc(schedule.eventOn(0).getVaccine(), conc);
    }

    private void assertKeys(Vaccine vaccine, String[] keys) {
        assertEquals(keys.length, vaccine.countComponents());

        for (int index = 0; index < keys.length; index++)
            assertEquals(keys[index], vaccine.viewComponents().get(index).getKey());
    }

    private void assertConc(Vaccine vaccine, double[] conc) {
        assertEquals(conc.length, vaccine.countComponents());

        for (int index = 0; index < conc.length; index++)
            assertDouble(conc[index], vaccine.viewComponents().get(index).getConcentration().doubleValue());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccinationScheduleShortcutTest");
    }
}
