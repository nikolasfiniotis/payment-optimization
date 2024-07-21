package com.example.paymentoptimization.service;

import com.example.paymentoptimization.config.BranchConfig;
import com.example.paymentoptimization.config.EdgeConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
public class PaymentOptimizerConcurrencyTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BranchConfig branchConfig;

    @Autowired
    private EdgeConfig edgeConfig;

    @Test
    public void contextLoads() {
        assertNotNull(branchConfig);
        assertNotNull(edgeConfig);
        assertNotNull(branchConfig.getBranches());
        assertNotNull(edgeConfig.getEdges());
    }

    @Test
    public void testConcurrentProcessPayment() throws InterruptedException {
        int threadCount = 10;
        int taskCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Use a thread-safe collection to store results
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        Runnable task = () -> {
            try {
                String result = paymentService.processPayment("A", "B");
                results.add(result);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < taskCount; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            throw new RuntimeException("Executor did not terminate in the specified time.");
        }

        assertFalse(results.isEmpty(), "Results should not be empty");
        results.forEach(result -> assertNotNull(result, "Result should not be null"));
    }
}