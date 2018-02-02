
package amat.antigen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jam.chem.Concentration;
import jam.chem.HalfLife;
import jam.math.DoubleComparator;
import jam.math.JamRandom;
import jam.util.ListView;

import amat.epitope.Epitope;
import amat.vaccine.Vaccine;
import amat.vaccine.VaccineComponent;

/**
 * Tracks the antigens (their identity and concentration) that are
 * present in a germinal center during affinity maturation.
 */
public final class AntigenPool {
    private final Map<Antigen, Concentration> concMap;

    // The set of selection probabilities (antigen concentration
    // divided by the total) created on demand by the first call
    // to select() and cached until the contents of the pool are
    // changed. Event indexes returned by the set are indexes of
    // the antigens in the "selectFrom" array.  The "selectProb"
    // array contains the selection probabilities (concentration
    // divided by total) for each antigen.
    private Antigen[] selectFrom = null;
    private double[]  selectProb = null;

    /**
     * Creates an empty antigen pool.
     */
    public AntigenPool() {
        this.concMap = new HashMap<Antigen, Concentration>();
    }

    /**
     * Creates an antigen pool seeded with vaccine components.
     *
     * @param components the seeding vaccine components.
     */
    public AntigenPool(Collection<VaccineComponent> components) {
        this();
        add(components);
    }

    /**
     * Creates an antigen pool seeded with a vaccine.
     *
     * @param vaccine the seeding vaccine.
     */
    public AntigenPool(Vaccine vaccine) {
        this();
        add(vaccine);
    }

    // Access the concentration map for an operation that will leave
    // its contents unchanged; the cache remains intact.
    private Map<Antigen, Concentration> viewMap() {
        return concMap;
    }

    // Access the concentration map for an operation that will alter
    // its contents and clear the cache, which will be invalidated by
    // the operation.
    private Map<Antigen, Concentration> alterMap() {
        clearCache();
        return concMap;
    }

    // Ensure that the antigen selection cache is ready for use...
    private void checkCache() {
        if (selectFrom == null)
            createCache();
    }

    // Invalidate the antigen selection cache...
    private void clearCache() {
        selectFrom = null;
        selectProb = null;
    }

    // Create the antigen selection cache...
    private void createCache() {
        if (isEmpty())
            throw new IllegalStateException("Empty pool.");

        selectFrom = viewAntigens().toArray(new Antigen[size()]);
        selectProb = new double[size()];

        double totalConc = getTotalConc().doubleValue();

        for (int index = 0; index < size(); index++)
            selectProb[index] = getConcentration(selectFrom[index]).doubleValue() / totalConc;
    }

    /**
     * Adds an antigen to this pool with a specific concentration.  If
     * the antigen is already present, its concentration will increase
     * by the given amount.
     *
     * @param antigen the antigen to add.
     *
     * @param concentration the antigen concentration to be added.
     *
     * @throws IllegalArgumentException if the concentration is
     * negative.
     */
    public void add(Antigen antigen, double concentration) {
        add(antigen, Concentration.valueOf(concentration));
    }

    /**
     * Adds an antigen to this pool with a specific concentration.  If
     * the antigen is already present, its concentration will increase
     * by the given amount.
     *
     * @param antigen the antigen to add.
     *
     * @param concentration the antigen concentration to be added.
     */
    public void add(Antigen antigen, Concentration concentration) {
        setConcentration(antigen, getConcentration(antigen).plus(concentration));
    }

    /**
     * Adds a vaccine component to this pool.
     *
     * @param component the vaccine component to add.
     */
    public void add(VaccineComponent component) {
        add(component.getAntigen(), component.getConcentration());
    }

    /**
     * Adds a collection of vaccine components to this pool.
     *
     * @param components the vaccine components to add.
     */
    public void add(Collection<VaccineComponent> components) {
        for (VaccineComponent component : components)
            add(component);
    }

    /**
     * Adds a vaccine to this pool.
     *
     * @param vaccine the vaccine to add.
     */
    public void add(Vaccine vaccine) {
        add(vaccine.viewComponents());
    }

    /**
     * Identifies antigens in this pool.
     *
     * @param antigen the antigen to query.
     *
     * @return {@code true} iff this pool contains the specified
     * antigen.
     */
    public boolean contains(Antigen antigen) {
        return viewMap().containsKey(antigen);
    }

    /**
     * Simulates decay of all antigens in this pool.
     *
     * @see AntigenPool#decay(Antigen, HalfLife)
     *
     * @param halfLife the half-life for antigen decay (applied
     * equally to all antigens) expressed as a number of germinal
     * center cycles.
     */
    public void decay(HalfLife halfLife) {
        for (Antigen antigen : viewAntigens())
            decay(antigen, halfLife);
    }

    /**
     * Simulates the decay of one antigen in this pool.
     *
     * <p><em>Decay</em> ultimately means reduced accessibility of B
     * cells to antigen.  In addition to biochemical degredation of
     * antigen, it may also encompass effects like the consumption of
     * antigen by B cells and antigen masking by antibodies produced
     * on previous rounds of affinity maturation.
     *
     * @param antigen the antigen to decay.
     *
     * @param halfLife the half-life for antigen decay, expressed as a
     * number of germinal center cycles.
     *
     * @throws IllegalStateException unless the antigen is present in
     * this pool.
     */
    public void decay(Antigen antigen, HalfLife halfLife) {
        require(antigen);
        setConcentration(antigen, getConcentration(antigen).decay(halfLife, 1));
    }

    /**
     * Returns the concentration of an antigen in this pool.
     *
     * @param antigen the antigen of interest.
     *
     * @return the concentration of the specified antigen (with zero
     * value if the antigen is not present).
     */
    public Concentration getConcentration(Antigen antigen) {
        Concentration result = viewMap().get(antigen);

        if (result != null)
            return result;
        else
            return Concentration.ZERO;
    }

