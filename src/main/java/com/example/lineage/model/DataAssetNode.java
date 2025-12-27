
package com.example.lineage.model;

import org.springframework.data.neo4j.core.schema.*;

@Node("DataAsset")
public class DataAssetNode {

    @Id
    private String nodeId;
    private String name;
    private String role;

    public DataAssetNode() {}
    public DataAssetNode(String nodeId, String name, String role) {
        this.nodeId = nodeId;
        this.name = name;
        this.role = role;
    }
}
