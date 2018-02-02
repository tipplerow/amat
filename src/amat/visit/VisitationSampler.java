
package amat.visit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.app.JamProperties;
import jam.dist.EmpiricalDiscreteDistribution;
import jam.math.IntRange;
import jam.util.SetUtil;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.vaccine.VaccinationSchedule;

/**
 * Driver application to sample the number of antigens encountered
 * during visits to the germinal center light zone.
 */
public final class VisitationSampler {
    private final int gcCycle;
    private final int trialCount;

    private final AntigenPool antigenPool;
    private final VisitationModel visitationModel;

    private EmpiricalDiscreteDistribution visitDist;
    private EmpiricalDiscreteDistribution revisitDist;

    private VisitationSampler() {
        this.gcCycle = resolveGcCycle();
        this.trialCount = resolveTrialCount();

        this.antigenPool = loadAntigenPool(gcCycle);
        this.visitationModel = VisitationModel.global();
    }

    private static int resolveGcCycle() {
        return JamProperties.getOptionalInt(GC_CYCLE_PROPERTY, IntRange.NON_NEGATIVE, 0);
    }

    private static int resolveTrialCount() {
        return JamProperties.getOptionalInt(TRIAL_COUNT_PROPERTY, IntRange.POSITIVE, TRIAL_COUNT_DEFAULT);
    }

    private static AntigenPool loadAntigenPool(int gcCycle) {
        return VaccinationSchedule.global().getAntigenPoolFootprint(gcCycle);
    }

    /**
     * Name of the system property which specifies the germinal center
     * cycle to sample (which matters only for time-dependent antigen
     * concentrations); defaults to zero.
     */
    public static final String GC_CYCLE_PROPERTY = "amat.VisitationSampler.gcCycle";

    /**
     * Name of the system property which specifies the number of
     * visitation trials to execute; defaults to {@code TRIAL_COUNT_DEFAULT}.
     */
    public static final String TRIAL_COUNT_PROPERTY = "amat.VisitationSampler.trialCount";

    /**
     *
     */
    public static final int TRIAL_COUNT_DEFAULT = 1000000;

    /**
     * Executes the visitation sampling.
     *
     * @param propFiles names of the property or configuration files
     * to load prior to execution.
     */
    public static void run(String... propFiles) {
        JamProperties.loadFiles(propFiles, false);

        VisitationSampler sampler = new VisitationSampler();
        sampler.run();
    }

    private void run() {
        sample();
        report();
    }

    private void sample() {
        Set<Antigen> revisited;
        Set<Antigen> prevVisit;
        Set<Antigen> currVisit = visit();

        Multiset<Integer> visitCounts = HashMultiset.create();
        Multiset<Integer> revisitCounts = HashMultiset.create();

        for (int trialIndex = 0; trialIndex < trialCount; ++trialIndex) {
            prevVisit = currVisit;
            currVisit = visit();
            revisited = SetUtil.intersection(currVisit, prevVisit);

            visitCounts.add(currVisit.size());
            revisitCounts.add(revisited.size());
        }

        visitDist = EmpiricalDiscreteDistribution.compute(visitCounts);
        revisitDist = EmpiricalDiscreteDistribution.compute(revisitCounts);
    }

    private Set<Antigen> visit() {
        return new HashSet<Antigen>(visitationModel.visit(gcCycle, antigenPool));
    }

    private void report() {
        int minCount = 0;
        int maxCount = visitDist.support().upper();

        System.out.println("Count, VisitProb, RevisitProb");

        for (int count = minCount; count <= maxCount; ++count)
            System.out.println(String.format("%5d, %9.6f, %11.6f", count, 
                                             visitDist.pdf(count), revisitDist.pdf(count)));

        System.out.println(String.format(" MEAN, %9.6f, %11.6f", visitDist.mean(), revisitDist.mean()));
        System.out.println(String.format("STERR, %9.6f, %11.6f", visitDist.sterr(), revisitDist.sterr()));
    }

    public static void main(String[] args) {
        run(args);
    }
}
