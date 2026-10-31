# syntax=docker/dockerfile:1

# ---------- Etapa 1: compilación ----------
FROM eclipse-temurin:17-jdk-noble AS build
WORKDIR /app

# Copiar primero solo lo necesario para resolver dependencias (aprovecha la caché de capas)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src/ src/
# Las pruebas ya se ejecutan en el pipeline antes de construir la imagen
RUN ./mvnw -B -q package -DskipTests \
    && java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination target/extracted

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:17-jre-noble
WORKDIR /app

# Usuario sin privilegios
RUN groupadd --system --gid 10001 ecomarket \
    && useradd --system --uid 10001 --gid ecomarket --no-create-home ecomarket

# Capas de Spring Boot: de la que menos cambia a la que más
COPY --from=build /app/target/extracted/dependencies/ ./
COPY --from=build /app/target/extracted/spring-boot-loader/ ./
COPY --from=build /app/target/extracted/snapshot-dependencies/ ./
COPY --from=build /app/target/extracted/application/ ./

USER 10001
EXPOSE 8080

# La JVM respeta los límites de memoria del contenedor
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
