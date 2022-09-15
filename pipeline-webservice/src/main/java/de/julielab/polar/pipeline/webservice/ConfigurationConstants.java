package de.julielab.polar.pipeline.webservice;

/**
 * Constants for configurable values. Values are specified by setting environment variables that equal the field name (NOT the value name).
 * @see <url>https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/boot-features-external-config.html</url>, 24.5
 */
public class ConfigurationConstants {
    public static final String POLAR_NUMCONCURRENTPIPELINES = "polar.numconcurrentpipelines";
    public static final String POLAR_DISEASEDICTIONARY = "polar.diseasedictionary";
    public static final String POLAR_MEDICATIONDICTIONARY = "polar.medicationdictionary";

}
