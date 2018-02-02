
package amat.vaccine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import jam.lang.Formatted;
import jam.math.DoubleUtil;
import jam.util.ListUtil;

import amat.antigen.Antigen;

/**
 * Defines a multivalent vaccine as a collection of antigens, each
 * administered at a specific concentration.
 *
 * <p>Vaccines may be created by parsing a string representation of
 * the vaccine.  There are two possible string representations, for
 * example: 
 *
 * <pre>
   AG1, 1.0; AG2, 2.0; AG3, 3.0
   AG1, AG2, AG3; 5.0
 * </pre>
 *
 * <p>The first representation contains individual vaccine components
 * separated by semicolons; each component is specified by an antigen
 * key and a concentration separated by a comma.  The first string
 * defines a vaccine with antigen {@code AG1} having concentration
 * {@code 1.0}, antigen {@code AG2} having concentration {@code 2.0},
 * and antigen {@code AG3} having concentration {@code 3.0}.
 *
 * <p>The second representation is an abbreviated format that may be
 * used when all antigens are administered at the same concentration.
 * This short-hand notation contains a comma-separated list of the
 * antigen keys followed by a semicolon and the single concentration
 * to be assigned to all antigens.  The second string above defines a
 * vaccine with antigens {@code AG1}, {@code AG2}, and {@code AG3} at
 * concentration {@code 5.0}.  The second string is equivalent to the
 * full format {@code "AG1, 5.0; AG2, 5.0; AG3, 5.0"}.
 */
public final class Vaccine implements Formatted {
    private final List<VaccineComponent> componentList;
    private final Map<String, VaccineComponent> componentMap;

    // Delimiter separating vaccine components in formatted strings...
    private static final String  COMPONENT_DELIM_STRING  = ";";
    private static final Pattern COMPONENT_DELIM_PATTERN = Pattern.compile(COMPONENT_DELIM_STRING);

    // Delimiter separating antigen keys in short-hand keys...
    private static final String  ANTIGEN_DELIM_STRING  = ",";
    private static final Pattern ANTIGEN_DELIM_PATTERN = Pattern.compile(ANTIGEN_DELIM_STRING);

    /**
     * Creates a new single-component vaccine.
     *
     * @param antigenKey the key of the antigen in the vaccine; the
     * antigen must be present in the global antigen registry.
     *
     * @param concentration the concentration at which the antigen
     * will be administered.
     *
     * @throws IllegalArgumentException unless the antigen is present
     * in the global registry and the concentration is positive.
     */
    public Vaccine(String antigenKey, double concentration) {
        this(new VaccineComponent(antigenKey, concentration));
    }

    /**
     * Creates a new vaccine from an array of components.
     *
     * @param components the antigens to comprise the vaccine, along
     * with their concentrations.
     */
    public Vaccine(VaccineComponent... components) {
        this(Arrays.asList(components));
    }

    /**
     * Creates a new vaccine from a collection of components.
     *
     * @param components the antigens to comprise the vaccine, along
     * with their concentrations.
     */
    public Vaccine(Collection<VaccineComponent> components) {
        this.componentMap  = mapComponents(components);
        this.componentList = ListUtil.view(componentMap.values());
    }

    private static TreeMap<String, VaccineComponent> mapComponents(Collection<VaccineComponent> components) {
        TreeMap<String, VaccineComponent> map = 
            new TreeMap<String, VaccineComponent>();

        if (components.isEmpty())
            throw new IllegalArgumentException("No components.");

        for (VaccineComponent component : components) {
            String key = component.getKey();

            if (map.containsKey(key))
                throw new IllegalArgumentException("Duplicate antigen.");

            map.put(key, component);
        }

        return map;
    }

    /**
     * Provides a quick method to create a standard vaccine cocktail.
     * To use this shortcut, antigens must be registered with keys
     * {@code E1, E2, ...}.
     *
     * @param agCount the number of antigens in the vaccine.
     *
     * @param eqConc the antigen concentration (equal for all).
     *
     * @return a vaccine containing antigens {@code E1, E2, ...} each
     * with the specified concentration.
     *
     * @throws RuntimeException unless the number of antigens and
     * concentration are positive and antigens are registered with
     * keys {@code E1, E2, ...}.
     */
    public static Vaccine shortcut(int agCount, double eqConc) {
        List<VaccineComponent> components = new ArrayList<VaccineComponent>(agCount);

        for (int agIndex = 1; agIndex <= agCount; agIndex++)
            components.add(new VaccineComponent(shortcutKey(agIndex), eqConc));

        return new Vaccine(components);
    }

    private static String shortcutKey(int agIndex) {
        return String.format("E%d", agIndex);
    }

    /**
     * Creates a new vaccine from its string representation.
     *
     * <p>The antigens must be present in the global antigen registry
     * before calling this method.
     *
     * @param str a string formatted as described in the class
     * comments.
     *
     * @return the vaccine encoded by the given string.
     *
     * @throws RuntimeException unless the string argument is properly
     * formatted and all antigens referenced are present in the global
     * registry.
     */
    public static Vaccine parse(String str) {
        String[] fields = COMPONENT_DELIM_PATTERN.split(str);

        if (isShortHand(fields))
            return parseShortHand(fields);
        else
            return parseMultiComp(fields);
    }

    private static boolean isShortHand(String[] fields) {
        //
        // The original vaccine representation is in "short hand"
        // (contains a single concentration to be assigned to all
        // angigens) if there are exactly two fields ( exactly one
        // semicolon in the original string) and the second field
        // contains a single concentration value.
        //
        return fields.length == 2 && DoubleUtil.isDouble(fields[1]);
    }

    private static Vaccine parseShortHand(String[] fields) {
        //
        // The first field is the comma-separated list of antigen
        // keys; the second field is the single concentration for
        // all antigens...
        //
        assert fields.length == 2;

        String[] keys = ANTIGEN_DELIM_PATTERN.split(fields[0]);
        double   conc = Double.parseDouble(fields[1]);

        List<VaccineComponent> components = new ArrayList<VaccineComponent>(keys.length);

        for (String key : keys)
            components.add(new VaccineComponent(key.trim(), conc));

        return new Vaccine(components);
    }

    private static Vaccine parseMultiComp(String[] fields) {
        //
        // Each field is a separate vaccine component...
        //
        List<VaccineComponent> components = new ArrayList<VaccineComponent>(fields.length);

        for (String field : fields)
            components.add(VaccineComponent.parse(field.trim()));

        return new Vaccine(components);
    }

    /**
     * Returns the number of components in this vaccine.
     *
     * @return the number of components in this vaccine.
     */
    public int countComponents() {
	return componentList.size();
    }

    /**
     * Returns the unique antigens contained in this vaccine.
     *
     * @return the unique antigens contained in this vaccine in an
     * unmodifiable set.
     */
    public Set<Antigen> getAntigens() {
        Set<Antigen> antigens = new TreeSet<Antigen>();

        for (VaccineComponent component : componentList)
            antigens.add(component.getAntigen());

        return Collections.unmodifiableSet(antigens);
    }

    /**
     * Returns a view of the components contained in this vaccine.
     *
     * @return a view (an unmodifiable list) of the components in
     * this vaccine.
     */
    public List<VaccineComponent> viewComponents() {
        return componentList;
    }

    @Override public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append(componentList.get(0).format());

        for (int index = 1; index < componentList.size(); index++) {
            builder.append(COMPONENT_DELIM_STRING);
            builder.append(" ");
            builder.append(componentList.get(index).format());
        }

        return builder.toString();
    }

    @Override public String toString() {
        return debug();
    }
}
