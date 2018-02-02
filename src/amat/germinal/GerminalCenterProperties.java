
package amat.germinal;

import jam.app.JamProperties;
import jam.chem.HalfLife;
import jam.math.DoubleRange;
import jam.math.IntRange;

/**
 * Manages germinal center characteristics defined through global
 * properties.
 *
 * <p><b>{@code amat.GerminalCenter.cycleLimit:}</b> Maximum number of
 * mutation/selection cycles.
 *
 * <p><b>{@code amat.GerminalCenter.residentCapacity:}</b> Maximum number
 * of B cells that may be supported by the germinal center.  Affinity
 * maturation will stop if the number of resident B cells exceeds this
 * number.
 *
 * <p><b>{@code amat.GerminalCenter.antigenHalfLife:}</b> Half-life for
 * antigen decay, expressed as a number of germinal center cycles.
 */
public final class GerminalCenterProperties {
    private GerminalCenterProperties() {} // Prevent instantiation...

    /**
     * Name of the global property which defines the maximum number of
     * mutation/selection cycles.
     */
    public static final String CYCLE_LIMIT_PROPERTY = "amat.GerminalCenter.cycleLimit";

    /**
     * Default value for the maximum number of mutation/selection
     * cycles.
     */
    public static final int DEFAULT_CYCLE_LIMIT = 100;

    /**
     * Name of the global property which defines the maximum number of
     * B cells that may be supported by the germinal center.
     */
    public static final String RESIDENT_CAPACITY_PROPERTY = "amat.GerminalCenter.residentCapacity";

    /**
     * Default value for the maximum number of B cells that may be
     * supported by the germinal center.
     */
    public static final int DEFAULT_RESIDENT_CAPACITY = 2000;

    /**
     * Name of the global property which defines the half-life for
     * antigen decay (in units of germinal center cycles).
     */
    public static final String ANTIGEN_HALF_LIFE_PROPERTY = "amat.GerminalCenter.antigenHalfLife";

    /**
     * Default value for the half-life of antigen decay (in units of
     * germinal center cycles).
     */
    public static final double DEFAULT_ANTIGEN_HALF_LIFE = Double.POSITIVE_INFINITY;

    /**
     * Returns the maximum number of mutation/selection cycles.
     *
     * @return the maximum number of mutation/selection cycles.
     */
    public static int getCycleLimit() {
        return JamProperties.getOptionalInt(CYCLE_LIMIT_PROPERTY, IntRange.POSITIVE, DEFAULT_CYCLE_LIMIT);
    }

    /**
     * Returns the maximum number of B cells that may be supported by
     * the germinal center. The affinity maturation process will stop
     * if the number of resident B cells exceeds this number.
     *
     * @return the maximum number of B cells that may be supported by
     * the germinal center.
     */
    public static int getResidentCapacity() {
        return JamProperties.getOptionalInt(RESIDENT_CAPACITY_PROPERTY, IntRange.POSITIVE, DEFAULT_RESIDENT_CAPACITY);
    }

    /**
     * Returns the half-life for antigen decay, expressed as a number
     * of germinal center cycles.
     *
     * @return the half-life for antigen decay, expressed as a number
     * of germinal center cycles.
     */
    public static HalfLife getAntigenHalfLife() {
        return HalfLife.valueOf(JamProperties.getOptionalDouble(ANTIGEN_HALF_LIFE_PROPERTY, DEFAULT_ANTIGEN_HALF_LIFE));
    }
}
