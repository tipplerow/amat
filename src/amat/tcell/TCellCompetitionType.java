
package amat.tcell;

/**
 * Enumerates the manner by which B cells in the light zone compete
 * for T cell help.
 */
public enum TCellCompetitionType {
    /**
     * B cells are ranked by the total quantity of antigen captured
     * and internalized during their search in the light zone.
     */
    ANTIGEN_QTY_RANK, 

    /**
     * Probabilistic selection as a function of {@code Q / mean(Q)},
     * where {@code Q} is the total quantity of antigen captured and
     * internalized, and {@code mean(Q)} is its average over all B
     * cells.
     */
    ANTIGEN_QTY_MEAN_RATIO, 

    /**
     * B cells are ranked by maximum affinity for antigens that they
     * encountered in the light zone.
     */
    MAX_AFFINITY_RANK,

    /**
     * Probabilistic selection as a function of total antigen captured
     * compared to the average amount captured by other B cells, as
     * defined by Wang et al., Cell 160, 785--797 (2015).
     */
    WANG_CELL;
}
