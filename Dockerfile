# ---- Build Frontend ----
FROM node:18 AS angular-build
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build --prod

# ---- Build Backend ----
FROM maven:3.9.4-amazoncorretto-17 AS spring-build
WORKDIR /backend
COPY backend/pom.xml ./
RUN mvn -e -B dependency:resolve dependency:resolve-plugins
COPY backend/src ./src
COPY --from=angular-build /frontend/dist/frontend /backend/src/main/resources/static
RUN mvn -e -B clean package

# ---- Create Image ----
FROM openjdk:18
WORKDIR /app
COPY --from=spring-build /backend/target/crossng-devtools.jar /app/crossng-devtools.jar
ENTRYPOINT ["java", "-jar", "crossng-devtools.jar"]