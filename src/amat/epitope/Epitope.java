
package amat.epitope;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jam.app.JamProperties;
import jam.data.DataMatrix;
import jam.io.FileParser;
import jam.lang.Formatted;
import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.math.IntUtil;
import jam.util.CollectionUtil;

import amat.structure.CVClassifier;
import amat.structure.DiscreteStructure;
import amat.structure.Structure;
import amat.structure.StructureType;

/**
 * Represents immunogenic entities that may bind with B cell or T cell
 * receptors.
 *
 * <p>All epitopes are identified by a unique key provided at the time
 * of creation via factory methods {@code add(String, Structure} and
 * {@code parse(String)}.  This class maintains a global registry of
 * epitopes indexed by their key strings.  Once an epitope is created,
 * it cannot be removed.  Attempting to add another epitope with the
 * same key will generate an exception.
 *
 * <p>Epitopes may be defined in configuration files formatted as
 * follows:
 *
 * <pre>
   # First eight bits are conserved, next eight are variable...
   E1: BitStructure(0000 0000 1111 1111)
   E2: BitStructure(0000 0000 1010 1010)
   E3: BitStructure(0000 0000 1100 1100)
   E4: SpinStructure(+--+--++---+-+--++) # Different types may be present...
 * </pre>
 *
 * Epitopes must be given on a single line, which must contain the key
 * and structure separated by a colon.  The structure is specified by
 * its class name and structual code in parentheses.  Note that the
 * structure may contain white space to aid in reading.
 *
 * <p>The hash sign ({@code '#'}) denotes a comment; all text from the
 * comment character to the end of the line will be ignored.  Blank
 * lines and white space beginning a line will be ignored.
 *
 * <p>The default configuration file is defined by the system property
 * {@code amat.Epitope.configFile}.  Calling {@link Epitope#load()}
 * (with no arguments) will resolve the file name using the system
 * property and load the desired file.
 */
public final class Epitope extends KeyedObject<String> implements Formatted {
    private final Structure structure;

    private static final Map<String, Epitope> instances = new LinkedHashMap<String, Epitope>();

    // Classifier is created on demand...
    private static CVClassifier classifier = null;

    // Comment text delimiter...
    private static final Pattern COMMENT_PATTERN = Pattern.compile("#");

    // Key-structure separator...
    private static final Pattern KEY_SEPARATOR = Pattern.compile(":");

    private static final class Loader extends FileParser {
        private Loader(File file) {
            super(file, COMMENT_PATTERN);
        }

        @Override protected void processLine(String dataLine) {
            Epitope.parse(dataLine);
        }
    }

    private Epitope(String key, Structure structure) {
        super(key);

        if (exists(key))
            throw JamException.runtime("Duplicate epitope key: [%s].", key);

        if (structure == null)
            throw new NullPointerException("Missing structure.");

        this.structure = structure;
        instances.put(key, this);

        // Any prior classification is invalidated...
        classifier = null;
    }

    /**
     * Name of the system property containing the name of the epitope
     * configuration file.
     */
    public static final String CONFIG_FILE_PROPERTY = "amat.Epitope.configFile";

    /**
     * Creates a new epitope with a fixed structure and unique key.
     *
     * @param key the key for the global registry.
     *
     * @param structure the fixed structure to assign.
     *
     * @return the new epitope.
     *
     * @throws RuntimeException if an epitope with the same key
     * already exists.
     */
    public static Epitope add(String key, Structure structure) {
        return new Epitope(key, structure);
    }

    /**
     * Returns a collection view of all epitopes in the registry.
     *
     * @return an unmodifiable collection containing all epitopes in
     * the registry;
     */
    public static Collection<Epitope> all() {
        return Collections.unmodifiableCollection(instances.values());
    }

    /**
     * Classifies structural elements as either <em>conserved</em> or
     * <em>variable</em> across all epitopes in the global registry.
     *
     * <p>Note that the classification is a property of the full set
     * of epitopes defined in the registry, not only epitopes that
     * were encountered during affinity maturation.
     *
     * @return a classifier that may be queried to identify conserved
     * and variable elements.
     */
    public static CVClassifier classify() {
        if (classifier == null) {
            classifier = CVClassifier.classify(getStructures(instances.values()));
        }

        return classifier;
    }

