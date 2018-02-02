
package amat.junit;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import jam.app.JamProperties;
import jam.math.DoubleUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.binding.AffinityModel;
import amat.epitope.Epitope;
import amat.bcell.BCell;
import amat.receptor.MutatorProperties;

import org.junit.*;
import static org.junit.Assert.*;

public class BCellTest {
    static {
        JamProperties.loadFile("test/driver_sample.prop", true);
        Epitope.load();
        Antigen.load();

        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "QUADRATIC");
        System.setProperty(AffinityModel.PRE_FACTOR_PROPERTY, "8.0");
        System.setProperty(AffinityModel.ACT_ENERGY_PROPERTY, "0.0");
    }

    @Test public void testFounder() {
        BCell founder = BCell.germline();

        assertTrue(founder.isFounder());
        assertNull(founder.getParent());
        assertEquals(founder, founder.getFounder());
        assertEquals(0, founder.getGeneration());
        assertEquals(0, founder.getMutationCount());
    }

    @Test public void testReplicate() {
        BCell founder = BCell.germline();
        BCell replica1 = founder.replicate();
        BCell replica2 = replica1.replicate();

        assertFalse(replica1.isFounder());
        assertFalse(replica2.isFounder());

        assertEquals(founder, replica1.getFounder());
        assertEquals(founder, replica2.getFounder());

        assertEquals(founder,  replica1.getParent());
        assertEquals(replica1, replica2.getParent());

        assertEquals(1, replica1.getGeneration());
        assertEquals(2, replica2.getGeneration());

        assertEquals(0, replica1.getMutationCount());
        assertEquals(0, replica2.getMutationCount());

        assertEquals(founder.getReceptor(), replica1.getReceptor());
        assertEquals(founder.getReceptor(), replica2.getReceptor());
    }

    @Test public void testDivide1() {
        int trialCount = 100000;

        BCell founder = BCell.germline();
        List<BCell> daughters = new ArrayList<BCell>();

        for (int index = 0; index < trialCount; index++)
            daughters.addAll(founder.divide());

        // Expect two daughters per division adjusted for the rate of
        // lethal mutations...
        double actualRate = DoubleUtil.ratio(daughters.size(), trialCount);
        double expectedRate = 2.0 * MutatorProperties.getReceptorSurvivalProbability().doubleValue();

        assertEquals(expectedRate, actualRate, 0.003);

        // Expect identical receptors for the silent mutations...
        int silentCount = 0;

        for (BCell daughter : daughters)
            if (daughter.getReceptor().equals(founder.getReceptor()))
                ++silentCount;

        actualRate = DoubleUtil.ratio(silentCount, trialCount);
        expectedRate = 2.0 * MutatorProperties.getReceptorSilentProbability().doubleValue();

        assertEquals(expectedRate, actualRate, 0.003);
    }

    @Test public void testDivide4() {
        BCell founder = BCell.germline();
        founder.setDivisionCount(4);

        int trialCount = 30000;
        Multiset<Integer> generations = HashMultiset.create();

        for (int index = 0; index < trialCount; index++)
            for (BCell daughter : founder.divide())
                generations.add(daughter.getGeneration());

        double actualRate1 = DoubleUtil.ratio(generations.count(1), trialCount);
        double actualRate2 = DoubleUtil.ratio(generations.count(2), trialCount);
        double actualRate3 = DoubleUtil.ratio(generations.count(3), trialCount);
        double actualRate4 = DoubleUtil.ratio(generations.count(4), trialCount);

        double expectedRate1 = 2.0 * MutatorProperties.getReceptorSurvivalProbability().doubleValue();
        double expectedRate2 = expectedRate1 * expectedRate1;
        double expectedRate3 = expectedRate1 * expectedRate2;
        double expectedRate4 = expectedRate1 * expectedRate3;

        assertEquals(expectedRate1, actualRate1, 0.02);
        assertEquals(expectedRate2, actualRate2, 0.02);
        assertEquals(expectedRate3, actualRate3, 0.02);
        assertEquals(expectedRate4, actualRate4, 0.02);
    }

    @Test public void testMaxAffinityComparator() {
        BCell[] cells = new BCell[100];
        Antigen antigen = Antigen.require("E1");

        AntigenPool pool = new AntigenPool();
        pool.add(antigen, 1.0);

        for (int k = 0; k < cells.length; k++)
            cells[k] = BCell.germline();

        for (int k = 0; k < cells.length; k++)
            cells[k].bind(pool);

        Arrays.sort(cells, BCell.MAX_AFFINITY_COMPARATOR);

        for (int k = 1; k < cells.length; k++)
            assertTrue(cells[k].getMaxAffinity() >= cells[k - 1].getMaxAffinity());
    }

    @Test public void testAntigenQtyComparator() {
        BCell[] cells = new BCell[100];

        AntigenPool pool = new AntigenPool();
        pool.add(Antigen.require("E1"), 1.0);
        pool.add(Antigen.require("E2"), 1.0);
        pool.add(Antigen.require("E3"), 1.0);
        pool.add(Antigen.require("E4"), 1.0);

        for (int k = 0; k < cells.length; k++)
            cells[k] = BCell.germline();

        for (int k = 0; k < cells.length; k++)
            cells[k].bind(pool);

        Arrays.sort(cells, BCell.ANTIGEN_QTY_COMPARATOR);

        for (int k = 1; k < cells.length; k++)
            assertTrue(cells[k].getAntigenQty() >= cells[k - 1].getAntigenQty());
    }

    @Test public void testTraceLineage() {
        List<BCell> lineage;

        BCell founder = BCell.germline();
        lineage = founder.traceLineage();

        assertEquals(1, lineage.size());
        assertEquals(founder, lineage.get(0));

        BCell daughter1 = founder.replicate();
        BCell daughter2 = daughter1.replicate();
        BCell daughter3 = daughter2.replicate();

        lineage = daughter3.traceLineage();

        assertEquals(4, lineage.size());
        assertEquals(founder, lineage.get(0));
        assertEquals(daughter1, lineage.get(1));
        assertEquals(daughter2, lineage.get(2));
        assertEquals(daughter3, lineage.get(3));

        lineage = daughter3.traceLineage(2);
        assertEquals(2, lineage.size());
        assertEquals(daughter2, lineage.get(0));
        assertEquals(daughter3, lineage.get(1));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.BCellTest");
    }
}
