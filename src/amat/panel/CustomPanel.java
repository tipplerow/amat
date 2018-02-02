
package amat.panel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import amat.epitope.Epitope;
import amat.receptor.Receptor;

/**
 * Implements a customized neutralization panel with explicitly
 * assigned epitopes.
 */
public abstract class CustomPanel extends NeutralizationPanel {
    private final Set<Epitope> epitopes = new HashSet<Epitope>();

    /**
     * Creates a new custom neutralization panel with a fixed epitope
     * collection.
     *
     * @param epitopes the epitopes composing the panel.
     *
     * @throws IllegalArgumentException if the epitope collection is
     * empty.
     */
    protected CustomPanel(Collection<Epitope> epitopes) {
        super();
        assignEpitopes(epitopes);
    }

    /**
     * Creates a new custom neutralization panel with a fixed affinity
     * threshold and epitope collection.
     *
     * @param affinityThreshold the minimum receptor-epitope binding
     * affinity (in units of kT) required to neutralize a member of
     * the panel.
     *
     * @param epitopes the epitopes composing the panel.
     *
     * @throws IllegalArgumentException unless the threshold is
     * positive and at least one epitope is supplied.
     */
    protected CustomPanel(double affinityThreshold, Collection<Epitope> epitopes) {
        super(affinityThreshold);
        assignEpitopes(epitopes);
    }

    private void assignEpitopes(Collection<Epitope> epitopes) {
        if (epitopes.isEmpty())
            throw new IllegalArgumentException("At least one epitope must be supplied.");

        this.epitopes.addAll(epitopes);
    }

    /**
     * Identifies epitopes in this panel.
     *
     * @param epitope the epitope to examine.
     *
     * @return {@code true} iff this panel contains the specified
     * epitope.
     */
    public boolean contains(Epitope epitope) {
        return epitopes.contains(epitope);
    }

    /**
     * Returns a read-only view of the epitopes in this panel.
     *
     * @return a read-only view of the epitopes in this panel.
     */
    public Collection<Epitope> viewEpitopes() {
        return Collections.unmodifiableCollection(epitopes);
    }

    @Override public double computeBreadth(Receptor receptor) {
        return computeBreadth(receptor, epitopes);
    }

    @Override public double computeMeanAffinity(Receptor receptor) {
        return computeMeanAffinity(receptor, epitopes);
    }

    @Override public long countEpitopes() {
        return epitopes.size();
    }
}
