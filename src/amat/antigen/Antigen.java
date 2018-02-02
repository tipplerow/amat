
package amat.antigen;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jam.app.JamProperties;
import jam.io.FileParser;
import jam.lang.Formatted;
import jam.lang.JamException;
import jam.lang.KeyedObject;

import amat.epitope.Epitope;

/**
 * Defines an antigen as an unordered collection of epitopes, each of
 * which may bind with a B cell or T cell receptor.
 *
 * <p>All antigens are identified by a unique key provided at the time
 * of creation via factory methods {@code add()} and {@code parse()}.
 * This class maintains a global registry of antigens indexed by their
 * key strings.  Once an antigen is added to the registry, it cannot
 * be removed.  Attempting to add another antigen with the same key
 * will generate an exception.
 *
 * <p>Antigens may be defined in configuration files formatted as
 * follows:
 *
 * <pre>
   # Epitopes must alread by added to the global registry...
   A1: E1
   A2: E2, E3 # Some antigens may contain multiple epitopes...
 * </pre>
 *
 * Antigens must be given on a single line, which must contain the key
 * and the epitope specification separated by a colon.  The epitopes
 * are identified by their keys from the global epitope registry; the
 * epitope keys must be separated by commas.
 *
 * <p>The hash sign ({@code '#'}) denotes a comment; all text from the
 * comment character to the end of the line will be ignored.  Blank
 * lines and white space beginning a line will be ignored.
 *
 * <p>The default configuration file is defined by the system property
 * {@code amat.Antigen.configFile}.  Calling {@link Antigen#load()}
 * (with no arguments) will resolve the file name using the system
 * property and load the desired file.  If no configuration file is
 * specified in the system properties, the antigens will be generated
 * automatically via {@link Antigen#auto()}.
 */
public final class Antigen extends KeyedObject<String> implements Formatted {
    private final List<Epitope> epitopes;

    private static final Map<String, Antigen> instances = new LinkedHashMap<String, Antigen>();

    // Comment text delimiter...
    private static final Pattern COMMENT_PATTERN = Pattern.compile("#");

    // Key-structure separator...
    private static final Pattern KEY_SEPARATOR = Pattern.compile(":");

    // Epitope separator...
    private static final Pattern EPITOPE_SEPARATOR = Pattern.compile(",");

    private static final class Loader extends FileParser {
        private Loader(File file) {
            super(file, COMMENT_PATTERN);
        }

        @Override protected void processLine(String dataLine) {
            Antigen.parse(dataLine);
        }
    }

    private Antigen(String key, Collection<Epitope> epitopes) {
        super(key);

        if (exists(key))
            throw JamException.runtime("Duplicate antigen key: [%s].", key);

        if (epitopes.isEmpty())
            throw JamException.runtime("At least one epitope is required.");

        for (Epitope epitope : epitopes)
            if (epitope == null)
                throw new NullPointerException("Missing epitope.");

        this.epitopes = Collections.unmodifiableList(new ArrayList<Epitope>(epitopes));
        instances.put(key, this);
    }

    /**
     * Name of the system property containing the name of the antigen
     * configuration file.
     */
    public static final String CONFIG_FILE_PROPERTY = "amat.Antigen.configFile";

    /**
     * Creates a new antigen from a sequence of epitopes.
     *
     * @param key the unique key for the antigen in the global
     * registry.
     *
     * @param epitopes the epitopes to comprise the antigen.
     *
     * @return the new antigen.
     *
     * @throws RuntimeException if an antigen with the same key
     * already exists.
     */
    public static Antigen add(String key, Epitope... epitopes) {
        return add(key, Arrays.asList(epitopes));
    }

    /**
     * Creates a new antigen from a sequence of epitopes.
     *
     * @param key the unique key for the antigen in the global
     * registry.
     *
     * @param epitopes the epitopes to comprise the antigen.
     *
     * @return the new antigen.
     *
     * @throws RuntimeException if an antigen with the same key
     * already exists or if the epitope collection is empty.
     */
    public static Antigen add(String key, Collection<Epitope> epitopes) {
        return new Antigen(key, epitopes);
    }

    /**
     * Returns a collection view of all antigens in the registry.
     *
     * @return an unmodifiable collection containing all antigens in
     * the registry;
     */
    public static Collection<Antigen> all() {
        return Collections.unmodifiableCollection(instances.values());
    }

    /**
     * Automatically creates one antigen for each epitope in the
     * epitope registry.  The antigens will contain exactly one
     * epitope and have the same key as that epitope.
     *
     * <p>This is useful when antigens contain only a single epitope
     * and there is only a semantic distinction between antigens and
     * epitopes.
     */
    public static void auto() {
        for (Epitope epitope : Epitope.all())
            add(epitope.getKey(), epitope);
    }

