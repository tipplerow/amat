
package amat.vaccine;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.IntRange;
import jam.util.ListUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.epitope.Epitope;

/**
 * Represents a complete vaccination schedule as a sequence of
 * pre-defined vaccination events.
 *
 * <p>Vaccination schedules may be defined in configuration files
 * formatted as follows:
 *
 * <pre>
   # Two single-antigen immunizations followed by a cocktail of three...
   0:  AG1, 1.0
   20: AG2, 0.5
   50: AG3, AG4, AG5; 2.5
 * </pre>
 *
 * Each vaccination event must be given on a single line, which must
 * contain the injection cycle and vaccine separated by a colon. The
 * vaccine is specified by the antigen keys and concentrations. See
 * {@link Vaccine} for a detailed description of supported formats.
 * The antigens referenced by key must be present in the global
 * antigen registry before the file is loaded.
 *
 * <p>The hash sign ({@code '#'}) denotes a comment; all text from the
 * comment character to the end of the line will be ignored.  Blank
 * lines and white space beginning a line will be ignored.
 *
 * <p>A global schedule ({@link VaccinationSchedule#global()}) may be
 * defined in one of the following ways (in order of priority):
 * <ol>
 * <li>
 *   Prior to the first call to {@code global()}, any schedule may be
 *   assigned as the global schedule by calling {@code setGlobal} with
 *   that schedule.  The method {@code setGlobal} may be called only
 *   once in an application, and it must be called before the global
 *   schedule is accessed via {@code global()}.  Violating these
 *   conditions will trigger an exception.
 * </li>
 * <li>
 *   If no global schedule has been assigned and the system property
 *   {@code amat.Vaccine.configFile} is set, the global schedule will
 *   be assigned to the schedule defined in the file with that name.
 *   In this scenario, the epitope and antigen files will be loaded
 *   automatically.
 * </li>
 * <li>
 *   If the system property {@code amat.Vaccine.configFile} is not set
 *   and no global schedule has been assigned via {@code setGlobal}, a
 *   bolus vaccine (a single vaccination event) is created with {@code
 *   amat.Vaccine.antigenCount} specifying the number of antigens and
 *   {@code amat.Vaccine.totalConc} specifying the total concentration
 *   of antigens.  In this scenario, the antigens must have keys
 *   {@code E1, E2, ...}.
 * </li>
 * </ol>
 */
public final class VaccinationSchedule {
    private final TreeMap<Integer, VaccinationEvent> eventMap;
    private final List<VaccinationEvent> eventList;

    // The Kth element in this list contains all antigens administered
    // on or before GC cycle K...
    private final List<Set<Antigen>> antigenFootprints = new AntigenFootprintList(this);

    // The Kth element in this list contains all epitopes administered
    // on or before GC cycle K...
    private final List<Set<Epitope>> epitopeFootprints = new EpitopeFootprintList(this);

    // The Kth element in this list contains all vaccines administered
    // on or before GC cycle K...
    private final List<List<Vaccine>> vaccineFootprints = new VaccineFootprintList(this);

    // The global instance...
    private static VaccinationSchedule global = null;

    /**
     * Name of the system property containing the name of the
     * vaccination schedule file.
     */
    public static final String CONFIG_FILE_PROPERTY = "amat.Vaccine.configFile";

    /**
     * Name of the system property containing the number of antigens
     * (for the "shortcut" specifcation of a bolus vaccine).
     */
    public static final String ANTIGEN_COUNT_PROPERTY = "amat.Vaccine.antigenCount";

    /**
     * Name of the system property containing the total antigen
     * concentration (for the "shortcut" specifcation of a bolus
     * vaccine).
     */
    public static final String TOTAL_CONC_PROPERTY = "amat.Vaccine.totalConc";

    /**
     * Creates a new vaccination schedule from an array of events.
     *
     * @param events the vaccination events to comprise the schedule.
     *
     * @throws IllegalArgumentException if any events occur on the
     * same injection cycle or if the first event does not occur on
     * cycle zero.
     */
    public VaccinationSchedule(VaccinationEvent... events) {
        this(Arrays.asList(events));
    }

    /**
     * Creates a new vaccination schedule as a collection of events.
     *
     * @param events the vaccination events to comprise the schedule.
     *
     * @throws IllegalArgumentException if any events occur on the
     * same injection cycle or if the first event does not occur on
     * cycle zero.
     */
    public VaccinationSchedule(Collection<VaccinationEvent> events) {
        this.eventMap  = mapEvents(events);
        this.eventList = ListUtil.view(eventMap.values());
    }

