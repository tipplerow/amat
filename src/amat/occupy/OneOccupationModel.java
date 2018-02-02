
package amat.occupy;

import java.util.Arrays;
import java.util.Collection;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Implements a follicular dendritic cell (FDC) occupation model in
 * which exactly one antigen occupies an FDC surface site.  
 *
 * The occupation probability for a given antigen is equal to its
 * fractional concentration (its concentration divided by the total
 * antigen concentration).
 */
public final class OneOccupationModel extends OccupationModel {
    private OneOccupationModel() {}

    /**
     * The singleton instance.
     */
    public static final OneOccupationModel INSTANCE = new OneOccupationModel();

    @Override public Collection<Antigen> visit(int cycle, AntigenPool pool) {
        return Arrays.asList(pool.select());
    }
}
