# syntax=docker/dockerfile:1

FROM eclipse-temurin:11-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
#RUN ./mvnw dependency:resolve
COPY src ./src
RUN ./mvnw clean package --settings .mvn/maven-settings.xml

FROM eclipse-temurin:11
# Set default locations for dictionaries
ENV POLAR_DISEASEDICTIONARY=/resources/disease_dict.tsv
ENV POLAR_MEDICATIONDICTIONARY=/resources/medication_dict.tsv
WORKDIR /app
COPY --from=build /app/target/polar-pipeline-webserver-*.jar /app/polar-pipeline-webserver.jar
CMD ["java", "-jar", "/app/polar-pipeline-webserver.jar"]