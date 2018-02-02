
package amat.junit;

import java.util.Collections;
import java.util.Set;

import amat.bcell.BCell;
import amat.tcell.TCellCompetitionModel;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenQtyRankCompetitionTest extends BCellSelectionTestBase {
    static {
        System.setProperty(TCellCompetitionModel.MODEL_TYPE_PROPERTY, "ANTIGEN_QTY_RANK");
        System.setProperty(TCellCompetitionModel.SURVIVAL_RATE_PROPERTY, "0.7");
    }

    @Test public void testCompetition() {
        int originalCount = 1000;
        
        Set<BCell> survived = prepare(originalCount);
        Set<BCell> perished = TCellCompetitionModel.global().apoptose(survived);

        BCell weakestSurvived   = Collections.min(survived, BCell.ANTIGEN_QTY_COMPARATOR);
        BCell strongestPerished = Collections.max(perished, BCell.ANTIGEN_QTY_COMPARATOR);

        double meanSurvived = survived.stream().mapToDouble(bcell -> bcell.getAntigenQty()).average().orElse(Double.NaN);
        double meanPerished = perished.stream().mapToDouble(bcell -> bcell.getAntigenQty()).average().orElse(Double.NaN);

        assertEquals(300, perished.size());
        assertEquals(700, survived.size());

        assertTrue(meanSurvived > meanPerished);
        assertTrue(weakestSurvived.getAntigenQty() >= strongestPerished.getAntigenQty());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AntigenQtyRankCompetitionTest");
    }
}
