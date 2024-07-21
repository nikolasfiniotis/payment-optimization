package com.example.paymentoptimization.api;

import com.example.paymentoptimization.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/addBranch")
    public ResponseEntity<String> addBranch(
            @RequestParam String name,
            @RequestParam int cost) {

        try {
            paymentService.addBranchAsync(name, cost).join();
            return ResponseEntity.ok("Branch added successfully");
        } catch (CompletionException e) {
            // Handle the CompletionException thrown by CompletableFuture.join()
            Throwable cause = e.getCause();
            String errorMessage = cause != null ? cause.getMessage() : "Unknown error occurred when trying to add new branch";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/addEdge")
    public ResponseEntity<String> addEdge(
            @RequestParam String from,
            @RequestParam String to) {

        try {
            if (paymentService.isInvalidBranch(from) || paymentService.isInvalidBranch(to)) {
                return ResponseEntity.badRequest().body("One or both branches do not exist");
            }

            paymentService.addEdgeAsync(from, to).join();
            return ResponseEntity.ok("Edge added successfully");
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            String errorMessage = cause != null ? cause.getMessage() : "Unknown error occurred when trying to add new edge";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestParam String origin,
            @RequestParam String destination) {

        if (origin == null || origin.isEmpty() || destination == null || destination.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid origin or destination branch");
        }

        // Validate if origin and destination branches exist
        if (paymentService.isInvalidBranch(origin) || paymentService.isInvalidBranch(destination)) {
            return ResponseEntity.badRequest().body("One or both branches do not exist");
        }

        String cheapestPath = paymentService.processPayment(origin, destination);

        if (cheapestPath == null) {
            return ResponseEntity.badRequest().body("No valid path found");
        }

        return ResponseEntity.ok(cheapestPath);
    }
}
