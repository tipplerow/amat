
package amat.junit;

import amat.receptor.ReceptorProperties;

import org.junit.*;
import static org.junit.Assert.*;

public class PottsReceptorTest extends ReceptorTestBase {
    static {
        System.setProperty(ReceptorProperties.STRUCTURE_TYPE_PROPERTY, "POTTS");
        System.setProperty(ReceptorProperties.CARDINALITY_PROPERTY, "4");
        System.setProperty(ReceptorProperties.LENGTH_PROPERTY, "3");
    }

    @Test public void testGenerator() {
        int receptorCount = 100000;

        double[] expectedMean  = new double[] { 1.5,  1.5,  1.5 };
        double[] expectedStDev = new double[] { 1.12, 1.12, 1.12 };

        double meanTolerance  = 0.005;
        double stDevTolerance = 0.01;

        runGeneratorTest(receptorCount, expectedMean, expectedStDev, meanTolerance, stDevTolerance);
    }

    @Test public void testUnique() {
        runUniqueTest(64000, 64, 100);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.PottsReceptorTest");
    }
}
