
package amat.divide;

public enum DZDivisionType {
    /**
     * All B cells divide a fixed number of times, regardless of the
     * amount of antigen captured.
     */
    FIXED_COUNT,

    /**
     * The expected number of divisions increases with the amount of
     * antigen captured, as quantified by a function of the ratio
     * {@code R = Q(k) / mean(Q)}, where {@code Q(k)} is the amount of
     * antigen captured by cell {@code k}.
     */
    MEAN_CAPTURE_RATIO;
}
