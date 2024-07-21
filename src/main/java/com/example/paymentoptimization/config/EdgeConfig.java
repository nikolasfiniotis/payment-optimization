package com.example.paymentoptimization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "edges-config")
@Data
public class EdgeConfig {
    private List<Edge> edges;

    @Data
    public static class Edge {
        private String from;
        private String to;
    }
}
