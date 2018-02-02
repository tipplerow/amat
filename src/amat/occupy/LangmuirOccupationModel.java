
package amat.occupy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import jam.chem.Concentration;
import jam.chem.Langmuir;
import jam.math.Probability;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Implements a follicular dendritic cell (FDC) occupation model in
 * which sites are occupied according to a multi-component Langmuir
 * adsorption isotherm.
 *
 * <p>The probability that antigen {@code k} occupies an FDC site is
 * equal to {@code C(k) / [1.0 + C(1) + C(2) + ... + C(N)]}, where
 * {@code C(k)} is its concentration.
 */
public final class LangmuirOccupationModel extends OccupationModel {
    private LangmuirOccupationModel() {}

    /**
     * The singleton instance.
     */
    public static final LangmuirOccupationModel INSTANCE = new LangmuirOccupationModel();

    @Override public Collection<Antigen> visit(int cycle, AntigenPool pool) {
        //
        // The probability that the site is occupied is given by the
        // Langmuir isotherm with the total antigen concentration. If
        // the site is occupied, the occupant probability is equal to
        // its fractional concentration.
        //
        if (isOccupied(pool))
            return Arrays.asList(pool.select());
        else
            return Collections.emptyList();
    }

    private boolean isOccupied(AntigenPool pool) {
        return getOccupationProbability(pool).accept();
    }

    private Probability getOccupationProbability(AntigenPool pool) {
        return Langmuir.probability(pool.getTotalConc());
    }
}