    /**
     * Returns the number of registered epitopes.
     *
     * @return the number of registered epitopes.
     */
    public static int count() {
        return instances.size();
    }

    /**
     * Identifies registered epitope keys.
     *
     * @param key the key to search for.
     *
     * @return {@code true} iff an epitope with the given key has been
     * created and registered.
     */
    public static boolean exists(String key) {
        return instances.containsKey(key);
    }

    /**
     * Returns a set view of all epitope keys in the registry.
     *
     * @return an unmodifiable set containing all the keys of all
     * epitopes in the registry;
     */
    public static Set<String> keys() {
        return Collections.unmodifiableSet(instances.keySet());
    }

    /**
     * Loads epitopes from the configuration file specified by the
     * system property {@code Epitope.configFile}.
     *
     * @throws RuntimeException unless the system property is defined,
     * the file defined by the property is readable, and it contains
     * properly formatted epitopes with unique keys.
     */
    public static void load() {
        load(JamProperties.getRequired(CONFIG_FILE_PROPERTY));
    }

    /**
     * Loads epitopes from a configuration file.
     *
     * @param fileName the name of an epitope configuration file
     * formatted as described in the class comments.
     *
     * @throws RuntimeException unless the file is readable and
     * contains properly formatted epitopes with unique keys.
     */
    public static void load(String fileName) {
        load(new File(fileName));
    }

    /**
     * Loads epitopes from a configuration file.
     *
     * @param file an epitope configuration file formatted as
     * described in the class comments.
     *
     * @throws RuntimeException unless the file is readable and
     * contains properly formatted epitopes with unique keys.
     */
    public static void load(File file) {
        Loader loader = new Loader(file);
        loader.processFile();
    }

    /**
     * Retrieves an epitope by its key.
     *
     * @param key the key to search for.
     *
     * @return the epitope registered with the given key; {@code null}
     * if there is no such epitope.
     */
    public static Epitope lookup(String key) {
        return instances.get(key);
    }

    /**
     * Computes the mutational distance between all pairs of epitopes
     * defined in the global registry.
     *
     * @return a data matrix containing the mutational distances
     * indexed by epitope key.
     */
    public static DataMatrix mutationalDistance() {
	Set<String> keySet = keys();
	String[]    keyVec = keySet.toArray(new String[0]);
	DataMatrix  result = new DataMatrix(keySet, keySet, 0.0);

	for (int i = 0; i < keyVec.length; i++) {
	    for (int j = i + 1; j < keyVec.length; j++) {
		String ki = keyVec[i];
		String kj = keyVec[j];

		Epitope ei = require(ki);
		Epitope ej = require(kj);

		double md = mutationalDistance(ei, ej);

		result.set(ki, kj, md);
		result.set(kj, ki, md);
	    }
	}

	return result;
    }

    /**
     * Computes the mutational distance between two epitopes.
     *
     * @param e1 the first epitope.
     *
     * @param e2 the second epitope.
     *
     * @return the mutational distance between the given epitopes.
     */
    public static double mutationalDistance(Epitope e1, Epitope e2) {
	return e1.getStructure().mutationalDistance(e2.getStructure());
    }

    /**
     * Creates a new epitope from its string representation and adds
     * it to the global registry.
     *
     * @param s a string formatted with the epitope key and structure
     * separated by a colon; see the class comments for examples.
     *
     * @return the new epitope.
     *
     * @throws RuntimeException unless the string is properly
     * formatted with a unique key.
     */
    public static Epitope parse(String s) {
        String[] fields = KEY_SEPARATOR.split(s);

        if (fields.length != 2)
            throw JamException.runtime("Invalid epitope specification: [%s].", s);

        String key = fields[0].trim();
        Structure structure = Structure.parse(fields[1].trim());

        return add(key, structure);
    }

