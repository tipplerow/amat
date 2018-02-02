
package amat.bcell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import jam.chem.Concentration;
import jam.lang.Lockable;
import jam.math.DoubleUtil;
import jam.util.AutoList;
import jam.util.ListUtil;
import jam.util.MultisetUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.binding.AffinityModel;
import amat.epitope.Epitope;
import amat.germinal.GerminalCenter;
import amat.receptor.MatchingCalculator;
import amat.receptor.Receptor;
import amat.receptor.ReceptorGenerator;
import amat.structure.CV;
import amat.structure.Structure;

/**
 * Represents a B cell in an affinity maturation simulation.
 *
 * <p>The receptor shape, parent cell, and founder cell are immutable,
 * but the bound antigen amount may change during affinity maturation.
 */
public final class BCell {
    private final long index;
    private final BCell parent;
    private final BCell founder;
    private final Receptor receptor;

    private final int gcCycle;
    private final int generation;
    private final int mutationCount;

    // Number of divisions to execute in the dark-zone. Defaults to 1;
    // may be re-assigned only once by the dark-zone division model.
    private final Lockable<Integer> divisionCount = Lockable.create(1);

    // Records of the encounters with antigens in the light zone...
    private final List<BindingEvent> bindingEvents = new ArrayList<BindingEvent>();

    // For efficiency when comparing affinities and bound quantities,
    // these statistics are computed only once (after simulating the
    // binding with a full antigen pool) and then stored.  The value
    // of the maximum affinity is Double.NEGATIVE_INFINITY initially
    // so that B cells with no bound antigen always compare as weaker
    // binders...
    private double antigenQty  = 0.0;
    private double maxAffinity = Double.NEGATIVE_INFINITY;

    // Number of instances created, used to assign the unique index...
    private static long instanceCount = 0;

    // Mean affinity for the epitopes encountered in the light zone,
    // grouped by GC cycle...
    private static final AutoList<ArrayList<Double>> affinityList =
        AutoList.create(ListUtil.arrayFactory());

    // Total quantity of antigen captured in the light zone, grouped
    // by GC cycle...
    private static final AutoList<ArrayList<Double>> quantityList =
        AutoList.create(ListUtil.arrayFactory());

    // Total number of antigens encountered by the B cell population,
    // grouped by GC cycle...
    private static final AutoList<HashMultiset<Integer>> totalVisitCounts = 
        AutoList.create(MultisetUtil.hashFactory());

    // Number of unique antigens encountered by the B cell population,
    // grouped by GC cycle...
    private static final AutoList<HashMultiset<Integer>> uniqueVisitCounts = 
        AutoList.create(MultisetUtil.hashFactory());

    // Number of unique antigens encountered by the B cell population
    // on consecutive cycles, grouped by GC cycle...
    private static final AutoList<HashMultiset<Integer>> uniqueRevisitCounts = 
        AutoList.create(MultisetUtil.hashFactory());

    /**
     * Places B cells in ascending order (least first) by the affinity
     * for the strongest binding epitope.
     */
    public static final Comparator<BCell> MAX_AFFINITY_COMPARATOR = new MaxAffinityComparator();

    private static class MaxAffinityComparator implements Comparator<BCell> {
        @Override public int compare(BCell cell1, BCell cell2) {
            return Double.compare(cell1.getMaxAffinity(), cell2.getMaxAffinity());
        }
    }

    /**
     * Places B cells in ascending order (least first) by the total
     * amount of antigen captured and internalized.
     */
    public static final Comparator<BCell> ANTIGEN_QTY_COMPARATOR = new AntigenQtyComparator();

    private static class AntigenQtyComparator implements Comparator<BCell> {
        @Override public int compare(BCell cell1, BCell cell2) {
            return Double.compare(cell1.getAntigenQty(), cell2.getAntigenQty());
        }
    }

    private BCell(BCell parent, Receptor receptor, int gcCycle) {
        //
        // These attributes must be assigned first...
        //
        this.index    = instanceCount++;
        this.parent   = parent;
        this.receptor = receptor;
        this.gcCycle  = gcCycle;

        // Then the remainder are derived from the parent and receptor...
        this.founder = resolveFounder();
        this.generation = resolveGeneration();
        this.mutationCount = resolveMutationCount();
    }

