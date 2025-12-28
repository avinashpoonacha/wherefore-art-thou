package com.example.lineage.ingest;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ManualLineageSource implements LineageSource {

    @Override
    public String sourceName() {
        return "manual";
    }

    @Override
    public List<Map<String, Object>> ingestRaw() {
        return List.of(
                Map.of(
                        "name", "legacy-billing",
                        "readsFrom", List.of("billing-db")
                )
        );
    }
}

