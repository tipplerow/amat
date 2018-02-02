
package amat.junit;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.DoubleUtil;

import amat.receptor.Mutator;
import amat.receptor.Receptor;
import amat.receptor.ReceptorGenerator;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class ReceptorTestBase extends StructureTestBase {

    public Receptor nextReceptor() {
        return ReceptorGenerator.global().generate();
    }

    public void assertReceptorSummary(List<Receptor> receptors,
                                     double[] expectedMean,
                                     double[] expectedStDev,
                                     double meanTolerance,
                                     double stDevTolerance) {
        List<Structure> structures = Receptor.getStructures(receptors);
        assertSummary(structures, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    public void runGeneratorTest(int receptorCount,
                                 double[] expectedMean,
                                 double[] expectedStDev,
                                 double meanTolerance,
                                 double stDevTolerance) {
        ArrayList<Receptor> receptors = new ArrayList<Receptor>(receptorCount);

        while (receptors.size() < receptorCount)
            receptors.add(nextReceptor());

        assertReceptorSummary(receptors, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    public void runUniqueTest(int receptorCount,
                              int expectedUnique,
                              int countTolerance) {
        HashMultiset<Receptor> receptorSet = HashMultiset.create();

        while (receptorSet.size() < receptorCount)
            receptorSet.add(nextReceptor());

        assertEquals(expectedUnique, receptorSet.elementSet().size());

        int minCount = (receptorCount / expectedUnique) - countTolerance;
        int maxCount = (receptorCount / expectedUnique) + countTolerance;

        for (Multiset.Entry<Receptor> entry : receptorSet.entrySet())
            assertTrue(minCount <= entry.getCount() && entry.getCount() <= maxCount);
    }
}
