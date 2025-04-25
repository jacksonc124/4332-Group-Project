package delft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LibrarianAccountsTesting {

    private LibraryAccounts account;

    @BeforeEach
    void setUp() {
        account = new LibraryAccounts();
    }

    @Test
    void AddCashTest() {
        account.addCashBalance(100);
        assertEquals(39100, account.getCashBalance());
    }

    @Test
    void ValidFundsWithdrawSalaryTest() {
        assertTrue(account.withdrawSalary(5000));
        assertEquals(34000, account.getCashBalance());
    }

    @Test
    void InvalidFundsWithdrawSalaryTest() {
        assertFalse(account.withdrawSalary(40000));
        assertEquals(39000, account.getCashBalance());
    }

    @Test
    void nullBookIDTest() {
        assertFalse(account.buyBooks(null));
        assertEquals(39000, account.getCashBalance());
    }

    @Test
    void BuyBookValidFundsTest() {
        Purchasing Purchase = mock(Purchasing.class);
        when(Purchase.processPurchase("Valid")).thenReturn(100.0);

        LibraryAccounts testAccount = new LibraryAccounts(Purchase);
        boolean result = testAccount.buyBooks("Valid");

        assertTrue(result);
        assertEquals(38900.0, testAccount.getCashBalance(), 0.01);
        verify(Purchase).processPurchase("Valid");
    }

    @Test
    void BuyBookInvalidFundsTest() {
        Purchasing Purchase = mock(Purchasing.class);
        when(Purchase.processPurchase("EXPENSIVE")).thenReturn(40000.0);

        LibraryAccounts testAccount = new LibraryAccounts(Purchase);
        boolean result = testAccount.buyBooks("EXPENSIVE");

        assertFalse(result);
        assertEquals(39000, testAccount.getCashBalance(), 0.01);
        verify(Purchase).processPurchase("EXPENSIVE");
    }
}
