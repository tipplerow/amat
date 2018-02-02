
package amat.bcell;

import java.util.Iterator;
import java.util.Set;

import jam.lang.ObjectUtil;

import amat.antigen.AntigenPool;

/**
 * Implements models of germinal center B cell apoptosis in which the
 * survival decision is made for each B cell sequentially (following
 * an initialization step applied to the full B cell population).
 */
public abstract class SequentialApoptosisModel implements ApoptosisModel {
    /**
     * Determines whether a B cell will die by apoptosis.
     *
     * @param cell the B cell to examine.
     *
     * @return {@code true} iff the specified B cell undergoes
     * apoptosis.
     */
    public abstract boolean apoptose(BCell cell);

    /**
     * Initializes any state variables required for the apoptosis test.
     *
     * <p>For example, in models for T cell help defined in terms of
     * the average quantity of antigen captured, that average quantity
     * must be computed here.
     *
     * @param cells the B cells competing for survival.
     *
     * @param pool the pool of immunizing antigens.
     */
    public abstract void initialize(Set<BCell> cells, AntigenPool pool);

    @Override synchronized public Set<BCell> apoptose(Set<BCell> cells, AntigenPool pool) {
        // Initialize any state variables required for the survival test...
        initialize(cells, pool);

        // Maintain the runtime type of the input set...
        Set<BCell> perished = ObjectUtil.like(cells); 
        Iterator<BCell> iterator = cells.iterator();

        while (iterator.hasNext()) {
            BCell cell = iterator.next();

            if (apoptose(cell)) {
                perished.add(cell); // Add to the set of cells that have died by apoptosis...
                iterator.remove();  // Remove from the input set...
            }
        }

        return perished;
    }
}
