
package amat.junit;

import jam.chem.Concentration;
import amat.occupy.AllOneOccupationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class AllOneOccupationModelTest extends OccupationModelTestBase {
    private final int TRANSITION_CYCLE = 10;

    @Override public AllOneOccupationModel getModel() {
        return new AllOneOccupationModel(TRANSITION_CYCLE);
    }

    @Test public void testVisit() {
        int minOcc = 3;
        int maxOcc = 3;

        double expected1 = 1.0;
        double expected2 = 1.0;
        double expected3 = 1.0;

        int sampleCount = 100;
        double tolerance = 1.0E-12;

        validateVisitation(0, minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
        validateVisitation(TRANSITION_CYCLE - 1, minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);

        minOcc = 1;
        maxOcc = 1;

        expected1 = Concentration.ratio(conc1, total);
        expected2 = Concentration.ratio(conc2, total);
        expected3 = Concentration.ratio(conc3, total);

        sampleCount = 50000;
        tolerance = 0.01;

        validateVisitation(TRANSITION_CYCLE, minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
        validateVisitation(TRANSITION_CYCLE + 100, minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AllOneOccupationModelTest");
    }
}
