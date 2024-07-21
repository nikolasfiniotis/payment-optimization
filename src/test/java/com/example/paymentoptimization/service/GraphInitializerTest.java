package com.example.paymentoptimization.service;

import com.example.paymentoptimization.config.BranchConfig;
import com.example.paymentoptimization.config.EdgeConfig;
import com.example.paymentoptimization.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;


public class GraphInitializerTest {

    @Mock
    private Graph graph;

    @Mock
    private BranchConfig branchConfig;

    @Mock
    private EdgeConfig edgeConfig;

    @InjectMocks
    private GraphInitializer graphInitializer;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void testInit() {
        BranchConfig.Branch branch1 = new BranchConfig.Branch();
        branch1.setName("Branch1");
        branch1.setCost(10);

        BranchConfig.Branch branch2 = new BranchConfig.Branch();
        branch2.setName("Branch2");
        branch2.setCost(20);

        EdgeConfig.Edge edge1 = new EdgeConfig.Edge();
        edge1.setFrom("Branch1");
        edge1.setTo("Branch2");

        when(branchConfig.getBranches()).thenReturn(List.of(branch1, branch2));
        when(edgeConfig.getEdges()).thenReturn(List.of(edge1));

        graphInitializer.init();

        verify(graph, times(1)).addBranch("Branch1", 10);
        verify(graph, times(1)).addBranch("Branch2", 20);
        verify(graph, times(1)).addEdge("Branch1", "Branch2");
    }
}
