
package amat.capture;

/**
 * Enumerates quantitative models for the amount of antigen captured
 * and internalized when B cells encounter antigen presented on the
 * surface of follicular dendritic cells (FDCs) in the light zone of
 * a germinal center.
 */
public enum EpitopeCaptureType {
    /**
     * B cells capture a quantity of antigen equal to the product of
     * the epitope concentration ("C") and the equilibrium constant
     * for epitope-receptor binding ("K"); the global affinity model
     * is used to compute the equilibrium constant.
     */
    CK,

    /**
     * One unit of antigen is captured with a probability equal to
     * {@code K / (1 + K)} (no antigen is captured with probability
     * {@code 1 / (1 + K)}), where {@code K} is the equilibrium
     * constant for epitope-receptor binding.
     */
    LANGMUIR;
}
