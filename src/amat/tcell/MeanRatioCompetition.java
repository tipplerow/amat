
package amat.tcell;

import java.util.Set;
import java.util.function.ToDoubleFunction;

import jam.chem.Langmuir;
import jam.math.Probability;

import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.bcell.SequentialApoptosisModel;

/**
 * Implements a T cell competition model where B cells receive T cell
 * help with probability {@code R / (1 + R)} with {@code R = Q / <Q>},
 * where {@code Q} is a scalar quantity of the B cell (i.e., the total
 * amount of antigen captured or the maximum binding affinity) and
 * {@code <Q>} is its mean value (averaged across all competing B
 * cells).
 */
public final class MeanRatioCompetition extends SequentialApoptosisModel {
    private final ToDoubleFunction<BCell> toDouble;
    private double mean;

    public MeanRatioCompetition(ToDoubleFunction<BCell> toDouble) {
        this.toDouble = toDouble;
    }

    @Override public boolean apoptose(BCell cell) {
        Probability survivalProb = computeSurvivalProb(cell);
        Probability apoptosisProb = survivalProb.not();

        return apoptosisProb.accept();
    }

    private Probability computeSurvivalProb(BCell cell) {
        return Langmuir.probability(toDouble.applyAsDouble(cell) / mean);
    }

    @Override public void initialize(Set<BCell> cells, AntigenPool pool) {
        mean = cells.stream().mapToDouble(toDouble).average().orElse(Double.NaN);

        if (!cells.isEmpty() && Double.isNaN(mean))
            throw new IllegalStateException("Missing mean value.");
    }
}
