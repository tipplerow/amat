
package amat.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.FileUtil;
import jam.lang.JamException;
import jam.math.DoubleUtil;
import jam.math.IntRange;

import amat.bcell.BCell;
import amat.germinal.GerminalCenter;
import amat.germinal.PopulationRecord;
import amat.receptor.Receptor;
import amat.report.AmatReport;

/**
 * Driver application to run affinity maturation simulations.
 *
 * <p>Report files are written to the directory specified by the
 * system property with the name {@link AmatDriver#REPORT_DIR_PROPERTY},
 * or if that property is not set, to the parent directory of the
 * first configuration file passed on the command line.
 */
public final class AmatDriver {
    private final String[] fileNames;

    private final int trialLimit;
    private final int plasmaTarget;

    private final List<GerminalCenter> germinalCenters = new ArrayList<GerminalCenter>();
    private final Multimap<Receptor, BCell> plasmaCells = HashMultimap.create();

    private static AmatDriver instance = null;

    /**
     * Name of the system property which specifies the number of
     * unique plasma cells (antibodies) to generate.
     */
    public static final String PLASMA_TARGET_PROPERTY = "amat.AmatDriver.plasmaTarget";

    /**
     * Name of the system property which specifies the maximum number
     * of independent affinity maturation trials to run.
     */
    public static final String TRIAL_LIMIT_PROPERTY = "amat.AmatDriver.trialLimit";

    /**
     * Name of the system property which specifies the directory in
     * which to write report files.
     */
    public static final String REPORT_DIR_PROPERTY = "amat.AmatDriver.reportDir";

    /**
     * Returns the single driver instance.
     *
     * @return the single driver instance.
     *
     * @throws IllegalStateException unless the affinity maturation
     * simulation has already been executed.
     */
    public static AmatDriver instance() {
        if (instance == null)
            throw new IllegalStateException("The simulation has not been executed.");

        return instance;
    }

    /**
     * Runs the affinity maturation simulation.
     *
     * <p>After execution, the driver object may be accessed via the
     * {@link AmatDriver#instance} method.
     *
     * @param fileNames names of the property or configuration files
     * to load prior to execution.
     *
     * @throws IllegalStateException if the affinity maturation
     * simulation has already been executed.
     */
    public static void run(String... fileNames) {
        if (instance != null)
            throw new IllegalStateException("The simulation has already been executed.");

        instance = new AmatDriver(fileNames);
        instance.run();
    }

    private AmatDriver(String[] fileNames) {
        JamProperties.loadFiles(fileNames, false);

        this.fileNames    = fileNames;
        this.trialLimit   = loadTrialLimit();
        this.plasmaTarget = loadPlasmaTarget();
    }

    private static int loadTrialLimit() {
        return JamProperties.getRequiredInt(TRIAL_LIMIT_PROPERTY, IntRange.POSITIVE);
    }

    private static int loadPlasmaTarget() {
        return JamProperties.getRequiredInt(PLASMA_TARGET_PROPERTY, IntRange.POSITIVE);
    }

    private void run() {
        while (trialIndex() < trialLimit && countPlasmaCells() < plasmaTarget) {

            JamLogger.info("----------------------------");
            JamLogger.info("TRIAL %5d: %5.1f%% complete", trialIndex(), percentComplete());
            JamLogger.info("----------------------------");

            GerminalCenter germinalCenter = GerminalCenter.run(trialIndex());
            germinalCenters.add(germinalCenter);

            for (BCell plasmaCell : germinalCenter.viewPlasmaCells())
                plasmaCells.put(plasmaCell.getReceptor(), plasmaCell);
        }

        JamLogger.info("--------------------------------------");
        JamLogger.info("Generated [%6d] plasma cells...", countPlasmaCells());
        JamLogger.info("Generated [%6d] unique receptors...", countReceptors());
        JamLogger.info("--------------------------------------");

        AmatReport.runAll();
    }

    private int countPlasmaCells() {
        return plasmaCells.size();
    }

    private int countReceptors() {
        return plasmaCells.keySet().size();
    }

    private double percentComplete() {
        return 100.0 * DoubleUtil.ratio(countPlasmaCells(), plasmaTarget);
    }

    private int trialIndex() {
        return germinalCenters.size();
    }

