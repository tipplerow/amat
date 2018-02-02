
package amat.vaccine;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.util.RecursiveList;

import amat.antigen.Antigen;

final class AntigenFootprintList extends RecursiveList<Set<Antigen>> {
    private final VaccinationSchedule schedule;

    AntigenFootprintList(VaccinationSchedule schedule) {
        super();
        this.schedule = schedule;
    }

    @Override protected Set<Antigen> compute(int index) {
        Set<Antigen> antigenFootprint = new TreeSet<Antigen>();
        List<Vaccine> vaccineFootprint = schedule.getVaccineFootprint(index);
            
        for (Vaccine vaccine : vaccineFootprint)
            antigenFootprint.addAll(vaccine.getAntigens());
            
        return Collections.unmodifiableSet(antigenFootprint);
    }
}
