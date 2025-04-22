package delft;

import java.util.Random;

public class Purchasing {

    private final Random random;

    // Just a lil constructor
    public Purchasing() {
        this.random = new Random();
    }

    //Generates a random cost for a book between $10 and $100.
    public double generateBookCost() {
        double cost = 10 + (90 * random.nextDouble());
        return Math.round(cost * 100.0) / 100.0;
    }


    // This logic is to help with LibraryAccounts.
   public double processPurchase(String bookTitle) {
       double cost = generateBookCost();
       System.out.println("Processing purchase request for book: " + bookTitle);
       System.out.println("Book cost: $" + cost);
       return cost;
   }

}
