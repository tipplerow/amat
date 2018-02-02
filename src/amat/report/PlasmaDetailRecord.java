
package amat.report;

import amat.bcell.BCell;
import amat.structure.Structure;

/**
 * Encapsulates relevant details for plasma cells produced during
 * affinity maturation.
 */
public final class PlasmaDetailRecord {
    private final int    trialIndex;
    private final long   plasmaIndex;
    private final long   founderIndex;
    private final int    generation;
    private final int    mutationCount;
    private final double founderDistance;
    private final String receptorStructure;

    /**
     * Creates a new detail record for a plasma cell.
     *
     * @param trialIndex index of the affinity maturation trial.
     *
     * @param cell the plasma cell of interest.
     */
    public PlasmaDetailRecord(int trialIndex, BCell cell) {
        this.trialIndex        = trialIndex;
        this.plasmaIndex       = cell.getIndex();
        this.founderIndex      = cell.getFounder().getIndex();
        this.generation        = cell.getGeneration();
        this.mutationCount     = cell.getMutationCount();
        this.founderDistance   = cell.getFounderDistance();
        this.receptorStructure = cell.getReceptor().getStructure().format();
    }

    /**
     * Returns a string suitable for the header line in the detail
     * report file.
     *
     * @return a string suitable for the header line in the detail
     * report file.
     */
    public static String header() {
        return "trialIndex,plasmaIndex,founderIndex,generation,mutationCount,founderDistance,receptorStructure";
    }

    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the detail report file.
     */
    public String format() {
        return String.format("%d,%d,%d,%d,%d,%f,%s",
                             trialIndex,
                             plasmaIndex,
                             founderIndex,
                             generation,
                             mutationCount,
                             founderDistance,
                             receptorStructure);
    }

    /**
     * Returns the index of the affinity maturation trial in which the
     * plasma cell was produced.
     *
     * @return the index of the affinity maturation trial in which the
     * plasma cell was produced.
     */
    public int getTrialIndex() {
        return trialIndex;
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
     * Returns the canonical string representation of the plasma cell
     * receptor.
     *
     * @return the canonical string representation of the plasma cell
     * receptor.
     */
    public String getReceptorStructure() {
        return receptorStructure;
    }
}
