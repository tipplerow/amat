
package amat.junit;

import java.util.HashSet;
import java.util.Set;

import jam.junit.NumericTestBase;
import jam.math.DoubleUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.binding.AffinityModel;
import amat.binding.HammingAffinity;
import amat.capture.EpitopeCaptureModel;
import amat.epitope.Epitope;
import amat.epitope.Epitope;
import amat.signal.BCRSignalingModel;
import amat.receptor.Receptor;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class QuantityLangmuirSignalingTest extends NumericTestBase {
    private static final Structure epiStruct = Structure.parse("BitStructure(0000 0000)");
    private static final Structure recStruct = Structure.parse("BitStructure(0000 1111)");

    private static final Epitope epitope = Epitope.add("E1", epiStruct);
    private static final Antigen antigen = Antigen.add("A1", epitope);

    private static final Receptor receptor = new Receptor(recStruct);

    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "HAMMING");
        System.setProperty(HammingAffinity.MATCH_GAIN_PROPERTY, "2.0");

        System.setProperty(BCRSignalingModel.MODEL_TYPE_PROPERTY, "QUANTITY_LANGMUIR");
        System.setProperty(EpitopeCaptureModel.MODEL_TYPE_PROPERTY, "CK");
    }

    @Test public void testApoptosis() {
        runApoptosisTest(0.2);
        runApoptosisTest(1.0);
        runApoptosisTest(5.0);
    }

    private void runApoptosisTest(double conc) {
        AntigenPool pool = new AntigenPool();
        pool.add(antigen, conc);

        BCell germline = BCell.germline(receptor);
        germline.bind(pool);

        // With zero affinity, the equilibrium constant is one, the
        // amount of antigen captured is equal to the concentration,
        // and the probability of survival is [conc / (1.0 + conc)].
        assertDouble(0.0, germline.getMaxAffinity());
        assertDouble(conc, germline.getAntigenQty());

        int originalSize = 10000;
        Set<BCell> survived = new HashSet<BCell>();

        while (survived.size() < originalSize) {
            germline = BCell.germline(receptor);
            germline.bind(pool);
            survived.add(germline);
        }

        Set<BCell> perished = BCRSignalingModel.global().apoptose(survived);
        assertEquals(originalSize, perished.size() + survived.size());

        double expectedSurvived = conc / (1.0 + conc);
        double expectedPerished = 1.0 - expectedSurvived;

        double actualSurvived = DoubleUtil.ratio(survived.size(), originalSize);
        double actualPerished = DoubleUtil.ratio(perished.size(), originalSize);

        assertEquals(expectedSurvived, actualSurvived, 0.01);
        assertEquals(expectedPerished, actualPerished, 0.01);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.QuantityLangmuirSignalingTest");
    }
}