    private BCell resolveFounder() {
        if (parent == null)
            return this;
        else
            return parent.founder;
    }

    private int resolveGeneration() {
        if (parent == null)
            return 0;
        else
            return parent.generation + 1;
    }

    private int resolveMutationCount() {
        if (parent == null)
            return 0;
        else if (receptor.equals(parent.receptor))
            return parent.mutationCount;
        else
            return parent.mutationCount + 1;
    }

    /**
     * Creates a germline (founder) B cell with a receptor created by
     * the global receptor generator.
     *
     * @return a new germline (founder) B cell.
     */
    public static BCell germline() {
        return germline(ReceptorGenerator.global().generate());
    }

    /**
     * Creates a germline (founder) B cell with a specific receptor.
     *
     * @param receptor the germline receptor.
     *
     * @return a new germline (founder) B cell.
     */
    public static BCell germline(Receptor receptor) {
        return new BCell(null, receptor, GerminalCenter.GERMLINE_CYCLE);
    }

    /**
     * Creates an identical daughter cell (without mutation).
     *
     * @return an identical daughter cell.
     */
    public BCell replicate() {
        return new BCell(this, this.receptor, this.gcCycle + 1);
    }

    /**
     * Creates a daughter cell with a mutated receptor; this cell is
     * unchanged.
     *
     * <p>This method calls the global {@code Mutator} instance to
     * generate a mutated receptor.  Since some mutations are lethal,
     * this method may return {@code null} to indicate that the
     * receptor mutation could not support a viable daughter cell.
     *
     * @param gcCycle the germinal center cycle when the mutation
     * occurs.
     *
     * @return a new daughter cell with a mutated receptor, or {@code
     * null} if the mutation was lethal.
     */
    public BCell mutate(int gcCycle) {
        Receptor mutated = this.receptor.mutate();

        if (mutated != null)
            return new BCell(this, mutated, gcCycle);
        else
            return null;
    }

    /**
     * Simulates cell division with mutation; this cell is unchanged.
     *
     * <p>This parent cell undergoes {@code getDivisionCount()} rounds
     * of division and mutation.  For each round, this method creates
     * two initially identical daughter cells and then subjects their
     * receptors to mutations governed by the global {@code Mutator}
     * instance.  Daughter cells that undergo lethal mutations will be
     * omitted from the returned list.
     *
     * @return a list containing the surviving daughter cells (which
     * may be empty if all mutations are lethal).
     */
    public List<BCell> divide() {
        // This dark-zone division ocurrs one GC cycle after this
        // parent cell was created...
        int divideCycle = this.gcCycle + 1;

        // The parent cells for each round of division; initially just
        // this parent cell...
        List<BCell> parents = Arrays.asList(this);

        // The descendents from all rounds of division...
        List<BCell> descendants = new ArrayList<BCell>();

        for (int round = 1; round <= getDivisionCount(); ++round) {
            //
            // Each parent cell divides once on this round...
            //
            List<BCell> daughters = new ArrayList<BCell>();

            for (BCell parent : parents)
                daughters.addAll(parent.divideOnce(divideCycle));

            // The daughters will be the parents on the next round...
            parents = daughters;
            descendants.addAll(daughters);
        }

        return descendants;
    }

    private List<BCell> divideOnce(int divideCycle) {
        List<BCell> daughters = new ArrayList<BCell>();

        BCell daughter1 = mutate(divideCycle);
        BCell daughter2 = mutate(divideCycle);

        if (daughter1 != null)
            daughters.add(daughter1);

        if (daughter2 != null)
            daughters.add(daughter2);

        return daughters;
    }

    /**
     * Simulates the binding of this B cell to each antigen in a pool:
     * each antigen in the pool is encountered exactly once.
     *
     * @param pool the pool containing all antigens present in the
     * germinal center.
     */
    public void bind(AntigenPool pool) {
        bind(pool, pool.viewAntigens());
    }

