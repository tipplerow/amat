
package amat.occupy;

import java.util.ArrayList;
import java.util.Collection;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Implements a follicular dendritic cell (FDC) occupation model in
 * which all antigen occupy all FDC surface sites.
 */
public final class AllOccupationModel extends OccupationModel {
    private AllOccupationModel() {}

    /**
     * The singleton instance.
     */
    public static final AllOccupationModel INSTANCE = new AllOccupationModel();

    @Override public Collection<Antigen> visit(int cycle, AntigenPool pool) {
        return new ArrayList<Antigen>(pool.viewAntigens());
    }
}
