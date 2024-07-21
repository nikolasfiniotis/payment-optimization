package com.example.paymentoptimization.model;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Graph {
    private final Map<String, Branch> branches = new ConcurrentHashMap<>();
    private final Map<String, List<String>> neighborList = new ConcurrentHashMap<>();

    public void addBranch(String name, int cost) {
        branches.put(name, new Branch(name, cost));
        neighborList.putIfAbsent(name, Collections.synchronizedList(new ArrayList<>()));
    }

    public void addEdge(String from, String to) {
        neighborList.get(from).add(to);
    }

    @Cacheable("branches")
    public Branch getBranch(String name) {
        return branches.get(name);
    }

    public List<String> getNeighborList(String name) {
        return neighborList.getOrDefault(name, Collections.emptyList());
    }
}