    /**
     * Simulates the binding of this B cell to the antigens
     * encountered in the light zone of a germinal center.
     *
     * @param pool the pool containing all antigens present in the
     * germinal center.
     *
     * @param antigens the antigens actually encountered in the light
     * zone (one occurrence for each encounter).
     */
    public void bind(AntigenPool pool, Collection<Antigen> antigens) {
        for (Antigen antigen : antigens)
            bind(antigen, pool.getConcentration(antigen));

        if (gcCycle >= GerminalCenter.REPLICATION_CYCLE) {
            recordTotalVisits();
            recordUniqueVisits();
        }

        if (gcCycle > GerminalCenter.REPLICATION_CYCLE)
            recordUniqueRevisits();

        if (!bindingEvents.isEmpty()) {
            antigenQty  = BindingEvent.getTotalQuantity(bindingEvents);
            maxAffinity = BindingEvent.getMaxAffinity(bindingEvents);
        }

        recordAffinity();
        recordQuantity();
    }

    private void bind(Antigen antigen, Concentration concentration) {
        for (Epitope epitope : antigen.viewEpitopes())
            bindingEvents.add(BindingEvent.create(this, antigen, epitope, concentration));
    }

    private void recordAffinity() {
        if (!bindingEvents.isEmpty())
            affinityList.get(gcCycle).add(BindingEvent.getMeanAffinity(bindingEvents));
    }

    private void recordQuantity() {
        quantityList.get(gcCycle).add(antigenQty);
    }

    private void recordTotalVisits() {
        totalVisitCounts.get(gcCycle).add(countTotalEpitopesEncountered());
    }

    private void recordUniqueVisits() {
        uniqueVisitCounts.get(gcCycle).add(countUniqueEpitopesEncountered());
    }

    private void recordUniqueRevisits() {
        uniqueRevisitCounts.get(gcCycle).add(countUniqueEpitopesRevisited());
    }

    /**
     * Returns the unique index for this B cell.
     *
     * @return the unique index for this B cell.
     */
    public long getIndex() {
        return index;
    }

    /**
     * Returns the founder cell from which this B cell is derived.
     *
     * @return the founder cell from which this B cell is derived.
     */
    public BCell getFounder() {
        return founder;
    }

    /**
     * Identifies founder cells.
     *
     * @return {@code true} iff this cell is a founder cell.
     */
    public boolean isFounder() {
        return founder == this;
    }

    /**
     * Returns the index of the germinal center cycle when this cell
     * was created.
     *
     * @return the index of the germinal center cycle when this cell
     * was created.
     */
    public int getGcCycle() {
        return gcCycle;
    }

    /**
     * Returns the number of cell divisions that have occurred since
     * the founder cell.
     *
     * @return the number of cell divisions that have occurred since
     * the founder cell.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the number of mutations this B cell has accumulated
     * from its originating founder cell.
     *
     * @return the number of mutations this B cell has accumulated
     * from its originating founder cell.
     */
    public int getMutationCount() {
        return mutationCount;
    }

    /**
     * Returns the mutational distance between the receptor of this B
     * cell and the receptor of its founder (as defined by the runtime
     * structure type).
     *
     * @return the mutational distance between the receptor of this B
     * cell and the receptor of its founder (zero if this is a founder
     * cell).
     */
    public double getFounderDistance() {
	if (founder != null)
	    return getMutationalDistance(founder);
	else
	    return 0.0;
    }

    /**
     * Returns the mutational distance between the receptor of this B
     * cell and the receptor of another B cell (as defined by the type 
     * of structure).
     *
     * @param that a reference B cell.
     *
     * @return the mutational distance between the receptor of this B
     * cell and the receptor of the input cell.
     *
     * @throws IllegalArgumentException if the input B cell contains a
     * receptor with different runtime type different from this B cell
     * and a sensible mutational distance cannot be defined.
     */
    public double getMutationalDistance(BCell that) {
	Structure thisStructure = this.getReceptor().getStructure();
	Structure thatStructure = that.getReceptor().getStructure();

	return thisStructure.mutationalDistance(thatStructure);
    }

    /**
     * Returns the parent of this B cell ({@code null} for founder cells).
     *
     * @return the parent of this B cell ({@code null} for founder cells).
     */
    public BCell getParent() {
        return parent;
    }

    /**
     * Returns the receptor for this B cell.
     *
     * @return the receptor for this B cell.
     */
    public Receptor getReceptor() {
        return receptor;
    }

    /**
     * Returns the number of times that this cell will divide in the
     * dark zone.
     *
     * @return the number of times that this cell will divide in the
     * dark zone.
     */
    public int getDivisionCount() {
        return divisionCount.get();
    }

