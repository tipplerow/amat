
package amat.bcell;

import java.util.Set;

import amat.antigen.AntigenPool;

/**
 * Represents models for the programmed death (apoptosis) of germinal
 * center B cells when they fail to receive survival signals because
 * of insufficient antigen binding or failure in the competition for 
 * T cell help.
 */
public interface ApoptosisModel {
    /**
     * Determines which B cells in a population will die by apoptosis.
     *
     * @param cells the B cells competing for survival; those that
     * undergo apoptosis are removed from the input set.
     *
     * @return the cells that perished, which are removed from the
     * input set.
     */
    public default Set<BCell> apoptose(Set<BCell> cells) {
        return apoptose(cells, null);
    }

    /**
     * Determines which B cells in a population will die by apoptosis.
     *
     * @param cells the B cells competing for survival; those that
     * undergo apoptosis are removed from the input set.
     *
     * @param pool the pool of immunizing antigens.
     *
     * @return the cells that perished, which are removed from the
     * input set.
     */
    public abstract Set<BCell> apoptose(Set<BCell> cells, AntigenPool pool);
}
