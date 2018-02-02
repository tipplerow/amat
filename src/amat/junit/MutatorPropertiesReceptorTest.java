
package amat.junit;

import jam.junit.NumericTestBase;

import amat.epitope.Epitope;
import amat.receptor.MutatorProperties;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class MutatorPropertiesReceptorTest extends NumericTestBase {
    static {
        //
        // The receptor length will match the epitope length, and
        // single-element (point) mutation probabilities will be
        // computed from the receptor length...
        //
        Epitope.add("E1", Structure.parse("BitStructure(0000 1111)"));

        System.setProperty(MutatorProperties.RECEPTOR_LETHAL_PROBABILITY_PROPERTY,  "0.35");
        System.setProperty(MutatorProperties.RECEPTOR_SOMATIC_PROBABILITY_PROPERTY, "0.25");
    }

    @Test public void testElementProbabilities() {
        assertTrue(MutatorProperties.getElementLethalProbability().equals( 0.05242374, 1.0E-08));
        assertTrue(MutatorProperties.getElementSilentProbability().equals( 0.89177953, 1.0E-08));
        assertTrue(MutatorProperties.getElementSomaticProbability().equals(0.05579673, 1.0E-08));
    }

    @Test public void testReceptorProbabilities() {
        assertTrue(MutatorProperties.getReceptorLethalProbability().equals(0.35));
        assertTrue(MutatorProperties.getReceptorSilentProbability().equals(0.40));
        assertTrue(MutatorProperties.getReceptorSomaticProbability().equals(0.25));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.MutatorPropertiesReceptorTest");
    }
}
