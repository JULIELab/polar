# JULIE Lab Analysis Pipeline for POLAR

In the context of the POLAR project the JULIE Lab offers a prototype of a text analysis pipeline for the automatic extraction of associations between a set of predefined conditions - fall events and delir states - with medications. Dictionaries have been tailored to recognize the corresponding entities in German clinical texts. Pairs of conditions and medications are the compiled from input texts and returned.

**NOTE:** The dictionaries created within the project are not part of this repository. Some resources used for the dictionaries prohibit their public distribution.

The pipeline is realized as a sequence of [JCoRe](https://github.com/JULIELab/jcore-base) components for the segmentation of inputs texts into sentences and tokens and the dictionary-based recognition of entities. This happens within a Java web service that offers a REST interface.

The web service is a [Spring Boot](https://spring.io/projects/spring-boot) application.

## Webservice Usage

The webservice can be run in multiple ways. When running the service, the locations of the entity dictionaries must be specified. There are several ways to do this. In the examples below, the respective configuration properties are specified inline as environment variables or command line arguments. If the path does not often change, a more comfortable alternative could be to put the property definitions into the `src/main/resources/application.properties` file. Note that when using environment variables, by convention the property names are upper-cased and dots are replaced by underscores. 

### As a development version with Maven

Use Maven to quickly run the application without the need to build JAR files:

`POLAR_DISEASEDICTIONARY=<path to condition dictionary> POLAR_MEDICATIONDICTIONARY=<path to medication dictionary> ./mvnw spring-boot:run`

### As a Java application

Compile the application into an executable JAR with the Maven command `mvn clean package`. The application can be run with a command like
```
java -jar target/polar-pipeline-webserver-0.0.1-SNAPSHOT.jar --polar.diseasedictionary=<path to condition dictionary> --polar.medicationdictionary=<path to medication dictionary>
```


## Service configuration

* POLAR_NUMCONCURRENTPIPELINES: maximum number of pipelines run in parallel
* POLAR_DISEASEDICTIONARY: File path to the dictionary that contains the condition terms.
* POLAR_MEDICATIONDICTIONARY: File path to the dictionary that contains the medication terms.