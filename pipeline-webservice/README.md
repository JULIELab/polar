# JULIE Lab Analysis Pipeline for POLAR

In the context of the [POLAR](https://www.medizininformatik-initiative.de/de/POLAR) project the JULIE Lab offers a prototype of a text analysis pipeline for the automatic extraction of associations between a set of predefined conditions - fall events and delir states - with textual mentions of medications. Dictionaries have been tailored to recognize the corresponding entities in German clinical texts. The application compiles pairs of conditions and medications occurring together from input texts and returns them.

**NOTE:** The dictionaries created within the project are not part of this repository. Some resources used for the dictionaries prohibit their public distribution.

The pipeline is realized as a sequence of [JCoRe](https://github.com/JULIELab/jcore-base) components for the segmentation of input texts into sentences and tokens and the dictionary-based recognition of entities. This happens within a Java web service that offers a REST interface.
The employed components are

- [A German medical sentence splitter](https://github.com/JULIELab/jcore-projects/tree/v2.6/jcore-jsbd-ae-medical-german)
- [A German medical tokenizer](https://github.com/JULIELab/jcore-projects/tree/v2.6/jcore-jtbd-ae-medical-german)
- [A dictionary-based entity recognizer](https://github.com/JULIELab/jcore-base/tree/v2.6/jcore-lingpipegazetteer-ae)

The web service is a [Spring Boot](https://spring.io/projects/spring-boot) application.

## Quickstart

The quickest way to start up the pipeline application is to use the official Docker image like this:
```
docker run --rm -p 8080:8080 -v /path/to/dictionary/dir:/resources julielab/polarpipeline:1.0.0-SNAPSHOT
```
where the condition and medication dictionaries must be present directly in the directory `/path/to/dictionary/dir`. The dictionary files must be named `disease_dict.tsv` and `medication_dict.tsv`, respectively.

## Web service Usage

The web service can be run in multiple ways. When running the service, the locations of the entity dictionaries must be specified. There are several ways to do this. In the examples below, the respective configuration properties are specified inline as environment variables or command line arguments. If the dictionary path does not often change, a more comfortable alternative could be to put the property definitions into the `src/main/resources/application.properties` file. Note that when using environment variables, by convention the property names are upper-cased and dots are replaced by underscores. The dictionary files must be named `disease_dict.tsv` and `medication_dict.tsv`, respectively.

The application offers a REST interface. It expects the input in JSON format and sends JSON responds. The input format is a list of input texts:

```json
[{"text": "Der Patient wurde mit einem Armbruch vorstellig. Es wurde Ibuprofen gegen die Schmerzen verschrieben.", "textId":  "4711"},{"text":  "...", "textId": "..."}]
```
There are two fields per input, `text` and `textId`. Both accept arbitrary strings. The value of the `text` property should be German medical text, undergoes linguistic analysis and serves as the basis for entity association extraction. The value of the `textId` property is used to identify the source document in the output.

The response format is the following:
```json
[
  {
    "doc_id":"4711",
    "medication_text":"Ibuprofen",
    "disease_text":"Armbruch",
    "medication_category":"Ibuprofen-product_list.txt|Ibuprofen-package_ext_list.txt|IBUPROFEN-package_list.txt|Ibuprofen-molecule_list.txt",
    "disease_category":"Bruch-hyponym-wkt|Bruch-hyponym-gmn",
    "medication_offset":58,
    "disease_offset":28,
    "token_distance":4,
    "coocurrence_type":"document",
    "context":"Der Patient wurde mit einem Armbruch vorstellig. Es wurde Ibuprofen gegen die Schmerzen verschrieben."
  }]
```

The `<entitytype>_text` properties show the input text portions that have been identified as entities by the dictionary lookup.

The `<entitytype>_category` properties disclose the exact source of the dictionary entry that led to the match. This is mostly important for debugging.

The `<entitytype>_offset` properties show where in the text the respective entity was found.

The `token_distance` property shows how many tokens - basically words - stand between the two entities that are subject of the response item.

The `coocurrence_type` property is either `sentence` or `document`. `sentence` means that the disease and medication entities of the response item were found in the same sentence. If they appear in different sentences, the value is `document``

The `context` property shows all sentences between the two entities, including the sentences that contain them.

Note that by default, the application will only process a single request at a time. To allow concurrency, set the `polar.numconcurrentpipelines` configuration parameter to a number larger than 1.

The next sections show how to start the application so that it is ready to accept and process input text.

### As a development version with Maven

Use Maven to quickly run the application without the need to build JAR files:

`POLAR_DISEASEDICTIONARY=<path to condition dictionary> POLAR_MEDICATIONDICTIONARY=<path to medication dictionary> ./mvnw spring-boot:run`

### As a Java application

Compile the application into an executable JAR with the Maven command `mvn clean package`. The application can be run with a command like
```
java -jar target/polar-pipeline-webserver-1.0.0-SNAPSHOT.jar --polar.diseasedictionary=<path to condition dictionary> --polar.medicationdictionary=<path to medication dictionary>
```

### As a Docker container

A Docker container with the pipeline application has been published to Docker Hub named `julielab/polarpipeline:1.0.0-SNAPSHOT`. Alternatively, this GitHub repository contains a Dockerfile that can be used to create a local Docker image. The next sections show how to use a Docker container as a pipeline service. A running Docker installation is required. The usage of the official Docker container has been tested on MacOS Bis Sur, Windows 10 and Debian Linux 4.19.

All commands specify the `--rm` option that will remove the container after it is stopped. Since the application does not have an internal state, it is not necessary to keep the container. The `-p 8080:8080` option maps the container-internal port 8080 to the host port 8080. The second number can be changed to use another host port. Finally, the `-v` option is required to mount the dictionaries into the container to allow the application access. The dictionaries must be placed directly into the given directory in the host file system.

All configuration variables can be set as environment variables using the `-e` switch in the command line. For example, to enable multithreading, use `-e POLAR_NUMCONCURRENTPIPELINES=4`.

#### Run the official Docker container from Docker Hub

On the command line, type
```
docker run --rm -p 8080:8080 -v /path/to/dictionary/dir:/resources julielab/polarpipeline:1.0.0-SNAPSHOT
```

This will download the official image, create and run a container. The web application is then available under port 8080.

#### With a Docker image built from the repository code

The `Dockerfile` in the repository allows to create a new, local Docker image from scratch. Run
```
docker build . -t mypolarwebapp:1.0.0-SNAPSHOT
```
to create a new image named `mypolarwebapp` with version `1.0.0-SNAPSHOT.`. Create and run a container using 
```
docker run --rm -p 8080:8080 -v /path/to/dictionary/dir:/resources mypolarwebapp:1.0.0-SNAPSHOT
```
just as with the official image.


### Testing the web service
On *nix-based systems, use cURL to send a test document:
```
curl -XPOST http://localhost:8080/pipeline -H 'Content-Type: application/json' -d '[{"text":"Der Patient wurde mit einem Armbruch vorstellig. Es wurde Ibuprofen gegen die Schmerzen verschrieben.","textId":4711}]'
```

On Windows, use the PowerShell like this:
```
PS> $inputText=ConvertTo-Json @(@{text="Der Patient wurde mit einem Armbruch vorstellig. Es wurde Ibuprofen gegen die Schmerzen verschrieben.";textId="4711"})
PS> Invoke-RestMethod -Method POST -ContentType "application/json" -uri http://localhost:8080/pipeline -Body $inputText
```
In both cases, a response should appear that contains a single association between `Armbruch` and `Ibuprofen`.

## Service configuration

These configuration parameters can be set for the application. When setting them as an environment variable, e.g. via the Docker `-e` switch, the variable name must be upper-cased and the dot must be replaced by a underscore ('_').

* polar.numconcurrentpipelines: Optional. Maximum number of pipelines that can be run in parallel. Defaults to 1.
* polar.diseasedictionary: Mandatory. File path to the dictionary that contains the condition terms.
* polar.medicationdictionary: Mandatory. File path to the dictionary that contains the medication terms.
