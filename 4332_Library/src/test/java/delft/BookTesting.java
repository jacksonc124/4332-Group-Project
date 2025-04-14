
package delft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class BookTesting {
    private Book book;

    @BeforeEach
    void setup() {
        book = new Book("The Martian Chronicles", "Ray Bradbury", 1950, "978-0062079930", "MC001", "Science Fiction");
    }

    // Specification Tests

    // Test book's availability once added is always set to true
    @Test
    void testInitialAvailability() {
        assertTrue(book.checkAvailability());
    }

    // Testing changing things about the book are updated properly
    @Test
    void testUpdateBookInfo() {
        book.updateBookInfo("Fahrenheit 451", "Ray Bradbury", 1953, "978-1451673319", "Dystopian");
        assertEquals("Fahrenheit 451", book.name);
        assertEquals(1953, book.year);
        assertEquals("Dystopian", book.genre);
    }

    // Test changing availability of the book
    @Test
    void testSetAvailability() {
        book.setAvailability(false);
        assertFalse(book.checkAvailability());
        book.setAvailability(true);
        assertTrue(book.checkAvailability());
    }

    // Test the toString and if it outputs the correct information in the correct order.
    @Test
    void testGetBookInfo() {
        Book book = new Book("The Martian Chronicles", "Ray Bradbury", 1950, "978-0062079930", "MC001", "Science Fiction");

        String expectedOutput = String.join(System.lineSeparator(),
                "Title: The Martian Chronicles",
                "Author: Ray Bradbury",
                "Year: 1950",
                "ISBN: 978-0062079930",
                "Book ID: MC001",
                "Genre: Science Fiction",
                "Available: Yes"
        ) + System.lineSeparator(); // Add trailing newline

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        book.getBookInfo(new PrintStream(outputStream));

        assertEquals(expectedOutput, outputStream.toString());
    }


    // Testing alternate book availability in toString
    @Test
    void testGetBookInfoWhenNotAvailable() {
        Book book = new Book(
                "The Martian Chronicles",
                "Ray Bradbury",
                1950,
                "978-0062079930",
                "MC001",
                "Science Fiction"
        );

        // Set book as not available
        book.setAvailability(false);

        // Correct line order and formatting
        String expectedOutput = String.join(System.lineSeparator(),
                "Title: The Martian Chronicles",
                "Author: Ray Bradbury",
                "Year: 1950",
                "ISBN: 978-0062079930",
                "Book ID: MC001",
                "Genre: Science Fiction",
                "Available: No"
        ) + System.lineSeparator();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        book.getBookInfo(new PrintStream(outputStream)); // Pass stream correctly

        assertEquals(expectedOutput, outputStream.toString());
    }

}
