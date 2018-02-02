
package amat.vaccine;

import java.util.regex.Pattern;

import jam.lang.Formatted;
import jam.lang.JamException;

/**
 * Represents one component of a vaccination schedule: a specific
 * vaccine administered at a specific time.
 *
 * <p>The vaccination time is defined as an <em>injection cycle</em>,
 * the number of affinity maturation cycles that have elapsed since
 * the initial injection.  The first cycle in a vaccination schedule
 * should be zero.
 *
 * <p>A vaccination event may be created from a string representation
 * of the event.  The string must contain the injection cycle followed
 * by a colon and the vaccine specification.  For example:
 *
 * <pre>
   0: AG1, 1.0; AG2, 2.0; AG3, 3.0
   10: AG1, AG2, AG3; 5.0
 * </pre>
 *
 * See {@link Vaccine} for a more detailed description of supported
 * formats.
 */
public final class VaccinationEvent implements Formatted {
    private final int inCycle;
    private final Vaccine vaccine;

    // Delimiter separating injection cycle and vaccine...
    private static final String  DELIM_STRING  = ":";
    private static final Pattern DELIM_PATTERN = Pattern.compile(DELIM_STRING);

    /**
     * Creates a new vaccination event.
     *
     * @param inCycle the injection cycle to specify when the vaccine
     * is administered.
     *
     * @param vaccine the vaccine formulation that is administered.
     *
     * @throws IllegalArgumentException if the injection cycle is
     * negative.
     */
    public VaccinationEvent(int inCycle, Vaccine vaccine) {
        validateInjectionCycle(inCycle);

        this.inCycle = inCycle;
        this.vaccine = vaccine;
    }

    /**
     * Creates an event for a bolus vaccine: a single vaccine
     * administered on injection cycle zero.
     * 
     * @param vaccine the vaccine.
     *
     * @return the bolus vaccination event.
     */
    public static VaccinationEvent bolus(Vaccine vaccine) {
        return new VaccinationEvent(0, vaccine);
    }

    /**
     * Creates a new vaccination event from its string representation.
     *
     * <p>The antigens must be present in the global antigen registry
     * before calling this method.
     *
     * @param str a string formatted as described in the class comments.
     *
     * @return the vaccination event encoded by the given string.
     *
     * @throws RuntimeException unless the string argument is properly
     * formatted and all antigens referenced are present in the global
     * registry.
     */
    public static VaccinationEvent parse(String str) {
        String[] fields = DELIM_PATTERN.split(str);

	if (fields.length != 2)
	    throw JamException.runtime("Invalid vaccine event: [%s].", str);

	int inCycle = Integer.parseInt(fields[0]);
	Vaccine vaccine = Vaccine.parse(fields[1]);

	return new VaccinationEvent(inCycle, vaccine);
    }

    /**
     * Returns the cycle when the vaccine will be administered.
     *
     * @return the cycle when the vaccine will be administered.
     */
    public int getInjectionCycle() {
        return inCycle;
    }

    /**
     * Returns a unique key for this event as a member of a schedule.
     *
     * @return the injection cycle, which serves as a unique key for
     * this event as a member of a schedule.
     */
    public Integer getKey() {
        return getKey(inCycle);
    }

    /**
     * Translates an injection cycle into a unique event key.
     *
     * @param inCycle an injection cycle.
     *
     * @return a unique key generated from the the injection cycle
     * (simply the cycle itself).
     */
    public static Integer getKey(int inCycle) {
        return Integer.valueOf(inCycle);
    }

    /**
     * Returns the vaccine to be administered.
     *
     * @return the vaccine to be administered.
     */
    public Vaccine getVaccine() {
        return vaccine;
    }

    /**
     * Validates an injection cycle.
     *
     * @param inCycle the injection cycle to validate.
     *
     * @throws IllegalArgumentException if the injection cycle is
     * negative.
     */
    public static void validateInjectionCycle(int inCycle) {
        if (inCycle < 0)
            throw new IllegalArgumentException("Negative injection cycle.");
    }

    @Override public String format() {
	return Integer.toString(inCycle) + DELIM_STRING + " " + vaccine.format();
    }

    @Override public String toString() {
        return debug();
    }
}
