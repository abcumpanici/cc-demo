# Phase 5: Dashboard and README (Simplified)

```
Add a simple web dashboard and project documentation.

DASHBOARD:

Create DashboardController with GET /dashboard endpoint.

Create dashboard.html Thymeleaf template:
- Shows recent job runs (job id, start time, end time, status, duration)
- Shows processed files list (filename, time, status, record count)
- Auto-refresh every 5 seconds
- Simple table styling with status colors (green for success, red for failed)

README:

Create README.md with:

1. Project title and short description

2. Architecture diagram using Mermaid showing:
   Kafka -> Spring Boot App -> PostgreSQL
   File system interaction for input/processed/failed folders

3. Tech stack list

4. Prerequisites: Java 21, Maven, Docker

5. Quick start steps:
   - docker-compose up -d
   - mvn spring-boot:run
   - ./scripts/generate-test-data.sh --rows 100
   - ./scripts/send-notification.sh --filename batch_001.csv
   - Open http://localhost:8081/dashboard
   - Check pgAdmin at http://localhost:5050

6. How to run tests: mvn test, mvn verify

7. Project folder structure
```