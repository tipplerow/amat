
package amat.vaccine;

import java.util.regex.Pattern;

import jam.chem.Concentration;
import jam.chem.HalfLife;

import jam.lang.Formatted;
import jam.lang.JamException;

import amat.antigen.Antigen;

/**
 * Encapsulates a single component of a multivalent vaccine: one
 * antigen administered at a fixed concentration.
 *
 * <p>Vaccine components have a well-define string representation: the
 * antigen key and concentration separated by a comma.  One example is
 * {@code "AG1, 5.0"}.  Components may be created from strings in this
 * format by the {@code parse(String)} method.  The antigen referenced
 * by key must be present in the global antigen registry before parsing 
 * the string.
 */
public final class VaccineComponent implements Formatted {
    private final Antigen antigen;
    private final Concentration concentration;

    // Delimiter separating antigen key and concentration in formatted
    // strings.
    private static final String  DELIM_STRING  = ",";
    private static final Pattern DELIM_PATTERN = Pattern.compile(DELIM_STRING);

    /**
     * Creates a new vaccine component with a fixed antigen and
     * concentration.
     *
     * @param antigenKey the key of the antigen in the component; the
     * antigen must be present in the global antigen registry.
     *
     * @param concentration the concentration at which the antigen
     * will be administered.
     *
     * @throws IllegalArgumentException unless the antigen is present
     * in the global registry and the concentration is positive.
     */
    public VaccineComponent(String antigenKey, double concentration) {
        this(Antigen.require(antigenKey), Concentration.valueOf(concentration));
    }

    /**
     * Creates a new vaccine component with a fixed antigen and
     * concentration.
     *
     * @param antigen the antigen in the component.
     *
     * @param concentration the concentration at which the antigen
     * will be administered.
     */
    public VaccineComponent(Antigen antigen, Concentration concentration) {
        this.antigen = antigen;
        this.concentration = concentration;
    }

    /**
     * Simulates decay of the antigen in this vaccine component.
     *
     * @param halfLife the half-life for antigen decay, expressed as a
     * number of germinal center cycles.
     *
     * @return a new vaccine component with the same antigen as this
     * component but with its concentration reduced by decay.
     */
    public VaccineComponent decay(HalfLife halfLife) {
        return new VaccineComponent(antigen, concentration.decay(halfLife, 1));
    }

    /**
     * Creates a new vaccine component from its string representation.
     *
     * <p>The antigen must be present in the global antigen registry
     * before calling this method.
     *
     * @param str a string formatted as described in the class
     * comments.
     *
     * @return the vaccine component encoded by the given string.
     *
     * @throws RuntimeException unless the string argument is properly
     * formatted and the antigen is present in the global registry.
     */
    public static VaccineComponent parse(String str) {
        String[] fields = DELIM_PATTERN.split(str);

        if (fields.length != 2)
            throw JamException.runtime("Invalid vaccine component: [%s].", str);

        String key  = fields[0].trim();
        double conc = Double.parseDouble(fields[1].trim());

        return new VaccineComponent(key, conc);
    }

    /**
     * Returns the antigen in this component.
     *
     * @return the antigen in this component.
     */
    public Antigen getAntigen() {
        return antigen;
    }

    /**
     * Returns the concentration at which the antigen will be
     * administered.
     *
     * @return the concentration at which the antigen will be
     * administered.
     */
    public Concentration getConcentration() {
        return concentration;
    }

    /**
     * Returns the key of the antigen in this component, which may
     * serve as a key for the component itself.
     *
     * @return the key of the antigen in this component, which may
     */
    public String getKey() {
        return antigen.getKey();
    }

    @Override public String format() {
        return antigen.getKey() + DELIM_STRING + " " + concentration.format();
    }

    @Override public String toString() {
	return debug();
    }
}
