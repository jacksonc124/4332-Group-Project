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

    @Test
    void testInitialAvailability() {
        assertTrue(book.checkAvailability());
    }

    @Test
    void testUpdateBookInfo() {
        book.updateBookInfo("Fahrenheit 451", "Ray Bradbury", 1953, "978-1451673319", "Dystopian");
        assertEquals("Fahrenheit 451", book.name);
        assertEquals(1953, book.year);
        assertEquals("Dystopian", book.genre);
    }

    @Test
    void testSetAvailability() {
        book.setAvailability(false);
        assertFalse(book.checkAvailability());
        book.setAvailability(true);
        assertTrue(book.checkAvailability());
    }

    @Test
    void testGetBookInfo() {
        String expectedOutput = String.join(System.lineSeparator(),
                "Book ID: MC001",
                "Title: The Martian Chronicles",
                "Author: Ray Bradbury",
                "Year: 1950",
                "ISBN: 978-0062079930",
                "Genre: Science Fiction",
                "Availability: Available",
                ""
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        book.getBookInfo();

        System.setOut(originalOut);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testGetBookInfoWhenNotAvailable() {
        book.setAvailability(false);

        String expectedOutput = String.join(System.lineSeparator(),
                "Book ID: MC001",
                "Title: The Martian Chronicles",
                "Author: Ray Bradbury",
                "Year: 1950",
                "ISBN: 978-0062079930",
                "Genre: Science Fiction",
                "Availability: Not Available",
                ""
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        book.getBookInfo();

        System.setOut(originalOut);

        assertEquals(expectedOutput, outputStream.toString());
    }
}