
package amat.junit;

import amat.epitope.Epitope;

import amat.structure.CVClassifier;
import amat.structure.StructureType;

import org.junit.*;
import static org.junit.Assert.*;

public class EpitopeLoadTest {
    static {
        System.setProperty(Epitope.CONFIG_FILE_PROPERTY, "test/epitope_sample.conf");
        Epitope.load();
    }

    @Test public void testLoad() {
        assertEquals(4, Epitope.count());

        assertTrue(Epitope.exists("E1"));
        assertTrue(Epitope.exists("E2"));
        assertTrue(Epitope.exists("E3"));
        assertTrue(Epitope.exists("E4"));
    }

    @Test(expected = RuntimeException.class)
    public void testBadLoad() {
        Epitope.load("test/bad_epitope.conf");
    }

    @Test public void testConserved() {
        CVClassifier classifier = Epitope.classify();
        int[] conserved = classifier.getConserved();

        assertEquals(16, conserved.length);

        for (int index = 0; index < conserved.length; index++)
            assertEquals(index, conserved[index]);
    }

    @Test public void testResolveLength() {
        assertEquals(32, Epitope.resolveLength());
    }

    @Test public void testResolveType() {
        assertEquals(StructureType.BIT, Epitope.resolveType());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.EpitopeLoadTest");
    }
}
