
package amat.junit;

import jam.chem.Concentration;
import amat.occupy.OneOccupationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class OneOccupationModelTest extends OccupationModelTestBase {
    @Override public OneOccupationModel getModel() {
        return OneOccupationModel.INSTANCE;
    }

    @Test public void testVisit() {
        int minOcc = 1;
        int maxOcc = 1;

        double expected1 = Concentration.ratio(conc1, total);
        double expected2 = Concentration.ratio(conc2, total);
        double expected3 = Concentration.ratio(conc3, total);

        int sampleCount = 500000;
        double tolerance = 0.001;

        validateVisitation(minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.OneOccupationModelTest");
    }
}
