
package amat.germinal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamLogger;
import jam.chem.HalfLife;
import jam.lang.JamException;
import jam.math.DoubleUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.bcell.ClonalDiversity;
import amat.binding.AffinityModel;
import amat.divide.DZDivisionModel;
import amat.epitope.Epitope;
import amat.germline.GermlineActivationModel;
import amat.memory.MemorySelectionModel;
import amat.plasma.PlasmaSelectionModel;
import amat.receptor.Receptor;
import amat.reentry.ReentryModel;
import amat.signal.BCRSignalingModel;
import amat.tcell.TCellCompetitionModel;
import amat.vaccine.VaccinationEvent;
import amat.vaccine.VaccinationSchedule;
import amat.vaccine.Vaccine;
import amat.vaccine.VaccineComponent;
import amat.visit.VisitationModel;

public final class GerminalCenter {
    private final int trialIndex;

    // Frequently used germinal center properties...
    private final int cycleLimit = GerminalCenterProperties.getCycleLimit();
    private final int residentCapacity = GerminalCenterProperties.getResidentCapacity();
    private final HalfLife antigenHalfLife = GerminalCenterProperties.getAntigenHalfLife();

    // The current affinity maturation cycle: incremented at the start
    // of each cycle, before any other actions have occurred.
    private int cycleIndex;

    // Number of B cells after the proliferation (replication) of
    // germline cells but before their light-zone selection...
    private int initialSize;

    // The current state of the germinal center...
    private GerminalCenterState gcState = GerminalCenterState.ACTIVE;

    // Antigens present in the germinal center along with their
    // current concentrations...
    private final AntigenPool antigenPool = new AntigenPool();

    // B cells participating in (and surviving) each cycle.  Most B
    // cells in the set at list element K will be of generation K, 
    // but previously exported memory cells are allowed to reenter, 
    // and they will be from an earlier generation...
    private final List<Set<BCell>> generations = new ArrayList<Set<BCell>>();

    // Population records for each generation; the first two records
    // (0 and 1) will be incomplete because the germline activation
    // and replication cycles are special...
    private final List<PopulationRecord> populations = new ArrayList<PopulationRecord>();

    // Memory cells outside of the germinal center (excluding any that
    // have re-entered)...
    private final Set<BCell> memoryCells = new HashSet<BCell>();

    // Plasma cells produced by affinity maturation...
    private final Set<BCell> plasmaCells = new HashSet<BCell>();

    // Unique plasma cell receptors mapped to their enclosing B
    // cells...
    private final Multimap<Receptor, BCell> antibodies = HashMultimap.create();

    private GerminalCenter(int trialIndex) {
        this.trialIndex = trialIndex;
    }

    /**
     * Index of the cycle in which germline cells are activated.
     */
    public static final int GERMLINE_CYCLE = 0;

    /**
     * Index of the cycle in which germline cells are replicated.
     */
    public static final int REPLICATION_CYCLE = 1;

    /**
     * Creates a new germinal center and simulates the affinity
     * maturation process for the global vaccination schedule.
     *
     * @param trialIndex the index of the maturation trial (for
     * tracking by the driver program).
     *
     * @return the germinal center, after affinity maturation has
     * terminated.
     *
     * @throws RuntimeException unless all required germinal center
     * properties have been assigned in the global property space and
     * the affinity maturation trial is successful.
     */
    public static GerminalCenter run(int trialIndex) {
        GerminalCenter gc = new GerminalCenter(trialIndex);
        return gc.runAM();
    }

    /**
     * Computes the antibody production rate for the germinal center
     * reaction.
     *
     * <p>The <em>antibody production rate</em> is defined as the
     * number of antibodies (unique plasma cell receptors) produced
     * during affinity maturation divided by the number of cells
     * present at the start of the germinal center reaction (after
     * proliferation of the germline cells).
     *
     * @return the antibody production rate for the germinal center
     * reaction.
     */
    public double computeAntibodyProdRate() {
	return DoubleUtil.ratio(countAntibodies(), getInitialSize());
    }