    /**
     * Retrieves an epitope by its key and throws an exception if the
     * epitope is not found.
     *
     * @param key the key to search for.
     *
     * @return the epitope registered with the given key.
     *
     * @throws RuntimeException unless the epitope is found.
     */
    public static Epitope require(String key) {
        Epitope epitope = lookup(key);

        if (epitope == null)
            throw JamException.runtime("Missing required epitope: [%s].", key);
            
        return epitope;
    }

    /**
     * Returns the unique cardinality for the epitopes in the global
     * registry.
     *
     * @return the unique cardinality for the epitopes in the global
     * registry.
     *
     * @throws RuntimeException unless there is at least one epitope
     * in the global registry and all epitopes in the registry have
     * discrete structures with identical cardinalities.
     */
    public static int resolveCardinality() {
        if (count() == 0)
            throw JamException.runtime("No epitopes.");

        int uniqueCardinality = 0;

        for (Epitope epitope : instances.values()) {
            int thisCardinality = 
                ((DiscreteStructure) epitope.getStructure()).cardinality();

            if (uniqueCardinality < 1)
                uniqueCardinality = thisCardinality;
            else if (thisCardinality != uniqueCardinality)
                throw JamException.runtime("Non-unique epitope cardinalities.");
        }
                
        return uniqueCardinality;
    }

    /**
     * Returns the unique length for the epitopes in the global
     * registry.
     *
     * @return the unique length for the epitopes in the global
     * registry.
     *
     * @throws RuntimeException unless there is at least one epitope
     * in the global registry and all epitopes in the registry have
     * identical lengths.
     */
    public static int resolveLength() {
        if (count() == 0)
            throw JamException.runtime("No epitopes.");

        int uniqueLength = 0;

        for (Epitope epitope : instances.values()) {
            int thisLength = epitope.getStructure().length();

            if (uniqueLength < 1)
                uniqueLength = thisLength;
            else if (thisLength != uniqueLength)
                throw JamException.runtime("Non-unique epitope lengths.");
        }
                
        return uniqueLength;
    }

    /**
     * Returns the unique structure type for the epitopes in the
     * global registry.
     *
     * @return the unique structure type for the epitopes in the
     * global registry.
     *
     * @throws RuntimeException unless there is at least one epitope
     * in the global registry and all epitopes in the registry have
     * identical structure types.
     */
    public static StructureType resolveType() {
        if (count() == 0)
            throw JamException.runtime("No epitopes.");

        StructureType uniqueType = null;

        for (Epitope epitope : instances.values()) {
            StructureType thisType = epitope.getStructure().getType();

            if (uniqueType == null)
                uniqueType = thisType;
            else if (!thisType.equals(uniqueType))
                throw JamException.runtime("Non-unique epitope structure types.");
        }
                
        return uniqueType;
    }

    /**
     * Returns the fixed structure of this epitope.
     *
     * @return the fixed structure of this epitope.
     */
    public Structure getStructure() {
        return structure;
    }

    /**
     * Extracts the keys from a collection of epitopes.
     *
     * @param epitopes the epitopes to process.
     *
     * @return a list containing the key of each epitope, in the
     * order returned by the collection iterator.
     */
    public static List<String> getKeys(Collection<Epitope> epitopes) {
        List<String> keys = new ArrayList<String>(epitopes.size());

        for (Epitope epitope : epitopes)
            keys.add(epitope.getKey());

        return keys;
    }

    /**
     * Extracts the structures from a collection of epitopes.
     *
     * @param epitopes the epitopes to process.
     *
     * @return a list containing the structure of each epitope, in the
     * order returned by the collection iterator.
     */
    public static List<Structure> getStructures(Collection<Epitope> epitopes) {
        List<Structure> structures = new ArrayList<Structure>(epitopes.size());

        for (Epitope epitope : epitopes)
            structures.add(epitope.getStructure());

        return structures;
    }

    /**
     * Returns one epitope from the global registry to serve as a
     * template for the conserved region.
     *
     * @return one epitope from the global registry to serve as a
     * template for the conserved region.
     */
    public static Epitope template() {
        return CollectionUtil.peek(all());
    }

    @Override public String format() {
        return getKey() + ": " + getStructure().format();
    }

    @Override public String toString() {
        return debug();
    }
}
