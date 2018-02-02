
package amat.junit;

import jam.junit.NumericTestBase;

import amat.binding.AffinityModel;
import amat.binding.HammingAffinity;
import amat.epitope.Epitope;
import amat.panel.NeutralizationPanel;
import amat.panel.PanelType;
import amat.panel.UniformDiscretePanel;
import amat.receptor.Receptor;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class UniformDiscretePanelTest extends NumericTestBase {
    private static final Structure s1 = Structure.parse("PottsStructure(4; AAAA AAAA AAAA)");
    private static final Structure s2 = Structure.parse("PottsStructure(4; AAAA BBBB BBBB)");
    private static final Structure s3 = Structure.parse("PottsStructure(4; AAAA CCCC CCCC)");
    private static final Structure s4 = Structure.parse("PottsStructure(4; AAAA DDDD DDDD)");

    private static final Epitope e1 = Epitope.add("E1", s1);
    private static final Epitope e2 = Epitope.add("E2", s2);
    private static final Epitope e3 = Epitope.add("E3", s3);
    private static final Epitope e4 = Epitope.add("E4", s4);

    // Two receptor structures matching no conserved elements...
    private static final Structure con0a = Structure.parse("PottsStructure(4; BBBB ABCD DCBA)");
    private static final Structure con0b = Structure.parse("PottsStructure(4; BBBB DCBA ABCD)");

    private static final Receptor r0a = new Receptor(con0a);
    private static final Receptor r0b = new Receptor(con0b);

    // Two receptor structures matching one conserved element...
    private static final Structure con1a = Structure.parse("PottsStructure(4; ABBB ABCD DCBA)");
    private static final Structure con1b = Structure.parse("PottsStructure(4; ABBB DCBA ABCD)");

    private static final Receptor r1a = new Receptor(con1a);
    private static final Receptor r1b = new Receptor(con1b);

    // Two receptor structures matching two conserved elements...
    private static final Structure con2a = Structure.parse("PottsStructure(4; AABB ABCD DCBA)");
    private static final Structure con2b = Structure.parse("PottsStructure(4; AABB DCBA ABCD)");

    private static final Receptor r2a = new Receptor(con2a);
    private static final Receptor r2b = new Receptor(con2b);

    // Two receptor structures matching three conserved elements...
    private static final Structure con3a = Structure.parse("PottsStructure(4; AAAB ABCD DCBA)");
    private static final Structure con3b = Structure.parse("PottsStructure(4; AAAB DCBA ABCD)");

    private static final Receptor r3a = new Receptor(con3a);
    private static final Receptor r3b = new Receptor(con3b);

    // Two receptor structures matching all four conserved elements...
    private static final Structure con4a = Structure.parse("PottsStructure(4; AAAA ABCD DCBA)");
    private static final Structure con4b = Structure.parse("PottsStructure(4; AAAA DCBA ABCD)");

    private static final Receptor r4a = new Receptor(con4a);
    private static final Receptor r4b = new Receptor(con4b);

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "HAMMING");
        System.setProperty(HammingAffinity.MATCH_GAIN_PROPERTY, "2.0");

        System.setProperty(NeutralizationPanel.PANEL_TYPE_PROPERTY, "UNIFORM_DISCRETE");
        System.setProperty(NeutralizationPanel.AFFINITY_THRESHOLD_PROPERTY, "4.0");
    }

    @Test public void testBreadth() {
        HammingAffinity model = (HammingAffinity) AffinityModel.global();

        assertDouble( 2.0, model.getMatchGain());
        assertDouble(18.0, model.getActivationEnergy());

        UniformDiscretePanel panel2 = new UniformDiscretePanel(2.0);
        UniformDiscretePanel panel4 = new UniformDiscretePanel(4.0);
        UniformDiscretePanel panel8 = new UniformDiscretePanel(8.0);

        assertEquals(4, panel2.getTotalMatchingThreshold());
        assertEquals(5, panel4.getTotalMatchingThreshold());
        assertEquals(7, panel8.getTotalMatchingThreshold());

        assertDouble(1.0,       panel2.computeMaximumBreadth());
        assertEquals(0.8998871, panel4.computeMaximumBreadth(), 1.0e-07);
        assertEquals(0.3214569, panel8.computeMaximumBreadth(), 1.0e-07);

        assertDouble(panel4.computeMaximumBreadth(), panel4.computeBreadth(r4a));
        assertDouble(panel4.computeMaximumBreadth(), panel4.computeBreadth(r4b));

        assertDouble(panel8.computeMaximumBreadth(), panel8.computeBreadth(r4a));
        assertDouble(panel8.computeMaximumBreadth(), panel8.computeBreadth(r4b));

        assertEquals(0.02729797, panel4.computeBreadth(r0a), 1.0e-08);
        assertEquals(0.02729797, panel4.computeBreadth(r0b), 1.0e-08);
        assertEquals(0.11381531, panel4.computeBreadth(r1a), 1.0e-08);
        assertEquals(0.11381531, panel4.computeBreadth(r1b), 1.0e-08);
        assertEquals(0.32145691, panel4.computeBreadth(r2a), 1.0e-08);
        assertEquals(0.32145691, panel4.computeBreadth(r2b), 1.0e-08);
        assertEquals(0.63291931, panel4.computeBreadth(r3a), 1.0e-08);
        assertEquals(0.63291931, panel4.computeBreadth(r3b), 1.0e-08);
        assertEquals(0.89988708, panel4.computeBreadth(r4a), 1.0e-08);
        assertEquals(0.89988708, panel4.computeBreadth(r4b), 1.0e-08);
    }

    @Test public void testGlobal() {
        UniformDiscretePanel panel = (UniformDiscretePanel) NeutralizationPanel.global();

        assertEquals(PanelType.UNIFORM_DISCRETE, panel.getType());
        assertDouble(4.0, panel.getAffinityThreshold());

        assertEquals( 4, panel.getConservedLength());
        assertEquals(12, panel.getEpitopeLength());
        assertEquals( 5, panel.getTotalMatchingThreshold());
        assertEquals( 8, panel.getVariableLength());

        assertDouble(3.0, panel.getConservedMatchingThreshold());
        assertDouble(6.0, panel.getMeanVariableDistance());
        assertDouble(2.0, panel.getMeanVariableMatching());
    }

    @Test public void testMeanAffinity() {
        HammingAffinity model = (HammingAffinity) AffinityModel.global();

        assertDouble( 2.0, model.getMatchGain());
        assertDouble(18.0, model.getActivationEnergy());

        // Mean affinity should not depend on the affinity threshold...
        UniformDiscretePanel panel4 = new UniformDiscretePanel(4.0);
        UniformDiscretePanel panel8 = new UniformDiscretePanel(8.0);

        assertDouble(-2.0, panel4.computeMeanAffinity(r0a));
        assertDouble(-2.0, panel8.computeMeanAffinity(r0b));

        assertDouble(0.0, panel4.computeMeanAffinity(r1a));
        assertDouble(0.0, panel8.computeMeanAffinity(r1b));

        assertDouble(2.0, panel4.computeMeanAffinity(r2a));
        assertDouble(2.0, panel8.computeMeanAffinity(r2b));

        assertDouble(4.0, panel4.computeMeanAffinity(r3a));
        assertDouble(4.0, panel8.computeMeanAffinity(r3b));

        assertDouble(6.0, panel4.computeMeanAffinity(r4a));
        assertDouble(6.0, panel8.computeMeanAffinity(r4b));
    }

    @Test public void testTotalMatchingThreshold() {
        UniformDiscretePanel panel02 = new UniformDiscretePanel( 2.0);
        UniformDiscretePanel panel03 = new UniformDiscretePanel( 3.0);
        UniformDiscretePanel panel04 = new UniformDiscretePanel( 4.0);
        UniformDiscretePanel panel10 = new UniformDiscretePanel(10.0);
        UniformDiscretePanel panel11 = new UniformDiscretePanel(11.0);
        UniformDiscretePanel panel99 = new UniformDiscretePanel(99.0);

        assertEquals( 4, panel02.getTotalMatchingThreshold());
        assertEquals( 5, panel03.getTotalMatchingThreshold());
        assertEquals( 5, panel04.getTotalMatchingThreshold());
        assertEquals( 8, panel10.getTotalMatchingThreshold());
        assertEquals( 9, panel11.getTotalMatchingThreshold());
        assertEquals(13, panel99.getTotalMatchingThreshold());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.UniformDiscretePanelTest");
    }
}
