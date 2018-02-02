
package amat.bcell;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.math.DoubleUtil;
import jam.math.Entropy;
import jam.math.Probability;

/**
 * Computes the <em>clonal diversity</em> for a collection of B cells.
 *
 * <p>We quantify the clonal diversity of a B cell population by (1)
 * the number of unique germlines present in the population, and (2)
 * the Shannon entropy of the fraction of cells derived from each
 * germline cell: {@code -[f1 * log(f1) + f2 * log(f2) + ...]}, where
 * {@code fk} is the fraction of the B cell population derived from
 * germline cell {@code k}.
 */
public final class ClonalDiversity {
    private final int count;
    private final double entropy;

    private ClonalDiversity(int count, double entropy) {
        this.count = count;
        this.entropy = entropy;
    }
    
    /**
     * Computes the clonal diversity for a given B cell population.
     *
     * @param cells the B cell population.
     *
     * @return the clonal diversity for the specified B cell
     * population.
     */
    public static ClonalDiversity compute(Collection<BCell> cells) {
        Multiset<BCell> founders = HashMultiset.create();

        for (BCell cell : cells)
            founders.add(cell.getFounder());

        assert founders.size() == cells.size();

        Collection<Probability> distribution = new ArrayList<Probability>();

        for (Multiset.Entry<BCell> entry : founders.entrySet())
            distribution.add(Probability.valueOf(DoubleUtil.ratio(entry.getCount(), cells.size())));

        int    count   = founders.elementSet().size();
        double entropy = Entropy.shannon(distribution.toArray(new Probability[0]));

        return new ClonalDiversity(count, entropy);
    }

    /**
     * Returns the number of unique germlines in the B cell
     * population.
     *
     * @return the number of unique germlines in the B cell
     * population.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the Shannon entropy for the fraction of cells derived
     * from each germline cell.
     *
     * @return the Shannon entropy for the fraction of cells derived
     * from each germline cell.
     */
    public double getEntropy() {
        return entropy;
    }
}
