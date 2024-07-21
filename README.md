# Payment Optimization

## Overview

**Payment Optimization** is a Spring Boot application designed to optimize payment routing based on a network of branches and edges. The application enables users to dynamically manage branches and edges, and to compute the cheapest path between branches using Dijkstra's algorithm.

## Summary

This application provides functionality for:

- **Branch Management:** Add and manage branches with associated costs.
- **Edge Management:** Define connections (edges) between branches.
- **Payment Processing:** Compute the cheapest route between two branches using Dijkstra's algorithm.
- **Asynchronous Operations:** Manage branch and edge modifications asynchronously to improve performance and scalability.

## Features

- **Dynamic Branch Management:** Add and manage branches with associated costs.
- **Edge Management:** Define connections (edges) between branches.
- **Payment Processing:** Compute the cheapest path from one branch to another.
- **Asynchronous Operations:** Perform branch and edge operations asynchronously to ensure scalability.

## Configuration

The application uses YAML files for configuration:

- **branches.yml**: Defines the branches with their names and costs.
- **edges.yml**: Defines the connections between branches.

These configuration files are located in the `src/main/resources` directory and are automatically loaded at startup.


## REST API Overview

This project exposes a REST API for managing branches and edges, and processing payments through the shortest path algorithm. 

The API includes endpoints to add branches (`POST /api/payments/addBranch`), add edges (`POST /api/payments/addEdge`), and process payments (`GET /api/payments/process`), with robust input validation and error handling. 

Configuration is managed via YAML files (`branches.yml` and `edges.yml`).

## Flexibility for Future Additions

The solution is designed to be highly flexible, allowing for the easy addition of new branches and links without requiring code modifications. 

This is achieved through the use of configuration files (`branches.yml` and `edges.yml`) which define branches and their costs, as well as the connections between them. The `GraphInitializer` service reads these configurations at startup and initializes the graph accordingly. 

Additionally, the `PaymentController` exposes REST endpoints for dynamically adding branches (`POST /api/payments/addBranch`) and edges (`POST /api/payments/addEdge`) at runtime. 

The asynchronous methods in the `PaymentService` interface ensure that new additions are handled efficiently, while the comprehensive input validation ensures data integrity. 

This architecture allows the system to seamlessly incorporate changes and expansions, facilitating easy scalability and adaptability.

## Performance and Scalability

The implementation is designed with performance and scalability in mind. The use of concurrent data structures, such as `ConcurrentHashMap` for storing branches and their neighbors, ensures thread-safe operations and efficient handling of concurrent requests. 

The `PriorityQueue` and Dijkstra's algorithm in the `PaymentOptimizer` service efficiently find the shortest path between branches, making it scalable as new branches and links are added. 

Asynchronous methods in the `PaymentService` interface allow for non-blocking operations, enabling the system to handle multiple requests simultaneously without performance degradation. 

Additionally, the caching mechanism, enabled through Spring's `@Cacheable` annotation, reduces the load on the system by storing frequently accessed branch data. 

These design choices collectively ensure that the implementation performs efficiently and scales well as the graph grows in size.

## Functional Test Coverage

The solution is designed with robust functional test coverage to ensure reliability and correctness. 

The `PaymentControllerTest` class comprehensively tests the REST API endpoints for processing payments, adding branches, and adding edges, including various edge cases and error handling scenarios. 

The `GlobalExceptionHandlerTest` verifies the handling of exceptions at a global level. 

Model classes such as `Branch` and `Graph` are thoroughly tested for their core functionalities, ensuring correct initialization, equality, and string representation. 

The `GraphInitializerTest` ensures proper initialization of the graph from configuration files. 

The `PaymentOptimizerTest` covers various payment processing scenarios, including valid paths, no paths, concurrency, large graph performance, and asynchronous operations. 

These tests collectively ensure that the solution has sufficient and appropriate functional test coverage, capable of handling different scenarios and validating the system's behavior under various conditions.

The code coverage for this project is tracked using JaCoCo. Below is a screenshot of the current code coverage:

![Code Coverage](/code-coverage.png)

## Thread Safety

The solution is designed to be thread-safe, allowing the `processPayment` method to handle concurrent calls from multiple threads. This is achieved through careful design and testing of asynchronous operations and concurrent data handling. 

The `PaymentOptimizer` service and its dependencies, such as the `Graph` and `PaymentService`, are structured to manage concurrent access without data races or inconsistencies. The use of thread-safe collections and proper synchronization mechanisms ensures that multiple threads can safely interact with shared resources. 

The `PaymentOptimizerConcurrencyTest` demonstrates this thread safety by running concurrent tasks that call the `processPayment` method, verifying that the service performs reliably and returns correct results under concurrent load. This design ensures that the system maintains integrity and performance even in a multi-threaded environment.



## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- An IDE or text editor of your choice


### Clone the Repository

```bash
git clone https://github.com/nikolasfiniotis/payment-optimization.git
cd payment-optimization
