
package com.example.lineage.controller;

import com.example.lineage.ingest.LineageAggregatorService;
import com.example.lineage.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class LineageController {

  private final LineageAggregatorService aggregatorService;

    private final LineageQueryService queryService;

    public LineageController(LineageAggregatorService aggregatorService,
                             LineageQueryService queryService) {
        this.aggregatorService = aggregatorService;
        this.queryService = queryService;
    }

    @GetMapping("/lineage/ingest")
    public String ingest() {
        aggregatorService.aggregate();
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
