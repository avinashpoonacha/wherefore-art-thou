# wherefore-art-thou — Quick Start

This is a small Spring Boot application that demonstrates ingesting synthetic Dynatrace-style entities into Neo4j and running lineage queries (blast radius and recovery order).

Prerequisites
- Java 21
- Maven
- A running Neo4j instance (default config in `src/main/resources/application.yml` points to bolt://localhost:7687 with username `neo4j` and password `password`).

Quick start (recommended: Docker)

1. Start Neo4j using Docker (creates a local DB with default credentials):

```bash
# Starts Neo4j Community with default user/password 'neo4j' / 'password'
docker run --rm -p7474:7474 -p7687:7687 -e NEO4J_AUTH=neo4j/password neo4j:5.13
```

2. Build and run the app from the project root:

```bash
mvn -DskipTests package
java -jar target/wherefore-art-thou-0.0.1-SNAPSHOT.jar
# or during development
mvn spring-boot:run
```

3. Ingest demo entities and create CALLS relationships

The project ships a small dataset at `src/main/resources/dynatrace-sim/entities.json`. To load it into Neo4j via the app:

```bash
# HTTP GET to trigger ingest
curl -s http://localhost:8080/lineage/ingest
# Response: "Ingested nodes and CALLS relationships"
```

4. Query the app

- Blast radius (find services that would be impacted by an upstream failure):

```bash
curl -s "http://localhost:8080/lineage/blast-radius/<nodeId>"
```

- Recovery order (a suggested order to recover dependent services):

```bash
curl -s http://localhost:8080/lineage/recovery-order
```

Notes
- The app reads Neo4j connection settings from `src/main/resources/application.yml`.
- Java version is set to 21 in `pom.xml`.
- If you change Neo4j credentials or host, update `application.yml` or set equivalent Spring Boot properties.

Troubleshooting
- If you get authentication errors, ensure the Neo4j container is started with `NEO4J_AUTH=neo4j/password` or update the password in `application.yml`.
- If queries return empty lists after ingesting, open the Neo4j Browser (http://localhost:7474) and inspect the nodes/relationships.

Next steps
- Add integration tests that run against a test Neo4j container.
- Improve queries and add pagination for large result sets.


