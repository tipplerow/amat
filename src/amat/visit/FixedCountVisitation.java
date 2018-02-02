
package amat.visit;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import jam.app.JamProperties;
import jam.math.IntRange;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.occupy.OccupationModel;

/**
 * Implements an FDC visitation model in which B cells visit a fixed
 * number of FDCs on each germinal center cycle.  The occupation of
 * the FDC sites (and therefore the number and type antigens visted)
 * is governed by the global {@code OccupationModel}.
 */
public final class FixedCountVisitation extends VisitationModel {
    private final int count;

    // The global model defined by system properties...
    private static FixedCountVisitation global = null;

    /**
     * Name of the system property which defines the number of sites
     * visited by each B cell.
     */
    public static final String VISIT_COUNT_PROPERTY = "amat.FixedCountVisitation.visitCount";

    /**
     * Valid range for the number of antigen or FDC sites visited.
     */
    public static final IntRange VISIT_COUNT_RANGE = IntRange.NON_NEGATIVE;

    /**
     * Creates a new fixed-count visitation model.
     *
     * @param count the number of FDCs visited by each B cell.
     */
    public FixedCountVisitation(int count) {
        VISIT_COUNT_RANGE.validate(count);
        this.count = count;
    }

    /**
     * Returns the global visitation model defined by system properties.
     *
     * @return the global visitation model defined by system properties.
     *
     * @throws RuntimeException unless the system properties required
     * by the visitation model are properly defined.
     */
    public static FixedCountVisitation global() {
        if (global == null)
            createGlobal();

        return global;
    }

    private static void createGlobal() {
        global = new FixedCountVisitation(resolveVisitCount());
    }

    private static int resolveVisitCount() {
        return JamProperties.getRequiredInt(VISIT_COUNT_PROPERTY);
    }

    /**
     * Returns the number of FDCs visited by each B cell.
     *
     * @return the number of FDCs visited by each B cell.
     */
    public int getCount() {
        return count;
    }

    @Override public Multiset<Antigen> visit(int cycle, AntigenPool pool) {
        Multiset<Antigen> antigens = HashMultiset.create();

        for (int index = 0; index < count; index++)
            antigens.addAll(OccupationModel.global().visit(cycle, pool));

        return antigens;
    }
}