    private static TreeMap<Integer, VaccinationEvent> mapEvents(Collection<VaccinationEvent> events) {
        TreeMap<Integer, VaccinationEvent> map = 
            new TreeMap<Integer, VaccinationEvent>();

        if (events.isEmpty())
            throw new IllegalArgumentException("No events.");

        for (VaccinationEvent event : events) {
            Integer key = event.getKey();

            if (map.containsKey(key))
                throw new IllegalArgumentException("Duplicate injection cycle.");

            map.put(key, event);
        }

        // Ensure that the first event occurs on cycle zero...
        if (map.firstKey().intValue() != 0)
            throw new IllegalArgumentException("First event must occur on cycle zero.");

        return map;
    }

    /**
     * Creates a schedule for a bolus vaccine: a single vaccine
     * administered on injection cycle zero.
     * 
     * @param vaccine the single vaccine.
     *
     * @return the bolus vaccination schedule.
     */
    public static VaccinationSchedule bolus(Vaccine vaccine) {
        return new VaccinationSchedule(Arrays.asList(VaccinationEvent.bolus(vaccine)));
    }

    /**
     * Returns the global vaccination schedule (as described in the
     * class header comments).
     *
     * @return the global vaccination schedule.
     *
     * @throws RuntimeException unless a global vaccination schedule
     * is properly defined.
     */
    public static VaccinationSchedule global() {
        if (global == null)
            loadGlobal();

        return global;
    }

    private static void loadGlobal() {
        if (Epitope.count() < 1)
            Epitope.load();

        if (Antigen.count() < 1)
            Antigen.load();

        if (JamProperties.isSet(CONFIG_FILE_PROPERTY))
            global = load(JamProperties.getRequired(CONFIG_FILE_PROPERTY));
        else
            global = shortcut();
    }

    /**
     * Assigns the global vaccination schedule.
     *
     * @param schedule the schedule to be marked as the global schedule.
     *
     * @throws IllegalStateException if the global schedule is already
     * set.
     */
    public static void setGlobal(VaccinationSchedule schedule) {
        if (global != null)
            throw new IllegalStateException("The global vaccination schedule is already defined.");

        global = schedule;
    }

    /**
     * Loads a vaccination schedule from a configuration file.
     *
     * @param fileName the name of a vaccination schedule file
     * formatted as described in the class comments.
     *
     * @return the vaccination schedule defined by the given file.
     *
     * @throws RuntimeException unless the configuration file is
     * readable and it contains a properly formatted vaccination
     * schedule.
     */
    public static VaccinationSchedule load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Loads a vaccination schedule from a configuration file.
     *
     * @param file a vaccination schedule file formatted as described
     * in the class comments.
     *
     * @return the vaccination schedule defined by the given file.
     *
     * @throws RuntimeException unless the configuration file is
     * readable and it contains a properly formatted vaccination
     * schedule.
     */
    public static VaccinationSchedule load(File file) {
        EventLoader loader = new EventLoader(file);
        loader.processFile();

        return loader.makeSchedule();
    }

    /**
     * Creates a bolus schedule using the "shortcut" convention: the
     * system property {@code amat.Vaccine.antigenCount} defines the
     * number of antigens and {@code amat.Vaccine.totalConc} defines
     * the total concentration of antigens.  In this scenario, the
     * antigens must have keys {@code E1, E2, ...}.
     *
     * @return the bolus vaccination schedule.
     *
     * @throws RuntimeException unless the required system properties
     * {@code amat.Vaccine.antigenCount} and {@code amat.Vaccine.totalConc}
     * are set and antigens are registered with keys {@code E1, E2, ...}.
     */
    public static VaccinationSchedule shortcut() {
        int    agCount   = JamProperties.getRequiredInt(ANTIGEN_COUNT_PROPERTY, IntRange.POSITIVE);
        double totalConc = JamProperties.getRequiredDouble(TOTAL_CONC_PROPERTY, DoubleRange.POSITIVE);

        return shortcut(agCount, totalConc);
    }

    /**
     * Creates a bolus schedule using the "shortcut" convention: A
     * single vaccine is administered on the first injection cycle.
     * It contains {@code agCount} antigens with keys {@code E1, E2,
     * ...} and a total antigen concentration of {@code totalConc}.
     * 
     * @param agCount the number of antigens in the vaccine.
     *
     * @param totalConc the total antigen concentration.
     *
     * @return the bolus vaccination schedule.
     *
     * @throws RuntimeException unless the number of antigens and
     * total concentration are positive and antigens are registered
     * with keys {@code E1, E2, ...}.
     */
    public static VaccinationSchedule shortcut(int agCount, double totalConc) {
        return bolus(Vaccine.shortcut(agCount, totalConc / agCount));
    }
        
