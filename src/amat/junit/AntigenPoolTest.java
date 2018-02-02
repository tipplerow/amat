
package amat.junit;

import java.util.Arrays;
import java.util.HashMap;

import jam.chem.Concentration;
import jam.chem.HalfLife;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenPoolTest {
    static {
        JamRandom.global(20161212L);

        Epitope.parse("E1: BitStructure(100)");
        Epitope.parse("E2: BitStructure(010)");
        Epitope.parse("E3: BitStructure(001)");
        Epitope.parse("E4: BitStructure(000)");
        
        Antigen.parse("AG1: E1");
        Antigen.parse("AG2: E2");
        Antigen.parse("AG3: E3");
        Antigen.parse("AG4: E4");
    }

    private final Antigen ag1 = Antigen.require("AG1");
    private final Antigen ag2 = Antigen.require("AG2");
    private final Antigen ag3 = Antigen.require("AG3");
    private final Antigen ag4 = Antigen.require("AG4");

    private void assertConc(double expected, Concentration actual) {
        assertTrue(actual.equals(expected));
    }

    @Test public void testAddRemove() {
        AntigenPool pool = new AntigenPool();

        assertEquals(0, pool.size());
        assertTrue(pool.isEmpty());

        assertFalse(pool.contains(ag1));
        assertFalse(pool.contains(ag2));
        assertFalse(pool.contains(ag3));

        assertConc(0.0, pool.getConcentration(ag1));
        assertConc(0.0, pool.getConcentration(ag2));
        assertConc(0.0, pool.getConcentration(ag3));
        assertConc(0.0, pool.getTotalConc());

        pool.add(ag1, 1.0);

        assertEquals(1, pool.size());
        assertFalse(pool.isEmpty());

        assertTrue(pool.contains(ag1));
        assertFalse(pool.contains(ag2));
        assertFalse(pool.contains(ag3));

        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(0.0, pool.getConcentration(ag2));
        assertConc(0.0, pool.getConcentration(ag3));
        assertConc(1.0, pool.getTotalConc());

        pool.add(ag2, 2.0);

        assertEquals(2, pool.size());
        assertFalse(pool.isEmpty());

        assertTrue(pool.contains(ag1));
        assertTrue(pool.contains(ag2));
        assertFalse(pool.contains(ag3));

        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));
        assertConc(0.0, pool.getConcentration(ag3));
        assertConc(3.0, pool.getTotalConc());

        pool.remove(ag1);

        assertEquals(1, pool.size());
        assertFalse(pool.isEmpty());

        assertFalse(pool.contains(ag1));
        assertTrue(pool.contains(ag2));
        assertFalse(pool.contains(ag3));

        assertConc(0.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));
        assertConc(0.0, pool.getConcentration(ag3));
        assertConc(2.0, pool.getTotalConc());
    }

    @Test public void testDecayAll() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, 1.0);
        pool.add(ag2, 2.0);
        pool.add(ag3, 3.0);

        pool.decay(HalfLife.valueOf(1.0));

        assertConc(0.5, pool.getConcentration(ag1));
        assertConc(1.0, pool.getConcentration(ag2));
        assertConc(1.5, pool.getConcentration(ag3));
        assertConc(3.0, pool.getTotalConc());
    }

    @Test public void testDecayOne() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, 1.0);
        pool.add(ag2, 2.0);
        pool.add(ag3, 3.0);

        pool.decay(ag1, HalfLife.valueOf(1.0));

        assertConc(0.5, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));
        assertConc(3.0, pool.getConcentration(ag3));
        assertConc(5.5, pool.getTotalConc());
    }

    @Test public void testIsUniform() {
        AntigenPool pool = new AntigenPool();
        assertTrue(pool.isUniform());

        pool.add(ag1, 1.0);
        assertTrue(pool.isUniform());

        pool.add(ag2, 1.0);
        assertTrue(pool.isUniform());

        pool.add(ag3, 2.0);
        assertFalse(pool.isUniform());

        pool.add(ag4, 3.0);
        assertFalse(pool.isUniform());
    }

    @Test public void testSelect() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, 1.0);
        validateSelection(pool, 1.0, 0.0, 0.0);

        pool.add(ag2, 2.0);
        validateSelection(pool, 0.333, 0.667, 0.0);

        pool.add(ag3, 3.0);
        validateSelection(pool, 0.167, 0.333, 0.500);
    }

    private void validateSelection(AntigenPool pool, 
                                   double expected1, 
                                   double expected2, 
                                   double expected3) {
        int sampleCount = 1000000;
        double tolerance = 0.002;

        HashMap<Antigen, Integer> agCount = new HashMap<Antigen, Integer>();

        agCount.put(ag1, 0);
        agCount.put(ag2, 0);
        agCount.put(ag3, 0);

        for (int index = 0; index < sampleCount; index++) {
            Antigen antigen = pool.select(JamRandom.global());
            agCount.put(antigen, 1 + agCount.get(antigen));
        }

        assertEquals(expected1, DoubleUtil.ratio(agCount.get(ag1), sampleCount), tolerance);
        assertEquals(expected2, DoubleUtil.ratio(agCount.get(ag2), sampleCount), tolerance);
        assertEquals(expected3, DoubleUtil.ratio(agCount.get(ag3), sampleCount), tolerance);
    }

    @Test(expected = RuntimeException.class)
    public void testSelectEmpty() {
        new AntigenPool().select(JamRandom.global());
    }

    @Test public void testSetConcentration() {
        AntigenPool pool = new AntigenPool();

        pool.setConcentration(ag1, 1.0);
        pool.setConcentration(ag2, 2.0);

        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));
        assertConc(3.0, pool.getTotalConc());

        pool.setConcentration(ag1, 5.0);
        pool.setConcentration(ag2, 8.0);

        assertConc(5.0, pool.getConcentration(ag1));
        assertConc(8.0, pool.getConcentration(ag2));
        assertConc(13.0, pool.getTotalConc());

        pool.setConcentration(ag1, 0.0);
        assertFalse(pool.contains(ag1));
        assertConc(0.0, pool.getConcentration(ag1));
        assertConc(8.0, pool.getConcentration(ag2));
        assertConc(8.0, pool.getTotalConc());
    }

    @Test public void testSubset() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, 1.0);
        pool.add(ag2, 2.0);
        pool.add(ag3, 3.0);
        pool.add(ag4, 4.0);

        AntigenPool subset = pool.subset(Arrays.asList(ag1, ag4));

        assertTrue(subset.contains(ag1));
        assertTrue(subset.contains(ag4));
        assertFalse(subset.contains(ag2));
        assertFalse(subset.contains(ag3));

        assertConc(1.0, subset.getConcentration(ag1));
        assertConc(0.0, subset.getConcentration(ag2));
        assertConc(0.0, subset.getConcentration(ag3));
        assertConc(4.0, subset.getConcentration(ag4));

        // Ensure that the original pool is unchanged when the subset
        // is altered...
        subset.decay(HalfLife.valueOf(1.0));

        assertConc(0.5, subset.getConcentration(ag1));
        assertConc(0.0, subset.getConcentration(ag2));
        assertConc(0.0, subset.getConcentration(ag3));
        assertConc(2.0, subset.getConcentration(ag4));

        assertConc(1.0, pool.getConcentration(ag1));
        assertConc(2.0, pool.getConcentration(ag2));
        assertConc(3.0, pool.getConcentration(ag3));
        assertConc(4.0, pool.getConcentration(ag4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubsetInvalid() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, 1.0);
        pool.add(ag2, 2.0);
        pool.add(ag3, 3.0);

        pool.subset(Arrays.asList(ag1, ag4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AntigenPoolTest");
    }
}
