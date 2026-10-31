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

# JRE a medida con jlink: solo los módulos de Java que usa la aplicación
# (calculados con jdeps sobre la aplicación y sus dependencias)
RUN jlink \
      --add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql.rowset,jdk.crypto.ec,jdk.jfr,jdk.management,jdk.net,jdk.unsupported \
      --strip-debug --no-man-pages --no-header-files --compress=2 \
      --output /opt/jre

# ---------- Etapa 2: ejecución ----------
FROM ubuntu:noble
WORKDIR /app

ENV JAVA_HOME=/opt/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=build /opt/jre /opt/jre

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

# La imagen no incluye curl ni wget: el chequeo usa bash para pedir el endpoint de salud
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=3 \
    CMD ["bash", "-c", "exec 3<>/dev/tcp/127.0.0.1/8080 && printf 'GET /actuator/health/liveness HTTP/1.1\\r\\nHost: localhost\\r\\nConnection: close\\r\\n\\r\\n' >&3 && grep -q '\"status\":\"UP\"' <&3"]

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
