
package amat.junit;

import java.util.List;
import java.util.Set;

import jam.app.JamProperties;
import jam.chem.Concentration;
import jam.junit.NumericTestBase;
import jam.util.SetUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;
import amat.vaccine.VaccinationEvent;
import amat.vaccine.VaccinationSchedule;
import amat.vaccine.Vaccine;
import amat.vaccine.VaccineComponent;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccinationScheduleTest extends NumericTestBase {
    static {
        System.setProperty(Epitope.CONFIG_FILE_PROPERTY, "test/epitope_sample.conf");
        System.setProperty(Antigen.CONFIG_FILE_PROPERTY, "test/antigen_sample.conf");
        System.setProperty(VaccinationSchedule.CONFIG_FILE_PROPERTY, "test/schedule_sample.conf");
    }

    private final VaccinationSchedule global = VaccinationSchedule.global();

    private void assertConc(double expected, Concentration actual) {
        assertDouble(expected, actual.doubleValue());
    }

    @Test public void testAntigenFootprint() {
        Antigen ag1 = Antigen.require("AG1");
        Antigen ag2 = Antigen.require("AG2");
        Antigen ag3 = Antigen.require("AG3");

        Set<Antigen> set1   = SetUtil.newTreeSet(ag1);
        Set<Antigen> set12  = SetUtil.newTreeSet(ag1, ag2);
        Set<Antigen> set123 = SetUtil.newTreeSet(ag1, ag2, ag3);

        // Request a large cycle index first, to ensure that the
        // previous cycles are properly generated on demand...
        assertEquals(set123, global.getAntigenFootprint(40));
        assertEquals(set123, global.getAntigenFootprint(31));
        assertEquals(set123, global.getAntigenFootprint(30));

        assertEquals(set12, global.getAntigenFootprint(29));
        assertEquals(set12, global.getAntigenFootprint(10));

        assertEquals(set1, global.getAntigenFootprint(9));
        assertEquals(set1, global.getAntigenFootprint(0));
    }

    @Test public void testAntigenPoolFootprint() {
        Antigen ag1 = Antigen.require("AG1");
        Antigen ag2 = Antigen.require("AG2");
        Antigen ag3 = Antigen.require("AG3");

        AntigenPool pool = global.getAntigenPoolFootprint(0);
        assertEquals(1, pool.size());
        assertConc(1.0, pool.getConcentration(ag1));

        pool = global.getAntigenPoolFootprint(9);
        assertEquals(1, pool.size());
        assertConc(1.0, pool.getConcentration(ag1));

        pool = global.getAntigenPoolFootprint(10);
        assertEquals(2, pool.size());
        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));

        pool = global.getAntigenPoolFootprint(29);
        assertEquals(2, pool.size());
        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));

        pool = global.getAntigenPoolFootprint(30);
        assertEquals(3, pool.size());
        assertConc(4.0, pool.getConcentration(ag1));
        assertConc(5.0, pool.getConcentration(ag2));
        assertConc(3.0, pool.getConcentration(ag3));

        pool = global.getAntigenPoolFootprint(100);
        assertEquals(3, pool.size());
        assertConc(4.0, pool.getConcentration(ag1));
        assertConc(5.0, pool.getConcentration(ag2));
        assertConc(3.0, pool.getConcentration(ag3));
    }

    @Test public void testContainsEvent() {
        assertTrue(global.containsEvent(0));
        assertTrue(global.containsEvent(10));
        assertTrue(global.containsEvent(30));

        assertFalse(global.containsEvent(-1));
        assertFalse(global.containsEvent(1));
        assertFalse(global.containsEvent(9));
        assertFalse(global.containsEvent(11));
        assertFalse(global.containsEvent(29));
        assertFalse(global.containsEvent(31));
    }

    @Test public void testEpitopeFootprint() {
        VaccinationSchedule schedule = VaccinationSchedule.load("test/schedule_sample.conf");

        Epitope ep1 = Epitope.require("E1");
        Epitope ep2 = Epitope.require("E2");
        Epitope ep3 = Epitope.require("E3");

        Set<Epitope> set1   = SetUtil.newTreeSet(ep1);
        Set<Epitope> set12  = SetUtil.newTreeSet(ep1, ep2);
        Set<Epitope> set123 = SetUtil.newTreeSet(ep1, ep2, ep3);

        // Request a large cycle index first, to ensure that the
        // previous cycles are properly generated on demand...
        assertEquals(set123, schedule.getEpitopeFootprint(50));
        assertEquals(set123, schedule.getEpitopeFootprint(31));
        assertEquals(set123, schedule.getEpitopeFootprint(30));

        assertEquals(set12, schedule.getEpitopeFootprint(29));
        assertEquals(set12, schedule.getEpitopeFootprint(10));

        assertEquals(set1, schedule.getEpitopeFootprint(9));
        assertEquals(set1, schedule.getEpitopeFootprint(0));
    }

    @Test public void testEventOn() {
        assertEquals(0, global.eventOn(0).getInjectionCycle());
        assertEquals(10, global.eventOn(10).getInjectionCycle());
        assertEquals(30, global.eventOn(30).getInjectionCycle());

        assertNull(global.eventOn(1));
        assertNull(global.eventOn(9));
        assertNull(global.eventOn(11));
        assertNull(global.eventOn(29));
        assertNull(global.eventOn(31));
    }

    @Test public void testLatest() {
        assertEquals(0, global.latestEvent(0).getInjectionCycle());
        assertEquals(0, global.latestEvent(9).getInjectionCycle());
        assertEquals(10, global.latestEvent(10).getInjectionCycle());
        assertEquals(10, global.latestEvent(29).getInjectionCycle());
        assertEquals(30, global.latestEvent(30).getInjectionCycle());
        assertEquals(30, global.latestEvent(10000).getInjectionCycle());
    }

    @Test(expected = RuntimeException.class)
    public void testLatestInvalid() {
        global.latestEvent(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testSetGlobalInvalid() {
        // Already set, should trigger an exception....
        VaccinationSchedule.setGlobal(global);
    }

    @Test public void testVaccineComponents() {
        Vaccine vaccine0 = global.viewEvents().get(0).getVaccine();
        Vaccine vaccine1 = global.viewEvents().get(1).getVaccine();
        Vaccine vaccine2 = global.viewEvents().get(2).getVaccine();

        assertKeys(vaccine0, "AG1");
        assertConc(vaccine0, 1.0);

        assertKeys(vaccine1, "AG2");
        assertConc(vaccine1, 2.0);

        assertKeys(vaccine2, "AG1", "AG2", "AG3");
        assertConc(vaccine2, 3.0, 3.0, 3.0);
    }

    private void assertKeys(Vaccine vaccine, String... keys) {
        assertEquals(keys.length, vaccine.countComponents());

        for (int index = 0; index < keys.length; index++)
            assertEquals(keys[index], vaccine.viewComponents().get(index).getKey());
    }

    private void assertConc(Vaccine vaccine, double... conc) {
        assertEquals(conc.length, vaccine.countComponents());

        for (int index = 0; index < conc.length; index++)
            assertDouble(conc[index], vaccine.viewComponents().get(index).getConcentration().doubleValue());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccinationScheduleTest");
    }
}
