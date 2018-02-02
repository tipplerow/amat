
package amat.report;

import amat.bcell.BCell;
import amat.structure.Structure;

/**
 * Encapsulates relevant details for founder cells produced during
 * affinity maturation.
 */
public final class FounderDetailRecord {
    private final int    trialIndex;
    private final long   founderIndex;
    private final String receptorStructure;

    /**
     * Creates a new detail record for a founder cell.
     *
     * @param trialIndex index of the affinity maturation trial.
     *
     * @param cell the founder cell of interest.
     */
    public FounderDetailRecord(int trialIndex, BCell cell) {
        this.trialIndex        = trialIndex;
        this.founderIndex      = cell.getIndex();
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
        return "trialIndex,founderIndex,receptorStructure";
    }

    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the detail report file.
     */
    public String format() {
        return String.format("%d,%d,%s", trialIndex, founderIndex, receptorStructure);
    }

    /**
     * Returns the index of the affinity maturation trial in which the
     * founder cell was activated.
     *
     * @return the index of the affinity maturation trial in which the
     * founder cell was activated.
     */
    public int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Returns the index of the founder cell.
     *
     * @return the index of the founder cell.
     */
    public long getFounderIndex() {
        return founderIndex;
    }

    /**
     * Returns the canonical string representation of the founder cell
     * receptor.
     *
     * @return the canonical string representation of the founder cell
     * receptor.
     */
    public String getReceptorStructure() {
        return receptorStructure;
    }
}
