
package amat.vaccine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.util.RecursiveList;

final class VaccineFootprintList extends RecursiveList<List<Vaccine>> {
    private final VaccinationSchedule schedule;

    VaccineFootprintList(VaccinationSchedule schedule) {
        super();
        this.schedule = schedule;
    }

    @Override protected List<Vaccine> compute(int index) {
        List<Vaccine> footprint = new ArrayList<Vaccine>();
        VaccinationEvent event = schedule.eventOn(index);
            
        if (event != null)
            footprint.add(event.getVaccine());
            
        if (index > 0)
            footprint.addAll(get(index - 1));

        return Collections.unmodifiableList(footprint);
    }
}
