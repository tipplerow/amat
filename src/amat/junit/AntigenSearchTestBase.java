
package amat.junit;

import java.util.Collection;
import java.util.HashMap;

import jam.chem.Concentration;
import jam.math.DoubleUtil;
import jam.math.JamRandom;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;
import amat.search.AntigenSearchModel;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class AntigenSearchTestBase {
    static {
        JamRandom.global(20161212L);

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
    public final Concentration conc2 = Concentration.valueOf(1.0);
    public final Concentration conc3 = Concentration.valueOf(3.0);

    public abstract AntigenSearchModel getModel();

    public AntigenPool getPool() {
        AntigenPool pool = new AntigenPool();

        pool.add(ag1, conc1);
        pool.add(ag2, conc2);
        pool.add(ag3, conc3);

        return pool;
    }

    public void validateVisitation(double expected1, 
                                   double expected2, 
                                   double expected3,
                                   int    sampleCount,
                                   double tolerance) {
        validateVisitation(0, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public void validateVisitation(int    cycleIndex,
                                   double expected1, 
                                   double expected2, 
                                   double expected3,
                                   int    sampleCount,
                                   double tolerance) {
        AntigenPool pool = getPool();
        HashMap<Antigen, Integer> agCount = new HashMap<Antigen, Integer>();

        agCount.put(ag1, 0);
        agCount.put(ag2, 0);
        agCount.put(ag3, 0);

        for (int index = 0; index < sampleCount; index++) 
            for (Antigen antigen : getModel().selectAntigens(cycleIndex, pool))
                agCount.put(antigen, 1 + agCount.get(antigen));

        assertEquals(expected1, DoubleUtil.ratio(agCount.get(ag1), sampleCount), tolerance);
        assertEquals(expected2, DoubleUtil.ratio(agCount.get(ag2), sampleCount), tolerance);
        assertEquals(expected3, DoubleUtil.ratio(agCount.get(ag3), sampleCount), tolerance);
    }
}
