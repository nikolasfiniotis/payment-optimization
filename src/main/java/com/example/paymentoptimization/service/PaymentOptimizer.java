package com.example.paymentoptimization.service;

import com.example.paymentoptimization.model.Branch;
import com.example.paymentoptimization.model.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentOptimizer implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentOptimizer.class);
    private final Graph graph;

    public PaymentOptimizer(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String processPayment(String originBranch, String destinationBranch) {
        logger.info("Processing payment from {} to {}", originBranch, destinationBranch);

        PriorityQueue<Branch> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Branch::getCost));
        Map<String, Integer> distances = new ConcurrentHashMap<>();
        Map<String, String> previousBranch = new ConcurrentHashMap<>();
        Set<String> visited = Collections.newSetFromMap(new ConcurrentHashMap<>()); // to avoid processing again the same branch

        distances.put(originBranch, 0);
        priorityQueue.add(new Branch(originBranch, 0));

        while (!priorityQueue.isEmpty()) {
            Branch current = priorityQueue.poll();
            if (!visited.add(current.getName())) { // continue if branch already checked
                continue;
            }

            if (current.getName().equals(destinationBranch)) {
                String path = createPath(previousBranch, destinationBranch);
                logger.info("Found path: {}", path);
                return path;
            }

            for (String neighbor : graph.getNeighborList(current.getName())) {
                int newCost = current.getCost() + graph.getBranch(current.getName()).getCost();
                if (newCost < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newCost);
                    previousBranch.put(neighbor, current.getName());
                    priorityQueue.add(new Branch(neighbor, newCost));
                }
            }
        }

        logger.warn("No valid path found from {} to {}", originBranch, destinationBranch);
        return null;  // If there is no path return null
    }

    @Override
    public boolean isInvalidBranch(String branch) {
        boolean invalid = graph.getBranch(branch) == null;
        if (invalid) {
            logger.warn("Invalid branch: {}", branch);
        }
        return invalid;
    }

    @Async
    @Override
    public CompletableFuture<Void> addBranchAsync(String name, int cost) {
        return CompletableFuture.runAsync(() -> {
            if (graph.getBranch(name) != null) {
                throw new RuntimeException("Branch already exists");
            }
            graph.addBranch(name, cost);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> addEdgeAsync(String from, String to) {
        return CompletableFuture.runAsync(() -> {
            if (isInvalidBranch(from) || isInvalidBranch(to)) {
                throw new RuntimeException("One or both branches do not exist");
            }
            graph.addEdge(from, to);
        });
    }

    private String createPath(Map<String, String> previous, String destination) {
        List<String> path = new LinkedList<>();
        for (String d = destination; d != null; d = previous.get(d)) {
            path.add(d);
        }
        Collections.reverse(path); // from origin to destination
        return String.join(",", path);
    }
}