    /**
     * Assigns the number of times that this cell will divide in the
     * dark zone.
     *
     * <p>The division count may be assigned only once, by the
     * dark-zone division model.  Attempting to reset the count 
     * will trigger an exception.
     *
     * @param count the number of times that this cell will divide in
     * the dark zone.
     *
     * @throws IllegalStateException if the division count has already
     * been assigned.
     */
    public void setDivisionCount(int count) {
        divisionCount.set(count);
        divisionCount.lock();
    }

    /**
     * Returns the total number of antigens encountered in the light
     * zone.
     *
     * @return the total number of antigens encountered in the light
     */
    public int countTotalEpitopesEncountered() {
        return bindingEvents.size();
    }

    /**
     * Returns the number of unique epitopes encountered in the light
     * zone.
     *
     * @return the number of unique epitopes encountered in the light
     * zone.
     */
    public int countUniqueEpitopesEncountered() {
        return getUniqueEpitopesEncountered().size();
    }

    /**
     * Returns the number of unique epitopes encountered in the light
     * zone that were also encountered by the parent cell.
     *
     * @return the number of unique epitopes encountered in the light
     * zone that were also encountered by the parent cell.
     */
    public int countUniqueEpitopesRevisited() {
        return getUniqueEpitopesRevisited().size();
    }

    /**
     * Returns the set of unique antigens encountered by this B cell
     * <em>and all other B cells in its lineage</em>.
     *
     * @return the set of unique antigens encountered by this B cell
     * <em>and all other B cells in its lineage</em>.
     */
    public Set<Antigen> getAntigenFootprint() {
        //
        // The founder is tested against all antigens, so start with
        // the replication cycle...
        //
        List<BCell>  lineage   = traceLineage(GerminalCenter.REPLICATION_CYCLE);
        Set<Antigen> footprint = new HashSet<Antigen>();

        for (BCell cell : lineage)
            for (BindingEvent event : cell.viewBindingEvents())
                footprint.add(event.getAntigen());

        return footprint;
    }

    /**
     * Returns the set of unique epitopes encountered by this B cell
     * <em>and all other B cells in its lineage</em>.
     *
     * @return the set of unique epitopes encountered by this B cell
     * <em>and all other B cells in its lineage</em>.
     */
    public Set<Epitope> getEpitopeFootprint() {
        //
        // The founder is tested against all epitopes, so start with
        // the replication cycle...
        //
        List<BCell>  lineage   = traceLineage(GerminalCenter.REPLICATION_CYCLE);
        Set<Epitope> footprint = new HashSet<Epitope>();

        for (BCell cell : lineage)
            for (BindingEvent event : cell.viewBindingEvents())
                footprint.add(event.getEpitope());

        return footprint;
    }

    /**
     * Returns the epitopes encountered in the light zone.
     *
     * @return the epitopes encountered in the light zone; the number
     * of occurrences for an epitope reflects the number of times it
     * was encountered.
     */
    public Multiset<Epitope> getEpitopesEncountered() {
        HashMultiset<Epitope> epitopes = HashMultiset.create();

        for (BindingEvent event : bindingEvents)
            epitopes.add(event.getEpitope());

        return epitopes;
    }

    /**
     * Returns the unique epitopes encountered in the light zone.
     *
     * @return the unique epitopes encountered in the light zone.
     */
    public Set<Epitope> getUniqueEpitopesEncountered() {
        return getEpitopesEncountered().elementSet();
    }

    /**
     * Returns the unique epitopes encountered in the light zone that
     * were also encountered by the parent of this cell.
     *
     * @return the unique epitopes encountered in the light zone that
     * were also encountered by the parent of this cell.
     */
    public Set<Epitope> getUniqueEpitopesRevisited() {
        if (isFounder())
            return Collections.emptySet();

        Set<Epitope> epitopes = getEpitopesEncountered().elementSet();
        epitopes.retainAll(parent.getEpitopesEncountered().elementSet());

        return epitopes;
    }

