
package amat.junit;

import java.util.Set;

import amat.bcell.BCell;
import amat.binding.AffinityModel;
import amat.epitope.Epitope;
import amat.germline.GermlineActivationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class GermlineActivationModelTest extends BCellSelectionTestBase {
    static {
        System.setProperty(GermlineActivationModel.AFFINITY_THRESHOLD_PROPERTY, "4.0");
        System.setProperty(GermlineActivationModel.GERMLINE_COUNT_PROPERTY,     "100");
        System.setProperty(GermlineActivationModel.REPLICATION_FACTOR_PROPERTY, "15");
    }

    @Test public void testActivation() {
        Set<BCell> germlines = 
            GermlineActivationModel.global().activate(antigenPool);

        assertEquals(100, germlines.size());

        for (BCell germline : germlines)
            assertTrue(computeMaxAffinity(germline) >= 3.9999);

        for (BCell germline : germlines) {
            assertTrue(germline.isFounder());
            assertEquals(0, germline.getMutationCount());
            assertEquals(0, germline.getGeneration());
        }
    }

    private double computeMaxAffinity(BCell germline) {
        double maxAffinity = Double.NEGATIVE_INFINITY;

        for (Epitope epitope : antigenPool.viewEpitopes()) {
            double epiAffinity = AffinityModel.global().computeAffinity(epitope, germline.getReceptor());

            if (epiAffinity > maxAffinity)
                maxAffinity = epiAffinity;
        }

        return maxAffinity;
    }

    @Test public void testReplication() {
        Set<BCell> germlines  = GermlineActivationModel.global().activate(antigenPool);
        Set<BCell> replicants = GermlineActivationModel.global().replicate(germlines);

        assertEquals(1500, replicants.size());

        for (BCell replicant : replicants) {
            assertFalse(replicant.isFounder());
            assertEquals(0, replicant.getMutationCount());
            assertEquals(1, replicant.getGeneration());
            assertTrue(germlines.contains(replicant.getFounder()));
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.GermlineActivationModelTest");
    }
}
