package com.example.lineage.ingest;


import com.example.lineage.service.DynatraceSimService;
import org.springframework.stereotype.Component;

@Component
public class DynatraceSimLineageSource  implements LineageSource {

    private final DynatraceSimService simService;

    public DynatraceSimLineageSource(DynatraceSimService simService) {
        this.simService = simService;
    }

    @Override
    public String sourceName() {
        return "dynatrace-sim";
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> ingestRaw() {
        // Simulated ingestion logic

        return simService.loadRawEntities();
    }
}
