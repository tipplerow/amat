
package amat.junit;

import amat.search.AllOneAntigenSearch;

import org.junit.*;
import static org.junit.Assert.*;

public class AllOneAntigenSearchTest extends AntigenSearchTestBase {
    private static final int TRANSITION_INDEX = 10;

    @Override public AllOneAntigenSearch getModel() {
        return new AllOneAntigenSearch(TRANSITION_INDEX);
    }

    @Test public void testVisit() {
        validateVisitation(0,                    1.0, 1.0, 1.0, 100, 1.0E-12);
        validateVisitation(TRANSITION_INDEX - 1, 1.0, 1.0, 1.0, 100, 1.0E-12);

        validateVisitation(TRANSITION_INDEX,       0.5 / 4.5, 1.0 / 4.5, 3.0 / 4.5, 200000, 0.002);
        validateVisitation(TRANSITION_INDEX + 100, 0.5 / 4.5, 1.0 / 4.5, 3.0 / 4.5, 200000, 0.002);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.AllOneAntigenSearchTest");
    }
}
