
package com.example.lineage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class DynatraceSimService {

    public List<Map<String, Object>> loadRawEntities() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("dynatrace-sim/entities.json").getInputStream();
            return mapper.readValue(is, List.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
