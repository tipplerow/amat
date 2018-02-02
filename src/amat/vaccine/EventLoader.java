
package amat.vaccine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jam.io.FileParser;

final class EventLoader extends FileParser {
    private final List<VaccinationEvent> events = new ArrayList<VaccinationEvent>();

    private static final Pattern COMMENT_PATTERN = Pattern.compile("#");

    EventLoader(File file) {
        super(file, COMMENT_PATTERN);
    }

    @Override protected void processLine(String dataLine) {
        events.add(VaccinationEvent.parse(dataLine));
    }

    VaccinationSchedule makeSchedule() {
        return new VaccinationSchedule(events);
    }
}
