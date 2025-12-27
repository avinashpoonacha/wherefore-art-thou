
package com.example.lineage.model;

import org.springframework.data.neo4j.core.schema.*;
import java.util.*;

@Node("Application")
public class ApplicationNode {

    @Id
    private String nodeId;

    private String name;

    @Relationship(type = "CALLS")
    private Set<CallsRelationship> calls = new HashSet<>();

    public ApplicationNode() {}

    public ApplicationNode(String nodeId, String name) {
        this.nodeId = nodeId;
        this.name = name;
    }

    public void addCall(ApplicationNode target) {
        calls.add(new CallsRelationship(target));
    }

    public String getNodeId() { return nodeId; }
    public String getName() { return name; }
}
