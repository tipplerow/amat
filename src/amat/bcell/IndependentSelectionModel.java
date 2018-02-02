
package amat.bcell;

import java.util.Iterator;
import java.util.Set;

import jam.lang.ObjectUtil;

/**
 * Represents models of B cell selection in which the selection
 * decision is made independently for each B cell, e.g., at the 
 * cell level rather than the population level.
 */
public abstract class IndependentSelectionModel implements SelectionModel {
    /**
     * Determines whether a B cell is selected.
     *
     * @param cell the B cell to examine.
     *
     * @return {@code true} iff the specified B cell is selected by
     * this model.
     */
    public abstract boolean select(BCell cell);

    @Override public Set<BCell> select(Set<BCell> cells) {
        // Maintain the runtime type of the input set...
        Set<BCell> selected = ObjectUtil.like(cells); 
        Iterator<BCell> iterator = cells.iterator();

        while (iterator.hasNext()) {
            BCell cell = iterator.next();

            if (select(cell)) {
                selected.add(cell); // Add to the selected set...
                iterator.remove();  // Remove from the input set...
            }
        }

        return selected;
    }
}
