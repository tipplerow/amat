
package amat.junit;

import jam.junit.NumericTestBase;

import amat.binding.AffinityModel;
import amat.binding.HammingAffinity;
import amat.epitope.Epitope;
import amat.receptor.Receptor;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class HammingAffinityPottsTest extends NumericTestBase {
    private final AffinityModel model = AffinityModel.global();

    private static final Structure structure1 = Structure.parse("PottsStructure(4; AAAA AAAA)");
    private static final Structure structure2 = Structure.parse("PottsStructure(4; AAAA ABCD)");
    private static final Structure structure3 = Structure.parse("PottsStructure(4; ABCD ABCD)");
    private static final Structure structure4 = Structure.parse("PottsStructure(4; ABBB BBBB)");
    private static final Structure structure5 = Structure.parse("PottsStructure(4; BBBB BBBB)");

    private static final Epitope epitope = Epitope.add("E1", structure1);

    private static final Receptor receptor1 = new Receptor(structure1);
    private static final Receptor receptor2 = new Receptor(structure2);
    private static final Receptor receptor3 = new Receptor(structure3);
    private static final Receptor receptor4 = new Receptor(structure4);
    private static final Receptor receptor5 = new Receptor(structure5);

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "HAMMING");
        System.setProperty(HammingAffinity.MATCH_GAIN_PROPERTY, "2.0");
    }

    @Test public void testAffinity() {
        assertDouble(12.0, model.computeAffinity(epitope, receptor1));
        assertDouble( 6.0, model.computeAffinity(epitope, receptor2));
        assertDouble( 0.0, model.computeAffinity(epitope, receptor3));
        assertDouble(-2.0, model.computeAffinity(epitope, receptor4));
        assertDouble(-4.0, model.computeAffinity(epitope, receptor5));
    }

    @Test public void testActEnergy() {
        assertDouble( 6.0, HammingAffinity.defaultActEnergy(1.0));
        assertDouble(12.0, HammingAffinity.defaultActEnergy(2.0));
        assertDouble(18.0, HammingAffinity.defaultActEnergy(3.0));
    }

    @Test public void testMeanDistance() {
        assertDouble(6.0, HammingAffinity.meanDistance());

        assertDouble(50.0, HammingAffinity.meanDistance(100, 2));
        assertDouble(75.0, HammingAffinity.meanDistance(100, 4));
        assertDouble(87.5, HammingAffinity.meanDistance(100, 8));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.HammingAffinityPottsTest");
    }
}
