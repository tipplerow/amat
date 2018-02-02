
package amat.junit;

import amat.epitope.Epitope;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class EpitopeTest {
    private static final Structure struct1 = Structure.parse("BitStructure(0000)");
    private static final Structure struct2 = Structure.parse("BitStructure(1111)");

    @Test public void testAdd() {
        Epitope epitope = Epitope.add("AddMe", struct1);

        assertEquals("AddMe", epitope.getKey());
        assertEquals(struct1, epitope.getStructure());
    }

    @Test(expected = RuntimeException.class)
    public void testAddDuplicate() {
        Epitope.add("DuplicateKey", struct1);
        Epitope.add("DuplicateKey", struct2);
    }

    @Test(expected = RuntimeException.class)
    public void testAllClear() {
        Epitope.all().clear();
    }

    @Test public void testExists() {
        String key = "TestExists";

        assertFalse(Epitope.exists(key));
        Epitope.add(key, struct1);
        assertTrue(Epitope.exists(key));
    }

    @Test(expected = RuntimeException.class)
    public void testKeysRemove() {
        Epitope.add("TestKeysRemove", struct1);
        Epitope.keys().remove("TestKeysRemove");
    }

    @Test public void testLookup() {
        String key = "TestLookup";

        assertNull(Epitope.lookup(key));
        Epitope epitope = Epitope.add(key, struct1);

        assertEquals(epitope, Epitope.lookup(key));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Epitope.add("TestNull", null);
    }

    @Test public void testParse() {
        assertFalse(Epitope.exists("TestParse"));

        Epitope epitope = Epitope.parse("TestParse: BitStructure(01010101)");
        
        assertTrue(Epitope.exists("TestParse"));
        assertEquals(epitope, Epitope.lookup("TestParse"));
    }

    @Test(expected = RuntimeException.class)
    public void testRequiredMissing() {
        Epitope.require("no such epitope");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.EpitopeTest");
    }
}
