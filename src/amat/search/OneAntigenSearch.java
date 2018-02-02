
package amat.search;

import java.util.Arrays;
import java.util.Collection;

import jam.math.JamRandom;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Selects one antigen for visitation to B cells.  The probability of
 * selecting a given antigen is equal to its concentration divided by
 * the total concentration.
 */
public final class OneAntigenSearch extends AntigenSearchModel {
    private OneAntigenSearch() {}

    /**
     * The singleton instance.
     */
    public static final OneAntigenSearch INSTANCE = new OneAntigenSearch();

    /**
     * Selects one antigen for visitation by B cells.
     *
     * <p>The probability of selecting a given antigen is equal to its
     * concentration divided by the total concentration.
     *
     * @param cycle the index of the current germinal center cycle
     * (ignored in this model).
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigen to be visited.
     */
    @Override public Collection<Antigen> selectAntigens(int cycle, AntigenPool pool) {
        return Arrays.asList(pool.select(JamRandom.global()));
    }
}
