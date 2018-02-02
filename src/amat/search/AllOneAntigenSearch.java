
package amat.search;

import java.util.ArrayList;
import java.util.Collection;

import jam.math.IntRange;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;

/**
 * Switches from a "see all" to "see one" search model at a fixed
 * germinal center cycle.
 */
public final class AllOneAntigenSearch extends AntigenSearchModel {
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
     * switches from "see all" to "see one" selection.
     */
    public AllOneAntigenSearch(int transition) {
        TRANSITION_RANGE.validate(transition);
        this.transition = transition;
    }

    /**
     * Returns the germinal center cycle at which this model switches
     * from "see all" to "see one" selection.
     *
     * @return the germinal center cycle at which this model switches
     * from "see all" to "see one" selection.
     */
    public int getTransition() {
        return transition;
    }

    /**
     * Prior to the transition cycle, selects all antigens for
     * visitation by B cells; on an after the transition cycle,
     * selects one antigen at random.
     *
     * @param cycle the index of the current germinal center cycle.
     *
     * @param pool the pool of available antigens.
     *
     * @return the antigens to be visited.
     */
    @Override public Collection<Antigen> selectAntigens(int cycle, AntigenPool pool) {
        if (cycle < transition)
            return AllAntigenSearch.INSTANCE.selectAntigens(cycle, pool);
        else
            return OneAntigenSearch.INSTANCE.selectAntigens(cycle, pool);
    }
}
