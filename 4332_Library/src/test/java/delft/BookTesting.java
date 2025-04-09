package delft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class BookTesting {
    private Book book;

    @BeforeEach
    void setup() {
        book = new Book("1984", "George Orwell", 1949, "123456789", "B001", "Dystopian");
    }

    // Specification Tests

    @Test
    void testInitialAvailability() {
        assertTrue(book.checkAvailability(), "New book should be available by default");
    }

    @Test
    void testUpdateBookInfo() {
        book.updateBookInfo("Animal Farm", "George Orwell", 1945, "987654321", "Political Fiction");
        assertEquals("Animal Farm", book.name); // Or book.getName() if implemented
        assertEquals(1945, book.year);
        assertEquals("Political Fiction", book.genre);
    }

    @Test
    void testSetAvailability() {
        book.setAvailability(false);
        assertFalse(book.checkAvailability(), "Book should be marked as not available");
        book.setAvailability(true);
        assertTrue(book.checkAvailability(), "Book should be marked as available");
    }

    @Test
    void testGetBookInfoOutput() {
        // Validate no exception is thrown
        assertDoesNotThrow(() -> book.getBookInfo());
    }

// Property Tests using jqwik

    @Property
    void testAvailabilityToggleProperty(@ForAll boolean status) {
        book.setAvailability(status);
        assertEquals(status, book.checkAvailability());
    }

}
