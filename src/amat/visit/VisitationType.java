
package amat.visit;

public enum VisitationType {
    /**
     * B cells visit a fixed number of FDC sites where they encounter
     * antigens in clusters, which are quantified by the probability
     * that a B cell will revisit the same antigen at the next site.
     */
    CLUSTER,

    /**
     * B cells visit a fixed number of FDCs on each germinal center
     * cycle; the occupation of the FDC sites (and therefore the
     * number and type antigens visted) is governed by the global
     * {@code OccupationModel}.
     */
    FIXED_COUNT;
}
