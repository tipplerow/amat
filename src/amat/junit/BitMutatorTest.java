
package amat.junit;

import amat.epitope.Epitope;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class BitMutatorTest extends MutatorTestBase {
    static {
        // Receptor properties will be derived from the epitope...
        Epitope.add("E1", Structure.parse("BitStructure(01010)"));
    }

    @Test public void testMutator() {
        int mutationCount = 100000;

        double lethalTolerance  = 0.01;
        double silentTolerance  = 0.01;
        double somaticTolerance = 0.01;

        double[] expectedMean  = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5 };
        double[] expectedStDev = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5 };

        double meanTolerance  = 0.01;
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
        org.junit.runner.JUnitCore.main("amat.junit.BitMutatorTest");
    }
}