    /**
     * Returns the number of registered antigens.
     *
     * @return the number of registered antigens.
     */
    public static int count() {
        return instances.size();
    }

    /**
     * Identifies registered antigen keys.
     *
     * @param key the key to search for.
     *
     * @return {@code true} iff an antigen with the given key has been
     * created and registered.
     */
    public static boolean exists(String key) {
        return instances.containsKey(key);
    }

    /**
     * Returns a set view of all antigen keys in the registry.
     *
     * @return an unmodifiable set containing all the keys of all
     * antigens in the registry;
     */
    public static Set<String> keys() {
        return Collections.unmodifiableSet(instances.keySet());
    }

    /**
     * Loads antigens from the configuration file specified by the
     * system property {@code Antigen.configFile}.  
     *
     * <p>If the system properties do not specify a configuration
     * file, the antigens will be generated automatically by the
     * {@code auto()} method.
     *
     * @throws RuntimeException if a configuration file is defined in
     * the system properties but does not contain properly formatted
     * antigens.
     */
    public static void load() {
        if (JamProperties.isSet(CONFIG_FILE_PROPERTY))
            load(JamProperties.getRequired(CONFIG_FILE_PROPERTY));
        else
            auto();
    }

    /**
     * Loads antigens from a configuration file.
     *
     * @param fileName the name of a file formatted as described in
     * the class comments.
     *
     * @throws RuntimeException unless the file is readable and
     * contains properly formatted antigens with unique keys.
     */
    public static void load(String fileName) {
        load(new File(fileName));
    }

    /**
     * Loads antigens from a configuration file.
     *
     * @param file a file formatted as described in the class
     * comments.
     *
     * @throws RuntimeException unless the file is readable and
     * contains properly formatted antigens with unique keys.
     */
    public static void load(File file) {
        Loader loader = new Loader(file);
        loader.processFile();
    }

    /**
     * Retrieves an antigen by its key.
     *
     * @param key the key to search for.
     *
     * @return the antigen registered with the given key; {@code null}
     * if there is no such antigen.
     */
    public static Antigen lookup(String key) {
        return instances.get(key);
    }

    /**
     * Creates a new antigen from its string representation and adds
     * it to the global registry.
     *
     * @param s a string formatted with the antigen key and structure
     * separated by a colon; see the class comments for examples.
     *
     * @return the new antigen.
     *
     * @throws RuntimeException unless the string is properly
     * formatted with a unique key.
     */
    public static Antigen parse(String s) {
        String[] fields = KEY_SEPARATOR.split(s);

        if (fields.length != 2)
            throw JamException.runtime("Invalid antigen specification: [%s].", s);

        String key = fields[0].trim();
        List<Epitope> epitopes = parseEpitopes(fields[1]);

        return add(key, epitopes);
    }

    private static List<Epitope> parseEpitopes(String s) {
        String[] fields = EPITOPE_SEPARATOR.split(s);
        List<Epitope> epitopes = new ArrayList<Epitope>();

        for (String epitopeKey : fields)
            epitopes.add(Epitope.require(epitopeKey.trim()));

        return epitopes;
    }

    /**
     * Retrieves an antigen by its key and throws an exception if the
     * antigen is not found.
     *
     * @param key the key to search for.
     *
     * @return the antigen registered with the given key.
     *
     * @throws RuntimeException unless the antigen is found.
     */
    public static Antigen require(String key) {
        Antigen antigen = lookup(key);

        if (antigen == null)
            throw JamException.runtime("Missing required antigen: [%s].", key);
            
        return antigen;
    }

    /**
     * Returns the keys of the epitopes contained in this antigen.
     *
     * @return a newly created list containing the keys of the
     * epitopes contained in this antigen; changes to the list will
     * not alter this antigen.
     */
    public List<String> getEpitopeKeys() {
        return Epitope.getKeys(epitopes);
    }

    /**
     * Returns a view of the epitopes contained in this antigen.
     *
     * @return a view (an unmodifiable collection) of the epitopes
     * contained in this antigen.
     */
    public Collection<Epitope> viewEpitopes() {
        return epitopes;
    }

    @Override public String format() {
        StringBuilder builder = new StringBuilder();

        builder.append(getKey());
        builder.append(": ");
        builder.append(epitopes.get(0).getKey());

        for (int index = 1; index < epitopes.size(); index++) {
            builder.append(", ");
            builder.append(epitopes.get(index).getKey());
        }

        return builder.toString();
    }

    @Override public String toString() {
	return debug();
    }
}
