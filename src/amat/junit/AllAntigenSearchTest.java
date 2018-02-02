
package amat.junit;

import amat.search.AllAntigenSearch;

import org.junit.*;
import static org.junit.Assert.*;

public class AllAntigenSearchTest extends AntigenSearchTestBase {
    @Override public AllAntigenSearch getModel() {
        return AllAntigenSearch.INSTANCE;
    }

    @Test public void testVisit() {
        validateVisitation(1.0, 1.0, 1.0, 100, 1.0E-12);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AllAntigenSearchTest");
    }
}
