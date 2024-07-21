package com.example.paymentoptimization.service;

import com.example.paymentoptimization.model.Branch;
import com.example.paymentoptimization.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class PaymentOptimizerTest {
    @Mock
    private Graph graph;

    @InjectMocks
    private PaymentOptimizer paymentService;

    @BeforeEach
    public void setUp() {
        openMocks(this);

        reset(graph);

        Branch branchA = new Branch("A", 5);
        Branch branchB = new Branch("B", 50);
        Branch branchC = new Branch("C", 10);
        Branch branchD = new Branch("D", 10);
        Branch branchE = new Branch("E", 20);
        Branch branchF = new Branch("F", 5);

        when(graph.getBranch("A")).thenReturn(branchA);
        when(graph.getBranch("B")).thenReturn(branchB);
        when(graph.getBranch("C")).thenReturn(branchC);
        when(graph.getBranch("D")).thenReturn(branchD);
        when(graph.getBranch("E")).thenReturn(branchE);
        when(graph.getBranch("F")).thenReturn(branchF);

        when(graph.getNeighborList(anyString())).thenAnswer(invocation -> {
            String node = invocation.getArgument(0);
            return switch (node) {
                case "A" -> Arrays.asList("B", "C");
                case "B" -> List.of("D");
                case "C" -> Arrays.asList("B", "E");
                case "D" -> Arrays.asList("E", "F");
                case "E" -> Arrays.asList("D", "F");
                default -> Collections.emptyList();
            };
        });
    }

    @ParameterizedTest
    @CsvSource({
            "A, D, 'A,C,E,D'",
            "A, B, 'A,B'",
            "A, C, 'A,C'",
            "C, F, 'C,E,F'",
            "B, E, 'B,D,E'"
    })
    public void testProcessPaymentValidPath(String start, String end, String expectedPath) {
        String actualPath = paymentService.processPayment(start, end);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testProcessPaymentNoPath() {
        String actualPath = paymentService.processPayment("A", "Z");
        assertNull(actualPath, "Expected no path but got one");
    }

    @Test
    public void testProcessPaymentSameOriginDestination() {
        String actualPath = paymentService.processPayment("A", "A");
        assertEquals("A", actualPath, "Expected path to be 'A'");
    }

    @Test
    public void testAddBranchAsync() throws Exception {
        String branchName = "G";
        int branchCost = 15;

        when(graph.getBranch(branchName)).thenReturn(null);

        CompletableFuture<Void> future = paymentService.addBranchAsync(branchName, branchCost);
        future.get();

        verify(graph).addBranch(branchName, branchCost);
    }

    @Test
    public void testAddEdgeAsync() throws Exception {
        String fromBranch = "A";
        String toBranch = "G";

        when(graph.getBranch(fromBranch)).thenReturn(new Branch(fromBranch, 5));
        when(graph.getBranch(toBranch)).thenReturn(new Branch(toBranch, 15));
        doNothing().when(graph).addEdge(fromBranch, toBranch);

        CompletableFuture<Void> future = paymentService.addEdgeAsync(fromBranch, toBranch);
        future.get();

        verify(graph).addEdge(fromBranch, toBranch);
    }

    @Test
    public void testSingleNodeGraph() {
        when(graph.getBranch("A")).thenReturn(new Branch("A", 5));
        when(graph.getNeighborList("A")).thenReturn(Collections.emptyList());
        String path = paymentService.processPayment("A", "A");
        assertEquals("A", path, "Expected path to be 'A'");
    }

    @Test
    public void testDisconnectedGraph() {
        when(graph.getNeighborList("A")).thenReturn(List.of("B"));
        when(graph.getNeighborList("B")).thenReturn(Collections.emptyList());
        when(graph.getBranch("C")).thenReturn(new Branch("C", 5));
        when(graph.getNeighborList("C")).thenReturn(Collections.emptyList());
        String path = paymentService.processPayment("A", "C");
        assertNull(path, "Expected no path due to disconnected components");
    }

    @Test
    public void testLargeGraph() {
        int numNodes = 1000;
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add("Node" + i);
        }

        for (String node : nodes) {
            when(graph.getBranch(node)).thenReturn(new Branch(node, 1));
        }

        for (String node : nodes) {
            List<String> neighbors = new ArrayList<>(nodes);
            neighbors.remove(node);
            when(graph.getNeighborList(node)).thenReturn(neighbors);
        }

        String startNode = "Node0";
        String endNode = "Node999";
        String path = paymentService.processPayment(startNode, endNode);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.startsWith(startNode), "Path should start with the start node");
        assertTrue(path.endsWith(endNode), "Path should end with the end node");
        System.out.println("Path found: " + path);
    }

    @Test
    public void testLargeGraphPerformance() {
        int numNodes = 1000;
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add("Node" + i);
        }

        for (String node : nodes) {
            when(graph.getBranch(node)).thenReturn(new Branch(node, 1));
        }

        for (String node : nodes) {
            List<String> neighbors = new ArrayList<>(nodes);
            neighbors.remove(node);
            when(graph.getNeighborList(node)).thenReturn(neighbors);
        }

        long startTime = System.currentTimeMillis();
        String startNode = "Node0";
        String endNode = "Node999";
        String path = paymentService.processPayment(startNode, endNode);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(path, "Path should not be null");
        System.out.println("Path found: " + path);
        System.out.println("Time taken: " + duration + " milliseconds");

        assertTrue(duration < 2000, "Pathfinding should be completed within 2 seconds");
    }

    @Test
    public void testAsyncOperations() throws InterruptedException, ExecutionException {
        String branchNameH = "H";
        int branchCostH = 25;
        String fromBranchA = "A";
        String toBranchH = "H";

        when(graph.getBranch(branchNameH)).thenReturn(null);
        Branch existingBranch = new Branch(fromBranchA, 5);
        when(graph.getBranch(fromBranchA)).thenReturn(existingBranch);
        when(graph.getBranch(toBranchH)).thenReturn(null);
        doNothing().when(graph).addBranch(anyString(), anyInt());
        doNothing().when(graph).addEdge(anyString(), anyString());

        CompletableFuture<Void> future1 = paymentService.addBranchAsync(branchNameH, branchCostH);
        future1.get();

        when(graph.getBranch(branchNameH)).thenReturn(new Branch(branchNameH, branchCostH));
        CompletableFuture<Void> future2 = paymentService.addEdgeAsync(fromBranchA, toBranchH);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);

        try {
            allOf.get();
        } catch (ExecutionException e) {
            System.out.println("ExecutionException: " + e.getCause().getMessage());
            throw e;
        }

        verify(graph).addBranch(branchNameH, branchCostH);
        verify(graph).addEdge(fromBranchA, toBranchH);
    }

    @Test
    public void testBranchMock() {
        String branchName = "H";
        int branchCost = 25;

        when(graph.getBranch(branchName)).thenReturn(new Branch(branchName, branchCost));

        Branch branch = graph.getBranch(branchName);
        assertNotNull(branch, "Branch should not be null");
        assertEquals(branchName, branch.getName(), "Branch name does not match");
        assertEquals(branchCost, branch.getCost(), "Branch cost does not match");

        verify(graph).getBranch(branchName);
    }

    @Test
    public void testResponseTime() {
        long start = System.currentTimeMillis();
        paymentService.processPayment("A", "D");
        long end = System.currentTimeMillis();
        long duration = end - start;
        assertTrue(duration < 2000, "Expected response time to be less than 2000 ms");
    }
}
