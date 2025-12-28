package com.example.lineage.ingest;

import java.util.List;
import java.util.Map;

public interface LineageSource {
    String sourceName();
    List<Map<String, Object>> ingestRaw();

}
