
package amat.junit;

import jam.junit.NumericTestBase;

import amat.epitope.Epitope;
import amat.receptor.MutatorProperties;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class MutatorPropertiesDefaultTest extends NumericTestBase {
    static {
        //
        // Receptor properties will be derived from the epitope,
        // receptor-aggregate (joint) mutation probabilities will be
        // assigned their default values, and single-element (point)
        // mutation probabilities will be computed from the epitope
        // length...
        //
        Epitope.add("E1", Structure.parse("BitStructure(0000 1111)"));
    }

    @Test public void testElementProbabilities() {
        assertTrue(MutatorProperties.getElementLethalProbability().equals( 0.04360509, 1.0E-08));
        assertTrue(MutatorProperties.getElementSilentProbability().equals( 0.91700404, 1.0E-08));
        assertTrue(MutatorProperties.getElementSomaticProbability().equals(0.03939086, 1.0E-08));
    }

    @Test public void testReceptorProbabilities() {
        assertEquals(MutatorProperties.DEFAULT_RECEPTOR_LETHAL_PROBABILITY, MutatorProperties.getReceptorLethalProbability());
        assertEquals(MutatorProperties.DEFAULT_RECEPTOR_SILENT_PROBABILITY, MutatorProperties.getReceptorSilentProbability());
        assertEquals(MutatorProperties.DEFAULT_RECEPTOR_SOMATIC_PROBABILITY, MutatorProperties.getReceptorSomaticProbability());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.MutatorPropertiesDefaultTest");
    }
}
