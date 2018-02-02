
package amat.junit;

import amat.binding.AffinityModel;
import amat.binding.HammingAffinity;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class HammingAffinityTest extends AffinityModelTestBase {
    private final AffinityModel model = AffinityModel.global();

    private static final Epitope bitEpitope   = Epitope.add("BIT1",   bitStructure1);
    private static final Epitope spinEpitope  = Epitope.add("SPIN1",  spinStructure1);
    private static final Epitope pottsEpitope = Epitope.add("POTTS1", pottsStructure1);

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "HAMMING");
        System.setProperty(HammingAffinity.ACT_ENERGY_PROPERTY, "6.0");
        System.setProperty(HammingAffinity.MATCH_GAIN_PROPERTY, "1.5");
    }

    @Test public void testBitStructure() {
        assertDouble(0.0, model.computeFreeEnergy(bitEpitope, bitReceptor1));
        assertDouble(6.0, model.computeAffinity(bitEpitope, bitReceptor1));

        assertDouble(12.0, model.computeFreeEnergy(bitEpitope, bitReceptor2));
        assertDouble(-6.0, model.computeAffinity(bitEpitope, bitReceptor2));

        assertDouble(4.5, model.computeFreeEnergy(bitEpitope, bitReceptor3));
        assertDouble(1.5, model.computeAffinity(bitEpitope, bitReceptor3));

        assertDouble( 7.5, model.computeFreeEnergy(bitEpitope, bitReceptor4));
        assertDouble(-1.5, model.computeAffinity(bitEpitope, bitReceptor4));
    }

    @Test public void testPottsStructure() {
        assertDouble(0.0, model.computeFreeEnergy(pottsEpitope, pottsReceptor1));
        assertDouble(6.0, model.computeAffinity(pottsEpitope, pottsReceptor1));

        assertDouble(4.5, model.computeFreeEnergy(pottsEpitope, pottsReceptor2));
        assertDouble(1.5, model.computeAffinity(pottsEpitope, pottsReceptor2));

        assertDouble(6.0, model.computeFreeEnergy(pottsEpitope, pottsReceptor3));
        assertDouble(0.0, model.computeAffinity(pottsEpitope, pottsReceptor3));

        assertDouble(10.5, model.computeFreeEnergy(pottsEpitope, pottsReceptor4));
        assertDouble(-4.5, model.computeAffinity(pottsEpitope, pottsReceptor4));
    }

    @Test public void testSpinStructure() {
        assertDouble(0.0, model.computeFreeEnergy(spinEpitope, spinReceptor1));
        assertDouble(6.0, model.computeAffinity(spinEpitope, spinReceptor1));

        assertDouble(12.0, model.computeFreeEnergy(spinEpitope, spinReceptor2));
        assertDouble(-6.0, model.computeAffinity(spinEpitope, spinReceptor2));

        assertDouble(4.5, model.computeFreeEnergy(spinEpitope, spinReceptor3));
        assertDouble(1.5, model.computeAffinity(spinEpitope, spinReceptor3));

        assertDouble( 7.5, model.computeFreeEnergy(spinEpitope, spinReceptor4));
        assertDouble(-1.5, model.computeAffinity(spinEpitope, spinReceptor4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.HammingAffinityTest");
    }
}
