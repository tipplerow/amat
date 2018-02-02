
package amat.occupy;

/**
 * Enumerates the manner in which antigens occupy sites on the surface
 * of follicular dendritic cells (FDCs) in the light zone of germinal
 * centers.
 */
public enum OccupationType {
    /**
     * All antigens occupy each FDC site.
     */
    ALL, 

    /**
     * One antigen (chosen at random with probability equal to its
     * fractional concentration) occupies an FDC site.
     */
    ONE,

    /**
     * Prior to an all-one transition, all antigens occupy each FDC
     * site; after the all-one transition, only one antigen (chosen at
     * random with probability equal to its fractional concentration)
     * occupies a site.
     */
    ALL_ONE, 

    /**
     * Occupation is modeled by the multi-species Langmuir adsorption
     * isotherm.  At most one antigen occupies an FDC site.  For the
     * antigen with index {@code k}, its occupation probability is
     * equal to {@code C(k) / [1.0 + C(1) + C(2) + ... + C(N)]}, where
     * {@code C(k)} is the concentration of antigen {@code k} and
     * {@code N} is the total number of antigens.
     */
    LANGMUIR;
}
