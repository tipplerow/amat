
package amat.report;

import jam.math.DoubleUtil;

import amat.bcell.BCell;
import amat.epitope.Epitope;
import amat.structure.Structure;

/**
 * Computes and records the generation at which a plasma cell exited
 * the germinal center, the number of mutations that it accumulated,
 * and the mutational distance from its germline (founder) cell.
 */
public final class GermDistDetail {
    private final long   plasmaIndex;
    private final long   founderIndex;
    private final int    generation;
    private final int    mutationCount;
    private final double mutationalDist;

    private GermDistDetail(long   plasmaIndex,
			   long   founderIndex,
			   int    generation,
			   int    mutationCount,
			   double mutationalDist) {
        this.plasmaIndex    = plasmaIndex;
        this.founderIndex   = founderIndex;
        this.generation     = generation;
	this.mutationCount  = mutationCount;
	this.mutationalDist = mutationalDist;
    }

    /**
     * Generates a detail record for a single plasma cell.
     *
     * @param plasmaCell a plasma cell for analysis.
     *
     * @return a new detail record for the specified plasma cell.
     */
    public static GermDistDetail compute(BCell plasmaCell) {
        long   plasmaIndex    = plasmaCell.getIndex();
	long   founderIndex   = plasmaCell.getFounder().getIndex();
	int    generation     = plasmaCell.getGeneration();
	int    mutationCount  = plasmaCell.getMutationCount();
	double mutationalDist = plasmaCell.getFounderDistance();

	return new GermDistDetail(plasmaIndex,
				  founderIndex,
				  generation,
				  mutationCount,
				  mutationalDist);
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
     * Returns the index of the founder cell.
     *
     * @return the index of the founder cell.
     */
    public long getFounderIndex() {
        return founderIndex;
    }

    /**
     * Returns the generation when the plasma cell exited the germinal
     * center.
     *
     * @return the generation when the plasma cell exited the germinal
     * center.
     */
    public int getGeneration() {
	return generation;
    }

    /**
     * Returns the number of mutations accumulated by the plasma cell.
     *
     * @return the number of mutations accumulated by the plasma cell.
     */
    public int getMutationCount() {
	return mutationCount;
    }

    /**
     * Returns the mutational distance between the plasma cell and its
     * germline (founder) cell.
     *
     * @return the mutational distance between the plasma cell and its
     * germline (founder) cell.
     */
    public double getMutationalDist() {
        return mutationalDist;
    }

    /**
     * Formats this record to be written to a file.
     *
     * @return a string to be written to the detail report file.
     */
    public String format() {
        return String.format("%d,%d,%d,%d,%8.4f",
                             plasmaIndex,
			     founderIndex,
			     generation,
			     mutationCount,
			     mutationalDist);
    }

    /**
     * Returns a string suitable for the header line in the detail
     * report file.
     *
     * @return a string suitable for the header line in the detail
     * report file.
     */
    public static String header() {
        return "plasmaIndex,founderIndex,generation,mutationCount,mutationalDist";
    }
}
