
package com.example.lineage.model;

import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
public class CallsRelationship {

    @Id @GeneratedValue
    private Long id;

    @TargetNode
    private ApplicationNode target;

    private boolean critical = true;

    public CallsRelationship() {}
    public CallsRelationship(ApplicationNode target) {
        this.target = target;
    }
}
