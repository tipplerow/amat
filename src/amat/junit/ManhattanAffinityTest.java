
package amat.junit;

import amat.binding.AffinityModel;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class ManhattanAffinityTest extends AffinityModelTestBase {
    private final AffinityModel model = AffinityModel.global();

    private static final Epitope bitEpitope  = Epitope.add("BIT1",  bitStructure1);
    private static final Epitope spinEpitope = Epitope.add("SPIN1", spinStructure1);

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "MANHATTAN");
        System.setProperty(AffinityModel.PRE_FACTOR_PROPERTY, "2.5");
        System.setProperty(AffinityModel.ACT_ENERGY_PROPERTY, "8.0");
    }

    @Test public void testBitStructure() {
        assertDouble(0.0, model.computeFreeEnergy(bitEpitope, bitReceptor1));
        assertDouble(2.5, model.computeFreeEnergy(bitEpitope, bitReceptor2));
        assertDouble(2.5 * 3.0 / 8.0, model.computeFreeEnergy(bitEpitope, bitReceptor3));
        assertDouble(2.5 * 5.0 / 8.0, model.computeFreeEnergy(bitEpitope, bitReceptor4));
    }

    @Test public void testSpinStructure() {
        assertDouble(0.0, model.computeFreeEnergy(spinEpitope, spinReceptor1));
        assertDouble(5.0, model.computeFreeEnergy(spinEpitope, spinReceptor2));
        assertDouble(5.0 * 3.0 / 8.0, model.computeFreeEnergy(spinEpitope, spinReceptor3));
        assertDouble(5.0 * 5.0 / 8.0, model.computeFreeEnergy(spinEpitope, spinReceptor4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.ManhattanAffinityTest");
    }
}
