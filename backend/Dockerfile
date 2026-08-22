FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy

RUN groupadd --system dayflow \
    && useradd --system --gid dayflow --home-dir /app dayflow

WORKDIR /app
COPY --from=build /workspace/target/dayflow-backend-0.0.1-SNAPSHOT.jar app.jar

USER dayflow
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0 -XX:+UseSerialGC"
EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
