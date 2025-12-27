package com.example.lineage.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LineageQueryService {

    private final Neo4jClient neo4j;

    public LineageQueryService(Neo4jClient neo4j) {
        this.neo4j = neo4j;
    }

  /*  public List<Map<String, Object>> blastRadius(String nodeId) {
        return new java.util.ArrayList<>(neo4j.query(
            "MATCH (n {nodeId:$id})<-[:CALLS*]-(impacted) RETURN impacted.nodeId AS nodeId"
        ).bind(nodeId).to("id").fetch().all());
    }
*/

    public List<Map<String, Object>> blastRadius(String nodeId) {
        return new java.util.ArrayList<>( neo4j.query(
                "MATCH (n {nodeId:$id}) " +
                        "MATCH (n)<-[:CALLS|READS_FROM|WRITES_TO|PUBLISHES_TO|CONSUMES_FROM*]-(impacted) " +
                        "RETURN DISTINCT impacted.nodeId AS nodeId"
        ).bind(nodeId).to("id").fetch().all());
    }

    public List<Map<String, Object>> recoveryOrder() {
        return new java.util.ArrayList<>(neo4j.query(
            "MATCH p=(a:Application)-[:CALLS*]->(d) RETURN DISTINCT d.nodeId AS nodeId"
        ).fetch().all());
    }
}
