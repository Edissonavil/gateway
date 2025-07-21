# Etapa de construcción
FROM openjdk:17-jdk-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src src

# Instalar Maven
RUN apk add --no-cache maven

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jre-alpine

WORKDIR /app

# Instalar curl para health checks
RUN apk --no-cache add curl

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Crear usuario no-root
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Cambiar propiedad del directorio
RUN chown -R spring:spring /app
USER spring:spring

# Exponer el puerto
EXPOSE 8080

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=railway
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XX:+UseG1GC"

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:${PORT}/gateway/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]