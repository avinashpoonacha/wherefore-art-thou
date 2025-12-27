Quick start guide

This project is a Spring Boot application that demonstrates application service lineage stored in Neo4j using Spring Data Neo4j.

Prerequisites
- Java 21 JDK
- Maven 3.8+
- A running Neo4j database (local or remote)

Run locally
1. Build the jar:

```bash
mvn -DskipTests package
```

2. Run the app (set Neo4j connection in `src/main/resources/application.yml` or via environment variables):

```bash
java -jar target/wherefore-art-thou-0.0.1-SNAPSHOT.jar
```

Data import
- The project includes a sample JSON at `src/main/resources/dynatrace-sim/entities.json` with simple service definitions (id, name, calls).
- The `DynatraceSimService` reads the JSON and persists `Application` nodes and `CALLS` relationships into Neo4j.

API endpoints
- See `LineageController` in `src/main/java/com/example/lineage/controller/LineageController.java` for available endpoints. Two main operations provided by the project:
    - `blastRadius(nodeId)` — returns services that would be impacted by a failure of the given node.
    - `recoveryOrder()` — returns a suggested order of services for recovery based on dependency direction.

Notes
- This README assumes you will run a Neo4j instance and configure the connection; no embedded Neo4j is included.
- Update the `LICENSE` file to replace `[OWNER NAME]` and `[OWNER CONTACT INFORMATION]` with your details.

