package delft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LibrarianAccountsTesting {

    private LibraryAccounts account;

    //Set up before each test
    @BeforeEach
    void setUp() {
        account = new LibraryAccounts();
    }

    //Cash is correctly added to the balance
    @Test
    void AddCashTest() {
        account.addCashBalance(100);
        assertEquals(39100, account.getCashBalance());
    }

    //Withdrawal succeeds when balance is sufficient
    @Test
    void ValidFundsWithdrawSalaryTest() {
        assertTrue(account.withdrawSalary(5000));
        assertEquals(34000, account.getCashBalance());
    }

    //Withdrawal fails when balance is insufficient
    @Test
    void InvalidFundsWithdrawSalaryTest() {
        assertFalse(account.withdrawSalary(40000));
        assertEquals(39000, account.getCashBalance());
    }

    //Buying a book fails if the book title is null
    @Test
    void nullBookTitleTest() {
        assertFalse(account.buyBooks(null));
        assertEquals(39000, account.getCashBalance());
    }

    //Book purchase succeeds with valid title and sufficient funds
    @Test
    void BuyBookValidFundsTest() {
        Purchasing Purchase = mock(Purchasing.class);
        when(Purchase.processPurchase("Valid")).thenReturn(100.0);

        LibraryAccounts testAccount = new LibraryAccounts(Purchase);
        boolean result = testAccount.buyBooks("Valid");

        // Should succeed due to sufficient funds and balance reflects purchase
        assertTrue(result);
        assertEquals(38900.0, testAccount.getCashBalance(), 0.01);
        verify(Purchase).processPurchase("Valid");
    }

    // Book purchase fails if book cost exceeds available balance
    @Test
    void BuyBookInvalidFundsTest() {
        Purchasing Purchase = mock(Purchasing.class);
        when(Purchase.processPurchase("EXPENSIVE")).thenReturn(40000.0);

        LibraryAccounts testAccount = new LibraryAccounts(Purchase);
        boolean result = testAccount.buyBooks("EXPENSIVE");

        // Should fail due to insufficient funds and balance remains unchanged
        assertFalse(result);
        assertEquals(39000, testAccount.getCashBalance(), 0.01);
        verify(Purchase).processPurchase("EXPENSIVE");
    }


}
