
package amat.junit;

import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.epitope.Epitope;
import amat.structure.Structure;
import amat.vaccine.VaccinationSchedule;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccinationScheduleGlobalTest extends NumericTestBase {
    static {
        Epitope.add("E1", Structure.parse("BitStructure(0101)"));
        Epitope.add("E2", Structure.parse("BitStructure(1010)"));

        Antigen.add("E1", Epitope.require("E1"));
        Antigen.add("E2", Epitope.require("E2"));
    }

    @Test public void testSet() {
        VaccinationSchedule shortcut = VaccinationSchedule.shortcut(2, 1.0);

        VaccinationSchedule.setGlobal(shortcut);
        assertTrue(VaccinationSchedule.global() == shortcut);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccinationScheduleGlobalTest");
    }
}
