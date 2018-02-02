
package amat.junit;

import jam.chem.Concentration;
import amat.occupy.AllOccupationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class AllOccupationModelTest extends OccupationModelTestBase {
    @Override public AllOccupationModel getModel() {
        return AllOccupationModel.INSTANCE;
    }

    @Test public void testVisit() {
        int minOcc = 3;
        int maxOcc = 3;

        double expected1 = 1.0;
        double expected2 = 1.0;
        double expected3 = 1.0;

        int sampleCount = 100;
        double tolerance = 1.0E-12;

        validateVisitation(minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AllOccupationModelTest");
    }
}
