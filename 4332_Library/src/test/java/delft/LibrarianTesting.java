package delft;

import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarianTesting {

 private Librarians librarians;

    @BeforeEach
    void setUp() {
        librarians = new Librarians();
    }

    //Test for valid librarian auth code
    @Test
    void testIsValidAuth_ValidCode() {
        assertTrue(librarians.isValidAuth("373737"));
        assertTrue(librarians.isValidAuth("234567"));
    }

  //Testing codes that are not members of the librarians.
    @Test
    void testIsValidAuth_InvalidCode() {
        assertFalse(librarians.isValidAuth("999999"));
        assertFalse(librarians.isValidAuth("000000"));
    }

    //Test for retrieving librarian name
    @Test
    void testGetLibrarianName() {
        assertEquals("Janice", librarians.getLibrarianName("373737"));
        assertNull(librarians.getLibrarianName("999999"));
    }

    //Test for recording salary withdrawal
    @Test
    void testRecordSalary() {
        librarians.recordSalary("373737", 1000.0);
        assertEquals(1000.0, librarians.getTotalSalaryWithdrawn("373737"));

        // Test for invalid librarian code
        librarians.recordSalary("999999", 500.0);
        assertEquals(0.0, librarians.getTotalSalaryWithdrawn("999999"));
    }

    // Test for book purchases
    @Test
    void testRecordBookPurchase() {
        librarians.recordBookPurchase("373737", "The Catcher in the Rye");
        assertTrue(librarians.getBooksPurchased("373737").contains("The Catcher in the Rye"));
    }

    @Property
    void testWithdrawSalaryPositive(@ForAll double amount) {
        // Check the salary withdrawal is only allowed for positive amounts
        if (amount > 0) {
            Librarians librarians = new Librarians();
            String validCode = "373737";  // A valid librarian code
            double initialSalary = librarians.getTotalSalaryWithdrawn(validCode);
            
            librarians.recordSalary(validCode, amount);
            assertEquals(initialSalary + amount, librarians.getTotalSalaryWithdrawn(validCode));
        }
    }

    //PROPERTY TESTING
    
    @Property
    void testInvalidWithdrawSalaryNegative(@ForAll double amount) {
        // Test invalid negative salary withdrawals
        if (amount <= 0) {
            Librarians librarians = new Librarians();
            String validCode = "373737";  // A valid librarian code
            double initialSalary = librarians.getTotalSalaryWithdrawn(validCode);
            
            librarians.recordSalary(validCode, amount);  // Should not allow negative withdrawal
            assertEquals(initialSalary, librarians.getTotalSalaryWithdrawn(validCode));
        }
    }

   //test Librarian purchasing ability.
    @Property
    void testBookPurchase(@ForAll("bookTitles") String bookTitle) {
        // Ensure that a librarian can purchase any book title
        Librarians librarians = new Librarians();
        String validCode = "373737";  // A valid librarian code
        
        librarians.recordBookPurchase(validCode, bookTitle);
        assertTrue(librarians.getBooksPurchased(validCode).contains(bookTitle));
    }

    @Provide
    Arbitrary<String> bookTitles() {
        return Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50);  // Random book titles
    }

    //Mock (Test Doubles)
    @Test
    void testRecordSalary_Mock() {
    Librarians mockLibrarians = mock(Librarians.class);
    mockLibrarians.recordSalary("373737", 1000.0);

    verify(mockLibrarians).recordSalary("373737", 1000.0);
}

}
