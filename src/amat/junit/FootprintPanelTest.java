
package amat.junit;

import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.binding.AffinityModel;
import amat.epitope.Epitope;
import amat.panel.FootprintPanel;
import amat.panel.NeutralizationPanel;
import amat.receptor.Receptor;
import amat.structure.Structure;
import amat.vaccine.VaccinationEvent;
import amat.vaccine.VaccinationSchedule;

import org.junit.*;
import static org.junit.Assert.*;

public class FootprintPanelTest extends NumericTestBase {
    private final FootprintPanel panel5  = FootprintPanel.instance(5);
    private final FootprintPanel panel15 = FootprintPanel.instance(15);
    private final FootprintPanel panel25 = FootprintPanel.instance(25);

    private final Receptor r1 = new Receptor(Structure.parse("SpinStructure(---- ----)"));
    private final Receptor r2 = new Receptor(Structure.parse("SpinStructure(---- ++++)"));
    private final Receptor r3 = new Receptor(Structure.parse("SpinStructure(++++ ++++)"));

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "QUADRATIC");
        System.setProperty(AffinityModel.ACT_ENERGY_PROPERTY, "0.0");
        System.setProperty(AffinityModel.PRE_FACTOR_PROPERTY, "2.0");

        System.setProperty(NeutralizationPanel.AFFINITY_THRESHOLD_PROPERTY, "1.5");

        Epitope.add("E1", Structure.parse("SpinStructure(---- ----)"));
        Epitope.add("E2", Structure.parse("SpinStructure(---- --++)"));
        Epitope.add("E3", Structure.parse("SpinStructure(---- ++++)"));

        Antigen.add("AG1", Epitope.require("E1"));
        Antigen.add("AG2", Epitope.require("E2"));
        Antigen.add("AG3", Epitope.require("E3"));

        assignGlobal();
    }

    private static void assignGlobal() {
        VaccinationEvent v1 = VaccinationEvent.parse("0:  AG1, 1.0");
        VaccinationEvent v2 = VaccinationEvent.parse("10: AG1, AG2; 2.0");
        VaccinationEvent v3 = VaccinationEvent.parse("20: AG1, AG2, AG3; 3.0");

        VaccinationSchedule.setGlobal(new VaccinationSchedule(v1, v2, v3));
    }

    @Test public void testAffinity() {
        assertDouble( 2.0, panel5.computeMeanAffinity(r1));
        assertDouble( 0.0, panel5.computeMeanAffinity(r2));
        assertDouble(-2.0, panel5.computeMeanAffinity(r3));

        assertDouble( 1.5, panel15.computeMeanAffinity(r1));
        assertDouble( 0.5, panel15.computeMeanAffinity(r2));
        assertDouble(-1.5, panel15.computeMeanAffinity(r3));

        assertDouble( 1.0, panel25.computeMeanAffinity(r1));
        assertDouble( 1.0, panel25.computeMeanAffinity(r2));
        assertDouble(-1.0, panel25.computeMeanAffinity(r3));
    }

    @Test public void testBreadth() {
        assertDouble(1.0, panel5.computeBreadth(r1));
        assertDouble(0.0, panel5.computeBreadth(r2));
        assertDouble(0.0, panel5.computeBreadth(r3));

        assertDouble(0.5, panel15.computeBreadth(r1));
        assertDouble(0.0, panel15.computeBreadth(r2));
        assertDouble(0.0, panel15.computeBreadth(r3));

        assertDouble(1.0 / 3.0, panel25.computeBreadth(r1));
        assertDouble(1.0 / 3.0, panel25.computeBreadth(r2));
        assertDouble(0.0,       panel25.computeBreadth(r3));
    }

    @Test public void testEpitopes() {
        assertEquals(1, panel5.viewEpitopes().size());
        assertEquals(2, panel15.viewEpitopes().size());
        assertEquals(3, panel25.viewEpitopes().size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.FootprintPanelTest");
    }
}
