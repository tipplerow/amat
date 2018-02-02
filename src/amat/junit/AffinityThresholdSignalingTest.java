
package amat.junit;

import java.util.Collections;
import java.util.Set;

import amat.bcell.BCell;
import amat.signal.BCRSignalingModel;

import org.junit.*;
import static org.junit.Assert.*;

public class AffinityThresholdSignalingTest extends BCellSelectionTestBase {
    static {
        System.setProperty(BCRSignalingModel.MODEL_TYPE_PROPERTY, "AFFINITY_THRESHOLD");
        System.setProperty(BCRSignalingModel.AFFINITY_THRESHOLD_PROPERTY, "1.2");
    }

    @Test public void testApoptosis() {
        int originalCount = 10000;

        Set<BCell> survived = prepare(originalCount);
        Set<BCell> perished = BCRSignalingModel.global().apoptose(survived);
        
        BCell weakestSurvived   = Collections.min(survived, BCell.MAX_AFFINITY_COMPARATOR);
        BCell strongestPerished = Collections.max(perished, BCell.MAX_AFFINITY_COMPARATOR);

        assertTrue(weakestSurvived.getMaxAffinity()   >= 1.2);
        assertTrue(strongestPerished.getMaxAffinity() <= 1.2);
        assertEquals(originalCount, perished.size() + survived.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AffinityThresholdSignalingTest");
    }
}
