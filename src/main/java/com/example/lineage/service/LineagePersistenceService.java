
package com.example.lineage.service;

import com.example.lineage.model.*;
import com.example.lineage.repository.*;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineagePersistenceService {

    private final ApplicationNodeRepository appRepo;
    private final DataAssetRepository dataRepo;
    private final MessageQueueRepository queueRepo;
    private final Neo4jClient neo4j;

    public LineagePersistenceService(ApplicationNodeRepository appRepo,
                                     DataAssetRepository dataRepo,
                                     MessageQueueRepository queueRepo,
                                     Neo4jClient neo4j) {
        this.appRepo = appRepo;
        this.dataRepo = dataRepo;
        this.queueRepo = queueRepo;
        this.neo4j = neo4j;
    }

    public void persistAll(List<Map<String, Object>> raw) {

        raw.forEach(e -> {
            String name = (String) e.get("name");
            appRepo.save(new ApplicationNode("application:" + name + ":prod", name));
        });

        raw.forEach(e -> {
            persistDataAssets((List<String>) e.get("readsFrom"));
            persistDataAssets((List<String>) e.get("writesTo"));
            persistQueues((List<String>) e.get("publishesTo"));
            persistQueues((List<String>) e.get("consumesFrom"));
        });

        raw.forEach(e -> {
            String appId = "application:" + e.get("name") + ":prod";
            linkData(appId, (List<String>) e.get("readsFrom"), "READS_FROM");
            linkData(appId, (List<String>) e.get("writesTo"), "WRITES_TO");
            linkQueue(appId, (List<String>) e.get("publishesTo"), "PUBLISHES_TO");
            linkQueue(appId, (List<String>) e.get("consumesFrom"), "CONSUMES_FROM");
        });
    }

    private void persistDataAssets(List<String> assets) {
        if (assets == null) return;
        for (String a : assets) {
            dataRepo.save(new DataAssetNode("data:" + a + ":prod", a, "SOR"));
        }
    }

    private void persistQueues(List<String> queues) {
        if (queues == null) return;
        for (String q : queues) {
            queueRepo.save(new MessageQueueNode("queue:" + q + ":prod", q));
        }
    }

    private void linkData(String appId, List<String> assets, String rel) {
        if (assets == null) return;
        for (String a : assets) {
            neo4j.query("MATCH (a:Application {nodeId:$app}), (d:DataAsset {nodeId:$data}) " +
                        "MERGE (a)-[r:" + rel + "]->(d)")
                  .bind(appId).to("app")
                  .bind("data:" + a + ":prod").to("data")
                  .run();
        }
    }

    private void linkQueue(String appId, List<String> queues, String rel) {
        if (queues == null) return;
        for (String q : queues) {
            neo4j.query("MATCH (a:Application {nodeId:$app}), (q:MessageQueue {nodeId:$queue}) " +
                        "MERGE (a)-[r:" + rel + "]->(q)")
                  .bind(appId).to("app")
                  .bind("queue:" + q + ":prod").to("queue")
                  .run();
        }
    }
}
