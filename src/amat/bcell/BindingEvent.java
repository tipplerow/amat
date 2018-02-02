
package amat.bcell;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jam.chem.Concentration;
import jam.math.DoubleComparator;
import jam.util.CollectionUtil;

import amat.antigen.Antigen;
import amat.binding.AffinityModel;
import amat.capture.EpitopeCaptureModel;
import amat.epitope.Epitope;

/**
 * Represents the outcome of the interaction between an epitope and a
 * B cell receptor.
 */
public final class BindingEvent {
    private final Antigen antigen;
    private final Epitope epitope;
    private final double  affinity;
    private final double  quantity; 

    private BindingEvent(Antigen antigen, Epitope epitope, double affinity, double quantity) {
        this.antigen  = antigen;
	this.epitope  = epitope;
	this.affinity = affinity;
	this.quantity = quantity;
    }

    private static class AffinityComparator implements Comparator<BindingEvent> {
        @Override public int compare(BindingEvent e1, BindingEvent e2) {
            return Double.compare(e1.affinity, e2.affinity);
        }
    }

    private static final Comparator<BindingEvent> AFFINITY_COMPARATOR = new AffinityComparator();

    /**
     * Creates a new binding event.
     *
     * @param bcell the B cell participating in the binding event.
     *
     * @param antigen the antigen participating in the binding event.
     *
     * @param epitope the epitope participating in the binding event.
     *
     * @param concentration the concentration of the antigen and
     * epitope in the germinal center.
     *
     * @return a new binding event for the specified parameters.
     */
    public static BindingEvent create(BCell bcell, Antigen antigen, Epitope epitope, Concentration concentration) {
        double affinity = AffinityModel.global().computeAffinity(epitope, bcell.getReceptor());
	double quantity = EpitopeCaptureModel.global().capture(affinity, concentration);

        return new BindingEvent(antigen, epitope, affinity, quantity);
    }

    /**
     * Returns the antigen presented to the B cell.
     *
     * @return the antigen presented to the B cell.
     */
    public Antigen getAntigen() {
        return antigen;
    }

    /**
     * Returns the epitope presented to the B cell.
     *
     * @return the epitope presented to the B cell.
     */
    public Epitope getEpitope() {
        return epitope;
    }

    /**
     * Returns the affinity between the epitope and B cell receptor.
     *
     * @return the affinity between the epitope and B cell receptor.
     */
    public double getAffinity() {
        return affinity;
    }

    /**
     * Returns the amount of the epitope that the B cell captured
     * (internalized).
     *
     * @return the amount of the epitope that the B cell captured
     * (internalized).
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Finds the maximum affinity in a list of events.
     *
     * @param events the events to process.
     *
     * @return the maximum affinity, or {@code Double.NEGATIVE_INFINITY}}
     * if the list is empty.
     */
    public static double getMaxAffinity(List<BindingEvent> events) {
        return CollectionUtil.max(events, event -> event.getAffinity());
    }

    /**
     * Finds the average affinity in a list of events.
     *
     * @param events the events to process.
     *
     * @return the average affinity, or {@code Double.NaN}} if the
     * list is empty.
     */
    public static double getMeanAffinity(List<BindingEvent> events) {
        return CollectionUtil.average(events, event -> event.getAffinity());
    }

    /**
     * Computes the total quantity of antigen captured (internalized)
     * from a list of events.
     *
     * @param events the events to process.
     *
     * @return the total qualtity of antigen captured (internalized),
     * or {@code 0.0} if the list is empty.
     */
    public static double getTotalQuantity(List<BindingEvent> events) {
        return CollectionUtil.sum(events, event -> event.getQuantity());
    }

    /**
     * Sorts a list of events into increasing order of affinity (so
     * that the strongest binding event is the last).
     *
     * @param events the events to sort.
     */
    public static void sortAffinity(List<BindingEvent> events) {
        Collections.sort(events, AFFINITY_COMPARATOR);
    }

    @Override public String toString() {
        return String.format("BindingEvent(%s: affinity = %8.4f, quantity = %8.4f", epitope.getKey(), affinity, quantity);
    }
}