    /**
     * Computes the plasma-cell production rate for the germinal
     * center reaction.
     *
     * <p>The <em>plasma-cell production rate</em> is defined as the
     * number of plasma cells produced during affinity maturation
     * divided by the number of cells present at the start of the
     * germinal center reaction (after proliferation of the germline
     * cells).
     *
     * @return the plasma-cell production rate for the germinal center
     * reaction.
     */
    public double computePlasmaCellProdRate() {
	return DoubleUtil.ratio(countPlasmaCells(), getInitialSize());
    }

    /**
     * Computes the plasma-cell diversity for the germinal center
     * reaction.
     *
     * <p>The <em>diversity</em> is the ratio of the number of unique
     * antibodies to the total number of plasma cells produced during
     * affinity maturation.
     *
     * @return the plasma-cell diversity for the germinal center
     * reaction.
     */
    public double computePlasmaCellDiversity() {
	return DoubleUtil.ratio(countAntibodies(), countPlasmaCells());
    }

    /**
     * Computes the clonal diversity of the B cell population at a
     * given cycle.
     *
     * @param cycle the germinal center cycle of interest.
     *
     * @return the clonal diversity of the B cell population at the
     * given cycle.
     */
    public ClonalDiversity computeClonalDiversity(int cycle) {
        return ClonalDiversity.compute(viewActiveCells(cycle));
    }

    /**
     * Returns the number of antibodies (unique plasma cell receptors)
     * produced by this germinal center.
     *
     * @return the number of antibodies (unique plasma cell receptors)
     * produced by this germinal center.
     */
    public int countAntibodies() {
	return antibodies.keySet().size();
    }

    /**
     * Returns the number of affinity maturation cycles executed by
     * this germinal center.
     *
     * @return the number of affinity maturation cycles executed by
     * this germinal center.
     */
    public int countCycles() {
        return generations.size();
    }

    /**
     * Returns the number B cells present in the germinal center after
     * the conclusion of a given cycle.
     *
     * @param cycle the desired germinal center cycle.
     *
     * @return the number B cells present in the germinal center after
     * the conclusion of the given cycle.
     */
    public int countActiveCells(int cycle) {
        if (cycle < populations.size())
            return populations.get(cycle).ending();
        else
            return 0;
    }

    /**
     * Returns the number of plasma cells produced by this germinal
     * center.
     *
     * @return the number of plasma cells produced by this germinal
     * center.
     */
    public int countPlasmaCells() {
	return plasmaCells.size();
    }

    /**
     * Returns the index of the affinity maturation trial (for
     * tracking by the driver program).
     *
     * @return the index of the affinity maturation trial.
     */
    public int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Returns the B cell population after the proliferation of the
     * germline cells.
     *
     * @return the B cell population after the proliferation of the
     * germline cells.
     */
    public int getInitialSize() {
	return initialSize;
    }

    /**
     * Returns the state of this germinal center at the end of
     * affinity maturation.
     *
     * @return the state of this germinal center at the end of
     * affinity maturation.
     */
    public GerminalCenterState getFinalState() {
        return gcState;
    }

    /**
     * Returns the B cell population record for a given cycle.
     *
     * @param cycle the desired germinal center cycle.
     *
     * @return the B cell population record for the specified cycle.
     */
    public PopulationRecord getPopulation(int cycle) {
        return populations.get(cycle);
    }

    /**
     * Returns the B cells present in the germinal center after the
     * conclusion of a given cycle.
     *
     * @param cycle the desired germinal center cycle.
     *
     * @return a read-only view of the B cells present in the germinal
     * center after the conclusion of the specified cycle.
     */
    public Set<BCell> viewActiveCells(int cycle) {
        return Collections.unmodifiableSet(generations.get(cycle));
    }

