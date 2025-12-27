
package com.example.lineage.controller;

import com.example.lineage.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class LineageController {

    private final DynatraceSimService simService;
    private final LineagePersistenceService persistenceService;
    private final LineageQueryService queryService;

    public LineageController(DynatraceSimService simService,
                             LineagePersistenceService persistenceService,
                             LineageQueryService queryService) {
        this.simService = simService;
        this.persistenceService = persistenceService;
        this.queryService = queryService;
    }

    @GetMapping("/lineage/ingest")
    public String ingest() {
        persistenceService.persistAll(simService.loadRawEntities());
        return "Ingested application + data + messaging lineage";
    }

    @GetMapping("/lineage/blast-radius/{nodeId}")
    public List<Map<String, Object>> blastRadius(@PathVariable String nodeId) {
        return queryService.blastRadius(nodeId);
    }

    @GetMapping("/lineage/recovery-order")
    public List<Map<String, Object>> recoveryOrder() {
        return queryService.recoveryOrder();
    }
}
