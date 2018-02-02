
package amat.junit;

import java.util.ArrayList;

import jam.math.DoubleUtil;

import amat.receptor.MutationType;
import amat.receptor.Mutator;
import amat.receptor.MutatorProperties;
import amat.receptor.Receptor;

import org.junit.*;
import static org.junit.Assert.*;

public class MutatorTestBase extends ReceptorTestBase {
    public final Mutator mutator = Mutator.global();

    public void runMutatorTest(int mutationCount,
                               double lethalTolerance,
                               double silentTolerance,
                               double somaticTolerance,
                               double[] expectedMean,
                               double[] expectedStDev,
                               double meanTolerance,
                               double stDevTolerance) {
        int lethalCount = 0;
        int silentCount = 0;

        ArrayList<Receptor> parents   = new ArrayList<Receptor>(mutationCount);
        ArrayList<Receptor> daughters = new ArrayList<Receptor>(mutationCount);

        for (int index = 0; index < mutationCount; index++) {
            Receptor parent   = nextReceptor();
            Receptor daughter = mutator.mutate(parent);

            parents.add(parent);

            if (daughter == null)
                ++lethalCount;
            else if (daughter == parent)
                ++silentCount;
            else
                daughters.add(daughter);
        }

        double actualLethal  = DoubleUtil.ratio(lethalCount, mutationCount);
        double actualSilent  = DoubleUtil.ratio(silentCount, mutationCount);
        double actualSomatic = DoubleUtil.ratio(daughters.size(), mutationCount);

        assertDouble(actualLethal,  mutator.getFrequency(MutationType.LETHAL));
        assertDouble(actualSilent,  mutator.getFrequency(MutationType.SILENT));
        assertDouble(actualSomatic, mutator.getFrequency(MutationType.SOMATIC));

        double expectedLethal  = MutatorProperties.getReceptorLethalProbability().doubleValue();
        double expectedSilent  = MutatorProperties.getReceptorSilentProbability().doubleValue();
        double expectedSomatic = MutatorProperties.getReceptorSomaticProbability().doubleValue();

        assertEquals(expectedLethal,  actualLethal,  lethalTolerance);
        assertEquals(expectedSilent,  actualSilent,  silentTolerance);
        assertEquals(expectedSomatic, actualSomatic, silentTolerance);

        assertReceptorSummary(parents, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
        assertReceptorSummary(daughters, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }
}
