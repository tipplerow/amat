
package amat.panel;

import jam.app.JamProperties;
import jam.dist.OccurrenceDistribution;
import jam.math.DoubleComparator;
import jam.math.DoubleUtil;
import jam.math.Probability;

import amat.binding.AffinityModel;
import amat.binding.AffinityType;
import amat.binding.HammingAffinity;
import amat.epitope.Epitope; 
import amat.receptor.Receptor;
import amat.structure.CVClassifier;
import amat.structure.Structure;

/**
 * Defines a neutralization panel in which all discrete variable
 * elements are present in equal proportion.
 *
 * <p>Panels of this type may only be used with discrete epitopes
 * having a unique cardinality and unique length and with a {@code
 * HammingAffinity} model for binding affinity.
 */
public final class UniformDiscretePanel extends NeutralizationPanel {
    private final int cardinality;
    private final int epitopeLength;

    private final int conservedLength;
    private final int variableLength;

    private final Epitope template;
    private final CVClassifier classifier;
    private final HammingAffinity hammingAffinity;
    private final OccurrenceDistribution matchingDist;

    private final int totalMatchingThreshold;

    private final double meanVariableMatching;
    private final double meanVariableDistance;

    // Need to determine (1) the total number of matching elements
    // required to exceed the neutralization threshold [identical for
    // all; pre-computed static variable], and (2) the probability
    // that the number of matching variable elements will exceed (#
    // matching conserved - threshold #) [required separately for each
    // receptor].

    /**
     * Creates a new uniform discrete panel with a fixed affinity
     * threshold.
     *
     * @param affinityThreshold the minimum receptor-epitope binding
     * affinity (in units of kT) required to neutralize a member of
     * the panel.
     *
     * @throws IllegalArgumentException unless the threshold is
     * positive.
     *
     * @throws IllegalStateException unless the epitopes in the global
     * registry have discrete structures with a unique cardinality and
     * length and the affinity model is a {@code HammingAffinity} model.
     */
    public UniformDiscretePanel(double affinityThreshold) {
        super(affinityThreshold);

        this.template   = Epitope.template();
        this.classifier = Epitope.classify();

        this.cardinality   = Epitope.resolveCardinality();
        this.epitopeLength = Epitope.resolveLength();

        this.conservedLength = classifier.countConserved();
        this.variableLength  = classifier.countVariable();

        if (conservedLength + variableLength != epitopeLength)
            throw new IllegalStateException("Conserved and variable lengths are inconsistent with the epitope length.");

        this.hammingAffinity = resolveAffinityModel();
        this.matchingDist    = createMatchingDistribution();

        this.meanVariableMatching = computeMeanVariableMatching();
        this.meanVariableDistance = computeMeanVariableDistance();

        this.totalMatchingThreshold = computeTotalMatchingThreshold(affinityThreshold);
    }

    private static HammingAffinity resolveAffinityModel() {
        if (AffinityModel.global().getType().equals(AffinityType.HAMMING))
            return (HammingAffinity) AffinityModel.global();
        else
            throw new IllegalStateException("Uniform discrete panels require a Hamming affinity model.");
    }

    private OccurrenceDistribution createMatchingDistribution() {
        //
        // Probability of matching a single element is (1 / C), and
        // the number of trials is equal to the number of VARIABLE
        // elements (since the number of conserved matches will be
        // computed explicitly for each receptor)...
        //
        int    trialCount = variableLength;
        double eventProb  = DoubleUtil.ratio(1, cardinality);
        
        return new OccurrenceDistribution(Probability.valueOf(eventProb), trialCount);
    }

    private double computeMeanVariableMatching() {
        //
        // Expected number of variable matches is not necessarily an
        // integer: With a 10-element variable region, for example,
        // and a Potts model with a cardinality of 4, the expected
        // number of matches is 10 / 4 = 2.5...
        //
        return DoubleUtil.ratio(variableLength, cardinality);
    }

    private double computeMeanVariableDistance() {
        return variableLength - meanVariableMatching;
    }

    private int computeTotalMatchingThreshold(double affinityThreshold) {
        for (int matchCount = 0; matchCount <= epitopeLength; ++matchCount) {
            double distance = epitopeLength - matchCount;
            double affinity = computeAffinity(distance);

            if (DoubleComparator.DEFAULT.GE(affinity, affinityThreshold))
                return matchCount;
        }

        return epitopeLength + 1;
    }

    /**
     * Returns the number of conserved elements across the epitopes in
     * this panel.
     *
     * @return the number of conserved elements across the epitopes in
     * this panel.
     */
    public int getConservedLength() {
        return conservedLength;
    }

    /**
     * Returns the number of matching conserved elements required (on
     * average) to exceed the neutralization threshold.
     *
     * @return the number of matching conserved elements required (on
     * average) to exceed the neutralization threshold.
     */
    public double getConservedMatchingThreshold() {
        return totalMatchingThreshold - meanVariableMatching;
    }

    /**
     * Returns the (unique) length of the epitopes in this panel.
     *
     * @return the (unique) length of the epitopes in this panel.
     */
    public int getEpitopeLength() {
        return epitopeLength;
    }

