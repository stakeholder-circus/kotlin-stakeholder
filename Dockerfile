FROM gradle:9.4.1-jdk17 AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew gradlew
COPY settings.gradle.kts build.gradle.kts ./
COPY src src
RUN chmod +x gradlew && ./gradlew --no-daemon ktlintCheck test installDist

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/install/kotlin-stakeholder /app/kotlin-stakeholder
ENTRYPOINT ["/app/kotlin-stakeholder/bin/kotlin-stakeholder"]
