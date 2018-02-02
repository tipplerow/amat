
package amat.junit;

import java.util.Collections;
import java.util.Set;

import amat.bcell.BCell;
import amat.plasma.PlasmaSelectionModel;

import org.junit.*;
import static org.junit.Assert.*;

public class PlasmaSelectionModelTest extends BCellSelectionTestBase {
    static {
        System.setProperty(PlasmaSelectionModel.AFFINITY_THRESHOLD_PROPERTY, "5.0");
        System.setProperty(PlasmaSelectionModel.SELECTION_PROBABILITY_PROPERTY, "0.1");
    }

    @Test public void testSelection() {
        int originalCount = 10000;
        
        Set<BCell> original = prepare(originalCount);
        Set<BCell> selected = PlasmaSelectionModel.global().select(original);

        BCell weakestSelected = Collections.min(selected, BCell.MAX_AFFINITY_COMPARATOR);

        assertTrue(selected.size() < 1000);
        assertTrue(weakestSelected.getMaxAffinity() >= 5.0);
        assertEquals(originalCount, original.size() + selected.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.PlasmaSelectionModelTest");
    }
}
