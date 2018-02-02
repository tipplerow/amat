
package amat.tcell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jam.math.DoubleRange;
import jam.util.SetUtil;

import amat.antigen.AntigenPool;
import amat.bcell.ApoptosisModel;
import amat.bcell.BCell;

/**
 * Ranks B cells on an arbitrary metric specified by a custom {@link
 * java.util.Comparator}. A fixed fraction having the highest values
 * of that metric receive T cell help and survive; the others die by
 * apoptosis.
 */
public final class RankCompetition implements ApoptosisModel {
    private final double survivalRate;
    private final Comparator<BCell> comparator;

    /**
     * Creates a new rank competition model.
     *
     * @param survivalRate the fraction of B cells to receive T cell
     * help and survive.
     *
     * @param comparator the comparator used to rank competing B cells.
     *
     * @throws IllegalArgumentException unless the survival rate lies
     * in the range {@code [0.0, 1.0]}.
     */
    public RankCompetition(double survivalRate, Comparator<BCell> comparator) {
        DoubleRange.FRACTIONAL.validate(survivalRate);

        this.comparator = comparator;
        this.survivalRate = survivalRate;
    }

    /**
     * Returns the comparator used to rank competing B cells.
     *
     * @return the comparator used to rank competing B cells.
     */
    public Comparator<BCell> getComparator() {
        return comparator;
    }

    /**
     * Returns the fraction of B cells that will receive T cell help
     * and survive.
     *
     * @return the fraction of B cells that will receive T cell help
     * and survive.
     */
    public double getSurvivalRate() {
        return survivalRate;
    }

    @Override public Set<BCell> apoptose(Set<BCell> cells, AntigenPool pool) {
        //
        // Sort the cells according to the metric for this
        // competition...
        //
        List<BCell> sorted = new ArrayList<BCell>(cells);
        Collections.sort(sorted, comparator);

        // The lowest fraction (1 - R) perish, while the highest
        // fraction R survive, where R is the survival rate...
        int originalCount = cells.size();
        int perishedCount = (int) Math.ceil((1.0 - survivalRate) * originalCount);
        int survivorCount = (int) Math.ceil(survivalRate * originalCount);

        List<BCell> survivors = 
            sorted.subList(originalCount - survivorCount, originalCount);

        Set<BCell> perished = SetUtil.difference(cells, survivors);
        cells.removeAll(perished);

        return perished;
    }
}
