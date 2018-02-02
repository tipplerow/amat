
package amat.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import amat.bcell.BCell;
import amat.structure.CV;

/**
 * Encapsulates the temporal evolution of the average fraction of
 * plasma cell receptor elements that exactly match the conserved and
 * variable regions of the epitopes presented for affinity maturation.
 */
public final class MatchingHistoryDetail {
    private final int trialIndex;
    private final long plasmaIndex;
    private final List<MatchingRecord> records = new ArrayList<MatchingRecord>();

    /**
     * Creates a new history record for a specific plasma cell.
     *
     * @param trialIndex the index of the affinity maturation trial
     * that produced the plasma cell.
     *
     * @param plasmaCell the plasma cell to analyze.
     */
    public MatchingHistoryDetail(int trialIndex, BCell plasmaCell) {
        this.trialIndex  = trialIndex;
        this.plasmaIndex = plasmaCell.getIndex();

        populateRecords(plasmaCell);
    }

    private void populateRecords(BCell plasmaCell) {
        List<BCell> lineage = plasmaCell.traceLineage();

        for (BCell bcell : lineage)
            records.add(MatchingRecord.compute(bcell, bcell.getGeneration()));
    }

    /**
     * Returns a string suitable for the header line in the history
     * report file.
     *
     * @return a string suitable for the header line in the history
     * report file.
     */
    public static String header() {
        return "trialIndex,plasmaIndex,regionType,matchHistory";
    }

    /**
     * Formats the matching history for output to the history file.
     *
     * @param cv the epitope region of interest.
     *
     * @return a string representation of the matching history for the
     * given epitope region.
     */
    public String format(CV cv) {
        StringBuilder builder = new StringBuilder();

        builder.append(trialIndex);
        builder.append(",");
        builder.append(plasmaIndex);
        builder.append(",");
        builder.append(cv);
        builder.append(",");

        for (MatchingRecord record : records) {
            builder.append(" ");
            builder.append(String.format("%6.4f", record.get(cv)));
        }

        return builder.toString();
    }

    /**
     * Returns the index of the affinity maturation trial that
     * produced the plasma cell.
     *
     * @return the index of the affinity maturation trial that
     * produced the plasma cell.
     */
    public int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Returns the index of the plasma cell being analyzed.
     *
     * @return the index of the plasma cell being analyzed.
     */
    public long getPlasmaIndex() {
        return plasmaIndex;
    }

    /**
     * Returns the generation at which the plasma cell exited the
     * germinal center.
     *
     * @return the generation at which the plasma cell exited the
     * germinal center.
     */
    public int getFinalGeneration() {
        return records.size() - 1;
    }

    /**
     * Returns the matching record for a given generation.
     *
     * @param generation the generation of interest.
     *
     * @return the matching record for the specified generation.
     */
    public MatchingRecord getRecord(int generation) {
        return records.get(generation);
    }

    /**
     * Returns a read-only view of the matching records.
     *
     * @return a read-only view of the matching records.
     */
    public List<MatchingRecord> viewRecords() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Groups the matching records from a collection of histories by
     * generation.
     *
     * @param details the detail histories to group.
     *
     * @return a list with element {@code k} containing all matching
     * records for generation {@code k}.
     */
    public static List<List<MatchingRecord>> groupByGeneration(Collection<MatchingHistoryDetail> details) {
        int maxGeneration =
            details.stream().mapToInt(x -> x.getFinalGeneration()).max().getAsInt();

        List<List<MatchingRecord>> generations = new ArrayList<List<MatchingRecord>>(maxGeneration + 1);

        for (int generation = 0; generation <= maxGeneration; ++generation)
            generations.add(new ArrayList<MatchingRecord>());

        for (MatchingHistoryDetail detail : details)
            for (int generation = 0; generation <= detail.getFinalGeneration(); ++generation)
                generations.get(generation).add(detail.getRecord(generation));

        return generations;
    }
}
