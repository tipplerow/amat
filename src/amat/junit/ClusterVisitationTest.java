
package amat.junit;

import jam.dist.DiscretePDF;
import jam.junit.NumericTestBase;
import jam.markov.StochasticMatrix;
import jam.math.Probability;
import jam.matrix.JamMatrix;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;
import amat.visit.ClusterVisitation;

import org.junit.*;
import static org.junit.Assert.*;

public class ClusterVisitationTest extends NumericTestBase {
    static {
        Epitope.parse("E1: BitStructure(0000)");
        Epitope.parse("E2: BitStructure(0001)");
        Epitope.parse("E3: BitStructure(0010)");
        Epitope.parse("E4: BitStructure(0011)");
        Epitope.parse("E5: BitStructure(0100)");
        
        Antigen.parse("AG1: E1");
        Antigen.parse("AG2: E2");
        Antigen.parse("AG3: E3");
        Antigen.parse("AG4: E4");
        Antigen.parse("AG5: E5");
    }

    private final Antigen ag1 = Antigen.require("AG1");
    private final Antigen ag2 = Antigen.require("AG2");
    private final Antigen ag3 = Antigen.require("AG3");
    private final Antigen ag4 = Antigen.require("AG4");
    private final Antigen ag5 = Antigen.require("AG5");

    private final AntigenPool pool2 = createPool2();
    private final AntigenPool pool5 = createPool5();

    private final DiscretePDF pdf2 = DiscretePDF.create(0, new double[] { 0.5, 0.5 });
    private final DiscretePDF pdf5 = DiscretePDF.create(0, new double[] { 0.20, 0.20, 0.20, 0.20, 0.20 });

    private static AntigenPool createPool2() {
        AntigenPool pool = new AntigenPool();

        pool.add(Antigen.require("AG1"), 1.0);
        pool.add(Antigen.require("AG2"), 1.0);

        return pool;
    }

    private static AntigenPool createPool5() {
        AntigenPool pool = new AntigenPool();

        pool.add(Antigen.require("AG1"), 0.20);
        pool.add(Antigen.require("AG2"), 0.20);
        pool.add(Antigen.require("AG3"), 0.20);
        pool.add(Antigen.require("AG4"), 0.20);
        pool.add(Antigen.require("AG5"), 0.20);

        return pool;
    }

    @Test public void testTwo() {
        StochasticMatrix m1 = ClusterVisitation.createStochasticMatrix(pool2, Probability.valueOf(0.0));
        StochasticMatrix m2 = ClusterVisitation.createStochasticMatrix(pool2, Probability.valueOf(0.25));
        StochasticMatrix m3 = ClusterVisitation.createStochasticMatrix(pool2, Probability.valueOf(0.50));
        StochasticMatrix m4 = ClusterVisitation.createStochasticMatrix(pool2, Probability.valueOf(0.75));
        StochasticMatrix m5 = ClusterVisitation.createStochasticMatrix(pool2, Probability.valueOf(0.95));

        assertEquals(JamMatrix.byrow(2, 2, 0.0,  1.0,  1.0,  0.0),  m1.getTransitionProbability());
        assertEquals(JamMatrix.byrow(2, 2, 0.25, 0.75, 0.75, 0.25), m2.getTransitionProbability());
        assertEquals(JamMatrix.byrow(2, 2, 0.5,  0.5,  0.5,  0.5),  m3.getTransitionProbability());
        assertEquals(JamMatrix.byrow(2, 2, 0.75, 0.25, 0.25, 0.75), m4.getTransitionProbability());
        assertEquals(JamMatrix.byrow(2, 2, 0.95, 0.05, 0.05, 0.95), m5.getTransitionProbability());

        assertEquals(pdf2, m1.getStationaryPDF());
        assertEquals(pdf2, m2.getStationaryPDF());
        assertEquals(pdf2, m3.getStationaryPDF());
        assertEquals(pdf2, m4.getStationaryPDF());
        assertEquals(pdf2, m5.getStationaryPDF());
    }

    @Test public void testFive() {
        StochasticMatrix m1 = ClusterVisitation.createStochasticMatrix(pool5, Probability.valueOf(0.0));
        StochasticMatrix m2 = ClusterVisitation.createStochasticMatrix(pool5, Probability.valueOf(0.1));
        StochasticMatrix m3 = ClusterVisitation.createStochasticMatrix(pool5, Probability.valueOf(0.2));
        StochasticMatrix m4 = ClusterVisitation.createStochasticMatrix(pool5, Probability.valueOf(0.5));
        StochasticMatrix m5 = ClusterVisitation.createStochasticMatrix(pool5, Probability.valueOf(0.9));

        assertEquals(JamMatrix.byrow(5, 5, 
                                     0.0,  0.25, 0.25, 0.25, 0.25,
                                     0.25, 0.0,  0.25, 0.25, 0.25,
                                     0.25, 0.25, 0.0,  0.25, 0.25,
                                     0.25, 0.25, 0.25, 0.0,  0.25,
                                     0.25, 0.25, 0.25, 0.25, 0.0),
                     m1.getTransitionProbability());

        assertEquals(JamMatrix.byrow(5, 5,
                                     0.1,   0.225, 0.225, 0.225, 0.225,
                                     0.225, 0.1,   0.225, 0.225, 0.225,
                                     0.225, 0.225, 0.1,   0.225, 0.225,
                                     0.225, 0.225, 0.225, 0.1,   0.225,
                                     0.225, 0.225, 0.225, 0.225, 0.1),
                     m2.getTransitionProbability());

        assertEquals(JamMatrix.byrow(5, 5,
                                     0.2, 0.2, 0.2, 0.2, 0.2,
                                     0.2, 0.2, 0.2, 0.2, 0.2,
                                     0.2, 0.2, 0.2, 0.2, 0.2,
                                     0.2, 0.2, 0.2, 0.2, 0.2,
                                     0.2, 0.2, 0.2, 0.2, 0.2),
                     m3.getTransitionProbability());

        assertEquals(JamMatrix.byrow(5, 5,
                                     0.5,   0.125, 0.125, 0.125, 0.125,
                                     0.125, 0.5,   0.125, 0.125, 0.125,
                                     0.125, 0.125, 0.5,   0.125, 0.125,
                                     0.125, 0.125, 0.125, 0.5,   0.125,
                                     0.125, 0.125, 0.125, 0.125, 0.5),
                     m4.getTransitionProbability());

        assertEquals(JamMatrix.byrow(5, 5,
                                     0.9,   0.025, 0.025, 0.025, 0.025,
                                     0.025, 0.9,   0.025, 0.025, 0.025,
                                     0.025, 0.025, 0.9,   0.025, 0.025,
                                     0.025, 0.025, 0.025, 0.9,   0.025,
                                     0.025, 0.025, 0.025, 0.025, 0.9),
                     m5.getTransitionProbability());

        assertEquals(pdf5, m1.getStationaryPDF());
        assertEquals(pdf5, m2.getStationaryPDF());
        assertEquals(pdf5, m3.getStationaryPDF());
        assertEquals(pdf5, m4.getStationaryPDF());
        assertEquals(pdf5, m5.getStationaryPDF());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.ClusterVisitationTest");
    }
}
