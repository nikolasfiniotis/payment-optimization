package com.example.paymentoptimization.service;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    /**
     * Process a payment returning the cheapest sequence of branches as a comma-separated String.
     * Implementations are expected to be thread-safe.
     * @param originBranch the starting branch
     * @param destinationBranch the destination branch
     * @return the cheapest sequence for the payment as a CSV (e.g. A, D, C) or null if no sequence is available
     */
    String processPayment(String originBranch, String destinationBranch);

    /**
     * Checks if a branch is invalid.
     * @param branch the branch name
     * @return true if the branch does not exist, false otherwise
     */
    boolean isInvalidBranch(String branch);

    CompletableFuture<Void> addBranchAsync(String name, int cost);

    CompletableFuture<Void> addEdgeAsync(String from, String to);
}