    /**
     * Computes the average number of unique epitopes encountered by
     * all cells in this B cells lineage.
     *
     * @return the average number of unique epitopes encountered by
     * all cells in this B cells lineage.
     */
    public double computeMeanLineageUniqueEpitopeEncounters() {
        // Do not examine founders or initial replicants...
        int MIN_GCCYCLE = GerminalCenter.REPLICATION_CYCLE; 
        
        int   lineageTotal   = 0;
        int   encounterTotal = 0;
        BCell currentCell    = this;

        while (currentCell.getGcCycle() >= MIN_GCCYCLE) {
            lineageTotal   += 1;
            encounterTotal += currentCell.countUniqueEpitopesEncountered();

            currentCell = currentCell.getParent();
        }

        if (lineageTotal > 0)
            return DoubleUtil.ratio(encounterTotal, lineageTotal);
        else
            return 0.0;
    }

    /**
     * Returns the total quantity of antigen captured (internalized)
     * during binding in the light zone.
     *
     * @return the total quantity of antigen captured (internalized)
     * during binding in the light zone ({@code 0.0} if no antigens
     * where found).
     */
    public double getAntigenQty() {
        return antigenQty;
    }

    /**
     * Returns the affinity for the strongest binding epitope among
     * those encountered by this B cell.
     *
     * @return the affinity for the strongest binding epitope, or
     * {@code Double.NEGATIVE_INFINITY} if no antigens were found
     * during the search of the light zone.
     */
    public double getMaxAffinity() {
        return maxAffinity;
    }

    /**
     * Returns the average fraction of receptor elements that exactly
     * match those in the conserved region of the epitopes present in
     * the global epitope registry.
     *
     * @return the average fraction of receptor elements that exactly
     * match those in the conserved region of the epitopes present in
     * the global epitope registry.
     */
    public double getFractionMatchingConserved() {
        return MatchingCalculator.instance().compute(receptor, CV.CONSERVED);
    }

    /**
     * Returns the average fraction of receptor elements that exactly
     * match those in the variable region of the epitopes present in
     * the global epitope registry.
     *
     * @return the average fraction of receptor elements that exactly
     * match those in the variable region of the epitopes present in
     * the global epitope registry.
     */
    public double getFractionMatchingVariable() {
        return MatchingCalculator.instance().compute(receptor, CV.VARIABLE);
    }

    /**
     * Extracts the receptors from a collection of B cells.
     *
     * @param cells the B cells to process.
     *
     * @return a list where element {@code k} is the receptor for
     * cell {@code k} in the input collection.
     */
    public static List<Receptor> getReceptors(Collection<BCell> cells) {
        List<Receptor> receptors = new ArrayList<Receptor>(cells.size());

        for (BCell cell : cells)
            receptors.add(cell.getReceptor());

        return receptors;
    }

    /**
     * Traces the lineage of this B cell.
     *
     * @return a list containing all parents of this B cell (and this
     * B cell itself), ordered by generation starting with the founder
     * cell.
     */
    public List<BCell> traceLineage() {
        return traceLineage(0);
    }

    /**
     * Traces the lineage of this B cell back to a specific GC cycle.
     *
     * @param firstCycle the earliest GC cycle to include in the
     * lineage.
     *
     * @return a list containing all parents of this B cell (and this
     * B cell itself), ordered by generation starting with the B cell
     * at the given GC cycle.
     *
     * @throws IllegalArgumentException if the GC cycle is negative.
     */
    public List<BCell> traceLineage(int firstCycle) {
        if (firstCycle < 0)
            throw new IllegalArgumentException("First generation must be non-negative.");

        BCell genCell = this;
        LinkedList<BCell> lineage = new LinkedList<BCell>();

        while (genCell != null && genCell.getGcCycle() >= firstCycle) {
            //
            // Push the current cycle onto the front of the
            // list...
            //
            lineage.addFirst(genCell);
            genCell = genCell.getParent();
        }

        // Return an ArrayList which will provide better performance
        // in most situations...
        return new ArrayList<BCell>(lineage);
    }

    /**
     * Returns a read-only view of the binding events that have
     * occurred for this B cell.
     *
     * @return a read-only view of the binding events that have
     * occurred for this B cell.
     */
    public List<BindingEvent> viewBindingEvents() {
        return Collections.unmodifiableList(bindingEvents);
    }

    /**
     * Returns a read-only view of the affinities for the epitopes
     * encountered for every B cell trip through the light zone.
     *
     * @return a read-only view of the affinities for the epitopes
     * encountered for every B cell trip through the light zone.
     */
    public static List<Double> viewAffinityList() {
        List<Double> aggregate = new ArrayList<Double>();

        for (List<Double> gcCycleList : affinityList)
            aggregate.addAll(gcCycleList);

        return aggregate;
    }

