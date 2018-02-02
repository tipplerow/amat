
package amat.junit;

import jam.junit.NumericTestBase;

import amat.epitope.Epitope;
import amat.receptor.MutatorProperties;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class MutatorPropertiesInvalid1Test extends NumericTestBase {
    static {
        Epitope.add("E1", Structure.parse("BitStructure(0000 1111)"));

        System.setProperty(MutatorProperties.ELEMENT_LETHAL_PROBABILITY_PROPERTY,  "0.04");
        System.setProperty(MutatorProperties.RECEPTOR_SOMATIC_PROBABILITY_PROPERTY, "0.20");
    }

    @Test(expected = RuntimeException.class)
    public void testProbabilities() {
        MutatorProperties.getElementLethalProbability();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.MutatorPropertiesInvalid1Test");
    }
}
