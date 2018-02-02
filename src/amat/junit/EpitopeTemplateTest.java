
package amat.junit;

import amat.epitope.Epitope;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class EpitopeTemplateTest {
    private static final Structure struct1 = Structure.parse("BitStructure(0000)");
    private static final Structure struct2 = Structure.parse("BitStructure(1111)");

    @Test public void testTemplate() {
        Epitope epitope1 = Epitope.add("E1", struct1);
        Epitope epitope2 = Epitope.add("E2", struct2);

        // The template is the first one added...
        assertEquals(epitope1, Epitope.template());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.EpitopeTemplateTest");
    }
}
