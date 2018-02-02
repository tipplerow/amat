
package amat.report;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import jam.app.JamProperties;
import jam.io.IOUtil;

import amat.driver.AmatDriver;

/**
 * Records the system properties that were set during the simulation.
 */
public final class PropertyReport extends AmatReport {
    private final Map<String, String> propertyMap = new TreeMap<String, String>();

    private PropertyReport() {}

    private static PropertyReport instance = null;

    /**
     * The system property with this name must be {@code true} to
     * schedule the report for execution.
     */
    public static final String RUN_PROPERTY = "amat.PropertyReport.run";

    /**
     * Base name of the report file.
     */
    public static final String REPORT_NAME = "system-prop.txt";

    /**
     * Returns the single report instance.
     *
     * @return the single report instance.
     */
    public static PropertyReport instance() {
        if (instance == null)
            instance = new PropertyReport();

        return instance;
    }

    /**
     * Runs the system property report if the system property 
     * {@link PropertyReport#RUN_PROPERTY} is {@code true}.
     */
    public static void run() {
        if (runRequested())
            instance().report();
    }

    private static boolean runRequested() {
        return JamProperties.getOptionalBoolean(RUN_PROPERTY, false);
    }

    private void report() {
        assemble();
        PrintWriter writer = openWriter(REPORT_NAME);

        for (Map.Entry<String, String> entry : propertyMap.entrySet())
            writer.println(String.format("%-50s = %s", entry.getKey(), entry.getValue()));

        IOUtil.close(writer);
    }

    private void assemble() {
        Properties properties = System.getProperties();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key   = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (reportProperty(key))
                propertyMap.put(key, value);
        }
    }

    private boolean reportProperty(String key) {
        return key.startsWith("jam.") || key.startsWith("amat.");
    }
}
