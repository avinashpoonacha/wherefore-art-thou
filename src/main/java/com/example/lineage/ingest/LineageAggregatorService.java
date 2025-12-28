package com.example.lineage.ingest;

import com.example.lineage.service.LineagePersistenceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LineageAggregatorService {

    private final List<LineageSource> sources;
    private final LineagePersistenceService persistenceService;

    public LineageAggregatorService(
            List<LineageSource> sources,
            LineagePersistenceService persistenceService) {
        this.sources = sources;
        this.persistenceService = persistenceService;
    }

    public void aggregate() {

        List<Map<String, Object>> aggregated = new ArrayList<>();

        for (LineageSource source : sources) {
            aggregated.addAll(source.ingestRaw());
        }

        persistenceService.persistAll(aggregated);
    }
}
