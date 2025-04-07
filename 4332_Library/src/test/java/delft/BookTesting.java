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

    // Specification-based Tests

    @Test
    void testInitialAvailability() {
        assertTrue(book.isAvailable(), "New book should be available by default");
    }

    @Test
    void testUpdateBookInfo() {
        book.updateBookInfo("Animal Farm", "George Orwell", 1945, "987654321", "Political Fiction");
        assertEquals("Animal Farm", book.getName());
        assertEquals(1945, book.getYear());
        assertEquals("Political Fiction", book.getGenre());
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
        // Just ensure it doesn't crash; actual console output can be tested with System rules (out of scope for now)
        assertDoesNotThrow(() -> book.getBookInfo());
    }

}
