
package amat.report;

import amat.bcell.BCell;

/**
 * Records the affinity of plasma cell BCRs for the epitopes presented
 * during affinity maturation and for those in the full neutralization
 * panel.
 */
public final class PlasmaAffinityDetail {
    private final long   plasmaIndex;
    private final long   founderIndex;
    private final int    generation;
    private final int    mutationCount;
    private final double founderDistance;

    private final AffinityRecord record;

    /**
     * Creates a new affinity record for a plasma cell.
     *
     * @param cell the plasma cell of interest.
     *
     * @param record the affinity record.
     */
    public PlasmaAffinityDetail(BCell cell, AffinityRecord record) {
        this.plasmaIndex     = cell.getIndex();
        this.founderIndex    = cell.getFounder().getIndex();
        this.generation      = cell.getGeneration();
        this.mutationCount   = cell.getMutationCount();
        this.founderDistance = cell.getFounderDistance();

        this.record = record;
    }

    /**
     * Returns a string suitable for the header line in the affinity
     * report file.
     *
     * @return a string suitable for the header line in the affinity
     * report file.
     */
    public static String header() {
        return "trialIndex"
            + ",plasmaIndex"
            + ",founderIndex"
            + ",generation"
            + ",mutationCount"
            + ",founderDistance"
            + ",footprintSize"
            + ",footprintAffinity"
            + ",footprintBreadth"
            + ",neutPanelAffinity"
            + ",neutPanelBreadth";
    }

    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the affinity report file.
     */
    public String format() {
        return String.format("%d,%d,%d,%d,%d,%.4f,%.4f,%.6f,%.4f,%.6f",
                             plasmaIndex,
                             founderIndex,
                             generation,
                             mutationCount,
                             founderDistance,
                             record.getFootprintSize(),
                             record.getFootprintAffinity(),
                             record.getFootprintBreadth(),
                             record.getNeutPanelAffinity(),
                             record.getNeutPanelBreadth());
    }

    /**
     * Returns the index of the plasma cell.
     *
     * @return the index of the plasma cell.
     */
    public long getPlasmaIndex() {
        return plasmaIndex;
    }

    /**
     * Returns the index of the founder cell from which the plasma
     * cell was derived.
     *
     * @return the index of the founder cell.
     */
    public long getFounderIndex() {
        return founderIndex;
    }

    /**
     * Returns the generation at which the plasma cell exited the
     * germinal center.
     *
     * @return the generation at which the plasma cell exited the
     * germinal center.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the number of mutations accumulated by the plasma
     * cell during affinity maturation.
     *
     * @return the number of mutations accumulated by the plasma
     * cell during affinity maturation.
     */
    public int getMutationCount() {
        return mutationCount;
    }

    /**
     * Returns the mutational distance between the plasma cell and its
     * founder.
     *
     * @return the mutational distance between the plasma cell and its
     * founder.
     */
    public double getFounderDistance() {
        return founderDistance;
    }

    /**
     * Returns the affinity record for the B cell.
     *
     * @return the affinity record for the B cell.
     */
    public AffinityRecord getAffinityRecord() {
        return record;
    }
}
