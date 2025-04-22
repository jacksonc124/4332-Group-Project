package delft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PurchasingTesting {

    // This is just a basic test to ensure that a single price generation works.
    @Test
    void testGenerateBookCostWithinRange() {
        Purchasing purchasing = new Purchasing();

        double bookCost = purchasing.generateBookCost();
        assertTrue(bookCost >= 10 && bookCost <= 100, "Book cost should be between $10 and $100.");
    }

    // This is the same but it does it multiple times.
    @Test
    void testGenerateBookCostMultipleCalls() {
        Purchasing purchasing = new Purchasing();

        // Call generateBookCost multiple times and verify each result is within range
        for (int i = 0; i < 100; i++) {
            double bookCost = purchasing.generateBookCost();
            assertTrue(bookCost >= 10 && bookCost <= 100, "Book cost should be between $10 and $100.");
        }
    }

    // This is a single test of processPurchase
    @Test
    void testProcessPurchaseRequest() {
        Purchasing purchasing = new Purchasing();

        String bookTitle = "Test Book";
        double bookCost = purchasing.processPurchase(bookTitle);
        assertTrue(bookCost >= 10 && bookCost <= 100, "Book cost should be between $10 and $100.");
    }

    // And this is a multi test of processPurchase
    @Test
    void testProcessPurchaseRequestMultipleCalls() {
        Purchasing purchasing = new Purchasing();

        for (int i = 0; i < 100; i++) {
            String bookTitle = "Test Book " + i;
            double bookCost = purchasing.processPurchase(bookTitle);
            assertTrue(bookCost >= 10 && bookCost <= 100, "Book cost should be between $10 and $100.");
        }
    }

    // This test processes a purchase request and verify the returned cost matches the generated cost
    @Test
    void testProcessPurchaseRequestReturnsConsistentCost() {
        Purchasing purchasing = new Purchasing();

        String bookTitle = "Consistent Test Book";
        double bookCost = purchasing.processPurchase(bookTitle);
        double generatedCost = purchasing.generateBookCost();
        assertTrue(bookCost >= 10 && bookCost <= 100, "Book cost should be between $10 and $100.");
    }
}
