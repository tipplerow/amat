
package amat.junit;

import java.util.Arrays;

import amat.antigen.Antigen;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenLoadTest {
    static {
        System.setProperty(Epitope.CONFIG_FILE_PROPERTY, "test/epitope_sample.conf");
        System.setProperty(Antigen.CONFIG_FILE_PROPERTY, "test/antigen_sample.conf");

        Epitope.load();
        Antigen.load();
    }

    @Test public void testLoad() {
        assertEquals(6, Antigen.count());

        assertTrue(Antigen.exists("AG1"));
        assertTrue(Antigen.exists("AG2"));
        assertTrue(Antigen.exists("AG3"));
        assertTrue(Antigen.exists("AG4"));
        assertTrue(Antigen.exists("AG12"));
        assertTrue(Antigen.exists("AG123"));

        assertEquals(Arrays.asList("E1"), Antigen.lookup("AG1").getEpitopeKeys());
        assertEquals(Arrays.asList("E2"), Antigen.lookup("AG2").getEpitopeKeys());
        assertEquals(Arrays.asList("E3"), Antigen.lookup("AG3").getEpitopeKeys());
        assertEquals(Arrays.asList("E4"), Antigen.lookup("AG4").getEpitopeKeys());
        assertEquals(Arrays.asList("E1", "E2"), Antigen.lookup("AG12").getEpitopeKeys());
        assertEquals(Arrays.asList("E1", "E2", "E3"), Antigen.lookup("AG123").getEpitopeKeys());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AntigenLoadTest");
    }
}
