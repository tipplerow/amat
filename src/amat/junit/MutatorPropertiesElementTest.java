
package amat.junit;

import jam.junit.NumericTestBase;

import amat.epitope.Epitope;
import amat.receptor.MutatorProperties;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class MutatorPropertiesElementTest extends NumericTestBase {
    static {
        //
        // The receptor length will match the epitope length, and
        // single-element (point) mutation probabilities will be
        // computed from the receptor length...
        //
        Epitope.add("E1", Structure.parse("BitStructure(0000 1111)"));

        System.setProperty(MutatorProperties.ELEMENT_LETHAL_PROBABILITY_PROPERTY,  "0.04");
        System.setProperty(MutatorProperties.ELEMENT_SOMATIC_PROBABILITY_PROPERTY, "0.03");
    }

    @Test public void testElementProbabilities() {
        assertTrue(MutatorProperties.getElementLethalProbability().equals( 0.04));
        assertTrue(MutatorProperties.getElementSilentProbability().equals( 0.93));
        assertTrue(MutatorProperties.getElementSomaticProbability().equals(0.03));
    }

    @Test public void testReceptorProbabilities() {
        assertTrue(MutatorProperties.getReceptorLethalProbability().equals( 0.27861042, 1.0E-08));
        assertTrue(MutatorProperties.getReceptorSilentProbability().equals( 0.55958181, 1.0E-08));
        assertTrue(MutatorProperties.getReceptorSomaticProbability().equals(0.16180777, 1.0E-08));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.MutatorPropertiesElementTest");
    }
}
