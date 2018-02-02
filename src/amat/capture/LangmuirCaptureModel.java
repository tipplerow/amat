
package amat.capture;

import jam.chem.Concentration;
import jam.chem.Langmuir;
import jam.math.Probability;

import amat.binding.AffinityModel;

/**
 * Implements an epitope capture model where one unit of antigen is
 * captured with a probability equal to {@code K / (1 + K)} (and no
 * antigen is captured with probability {@code 1 / (1 + K)}), where
 * {@code K} is the equilibrium constant for epitope-receptor binding.
 */
public final class LangmuirCaptureModel extends EpitopeCaptureModel {
    private LangmuirCaptureModel() {}

    /**
     * The singleton instance.
     */
    public static final LangmuirCaptureModel INSTANCE = new LangmuirCaptureModel();

    @Override public double capture(double affinity, Concentration concentration) {
        if (isCaptured(affinity))
            return 1.0;
        else
            return 0.0;
    }

    private boolean isCaptured(double affinity) {
        return captureProbability(affinity).accept();
    }

    private Probability captureProbability(double affinity) {
        return Langmuir.probability(AffinityModel.computeEquilConst(affinity));
    }
}