    /**
     * Returns the fractional concentration of an antigen in this
     * pool: its concentration divided by the total.
     *
     * @param antigen the antigen of interest.
     *
     * @return the fractional concentration of the specified antigen.
     */
    public double getFraction(Antigen antigen) {
        return Concentration.ratio(getConcentration(antigen), getTotalConc());
    }

    /**
     * Returns the total concentration of all antigens in this pool.
     *
     * @return the total concentration of all antigens in this pool.
     */
    public Concentration getTotalConc() {
        return Concentration.total(viewMap().values());
    }

    /**
     * Identifies empty antigen pools.
     *
     * @return {@code true} iff this pool contains no antigens.
     */
    public boolean isEmpty() {
        return viewMap().isEmpty();
    }

    /**
     * Identifies antigen pools in which all antigens are present at
     * the same concentration.
     *
     * @return {@code true} iff this pool is empty, contains only one
     * antigen, or all (two or more) antigens are present in the same
     * concentration.
     */
    public boolean isUniform() {
        Concentration uniformConc = null;

        for (Concentration conc : concMap.values()) {
            if (uniformConc == null)
                uniformConc = conc;
            else if (!conc.equals(uniformConc))
                return false;
        }

        return true;
    }

    /**
     * Returns a read-only list of the antigens in this pool.
     *
     * @return a read-only list of the antigens in this pool.
     */
    public ListView<Antigen> listAntigens() {
        return ListView.create(new ArrayList<Antigen>(viewAntigens()));
    }

    /**
     * Removes an antigen from this pool.
     *
     * <p>If the antigen is not present, this method returns silently
     * having no effect on the pool.
     *
     * @param antigen the antigen to remove.
     */
    public void remove(Antigen antigen) {
        alterMap().remove(antigen);
    }

    /**
     * Ensures that this pool contains a specific antigen.
     *
     * @param antigen the antigen to test for.
     *
     * @throws IllegalStateException unless this pool contains the
     * specified antigen.
     */
    public void require(Antigen antigen) {
        if (!contains(antigen))
            throw new IllegalStateException(String.format("Missing antigen: [%s].", antigen.getKey()));
    }

    /**
     * Selects one antigen from this pool at random using the global
     * random number source.
     *
     * <p>The probability of selecting a particular antigen is equal
     * to its concentration divided by the total pool concentration.
     *
     * @return an antigen selected at random.
     *
     * @throws IllegalStateException if this pool is empty.
     */
    public Antigen select() {
        return select(JamRandom.global());
    }

    /**
     * Selects one antigen from this pool at random.
     *
     * <p>The probability of selecting a particular antigen is equal
     * to its concentration divided by the total pool concentration.
     *
     * @param random the random number source.
     *
     * @return an antigen selected at random.
     *
     * @throws IllegalStateException if this pool is empty.
     */
    public Antigen select(JamRandom random) {
        checkCache();
        return selectFrom[random.selectPDF(selectProb)];
    }

    /**
     * Assigns a new concentration for an antigen in this pool.
     *
     * <p>The antigen will be added if it is not already present and
     * the concentration is positive; the antigen will be removed if
     * the concentration is zero.
     *
     * @param antigen the antigen to assign.
     *
     * @param concentration the concentration to assign.
     *
     * @throws IllegalArgumentException if the concentration is
     * negative.
     */
    public void setConcentration(Antigen antigen, double concentration) {
        setConcentration(antigen, Concentration.valueOf(concentration));
    }

    /**
     * Assigns a new concentration for an antigen in this pool.
     *
     * <p>The antigen will be added if it is not already present and
     * the concentration is positive; the antigen will be removed if
     * the concentration is zero.
     *
     * @param antigen the antigen to assign.
     *
     * @param concentration the concentration to assign.
     */
    public void setConcentration(Antigen antigen, Concentration concentration) {
        if (concentration.equals(0.0))
            remove(antigen);
        else
            alterMap().put(antigen, concentration);
    }

    /**
     * Returns the number of antigens in this pool.
     *
     * @return the number of antigens in this pool.
     */
    public int size() {
        return viewMap().size();
    }

    /**
     * Creates a subset of this pool.
     *
     * @param antigens the antigens to include in the subset; each
     * antigen must be present in this pool.
     *
     * @return a new antigen pool containing only the antigens from
     * the input collection (at the same concentration as they have
     * in this pool).
     *
     * @throws IllegalArgumentException unless all input antigens are
     * present in this pool.
     */
    public AntigenPool subset(Collection<Antigen> antigens) {
        AntigenPool subset = new AntigenPool();

        for (Antigen antigen : antigens) {
            if (!contains(antigen))
                throw new IllegalArgumentException("Invalid subset.");

            subset.add(antigen, getConcentration(antigen));
        }

        return subset;
    }

    /**
     * Returns a read-only view of the antigens in this pool.
     *
     * @return a read-only (unmodifiable) set containing all antigens
     * contained by this pool.
     */
    public Set<Antigen> viewAntigens() {
        return Collections.unmodifiableSet(viewMap().keySet());
    }

    /**
     * Returns a read-only view of the unique epitopes in this pool.
     *
     * @return a read-only (unmodifiable) set containing all epitopes
     * contained by this pool.
     */
    public Set<Epitope> viewEpitopes() {
        Set<Epitope> epitopes = new HashSet<Epitope>();

        for (Antigen antigen : viewAntigens())
            epitopes.addAll(antigen.viewEpitopes());

        return Collections.unmodifiableSet(epitopes);
    }

    @Override public String toString() {
        return viewMap().toString();
    }
}
