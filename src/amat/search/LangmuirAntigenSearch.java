
package amat.search;

import java.util.ArrayList;
import java.util.Collection;

import jam.chem.Concentration;
import jam.chem.Langmuir;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Selects antigens for visitation stochastically with a probability
 * having the functional form of a Langmiur adsorption isotherm.
 *
 * <p>The probability that an antigen will be visited is equal to
 * {@code conc / (1.0 + conc)}, where {@code conc} is its concentration 
 * in the antigen pool.  The selection of each antigen is independent
 * of the selection decision for the others.
 */
public final class LangmuirAntigenSearch extends AntigenSearchModel {
    private LangmuirAntigenSearch() {}

    /**
     * The singleton instance.
     */
    public static final LangmuirAntigenSearch INSTANCE = new LangmuirAntigenSearch();

    /**
     * Selects antigens for visitation by a B cell stochastically with
     * a probability having the functional form of a Langmiur isotherm.
     *
     * @param cycle the index of the current germinal center cycle
     * (ignored in this model).
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigens to be visited.
     */
    @Override public Collection<Antigen> selectAntigens(int cycle, AntigenPool pool) {
        Collection<Antigen> antigens = new ArrayList<Antigen>();

        for (Antigen antigen : pool.viewAntigens())
            if (select(pool.getConcentration(antigen)))
                antigens.add(antigen);

        return antigens;
    }

    private static boolean select(Concentration conc) {
        return Langmuir.probability(conc).accept();
    }
}