    /**
     * Returns the directory where report files should be written.
     *
     * <p>The report directory is specified by the system property
     * with the name {@link AmatDriver#REPORT_DIR_PROPERTY}, or if
     * that property is not set, the parent directory of the first
     * configuration file passed on the command line.
     *
     * @return the directory where report files should be written.
     */
    public File getReportDir() {
        String defaultDir = getDefaultReportDir();
        String reportDir  = JamProperties.getOptional(REPORT_DIR_PROPERTY, defaultDir);

        return new File(reportDir);
    }

    private String getDefaultReportDir() {
        //
        // Parent directory of the first configuration file...
        //
        return FileUtil.getParentName(new File(fileNames[0]));
    }

    /**
     * Returns the number of germinal centers generated by this
     * driver.
     *
     * @return the number of germinal centers generated by this
     * driver.
     */
    public int countGerminalCenters() {
        return germinalCenters.size();
    }

    /**
     * Returns a germinal center referenced by its position in the
     * simulation sequence.
     *
     * @param gcIndex the (zero-offset) index of the germinal center.
     *
     * @return the germinal center with the specified index.
     */
    public GerminalCenter getGerminalCenter(int gcIndex) {
        return germinalCenters.get(gcIndex);
    }

    /**
     * Collects the active cells from all germinal centers for a given
     * GC cycle.
     *
     * @param cycleIndex the index of the desired germinal center
     * cycle.
     *
     * @return the active cells from all germinal centers for the
     * given GC cycle (empty if no germinal centers survived to the
     * given cycle).
     */
    public Set<BCell> getActiveCells(int cycleIndex) {
        Set<BCell> activeCells = new HashSet<BCell>();

        for (GerminalCenter gc : germinalCenters)
            if (cycleIndex < gc.countCycles())
                activeCells.addAll(gc.viewActiveCells(cycleIndex));

        return activeCells;
    }

    /**
     * Collects the population records from all germinal centers for
     * a given GC cycle.
     *
     * @param cycleIndex the index of the desired germinal center
     * cycle.
     *
     * @return the population records from all germinal centers for
     * the given GC cycle (empty if no germinal centers survived to
     * the given cycle).
     */
    public List<PopulationRecord> getPopulationRecords(int cycleIndex) {
        List<PopulationRecord> records = new ArrayList<PopulationRecord>();

        for (GerminalCenter gc : germinalCenters)
            if (cycleIndex < gc.countCycles())
                records.add(gc.getPopulation(cycleIndex));

        return records;
    }

    /**
     * Returns a read-only view of the germinal centers generated by
     * this driver.
     *
     * @return an unmodifiable list containing the germinal centers
     * generated by this driver.
     */
    public List<GerminalCenter> viewGerminalCenters() {
        return Collections.unmodifiableList(germinalCenters);
    }

    /**
     * Returns a read-only mapping from B cell receptor to plasma
     * cell, containing all plasma cells and unique receptors
     * generated by the affinity maturation trials.
     *
     * @return a read-only mapping from B cell receptor to plasma
     * cell.
     */
    public ImmutableMultimap<Receptor, BCell> viewPlasmaCells() {
        return ImmutableMultimap.copyOf(plasmaCells);
    }

    /**
     * Returns a read-only view of the B cells in the lineage of the
     * plasma cells (back to a specific generation) that were produced
     * by the simulation.
     *
     * @param firstGeneration the earliest generation to include in the
     * lineage.
     *
     * @return a read-only view of the B cells in the lineage of the
     * plasma cells that were produced by the simulation.
     */
    public List<BCell> viewPlasmaLineage(int firstGeneration) {
        List<BCell> lineage = new ArrayList<BCell>();

        for (BCell plasmaCell : plasmaCells.values())
            lineage.addAll(plasmaCell.traceLineage(firstGeneration));

        return lineage;
    }

    public List<Double> collectPlasmaTraits(ToDoubleFunction<BCell> toDouble) {
        List<Double> traits = new ArrayList<Double>();

        for (BCell plasmaCell : plasmaCells.values())
            traits.add(toDouble.applyAsDouble(plasmaCell));

        return traits;
    }

    public List<Double> collectPlasmaLineageTraits(ToDoubleFunction<BCell> toDouble) {
        List<Double> traits = new ArrayList<Double>();

        for (BCell plasmaCell : plasmaCells.values())
            for (BCell lineageCell : plasmaCell.traceLineage(GerminalCenter.REPLICATION_CYCLE + 1))
                traits.add(toDouble.applyAsDouble(lineageCell));

        return traits;
    }

    public static void main(String[] args) {
        run(args);
    }
}
