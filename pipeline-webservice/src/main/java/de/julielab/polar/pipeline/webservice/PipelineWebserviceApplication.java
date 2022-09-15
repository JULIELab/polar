package de.julielab.polar.pipeline.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is a standard spring boot application. The only controller is {@link PipelineController} that offers
 * the functionality of the application. That functionality is the extraction of co-occurring disease and medication
 * entities in the input texts. The output consists of pairs of disease and medication expressions with some
 * additional information like text position and context of appearance.
 * @see PipelineController
 */
@SpringBootApplication
public class PipelineWebserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PipelineWebserviceApplication.class, args);
	}

}
