# ---- Build stage ----
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# ---- Runtime stage ----
FROM amazoncorretto:21.0.5
WORKDIR /app
COPY --from=build ./build/target/*.jar ./app.jar

COPY start.sh ./start.sh
RUN chmod +x ./start.sh

ENV TZ=America/Sao_Paulo
ENV SPRING_PROFILES_ACTIVE="dev"

EXPOSE 8081

ENTRYPOINT ["./start.sh"]
