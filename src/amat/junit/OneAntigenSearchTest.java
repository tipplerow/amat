
package amat.junit;

import amat.search.OneAntigenSearch;

import org.junit.*;
import static org.junit.Assert.*;

public class OneAntigenSearchTest extends AntigenSearchTestBase {
    @Override public OneAntigenSearch getModel() {
        return OneAntigenSearch.INSTANCE;
    }

    @Test public void testVisit() {
        validateVisitation(0.5 / 4.5, 1.0 / 4.5, 3.0 / 4.5, 200000, 0.002);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.OneAntigenSearchTest");
    }
}
