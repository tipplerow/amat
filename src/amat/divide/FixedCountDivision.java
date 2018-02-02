
package amat.divide;

import java.util.Collection;

import jam.math.IntRange;

import amat.bcell.BCell;

/**
 * Implements a dark-zone division model in which all B cells divide a
 * fixed number of times, regardless of the amount of antigen captured.
 */
public final class FixedCountDivision extends DZDivisionModel {
    private final int count;

    /**
     * Valid range for the number of B cell divisions.
     */
    public static final IntRange COUNT_RANGE = IntRange.NON_NEGATIVE;

    /**
     * Creates a new fixed-count division model.
     *
     * @param count the number of FDCs visited by each B cell.
     */
    public FixedCountDivision(int count) {
        COUNT_RANGE.validate(count);
        this.count = count;
    }

    /**
     * Returns the number of divisions per B cell.
     *
     * @return the number of divisions per B cell.
     */
    public int getCount() {
        return count;
    }

    @Override public void assignDivisionCount(Collection<BCell> bcells) {
        for (BCell bcell : bcells)
            bcell.setDivisionCount(count);
    }
}
