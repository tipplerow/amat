
package amat.bcell;

import java.util.Set;

import amat.antigen.AntigenPool;

/**
 * Represents models of germinal center B cell apoptosis in which the
 * survival decision is made independently for each B cell, e.g., at
 * the cell level rather than the population level.
 */
public abstract class IndependentApoptosisModel extends SequentialApoptosisModel {
    @Override public void initialize(Set<BCell> cells, AntigenPool pool) {
        //
        // A no-op: Each apoptosis decision is independent, so there
        // is no state information necessary....
        //
    }
}