    /**
     * Identifies cycles on which events occur.
     *
     * @param cycleGC the germinal center cycle to examine.
     *
     * @return {@code true} iff this schedule contains an event
     * occurring on the specified germinal center cycle.
     */
    public boolean containsEvent(int cycleGC) {
        return eventMap.containsKey(VaccinationEvent.getKey(cycleGC));
    }

    /**
     * Returns the number of distinct events in this schedule.
     *
     * @return the number of distinct events in this schedule.
     */
    public int countEvents() {
	return eventMap.size();
    }

    /**
     * Returns the event occurring on a specific germinal center
     * cycle.
     *
     * @param cycleGC the germinal center cycle to examine.
     *
     * @return the vaccination event occurring on the specified cycle,
     * or {@code null} if no event occurs exactly on that cycle.
     */
    public VaccinationEvent eventOn(int cycleGC) {
        return eventMap.get(VaccinationEvent.getKey(cycleGC));
        
    }

    /**
     * Returns the antigen <em>footprint</em> for a specific germinal
     * center cycle: the set of all antigens administered on or before
     * that cycle.
     *
     * @param cycleGC the germinal center cycle of interest.
     *
     * @return an unmodifiable set containing the antigen footprint
     * for the specified cycle.
     *
     * @throws RuntimeException if the cycle is negative.
     */
    public Set<Antigen> getAntigenFootprint(int cycleGC) {
        return antigenFootprints.get(cycleGC);
    }

    /**
     * Returns the antigen pool <em>footprint</em> for a specific
     * germinal center cycle: the aggregate pool of all antigens
     * administered on or before that cycle.
     *
     * @param cycleGC the germinal center cycle of interest.
     *
     * @return the antigen pool footprint for the specified cycle.
     *
     * @throws RuntimeException if the cycle is negative.
     */
    public AntigenPool getAntigenPoolFootprint(int cycleGC) {
        AntigenPool pool = new AntigenPool();

        for (Vaccine vaccine : getVaccineFootprint(cycleGC))
            pool.add(vaccine);

        return pool;
    }

    /**
     * Returns the epitope <em>footprint</em> for a specific germinal
     * center cycle: the set of all epitopes administered on or before
     * that cycle.
     *
     * @param cycleGC the germinal center cycle of interest.
     *
     * @return an unmodifiable set containing the epitope footprint
     * for the specified cycle.
     *
     * @throws RuntimeException if the cycle is negative.
     */
    public Set<Epitope> getEpitopeFootprint(int cycleGC) {
        return epitopeFootprints.get(cycleGC);
    }

    /**
     * Returns the vaccine <em>footprint</em> for a specific germinal
     * center cycle: a list of all vaccines administered on or before
     * that cycle.
     *
     * @param cycleGC the germinal center cycle of interest.
     *
     * @return an unmodifiable list containing the vaccine footprint
     * for the specified cycle.
     *
     * @throws RuntimeException if the cycle is negative.
     */
    public List<Vaccine> getVaccineFootprint(int cycleGC) {
        return vaccineFootprints.get(cycleGC);
    }

    /**
     * Returns the most recent vaccination event to occur (the event
     * that is still active for particular cycle of affinity maturation.
     *
     * @param cycleGC the current germinal center cycle.
     *
     * @return the event that is active on the specified germinal
     * center cycle.
     *
     * @throws IllegalArgumentException if the germinal center cycle
     * is negative.
     */
    public VaccinationEvent latestEvent(int cycleGC) {
        if (cycleGC < 0)
            throw new IllegalArgumentException("Negative cycle.");

        // Find the event with the greatest injection cycle less than
        // or equal to the germinal center cycle...
        Map.Entry<Integer, VaccinationEvent> entry
            = eventMap.floorEntry(VaccinationEvent.getKey(cycleGC));

        // Since the germinal center cycle is zero or greater, and the
        // first event occurs at cycle zero, we must find an event...
        assert entry != null;

        return entry.getValue();
    }

    /**
     * Returns a view of the events contained in this schedule,
     * arranged in chronological order.
     *
     * @return a view (an unmodifiable list) of the events in
     * this vaccine.
     */
    public List<VaccinationEvent> viewEvents() {
        return eventList;
    }
}
