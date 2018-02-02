
package amat.bcell;

import java.util.Set;

/**
 * Represents the manner by which B cells are selected for special
 * events during affinity maturation, e.g., for selection into the
 * memory and plasma cell compartments.
 */
public interface SelectionModel {
    /**
     * Selects B cells by the criteria defined by this model.
     *
     * @param cells the pool of B cells from which to select; the
     * selected cells are removed from this set.
     *
     * @return a new set containing the selected cells, which are
     * removed from the input set.
     */
    public abstract Set<BCell> select(Set<BCell> cells);
}
