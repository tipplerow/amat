
package amat.germinal;

/**
 * Enumerates key events that occur in each germinal center cycle.
 *
 * <p>The enum values must be defined in <b>chronological order</b> of
 * occurrence in the germinal center cycle.  Their ordinal values will
 * then correspond to the sequence of events occurring on each cycle.
 */
public enum GerminalCenterEvent {
    /**
     * Reentry of memory cells into the dark zone.
     */
    MEMORY_REENTRY,

    /**
     * Division and mutation in the dark zone.
     */
    DIVISION_MUTATION,

    /**
     * Survival or apoptosis after the binding of antigens triggers B
     * cell receptor signaling.
     */
    BCR_SIGNALING,

    /**
     * Survival or apoptosis after the competition for T cell help.
     */
    TCELL_COMPETITION,

    /**
     * Selection for exiting the germinal center as memory cells.
     */
    MEMORY_SELECTION,

    /**
     * Selection for exiting the germinal center as plasma cells.
     */
    PLASMA_SELECTION;
}
