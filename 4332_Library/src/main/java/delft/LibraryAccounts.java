package delft;

public class LibraryAccounts {

    // Initial cash balance for the library account
    private double cashBalance = 39000;

    // Purchasing dependency used to process book purchases
    private final Purchasing purchasing;

    // Default constructor initializes with a Purchasing object
    public LibraryAccounts() {
        this.purchasing = new Purchasing();
    }

    // Constructor for custom Purchasing
    public LibraryAccounts(Purchasing purchasing) {
        this.purchasing = purchasing;
    }

    // Increases the library's cash balance by the specified amount
    public void addCashBalance(double cash) {
        cashBalance += cash;
    }

    // Attempts to withdraw the given salary amount from the balance
    // Returns true if successful, false if insufficient funds
    public boolean withdrawSalary(double salary) {
        if (cashBalance >= salary) {
            cashBalance -= salary;
            return true;
        } else {
            System.out.println("Insufficient fund in balance");
            return false;
        }
    }

    // Attempts to purchase a book using its ID
    // Returns true if the purchase succeeds and balance is sufficient
    // Returns false if book ID is null or funds are insufficient
    public boolean buyBooks(String bookID) {
        if (bookID != null) {
            double bookCost = purchasing.processPurchase(bookID);
            if (cashBalance >= bookCost) {
                cashBalance -= bookCost;
                return true;
            } else {
                System.out.println("Insufficient fund in balance");
                return false;
            }
        } else {
            System.out.println("Book ID is needed to buy books");
            return false;
        }
    }

    // Returns the current cash balance of the library account
    public double getCashBalance() {
        return cashBalance;
    }
}
