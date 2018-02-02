
package amat.junit;

import java.util.Arrays;

import amat.antigen.Antigen;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenTest {
    static {
        Epitope.parse("E1: BitStructure(1000)");
        Epitope.parse("E2: BitStructure(0100)");
        Epitope.parse("E3: BitStructure(0010)");
        Epitope.parse("E4: BitStructure(0001)");
    }

    @Test public void testAdd() {
        Antigen antigen = Antigen.add("AddMe", Epitope.require("E1"), Epitope.require("E2"));

        assertEquals("AddMe", antigen.getKey());
        assertEquals(Arrays.asList("E1", "E2"), antigen.getEpitopeKeys());
    }

    @Test(expected = RuntimeException.class)
    public void testAddDuplicate() {
        Antigen.add("DuplicateKey", Epitope.require("E1"));
        Antigen.add("DuplicateKey", Epitope.require("E2"));
    }

    @Test(expected = RuntimeException.class)
    public void testAllClear() {
        Epitope.all().clear();
    }

    @Test public void testAuto() {
        assertFalse(Antigen.exists("E1"));
        assertFalse(Antigen.exists("E2"));
        assertFalse(Antigen.exists("E3"));
        assertFalse(Antigen.exists("E4"));

        Antigen.auto();

        assertEquals(Arrays.asList("E1"), Antigen.lookup("E1").getEpitopeKeys());
        assertEquals(Arrays.asList("E2"), Antigen.lookup("E2").getEpitopeKeys());
        assertEquals(Arrays.asList("E3"), Antigen.lookup("E3").getEpitopeKeys());
        assertEquals(Arrays.asList("E4"), Antigen.lookup("E4").getEpitopeKeys());
    }

    @Test public void testExists() {
        String key = "TestExists";

        assertFalse(Antigen.exists(key));
        Antigen.add(key, Epitope.require("E1"));
        assertTrue(Antigen.exists(key));
    }

    @Test(expected = RuntimeException.class)
    public void testKeysRemove() {
        Antigen.add("TestKeysRemove", Epitope.require("E1"));
        Antigen.keys().remove("TestKeysRemove");
    }

    @Test public void testLookup() {
        String key = "TestLookup";

        assertNull(Antigen.lookup(key));
        Antigen antigen = Antigen.add(key, Epitope.require("E4"));

        assertEquals(antigen, Antigen.lookup(key));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Antigen.add("TestNull", Epitope.require("E1"), null);
    }

    @Test public void testParse() {
        Antigen ag1   = Antigen.parse("AG1:   E1");
        Antigen ag12  = Antigen.parse("AG12:  E1, E2");
        Antigen ag234 = Antigen.parse("AG234: E2, E3, E4");

        assertEquals("AG1", ag1.getKey());
        assertEquals("AG12", ag12.getKey());
        assertEquals("AG234", ag234.getKey());

        assertEquals(Arrays.asList("E1"), ag1.getEpitopeKeys());
        assertEquals(Arrays.asList("E1", "E2"), ag12.getEpitopeKeys());
        assertEquals(Arrays.asList("E2", "E3", "E4"), ag234.getEpitopeKeys());
    }

    @Test(expected = RuntimeException.class)
    public void testRequiredMissing() {
        Antigen.require("no such antigen");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AntigenTest");
    }
}