    /**
     * Returns a (read-only) set view of the activated germline
     * (founder) cells.
     *
     * @return an unmodifiable set containing the activated germline
     * (founder) cells.
     */
    public Set<BCell> viewFounderCells() {
        return viewActiveCells(GERMLINE_CYCLE);
    }

    /**
     * Returns a (read-only) set view of the antibodies (unique plasma
     * cell receptors) generated during affinity maturation.
     *
     * @return an unmodifiable set containing the antibodies (unique
     * plasma cell receptors) generated during affinity maturation.
     */
    public Set<Receptor> viewAntibodies() {
        return Collections.unmodifiableSet(antibodies.keySet());
    }

    /**
     * Returns a (read-only) set view of the plasma cells generated
     * during affinity maturation.
     *
     * @return an unmodifiable set containing the plasma cells
     * generated during affinity maturation.
     */
    public Set<BCell> viewPlasmaCells() {
        return Collections.unmodifiableSet(plasmaCells);
    }

    private GerminalCenter runAM() {
        //
        // Special first cycle...
        //
        cycleIndex = 0;
        initializeAgPool();
        activateGermlines();
        updateState();

        while (continueMaturation()) {
            //
            // Execute all other cycles..
            // 
            ++cycleIndex;
            darkZoneCycle();
            lightZoneCycle();
            updateState();
        }

        logState(JamLogger.Level.INFO);
	mapAntibodies();

        return this;
    }

    private void initializeAgPool() {
        VaccinationEvent event = VaccinationSchedule.global().eventOn(0);

        if (event == null)
            throw JamException.runtime("No vaccine administered on cycle zero.");

        antigenPool.add(event.getVaccine());
    }

    private void activateGermlines() {
        assert cycleIndex == GERMLINE_CYCLE;

        GermlineActivationModel model = GermlineActivationModel.global();
        Set<BCell> germlines = model.activate(antigenPool);

        addGeneration(germlines);
    }

    private void addGeneration(Set<BCell> generation) {
        assert generations.size() == cycleIndex;
        assert populations.size() == cycleIndex;

        generations.add(generation);
        populations.add(new PopulationRecord(generation.size()));
    }

    private void updateState() {
        Set<BCell> activeCells = getActiveCells();

        if (activeCells.isEmpty()) {
            gcState = GerminalCenterState.EXTINGUISHED;
        }
        else if (activeCells.size() > residentCapacity) {
            gcState = GerminalCenterState.EXCEEDED_CAPACITY;
        }
        else if (cycleIndex >= cycleLimit) {
            gcState = GerminalCenterState.EXCEEDED_TIME;
        }
        else if (antigenPool.isEmpty()) {
            gcState = GerminalCenterState.ANTIGEN_CONSUMED;
        }
        else {
            gcState = GerminalCenterState.ACTIVE;
        }

        logState(JamLogger.Level.DEBUG);
    }

    private void logState(JamLogger.Level level) {
        JamLogger.log(level, "CYCLE:  %8d", cycleIndex);
        JamLogger.log(level, "ACTIVE: %8d", getActiveCells().size());
        JamLogger.log(level, "PLASMA: %8d", plasmaCells.size());
        JamLogger.log(level, "MEMORY: %8d", memoryCells.size());
        JamLogger.log(level, "STATE:   %s", gcState);
    }

    private Set<BCell> getActiveCells() {
        assert generations.size() == cycleIndex + 1;
        return generations.get(cycleIndex);
    }

    private boolean continueMaturation() {
        return gcState.equals(GerminalCenterState.ACTIVE);
    }

    private void darkZoneCycle() {
        if (isReplicationCycle()) {
            replicateGermlines();
        }
        else {
            newGeneration();
            reenterMemory();
            divideActive();
        }
    }

    private boolean isReplicationCycle() {
        return cycleIndex == REPLICATION_CYCLE;
    }

