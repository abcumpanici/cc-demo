Create a Java 21 Spring Boot 3.3.x Maven project.

Group: com.example
Artifact: file-processor  
Package: com.example.fileprocessor

Add these dependencies: spring-boot-starter-batch, spring-boot-starter-data-jpa, spring-boot-starter-web, spring-kafka, postgresql, flyway-core, lombok, testcontainers

Create docker-compose.yml with:
- PostgreSQL 16 on port 5432 (db: fileprocessor, user: demo, pass: demo)
- Kafka on port 9092 with kafka-ui on port 8080
- pgAdmin on port 5050

Create basic application.yml with datasource and kafka config.
Server port 8081.