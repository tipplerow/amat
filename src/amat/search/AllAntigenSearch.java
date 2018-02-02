
package amat.search;

import java.util.ArrayList;
import java.util.Collection;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Selects all antigens for visitation by B cells.
 */
public final class AllAntigenSearch extends AntigenSearchModel {
    private AllAntigenSearch() {}

    /**
     * The singleton instance.
     */
    public static final AllAntigenSearch INSTANCE = new AllAntigenSearch();

    /**
     * Selects all antigens for visitation by B cells.
     *
     * @param cycle the index of the current germinal center cycle
     * (ignored in this model).
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigens to be visited.
     */
    @Override public Collection<Antigen> selectAntigens(int cycle, AntigenPool pool) {
        return new ArrayList<Antigen>(pool.viewAntigens());
    }
}
