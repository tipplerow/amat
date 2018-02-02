
package amat.junit;

import java.util.Arrays;

import amat.antigen.Antigen;
import amat.epitope.Epitope;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenAutoLoadTest {
    static {
        System.setProperty(Epitope.CONFIG_FILE_PROPERTY, "test/epitope_sample.conf");

        Epitope.load();
        Antigen.load();
    }

    @Test public void testLoad() {
        assertEquals(4, Antigen.count());

        assertTrue(Antigen.exists("E1"));
        assertTrue(Antigen.exists("E2"));
        assertTrue(Antigen.exists("E3"));
        assertTrue(Antigen.exists("E4"));

        assertEquals(Arrays.asList("E1"), Antigen.lookup("E1").getEpitopeKeys());
        assertEquals(Arrays.asList("E2"), Antigen.lookup("E2").getEpitopeKeys());
        assertEquals(Arrays.asList("E3"), Antigen.lookup("E3").getEpitopeKeys());
        assertEquals(Arrays.asList("E4"), Antigen.lookup("E4").getEpitopeKeys());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AntigenAutoLoadTest");
    }
}
