package delft;

import static org.mockito.ArgumentMatchers.booleanThat;

public class LibraryAccounts {

    private double cashBalance = 39000;

    // allow librarian to add cash to balance at any point
    public void addCashBalance(double cash) {
        cashBalance += cash;
    }

    // allow librarian to withdraw Salary from balance
    public boolean withdrawSalary(double salary) {
        if (cashBalance >= salary) {
            cashBalance -= salary;
            return true;
        } else {
            return false;
        }
    }

    // orders new books from purchasing. deducted from cash balance
    public boolean buyBooks(String bookTitle, String bookID) {
        if (bookID != null && bookTitle != null) {
            Purchasing orders = new Purchasing();
            double bookCost = orders.processPurchase(bookTitle);

            // deduct only if enough money
            if (cashBalance >= bookCost) {
                cashBalance -= bookCost;
                return true;
            } else {
                System.out.println("Insufficient fund in balance");
                return false;
            }
        } else {
            return false;
        }
    }

    public double getCashBalance() {
        return cashBalance;
    }

}
