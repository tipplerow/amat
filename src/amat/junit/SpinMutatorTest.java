
package amat.junit;

import amat.epitope.Epitope;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class SpinMutatorTest extends MutatorTestBase {
    static {
        // Receptor properties will be derived from the epitope...
        Epitope.add("E1", Structure.parse("SpinStructure(+-+-+)"));
    }

    @Test public void testMutator() {
        int mutationCount = 100000;

        double lethalTolerance  = 0.01;
        double silentTolerance  = 0.01;
        double somaticTolerance = 0.01;

        double[] expectedMean  = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
        double[] expectedStDev = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };

        double meanTolerance  = 0.02;
        double stDevTolerance = 0.0001;

        runMutatorTest(mutationCount,
                       lethalTolerance,
                       silentTolerance,
                       somaticTolerance,
                       expectedMean,
                       expectedStDev,
                       meanTolerance,
                       stDevTolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.SpinMutatorTest");
    }
}
