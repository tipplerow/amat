
package amat.vaccine;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import jam.util.RecursiveList;

import amat.antigen.Antigen;
import amat.epitope.Epitope;

final class EpitopeFootprintList extends RecursiveList<Set<Epitope>> {
    private final VaccinationSchedule schedule;

    EpitopeFootprintList(VaccinationSchedule schedule) {
        super();
        this.schedule = schedule;
    }

    @Override protected Set<Epitope> compute(int index) {
        Set<Epitope> epFootprint = new TreeSet<Epitope>();
        Set<Antigen> agFootprint = schedule.getAntigenFootprint(index);

        for (Antigen antigen : agFootprint)
            epFootprint.addAll(antigen.viewEpitopes());

        return Collections.unmodifiableSet(epFootprint);
    }
}
