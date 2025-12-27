
package com.example.lineage.repository;

import com.example.lineage.model.MessageQueueNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MessageQueueRepository extends Neo4jRepository<MessageQueueNode, String> {}
