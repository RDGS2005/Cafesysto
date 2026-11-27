# ---------- Etapa 1: compilar ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cachear dependencias de Maven
COPY pom.xml .
RUN mvn -B -q dependency:go-offline -Dexec.skip=true

# Compilar (exec.skip para omitir las pruebas)
COPY src ./src
RUN mvn -B -q package -DskipTests -Dexec.skip=true

# ---------- Etapa 2: ejecutar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --no-create-home appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]