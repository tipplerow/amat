
package amat.junit;

import java.util.List;

import jam.junit.NumericTestBase;
import jam.math.StatSummary;
import jam.matrix.JamMatrix;
import jam.vector.VectorView;

import amat.structure.NumericStructure;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class StructureTestBase extends NumericTestBase {

    public void assertSummary(List<Structure> structures,
                              double[] expectedMean,
                              double[] expectedStDev,
                              double meanTolerance,
                              double stDevTolerance) {
        JamMatrix structMatrix = assemble(structures);

        assertEquals(structMatrix.ncol(), expectedMean.length);
        assertEquals(structMatrix.ncol(), expectedStDev.length);

        for (int col = 0; col < structMatrix.ncol(); col++)
            assertSummary(structMatrix.viewColumn(col),
                          expectedMean[col],
                          expectedStDev[col],
                          meanTolerance,
                          stDevTolerance);
    }

    private static JamMatrix assemble(List<Structure> structures) {
        int nrow = structures.size();
        int ncol = structures.get(0).length();

        int row = 0;
        JamMatrix structMatrix = new JamMatrix(nrow, ncol);

        for (Structure structure : structures)
            structMatrix.setRow(row++, ((NumericStructure) structure).asNumeric());

        return structMatrix;
    }

    private static void assertSummary(VectorView numericStruct,
                                      double expectedMean,
                                      double expectedStDev,
                                      double meanTolerance,
                                      double stDevTolerance) {
        StatSummary summary = 
            StatSummary.compute(numericStruct);

        assertEquals(expectedMean,  summary.getMean(), meanTolerance);
        assertEquals(expectedStDev, summary.getSD(),   stDevTolerance);
    }
}
