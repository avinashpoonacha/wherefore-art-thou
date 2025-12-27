
package com.example.lineage.repository;

import com.example.lineage.model.DataAssetNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DataAssetRepository extends Neo4jRepository<DataAssetNode, String> {}
