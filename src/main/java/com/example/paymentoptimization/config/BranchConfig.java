package com.example.paymentoptimization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "branches-config")
@Data
public class BranchConfig {
    private List<Branch> branches;

    @Data
    public static class Branch {
        private String name;
        private int cost;
    }
}
