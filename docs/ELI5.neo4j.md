ELI5: What is Neo4j?

Neo4j is a graph database. Instead of storing data as rows and tables (like a relational database), Neo4j stores data as nodes and relationships. Think of it like a whiteboard where each thing is a circle (a node) and arrows between circles are relationships. This makes it very fast and natural to model connected data — for example, services that call other services.

Key concepts (very simple):
- Node: an entity (e.g., an Application node representing a service).
- Relationship: a directed connection between two nodes (e.g., A CALLS B).
- Property: key/value attached to nodes or relationships (e.g., nodeId: "service-1").
- Cypher: Neo4j's query language (readable, pattern-based). Example: `MATCH (a)-[:CALLS]->(b) RETURN a, b`.

Why Neo4j here?
This project models application call relationships as a graph so you can easily query impact (blast radius) and recovery ordering using graph traversal (variable-length paths).

Deep dive: the Cypher queries used in this project

There are two simple queries used in `LineageQueryService`.

1) Blast radius

Cypher used:

```
MATCH (n {nodeId:$id})<-[:CALLS*]-(impacted) RETURN impacted.nodeId AS nodeId
```

Explanation (line-by-line):
- `MATCH (n {nodeId:$id})` — find a node whose `nodeId` property matches the parameter `$id` (the service you care about).
- `<-[:CALLS*]-(impacted)` — traverse any number (1..N) of incoming `CALLS` relationships to find nodes that call `n` (the direction is `caller -> callee`, so `<-[:CALLS*]-` walks from callers toward the callee). The `*` means "variable-length path": find direct callers, callers-of-callers, etc.
- `impacted` names the nodes found by the traversal — these are the services that (directly or indirectly) depend on `n` and therefore would be impacted if `n` failed.
- `RETURN impacted.nodeId AS nodeId` — return the `nodeId` property for each impacted node.

Notes & gotchas:
- This returns every impacted node found along any length path. If you want to limit depth, use `[:CALLS*1..3]`.
- The query as-written may return duplicates if there are multiple paths to the same impacted node; you can use `RETURN DISTINCT impacted.nodeId` to dedupe.

2) Recovery order

Cypher used:

```
MATCH p=(a:Application)-[:CALLS*]->(d) RETURN DISTINCT d.nodeId AS nodeId
```

Explanation:
- `MATCH p=(a:Application)-[:CALLS*]->(d)` — find any path starting at an `Application` node and following outgoing `CALLS` relationships (callers -> callees) to reach node `d`.
- `RETURN DISTINCT d.nodeId AS nodeId` — return distinct destination node IDs. In the app this acts as a basic way to enumerate downstream dependencies; however this simple form doesn't compute a strict topological recovery ordering. For a true recovery plan you'd typically run a topological sort on the dependency DAG.

Implementation notes in the Java service

- The Java code uses `Neo4jClient#query(...).bind(...).to(...).fetch().all()` to execute parameterized Cypher and retrieve all rows. The `fetch().all()` method returns a Collection<Map<String,Object>> where each map represents a row with column names as keys.
- The service methods return `List<Map<String,Object>>`. To satisfy that return type the code wraps the Collection in `new ArrayList<>(...)`.

Performance tips

- Add indexes for frequently matched properties (e.g., `nodeId`) to speed up `MATCH (n {nodeId:$id})` lookups.
- Be careful with `*` (variable-length) traversals on very large graphs — consider depth limits or using `shortestPath()` where appropriate.

How to experiment interactively

- Start Neo4j and open the browser on http://localhost:7474.
- Paste the Cypher queries above, replacing parameters (e.g., `$id`) with concrete values.

Further reading
- Neo4j Cypher documentation: https://neo4j.com/docs/cypher-manual/current/
- Neo4j concepts: https://neo4j.com/developer/graph-database/


