package com.example.paymentoptimization.service;

import com.example.paymentoptimization.config.BranchConfig;
import com.example.paymentoptimization.config.EdgeConfig;
import com.example.paymentoptimization.model.Graph;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class GraphInitializer {

    private final Graph graph;
    private final BranchConfig branchConfig;
    private final EdgeConfig edgeConfig;

    public GraphInitializer(Graph graph, BranchConfig branchConfig, EdgeConfig edgeConfig) {
        this.graph = graph;
        this.branchConfig = branchConfig;
        this.edgeConfig = edgeConfig;
    }

    @PostConstruct
    public void init() {
        for (BranchConfig.Branch branch : branchConfig.getBranches()) {
            graph.addBranch(branch.getName(), branch.getCost());
        }

        for (EdgeConfig.Edge edge : edgeConfig.getEdges()) {
            graph.addEdge(edge.getFrom(), edge.getTo());
        }
    }
}
