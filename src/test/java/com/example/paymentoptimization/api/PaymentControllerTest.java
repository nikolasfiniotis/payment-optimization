package com.example.paymentoptimization.api;

import com.example.paymentoptimization.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    public void testProcessPayment() throws Exception {
        // Valid path
        when(paymentService.processPayment("A", "B")).thenReturn("A,B");
        mockMvc.perform(get("/api/payments/process")
                        .param("origin", "A")
                        .param("destination", "B"))
                .andExpect(status().isOk())
                .andExpect(content().string("A,B"));

        // Invalid origin
        mockMvc.perform(get("/api/payments/process")
                        .param("origin", "")
                        .param("destination", "B"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid origin or destination branch"));

        // Invalid destination
        mockMvc.perform(get("/api/payments/process")
                        .param("origin", "A")
                        .param("destination", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid origin or destination branch"));

        // No valid path
        when(paymentService.processPayment("A", "Z")).thenReturn(null);
        mockMvc.perform(get("/api/payments/process")
                        .param("origin", "A")
                        .param("destination", "Z"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No valid path found"));

        // Branch invalid
        when(paymentService.isInvalidBranch("A")).thenReturn(true);
        when(paymentService.isInvalidBranch("B")).thenReturn(true);
        mockMvc.perform(get("/api/payments/process")
                        .param("origin", "A")
                        .param("destination", "B"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("One or both branches do not exist"));
    }

    @Test
    public void testAddBranch() throws Exception {
        // Successful addition
        when(paymentService.addBranchAsync("H", 25)).thenReturn(CompletableFuture.completedFuture(null));
        mockMvc.perform(post("/api/payments/addBranch")
                        .param("name", "H")
                        .param("cost", "25"))
                .andExpect(status().isOk())
                .andExpect(content().string("Branch added successfully"));
        verify(paymentService, times(1)).addBranchAsync("H", 25);

        // Branch already exists
        when(paymentService.addBranchAsync("H", 15)).thenThrow(new RuntimeException("Branch already exists"));
        mockMvc.perform(post("/api/payments/addBranch")
                        .param("name", "H")
                        .param("cost", "15"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Branch already exists"));
        verify(paymentService, times(1)).addBranchAsync("H", 15);

        // Exception handling
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new CompletionException(new RuntimeException("Branch cannot be added")));
        when(paymentService.addBranchAsync(anyString(), anyInt())).thenReturn(future);
        mockMvc.perform(post("/api/payments/addBranch")
                        .param("name", "TestBranch")
                        .param("cost", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Branch cannot be added"));
        verify(paymentService, times(1)).addBranchAsync("TestBranch", 10);
    }

    @Test
    public void testAddEdge() throws Exception {
        // Test valid branches and successful addition
        when(paymentService.isInvalidBranch("A")).thenReturn(false);
        when(paymentService.isInvalidBranch("B")).thenReturn(false);
        when(paymentService.addEdgeAsync("A", "B")).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/payments/addEdge")
                        .param("from", "A")
                        .param("to", "B"))
                .andExpect(status().isOk())
                .andExpect(content().string("Edge added successfully"));

        verify(paymentService, times(1)).addEdgeAsync("A", "B");

        // Reset interactions and mock for next test case
        reset(paymentService);

        // Test invalid branches
        when(paymentService.isInvalidBranch("A")).thenReturn(true);

        mockMvc.perform(post("/api/payments/addEdge")
                        .param("from", "A")
                        .param("to", "B"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("One or both branches do not exist"));

        verify(paymentService, never()).addEdgeAsync(anyString(), anyString());

        // Reset interactions and mock for next test case
        reset(paymentService);

        // Test exception handling
        when(paymentService.isInvalidBranch("ValidBranch1")).thenReturn(false);
        when(paymentService.isInvalidBranch("ValidBranch2")).thenReturn(false);
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new CompletionException(new RuntimeException("Edge cannot be added")));
        when(paymentService.addEdgeAsync("ValidBranch1", "ValidBranch2")).thenReturn(future);

        mockMvc.perform(post("/api/payments/addEdge")
                        .param("from", "ValidBranch1")
                        .param("to", "ValidBranch2"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Edge cannot be added"));

        verify(paymentService, times(1)).addEdgeAsync("ValidBranch1", "ValidBranch2");
    }
}
