
package amat.junit;

import java.util.Collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.chem.Concentration;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;
import amat.occupy.OccupationModel;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class OccupationModelTestBase {
    static {
        JamRandom.global(20170517L);

        Epitope.parse("E1: BitStructure(100)");
        Epitope.parse("E2: BitStructure(010)");
        Epitope.parse("E3: BitStructure(001)");
        
        Antigen.parse("AG1: E1");
        Antigen.parse("AG2: E2");
        Antigen.parse("AG3: E3");
    }

    public final Antigen ag1 = Antigen.require("AG1");
    public final Antigen ag2 = Antigen.require("AG2");
    public final Antigen ag3 = Antigen.require("AG3");

    public final Concentration conc1 = Concentration.valueOf(0.5);
    public final Concentration conc2 = Concentration.valueOf(1.5);
    public final Concentration conc3 = Concentration.valueOf(2.0);
    public final Concentration total = Concentration.total(conc1, conc2, conc3);

    public abstract OccupationModel getModel();

    public AntigenPool getPool() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, conc1);
        pool.add(ag2, conc2);
        pool.add(ag3, conc3);

        return pool;
    }

    public void validateVisitation(int    minOcc,
                                   int    maxOcc,
                                   double expected1, 
                                   double expected2, 
                                   double expected3,
                                   int    sampleCount,
                                   double tolerance) {
        validateVisitation(0, minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public void validateVisitation(int    cycleIndex,
                                   int    minOcc,
                                   int    maxOcc,
                                   double expected1, 
                                   double expected2, 
                                   double expected3,
                                   int    sampleCount,
                                   double tolerance) {
        AntigenPool pool = getPool();
        Multiset<Antigen> antigens = HashMultiset.create();

        for (int index = 0; index < sampleCount; index++) {
            Collection<Antigen> occupied = getModel().visit(cycleIndex, pool);

            assertTrue(occupied.size() >= minOcc);
            assertTrue(occupied.size() <= maxOcc);

            for (Antigen antigen : occupied)
                antigens.add(antigen);
        }

        assertEquals(expected1, DoubleUtil.ratio(antigens.count(ag1), sampleCount), tolerance);
        assertEquals(expected2, DoubleUtil.ratio(antigens.count(ag2), sampleCount), tolerance);
        assertEquals(expected3, DoubleUtil.ratio(antigens.count(ag3), sampleCount), tolerance);
    }
}
