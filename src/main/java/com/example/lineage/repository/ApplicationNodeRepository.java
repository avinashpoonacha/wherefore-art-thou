package com.example.lineage.repository;

import com.example.lineage.model.ApplicationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ApplicationNodeRepository extends Neo4jRepository<ApplicationNode, String> {
}
