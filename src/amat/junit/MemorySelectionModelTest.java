
package amat.junit;

import java.util.Set;

import jam.math.IntRange;

import amat.bcell.BCell;
import amat.memory.MemorySelectionModel;

import org.junit.*;
import static org.junit.Assert.*;

public class MemorySelectionModelTest extends BCellSelectionTestBase {
    static {
        System.setProperty(MemorySelectionModel.SELECTION_PROBABILITY_PROPERTY, "0.1");
    }

    @Test public void testSelection() {
        int originalCount = 10000;
        
        Set<BCell> original = prepare(originalCount);
        Set<BCell> selected = MemorySelectionModel.global().select(original);

        IntRange selectedRange = new IntRange(950, 1050);

        assertTrue(selectedRange.contains(selected.size()));
        assertEquals(originalCount, original.size() + selected.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.MemorySelectionModelTest");
    }
}
