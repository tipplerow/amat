
package amat.junit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.DoubleUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;
import amat.visit.FixedCountVisitation;

import org.junit.*;
import static org.junit.Assert.*;

public class FixedCountVisitationTest extends AmatTestBase {
    static {
        Epitope.parse("E1: BitStructure(0000)");
        Epitope.parse("E2: BitStructure(0001)");
        Epitope.parse("E3: BitStructure(0010)");
        
        Antigen.parse("AG1: E1");
        Antigen.parse("AG2: E2");
        Antigen.parse("AG3: E3");
    }

    private final Antigen ag1 = Antigen.require("AG1");
    private final Antigen ag2 = Antigen.require("AG2");
    private final Antigen ag3 = Antigen.require("AG3");

    private final AntigenPool pool3 = createPool3();

    private static AntigenPool createPool3() {
        AntigenPool pool = new AntigenPool();

        pool.add(Antigen.require("AG1"), 0.5);
        pool.add(Antigen.require("AG2"), 1.0);
        pool.add(Antigen.require("AG3"), 1.5);

        return pool;
    }

    @Test public void testVisit() {
        validateVisit( 1, 100000, 0.01, 0.125, 0.250, 0.375);
        validateVisit(10, 100000, 0.01, 1.25,  2.50,  3.75);
    }

    private void validateVisit(int    fixedCount,
                               int    trialCount, 
                               double tolerance, 
                               double expected1,
                               double expected2,
                               double expected3) {
        Multiset<Antigen> visited = HashMultiset.create();
        FixedCountVisitation model = new FixedCountVisitation(fixedCount);

        for (int trialIndex = 0; trialIndex < trialCount; ++trialIndex)
            visited.addAll(model.visit(0, pool3));

        assertEquals(expected1, DoubleUtil.ratio(visited.count(ag1), trialCount), tolerance);
        assertEquals(expected2, DoubleUtil.ratio(visited.count(ag2), trialCount), tolerance);
        assertEquals(expected3, DoubleUtil.ratio(visited.count(ag3), trialCount), tolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.FixedCountVisitationTest");
    }
}
