
package amat.occupy;

import java.util.ArrayList;
import java.util.Collection;

import jam.math.IntRange;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Implements a follicular dendritic cell (FDC) occupation model that
 * switches from ALL to ONE occupation at a fixed GC cycle.
 */
public final class AllOneOccupationModel extends OccupationModel {
    private final int transition;

    /**
     * Valid range for the transition cycle.
     */
    public static final IntRange TRANSITION_RANGE = IntRange.NON_NEGATIVE;

    /**
     * Creates a new all-one search model with a fixed transition
     * cycle.
     *
     * @param transition the germinal center cycle at which this model
     * switches from ALL to ONE occupation.
     */
    public AllOneOccupationModel(int transition) {
        TRANSITION_RANGE.validate(transition);
        this.transition = transition;
    }

    /**
     * Returns the germinal center cycle at which this model switches
     * from ALL to ONE occupation.
     *
     * @return the germinal center cycle at which this model switches
     * from ALL to ONE occupation.
     */
    public int getTransition() {
        return transition;
    }

    @Override public Collection<Antigen> visit(int cycle, AntigenPool pool) {
        if (cycle < transition)
            return AllOccupationModel.INSTANCE.visit(cycle, pool);
        else
            return OneOccupationModel.INSTANCE.visit(cycle, pool);
    }
}
