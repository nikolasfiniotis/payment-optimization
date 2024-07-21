package com.example.paymentoptimization.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GraphTest {

    private Graph graph;

    @BeforeEach
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void testAddBranch() {
        String branchName = "A";
        int branchCost = 10;

        graph.addBranch(branchName, branchCost);

        Branch branch = graph.getBranch(branchName);
        assertNotNull(branch, "Branch added to the graph.");
        assertEquals(branchName, branch.getName(), "Branch name should match.");
        assertEquals(branchCost, branch.getCost(), "Branch cost should match.");
    }

    @Test
    public void testAddEdge() {
        String fromBranch = "A";
        String toBranch = "B";

        graph.addBranch(fromBranch, 10);
        graph.addBranch(toBranch, 20);

        graph.addEdge(fromBranch, toBranch);

        List<String> neighbors = graph.getNeighborList(fromBranch);
        assertTrue(neighbors.contains(toBranch), "Edge added to the neighbor list.");
    }

    @Test
    public void testGetBranch() {
        String branchName = "A";
        int branchCost = 10;
        graph.addBranch(branchName, branchCost);

        Branch branch = graph.getBranch(branchName);

        assertNotNull(branch, "Branch should be retrievable from the graph.");
        assertEquals(branchName, branch.getName(), "Branch name should match.");
        assertEquals(branchCost, branch.getCost(), "Branch cost should match.");
    }

    @Test
    public void testGetNeighborList() {
        String fromBranch = "A";
        String toBranch = "B";

        graph.addBranch(fromBranch, 10);
        graph.addBranch(toBranch, 20);
        graph.addEdge(fromBranch, toBranch);

        List<String> neighbors = graph.getNeighborList(fromBranch);

        assertNotNull(neighbors, "Neighbor list should not be null.");
        assertTrue(neighbors.contains(toBranch), "Neighbor list should contain the edge.");
    }

    @Test
    public void testGetNeighborListEmpty() {
        String branchName = "A";

        graph.addBranch(branchName, 10);

        List<String> neighbors = graph.getNeighborList(branchName);

        assertNotNull(neighbors, "Neighbor list should not be null.");
        assertTrue(neighbors.isEmpty(), "Neighbor list should be empty for a branch with no edges.");
    }
}