    /**
     * Returns a read-only view of the affinities for the epitopes
     * encountered by each B cell in a given GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return a read-only view of the affinities for the epitopes
     * encountered by each B cell in a given GC cycle.
     */
    public static List<Double> viewAffinityList(int gcCycle) {
        return Collections.unmodifiableList(affinityList.get(gcCycle));
    }

    /**
     * Returns a read-only view of the quantity of antigen captured
     * for every B cell trip through the light zone.
     *
     * @return a read-only view of the quantity of antigen captured
     * for every B cell trip through the light zone.
     */
    public static List<Double> viewQuantityList() {
        List<Double> aggregate = new ArrayList<Double>();

        for (List<Double> gcCycleList : quantityList)
            aggregate.addAll(gcCycleList);

        return aggregate;
    }

    /**
     * Returns a read-only view of the quantity of antigen captured
     * for each B cell in a given GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return a read-only view of the quantity of antigen captured
     * for each B cell in a given GC cycle.
     */
    public static List<Double> viewQuantityList(int gcCycle) {
        return Collections.unmodifiableList(quantityList.get(gcCycle));
    }

    /**
     * Returns a read-only view of the total number of epitopes
     * encountered for every B cell trip through the light zone.
     *
     * @return a read-only view of the total number of epitopes
     * encountered for every B cell trip through the light zone.
     */
    public static Multiset<Integer> viewTotalEncounters() {
        return Multisets.unmodifiableMultiset(MultisetUtil.hash(totalVisitCounts));
    }

    /**
     * Returns a read-only view of the total number of epitopes
     * encountered by each B cell in a given GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return a read-only view of the total number of epitopes
     * encountered by each B cell in a given GC cycle.
     */
    public static Multiset<Integer> viewTotalEncounters(int gcCycle) {
        return Multisets.unmodifiableMultiset(totalVisitCounts.get(gcCycle));
    }

    /**
     * Returns a read-only view of the number of unique epitopes
     * encountered for every B cell trip through the light zone.
     *
     * @return a read-only view of the number of unique epitopes
     * encountered for every B cell trip through the light zone.
     */
    public static Multiset<Integer> viewUniqueEncounters() {
        return Multisets.unmodifiableMultiset(MultisetUtil.hash(uniqueVisitCounts));
    }

    /**
     * Returns a read-only view of the number of unique epitopes
     * encountered by each B cell in a given GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return a read-only view of the number of unique epitopes
     * encountered by each B cell in a given GC cycle.
     */
    public static Multiset<Integer> viewUniqueEncounters(int gcCycle) {
        return Multisets.unmodifiableMultiset(uniqueVisitCounts.get(gcCycle));
    }

    /**
     * Returns a read-only view of the number of unique epitopes
     * revisited (seen by the parent and daughter) for each B cell
     * trip through the light zone.
     *
     * @return a read-only view of the number of unique epitopes
     * revisited (seen by the parent and daughter) for each B cell
     * trip through the light zone.
     */
    public static Multiset<Integer> viewUniqueRevisits() {
        return Multisets.unmodifiableMultiset(MultisetUtil.hash(uniqueRevisitCounts));
    }

    /**
     * Returns a read-only view of the number of unique epitopes
     * encountered by each B cell in a given GC cycle that were also
     * encountered by their parents on the previous GC cycle.
     *
     * @param gcCycle the GC cycle of interest.
     *
     * @return a read-only view of the number of unique epitopes
     * encountered by each B cell in a given GC cycle that were also
     * encountered by their parents on the previous GC cycle.
     */
    public static Multiset<Integer> viewUniqueRevisits(int gcCycle) {
        return Multisets.unmodifiableMultiset(uniqueRevisitCounts.get(gcCycle));
    }

    @Override public boolean equals(Object that) {
        return (that instanceof BCell) && equalsBCell((BCell) that);
    }

    private boolean equalsBCell(BCell that) {
        return this.index == that.index;
    }

    @Override public int hashCode() {
        return (int) index;
    }

    @Override public String toString() {
        return String.format("BCell(%d, %s)", gcCycle, receptor.toString());
    }
}
