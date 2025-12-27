package com.example.lineage.model;

import java.util.Map;

public record LineageNode(
        String nodeId,
        String nodeType,
        String name,
        Map<String, Object> attributes
) {}
