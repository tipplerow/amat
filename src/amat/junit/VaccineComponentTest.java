
package amat.junit;

import jam.chem.HalfLife;
import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.epitope.Epitope;
import amat.structure.Structure;
import amat.vaccine.VaccineComponent;

import org.junit.*;
import static org.junit.Assert.*;

public class VaccineComponentTest extends NumericTestBase {
    private static final Structure structure = Structure.parse("BitStructure(00110011)");
    private static final Epitope   epitope   = Epitope.add("E1", structure);
    private static final Antigen   antigen   = Antigen.add("AG1", epitope);

    @Test public void testDecay() {
        VaccineComponent original = new VaccineComponent("AG1", 2.0);
        VaccineComponent decayed  = original.decay(HalfLife.valueOf(0.5));
        
        assertEquals(original.getAntigen(), decayed.getAntigen());
        assertDouble(2.0, original.getConcentration().doubleValue());
        assertDouble(0.5, decayed.getConcentration().doubleValue());
    }

    @Test public void testParse() {
        VaccineComponent component = VaccineComponent.parse(" AG1  ,   3.3 ");

        assertEquals(antigen, component.getAntigen());
        assertDouble(3.3, component.getConcentration().doubleValue());
    }

    @Test(expected = RuntimeException.class)
    public void testParseNoDelim() {
        VaccineComponent.parse("AG1 1.0");
    }

    @Test(expected = RuntimeException.class)
    public void testParseWrongOrder() {
        VaccineComponent.parse("1.0, AG1");
    }

    @Test(expected = RuntimeException.class)
    public void testParseMissingAntigen() {
        VaccineComponent.parse("no antigen, 1.0");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.VaccineComponentTest");
    }
}