    /**
     * Returns the mean Hamming distance between an arbitrary B cell
     * receptor and the epitopes in this panel <em>examining only the
     * variable region</em>.
     *
     * @return the mean Hamming distance between an arbitrary B cell
     * receptor and the variable region of the epitopes in this panel.
     */
    public double getMeanVariableDistance() {
        return meanVariableDistance;
    }

    /**
     * Returns the average number of elements that a receptor will
     * match in the variable region.
     *
     * @return the average number of elements that a receptor will
     * match in the variable region.
     */
    public double getMeanVariableMatching() {
        return meanVariableMatching;
    }

    /**
     * Returns the total number of matching elements required to
     * neutralize an epitope in this panel.
     *
     * @return the total number of matching elements required to
     * neutralize an epitope in this panel.
     */
    public int getTotalMatchingThreshold() {
        return totalMatchingThreshold;
    }

    /**
     * Returns the number of variable elements across the epitopes in
     * this panel.
     *
     * @return the number of variable elements across the epitopes in
     * this panel.
     */
    public int getVariableLength() {
        return variableLength;
    }

    /**
     * Computes the binding affinity for a receptor-epitope pair
     * separated by a given Hamming distance.
     *
     * @param hammingDistance the Hamming distance separating the
     * receptor and epitope.
     *
     * @return the binding affinity for a receptor-epitopr pair
     * separated by the specified Hamming distance.
     */
    public double computeAffinity(double hammingDistance) {
        return hammingAffinity.computeAffinity(hammingAffinity.computeFreeEnergy(hammingDistance));
    }

    /**
     * Computes the breadth of a receptor as a function of the number
     * of elements matching the conserved elements of the epitopes in
     * this panel.
     *
     * @param conservedMatching the number of elements in the B cell
     * receptor that match conserved elements of the panel epitopes.
     *
     * @return the breadth of a receptor with the specified number of
     * conserved matching elements.
     */
    public double computeBreadth(int conservedMatching) {
        return matchingDist.atLeast(computeRequiredVariableMatching(conservedMatching));
    }

    private int computeRequiredVariableMatching(int conservedMatching) {
        return totalMatchingThreshold - conservedMatching;
    }

    /**
     * Computes the maximum possible breadth for a B cell receptor
     * (one that matches all conserved epitope elements exactly).
     *
     * @return the maximum possible breadth for a B cell receptor.
     */
    public double computeMaximumBreadth() {
        return computeBreadth(conservedLength);
    }

    /**
     * Computes the mean Hamming distance between a B cell receptor
     * and the epitopes in this panel.
     *
     * @param receptor the receptor to examine.
     *
     * @return the mean Hamming distance between the given B cell
     * receptor and the epitopes in this panel.
     *
     * @throws IllegalArgumentException unless the length of the
     * receptor matches the (unique) length of the epitopes in this
     * panel.
     */
    public double computeMeanDistance(Receptor receptor) {
        return computeConservedDistance(receptor) + meanVariableDistance;
    }

    private int computeConservedDistance(Receptor receptor) {
        return conservedLength - computeConservedMatching(receptor);
    }

    private int computeConservedMatching(Receptor receptor) {
        Structure structEpi = template.getStructure();
        Structure structBCR = receptor.getStructure();

        if (structBCR.length() != epitopeLength)
            throw new IllegalArgumentException("Receptor length does not match the epitope length.");
        
        return classifier.countMatchingConserved(structEpi, structBCR);
    }

    @Override public double computeBreadth(Receptor receptor) {
        //
        // The breadth is the probability that the number of variable
        // matches is greater than or equal to the threshold required
        // for neutralization...
        //
        return computeBreadth(computeConservedMatching(receptor));
    }

    @Override public double computeMeanAffinity(Receptor receptor) {
        return computeAffinity(computeMeanDistance(receptor));
    }

    @Override public long countEpitopes() {
        return (long) Math.min(Math.pow(cardinality, variableLength), (double) Long.MAX_VALUE);
    }

    @Override public PanelType getType() {
        return PanelType.UNIFORM_DISCRETE;
    }

    private static void usage() {
        System.err.println("java [OPTIONS] amat.panel.UniformDiscretePanel FILE1 [FILE2 ...]");
        System.exit(1);
    }

    /**
     * Reports the neutralization breadth as a function of the number
     * of matching conserved elements for a given epitope collection
     * and neutralization threshold.
     *
     * @param args one or more names for the files containing the
     * system properties required to define the Hamming affinity
     * model, the epitope collection, and the neutralization panel.
     */
    public static void main(String[] args) {
        if (args.length < 1)
            usage();

        for (String fileName : args)
            JamProperties.loadFile(fileName, true);

        Epitope.load();
        UniformDiscretePanel panel = 
            (UniformDiscretePanel) NeutralizationPanel.global();

        System.out.println("Conserved Match Count        Neutralization Breadth");
        System.out.println("---------------------        ----------------------");

        for (int matchCount = 0; matchCount <= panel.getConservedLength(); ++matchCount)
            System.out.println(String.format("%21d        %22.6f", matchCount, panel.computeBreadth(matchCount)));
    }
}
