
package amat.junit;

import jam.chem.MultiLangmuir;
import amat.occupy.LangmuirOccupationModel;

import org.junit.*;
import static org.junit.Assert.*;

public class LangmuirOccupationModelTest extends OccupationModelTestBase {
    @Override public LangmuirOccupationModel getModel() {
        return LangmuirOccupationModel.INSTANCE;
    }

    @Test public void testVisit() {
        MultiLangmuir lang = new MultiLangmuir(conc1, conc2, conc3);

        int minOcc = 0;
        int maxOcc = 1;

        double expected1 = lang.evaluate(0);
        double expected2 = lang.evaluate(1);
        double expected3 = lang.evaluate(2);

        int sampleCount = 750000;
        double tolerance = 0.001;

        validateVisitation(minOcc, maxOcc, expected1, expected2, expected3, sampleCount, tolerance);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.LangmuirOccupationModelTest");
    }
}
