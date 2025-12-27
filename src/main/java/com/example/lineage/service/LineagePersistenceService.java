
package com.example.lineage.service;

import com.example.lineage.model.*;
import com.example.lineage.repository.ApplicationNodeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineagePersistenceService {

    private final ApplicationNodeRepository repository;

    public LineagePersistenceService(ApplicationNodeRepository repository) {
        this.repository = repository;
    }

    public void persistWithRelationships(List<Map<String, Object>> rawEntities) {

        Map<String, ApplicationNode> cache = new HashMap<>();

        for (Map<String, Object> e : rawEntities) {
            String name = (String) e.get("name");
            String nodeId = "application:" + name + ":prod";
            cache.put(nodeId, repository.save(new ApplicationNode(nodeId, name)));
        }

        for (Map<String, Object> e : rawEntities) {
            String sourceId = "application:" + e.get("name") + ":prod";
            ApplicationNode source = cache.get(sourceId);

            List<String> calls = (List<String>) e.get("calls");
            if (calls != null) {
                for (String targetName : calls) {
                    String targetId = "application:" + targetName + ":prod";
                    ApplicationNode target = cache.get(targetId);
                    if (target != null) {
                        source.addCall(target);
                    }
                }
                repository.save(source);
            }
        }
    }
}
