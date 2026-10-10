# syntax=docker/dockerfile:1
#
# Imagen del backend para Render. Se construye desde la raíz del repo porque
# el build genera código a partir de api/openapi.yaml:
#
#   docker build -f backend/Dockerfile -t portfolio-backend .

# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-25 AS build

# El pom exige un toolchain JDK 25 (maven-toolchains-plugin). Dentro del contenedor
# se declara el JDK de la propia imagen como versión 25. Este fichero no comprueba
# qué JDK hay realmente en $JAVA_HOME: si la imagen dejara de traer un JDK 25, lo que
# haría fallar el build es el compilador (--release 25 no existe en JDKs anteriores).
RUN mkdir -p /root/.m2 && cat > /root/.m2/toolchains.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>25</version>
    </provides>
    <configuration>
      <jdkHome>${JAVA_HOME}</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
EOF

WORKDIR /workspace/backend

# Dependencias en una capa propia: solo se rehace si cambia el pom o el wrapper.
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# El contrato va en ../api, la misma ruta relativa que usa inputSpec en el pom.
COPY api/openapi.yaml /workspace/api/openapi.yaml
COPY backend/src/ src/

# Los tests (Testcontainers) necesitan Docker; se ejecutan en CI, no aquí.
RUN ./mvnw -B -q -DskipTests package \
    && cp target/portfolio-*.jar /workspace/app.jar

# ---------- Runtime ----------
FROM eclipse-temurin:25-jre

RUN useradd --system --no-create-home --shell /usr/sbin/nologin app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER app

# Ajustes para contenedores pequeños (512 MB en Render). Se pueden sobrescribir
# desde Render definiendo JAVA_TOOL_OPTIONS sin reconstruir la imagen.
# Medido con 512 MB: fuera del heap la JVM ocupa ~220 MB (metaspace, code cache,
# hilos, GC, malloc). Con el heap al 50 % (~256 MB) el peor caso cabe en 512 MB.
# MALLOC_ARENA_MAX limita las arenas de glibc, que inflan el RSS en contenedores
# (en la medición redujo el RSS ~55 MB).
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=50 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Xss512k" \
    MALLOC_ARENA_MAX=2

# Render inyecta PORT; la aplicación lo lee con server.port=${PORT:8080}.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
