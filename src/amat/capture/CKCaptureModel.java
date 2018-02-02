
package amat.capture;

import jam.chem.Concentration;

import amat.binding.AffinityModel;

/**
 * Implements an epitope capture model with the capture rate equal to
 * the product of the epitope concentration ("C") and the equilibrium
 * constant for epitope-receptor binding ("K").  The global affinity
 * model is used to compute the equilibrium constant.
 */
public final class CKCaptureModel extends EpitopeCaptureModel {
    private CKCaptureModel() {}

    /**
     * The singleton instance.
     */
    public static final CKCaptureModel INSTANCE = new CKCaptureModel();

    @Override public double capture(double affinity, Concentration concentration) {
        return concentration.doubleValue() * AffinityModel.computeEquilConst(affinity);
    }
}
