
package amat.signal;

/**
 * Enumerates the manner by which BCR signaling after antigen capture
 * allows B cells in the light zone to avoid apoptosis.
 */
public enum BCRSignalingType {
    /**
     * B cells receive survival signals if they bind one or more
     * antigens with an affinity exceeding a threshold. (This is 
     * a deterministic rule.)
     */
    AFFINITY_THRESHOLD, 

    /**
     * B cells receive survival signals if they capture antigen in a
     * quantity exceeding a threshold. (This is a deterministic rule.)
     */
    QUANTITY_THRESHOLD, 

    /**
     * B cells receive survival signals with probability {@code Q / (1.0 + Q)}, 
     * where {@code Q} is the total amount of antigen captured.
     */
   QUANTITY_LANGMUIR;
}
