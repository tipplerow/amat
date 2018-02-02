
package amat.germinal;

import java.util.Collection;

import jam.lang.JamException;
import jam.math.DoubleUtil;
import jam.util.EnumUtil;

public final class PopulationRecord {
    private final int[] population;

    // Package scope: New records are only created by a germinal
    // center.  The argument "pop" is the B cell population at the
    // start of the cycle...
    PopulationRecord(int pop) {
        this.population = new int[EnumUtil.count(GerminalCenterEvent.class) + 1];

        for (int index = 0; index < this.population.length; index++)
            this.population[index] = pop;
    }

    // Package scope: Assign the population prior to an event...
    void before(GerminalCenterEvent event, int pop) {
        population[beforeIndex(event)] = pop;
    }

    private static int beforeIndex(GerminalCenterEvent event) {
        return event.ordinal();
    }

    // Package scope: Assign the population prior to an event...
    void after(GerminalCenterEvent event, int pop) {
        population[afterIndex(event)] = pop;
    }

    private static int afterIndex(GerminalCenterEvent event) {
        return beforeIndex(event) + 1;
    }

    /**
     * Returns the number of B cells present in the germinal center
     * before the occurrence of an event.
     *
     * @param event the event in question.
     *
     * @return the number of B cells present in the germinal center
     * before the specified event occured.
     */
    public int before(GerminalCenterEvent event) {
        return population[beforeIndex(event)];
    }

    /**
     * Returns the number of B cells present in the germinal center
     * after the occurrence of an event.
     *
     * @param event the event in question.
     *
     * @return the number of B cells present in the germinal center
     * after the specified event occured.
     */
    public int after(GerminalCenterEvent event) {
        return population[afterIndex(event)];
    }

    /**
     * Returns the population at the beginning of the germinal center
     * cycle.
     *
     * @return the population at the beginning of the germinal center
     * cycle.
     */
    public int beginning() {
        return population[0];
    }

    /**
     * Returns the population at the end of the germinal center cycle.
     *
     * @return the population at the end of the germinal center cycle.
     */
    public int ending() {
        return population[population.length - 1];
    }

    /**
     * Computes the population growth rate for the germinal center
     * cycle: the ratio of the ending population to the beginning
     * population.
     *
     * @return the population growth rate for the germinal center cycle.
     */
    public double computeGrowthRate() {
        return DoubleUtil.ratio(ending(), beginning());
    }

    /**
     * Computes the rate of survival for each major germinal center
     * event.
     *
     * @param event the event of interest.
     *
     * @return the fraction of B cells that survived the specified
     * event.
     */
    public double computeSurvivalRate(GerminalCenterEvent event) {
        switch (event) {
        case DIVISION_MUTATION:
            return DoubleUtil.ratio(after(event), before(event)) / 2.0;

        case BCR_SIGNALING:     // Fall-through...
        case TCELL_COMPETITION:
            return DoubleUtil.ratio(after(event), before(event));

        case MEMORY_REENTRY:   // Fall-through...
        case MEMORY_SELECTION: // Fall-through...
        case PLASMA_SELECTION:
            return 1.0;

        default:
            throw JamException.runtime("Unknown event: [%s].", event);
        }
    }
}