    private void replicateGermlines() {
        GermlineActivationModel model = GermlineActivationModel.global();

        Set<BCell> germlines = generations.get(GERMLINE_CYCLE);
        Set<BCell> replicants = model.replicate(germlines);

        addGeneration(replicants);
	initialSize = replicants.size();
    }

    private void newGeneration() {
        //
        // The cells remaining in the light zone at the end of the
        // previous cycle are recycled back into the dark zone and
        // become the initial members of the new generation...
        //
        assert cycleIndex > REPLICATION_CYCLE;

        Set<BCell> prevGeneration = generations.get(cycleIndex - 1);
        addGeneration(new HashSet<BCell>(prevGeneration));
    }

    private void reenterMemory() {
        getActiveCells().addAll(ReentryModel.global().select(memoryCells));
        updatePopulation(GerminalCenterEvent.MEMORY_REENTRY);
    }

    private void updatePopulation(GerminalCenterEvent event) {
        assert populations.size() == cycleIndex + 1;
        populations.get(cycleIndex).after(event, getActiveCells().size());
    }

    private void divideActive() {
        //
        // The active cells are the parents in this round of division.
        // Divide each parent cell, store its daughters in a temporary
        // collection, then remove the parent from the active cell set.
        // After the iteration over parents, the active cell set should
        // be empty.  Then add all daughters to the active cell set and
        // they become the active cells for the round of selection.
        //
        Set<BCell> activeCells = getActiveCells();
        Set<BCell> daughters = new HashSet<BCell>();
        Iterator<BCell> iterator = activeCells.iterator();

        while (iterator.hasNext()) {
            BCell parent = iterator.next();

            daughters.addAll(parent.divide());
            iterator.remove();
        }

        // All active cells should have been processed...
        assert activeCells.isEmpty();

        activeCells.addAll(daughters);
        updatePopulation(GerminalCenterEvent.DIVISION_MUTATION);
    }

    private void lightZoneCycle() {
        updateAgPool();
        bindAntigens();
        decayAntigen();

        testSignals();
        competeHelp();

	selectMemory();
	selectPlasma();
    }

    private void updateAgPool() {
        VaccinationEvent event = VaccinationSchedule.global().eventOn(cycleIndex);

        if (event != null) {
            antigenPool.add(event.getVaccine());
            JamLogger.info("New vaccination event for cycle [%d]...", cycleIndex);
        }
    }

    private void bindAntigens() {
        for (BCell activeCell : getActiveCells())
            bindAntigen(activeCell);
    }

    private void bindAntigen(BCell activeCell) {
        activeCell.bind(antigenPool, VisitationModel.global().visit(cycleIndex, antigenPool));
    }

    private void decayAntigen() {
	antigenPool.decay(antigenHalfLife);
    }

    private void testSignals() {
        BCRSignalingModel.global().apoptose(getActiveCells(), antigenPool);
        updatePopulation(GerminalCenterEvent.BCR_SIGNALING);
    }

    private void competeHelp() {
        TCellCompetitionModel.global().apoptose(getActiveCells(), antigenPool);
        DZDivisionModel.global().assignDivisionCount(getActiveCells());
        updatePopulation(GerminalCenterEvent.TCELL_COMPETITION);
    }

    private void selectMemory() {
        memoryCells.addAll(MemorySelectionModel.global().select(getActiveCells()));
        updatePopulation(GerminalCenterEvent.MEMORY_SELECTION);
    }

    private void selectPlasma() {
        plasmaCells.addAll(PlasmaSelectionModel.global().select(getActiveCells()));
        updatePopulation(GerminalCenterEvent.PLASMA_SELECTION);
    }

    private void mapAntibodies() {
	for (BCell plasmaCell : plasmaCells)
	    antibodies.put(plasmaCell.getReceptor(), plasmaCell);
    }

    public static void main(String[] args) {
        jam.app.JamProperties.loadFile("test/germinal_sample.prop", true);
        run(0);
    }
}
