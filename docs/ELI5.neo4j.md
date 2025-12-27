ELI5: What is Neo4j?

Neo4j is a graph database — instead of storing data in tables like a relational database, it stores data as nodes (things) and relationships (connections between things). This makes it great for modeling networks: social graphs, dependencies, call graphs, and more.

Key concepts
- Node: an entity with key-value properties and optional labels (like types).
- Relationship: a directed connection between two nodes with a type and optional properties.
- Label: a category for nodes (e.g., `Application`).
- Cypher: Neo4j's query language (similar to SQL but designed for graphs).

How this project models data
- Each service/application is an `Application` node with properties `nodeId` and `name`.
- Calls between services are represented by `CALLS` relationships. The relationship has a `critical` boolean property (default true).
- The sample data is in `src/main/resources/dynatrace-sim/entities.json`.

Cypher queries used in this project

1) Blast radius (impact analysis)

Query used in `LineageQueryService.blastRadius`:

MATCH (n {nodeId:$id})<-[:CALLS*]-(impacted) RETURN impacted.nodeId AS nodeId

Explanation (step-by-step):
- MATCH (n {nodeId:$id}): find the node whose `nodeId` equals the supplied parameter `id`.
- <-[:CALLS*]-(impacted): follow incoming `CALLS` relationships of any length (the `*` quantifier). The arrow `<-` means we traverse relationships pointing to `n`, so we find nodes that call ... -> n. In other words, nodes that depend on `n`.
- This finds all callers (direct and indirect) that would be impacted if `n` fails — the "blast radius".
- RETURN impacted.nodeId AS nodeId: return the `nodeId` for each impacted node.

Notes and variants:
- To limit to direct callers only, remove the `*` (MATCH (n {nodeId:$id})<-[:CALLS]-(impacted)).
- To include the distance (how many hops away an impacted node is), use a path variable and length functions, e.g.:
  MATCH p=(impacted)-[:CALLS*1..]->(n {nodeId:$id})
  RETURN impacted.nodeId AS nodeId, length(p) AS hops
- To respect `critical=false` relationships, add a WHERE clause filtering relationship property using pattern comprehension or UNWIND + relationships(p).

2) Recovery order

Query used in `LineageQueryService.recoveryOrder`:

MATCH p=(a:Application)-[:CALLS*]->(d) RETURN DISTINCT d.nodeId AS nodeId

Explanation:
- MATCH p=(a:Application)-[:CALLS*]->(d): find any path starting at an `Application` node and following outgoing `CALLS` relationships of any length to destination nodes `d`.
- RETURN DISTINCT d.nodeId AS nodeId: return the distinct destination nodeIds. The intention here is to list nodes that are dependencies (downstream targets), but note the query as written returns the set of nodes that are at the end of some CALLS path — it doesn't explicitly order a recovery sequence.

Notes and improvements for a realistic "recovery order":
- A true recovery order should consider dependencies and produce a topological sort (start with nodes with no outgoing CALLS or nodes that nothing depends on first). Cypher can approximate this but often it's simpler to load the graph and perform a topological sort in application code.
- Example to return nodes with their in-degree (how many callers):
  MATCH (n:Application)
  OPTIONAL MATCH (x)-[:CALLS]->(n)
  RETURN n.nodeId AS nodeId, count(x) AS callers
  ORDER BY callers DESC

- If you want the reverse (start with least dependent), order by callers ASC.

How the Java mapping maps to Neo4j
- `ApplicationNode` class is annotated with `@Node("Application")`, so instances become nodes with label `Application`.
- `CallsRelationship` is a `@RelationshipProperties` class; the `@TargetNode` points to the called `ApplicationNode` and stores `critical` as a relationship property.
- When `DynatraceSimService` persists nodes and calls, it constructs `ApplicationNode` objects and adds `CallsRelationship` entries via `addCall(...)`.

Security and performance notes
- For large graphs, avoid MATCH with unbounded variable-length patterns without limits; they can be expensive. Prefer bounded ranges, filtering, and pagination.
- Use indexes on frequently queried properties (e.g., `nodeId`). With Neo4j, create an index: `CREATE INDEX FOR (n:Application) ON (n.nodeId)`.

Further reading
- Neo4j Cypher ref: https://neo4j.com/docs/cypher-refcard/current/
- Spring Data Neo4j: https://spring.io/projects/spring-data-neo4j

