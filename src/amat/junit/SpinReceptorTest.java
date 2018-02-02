
package amat.junit;

import amat.receptor.ReceptorProperties;

import org.junit.*;
import static org.junit.Assert.*;

public class SpinReceptorTest extends ReceptorTestBase {
    static {
        System.setProperty(ReceptorProperties.STRUCTURE_TYPE_PROPERTY, "SPIN");
        System.setProperty(ReceptorProperties.LENGTH_PROPERTY, "5");
    }

    @Test public void testGenerator() {
        int receptorCount = 100000;

        double[] expectedMean  = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
        double[] expectedStDev = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };

        double meanTolerance  = 0.005;
        double stDevTolerance = 0.00001;

        runGeneratorTest(receptorCount, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    @Test public void testUnique() {
        runUniqueTest(32000, 32, 100);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.SpinReceptorTest");
    }
}
