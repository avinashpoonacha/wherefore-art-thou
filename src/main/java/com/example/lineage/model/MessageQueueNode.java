
package com.example.lineage.model;

import org.springframework.data.neo4j.core.schema.*;

@Node("MessageQueue")
public class MessageQueueNode {

    @Id
    private String nodeId;
    private String name;

    public MessageQueueNode() {}
    public MessageQueueNode(String nodeId, String name) {
        this.nodeId = nodeId;
        this.name = name;
    }
}
