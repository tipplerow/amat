
package amat.junit;

import amat.search.LangmuirAntigenSearch;

import org.junit.*;
import static org.junit.Assert.*;

public class LangmuirAntigenSearchTest extends AntigenSearchTestBase {
    @Override public LangmuirAntigenSearch getModel() {
        return LangmuirAntigenSearch.INSTANCE;
    }

    @Test public void testVisit() {
        validateVisitation(0.5 / 1.5, 1.0 / 2.0, 3.0 / 4.0, 200000, 0.001);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.LangmuirAntigenSearchTest");
    }
}
