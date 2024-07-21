package com.example.paymentoptimization.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BranchTest {

    @Test
    public void testBranchConstructorAndGetters() {
        String name = "A";
        int cost = 10;

        Branch branch = new Branch(name, cost);

        assertEquals(name, branch.getName(), "Branch name correctly initialized.");
        assertEquals(cost, branch.getCost(), "Branch cost correctly initialized.");
    }

    @Test
    public void testEqualsAndHashCode() {
        Branch branch1 = new Branch("A", 10);
        Branch branch2 = new Branch("A", 10);
        Branch branch3 = new Branch("B", 15);

        assertEquals(branch1, branch2, "Branches with the same name and cost should be equal.");
        assertNotEquals(branch1, branch3, "Branches with different names or costs should not be equal.");
        assertEquals(branch1.hashCode(), branch2.hashCode(), "Branches that are equal should have the same hash code.");
    }

    @Test
    public void testToString() {
        Branch branch = new Branch("A", 10);

        String expectedToString = "Branch(name=A, cost=10)";
        String actualToString = branch.toString();

        assertEquals(expectedToString, actualToString, "Branch toString should match the expected format.");
    }
}
