
package amat.junit;

import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.epitope.Epitope;
import amat.structure.Structure;
import amat.vaccine.Vaccine;
import amat.vaccine.VaccineComponent;
import amat.vaccine.VaccinationEvent;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccineTestBase extends NumericTestBase {
    protected static final Structure S1 = Structure.parse("BitStructure(00110011)");
    protected static final Structure S2 = Structure.parse("BitStructure(00001111)");
    protected static final Structure S3 = Structure.parse("BitStructure(11001100)");

    protected static final Epitope E1 = Epitope.add("E1", S1);
    protected static final Epitope E2 = Epitope.add("E3", S2);
    protected static final Epitope E3 = Epitope.add("E2", S3);

    protected static final Antigen AG1 = Antigen.add("AG1", E1);
    protected static final Antigen AG2 = Antigen.add("AG2", E2);
    protected static final Antigen AG3 = Antigen.add("AG3", E3);

    static {
        Antigen.add("E1", E1);
        Antigen.add("E2", E2);
        Antigen.add("E3", E3);
    }

    protected void assertComponent(VaccineComponent component, Antigen antigen, double concentration) {
	assertEquals(antigen, component.getAntigen());
	assertDouble(concentration, component.getConcentration().doubleValue());
    }

    protected void assertComponent(Vaccine vaccine, int index, Antigen antigen, double concentration) {
	assertComponent(vaccine.viewComponents().get(index), antigen, concentration);
    }
}
