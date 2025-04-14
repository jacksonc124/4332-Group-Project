package delft;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {

    private String runCLIWithInput(String input) {
        // This is used to more or less fake a stream of inputs going through the cases.
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Library library = new Library();
        LibInterface cli = new LibInterface(library, new Scanner(in), new PrintStream(out));
        cli.run();
        return out.toString();
    }

    // This test just confirms that before anything is done that the library is empty.
    @Test
    void case1() {
        String input = "1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("No books are currently in the library."));
    }

    // This test adds a book then runs list all books to confirm that the book is actually there.
    @Test
    void case2() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("Veronica Roth"));
        assertTrue(output.contains("2011"));
        assertTrue(output.contains("ISBN-1234"));
        assertTrue(output.contains("1234"));
        assertTrue(output.contains("Dystopian"));
    }

    // This test removes a book then runs list all books to confirm that the book is actually there.
    @Test
    void case3() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n3\n1234\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Book removed successfully."));
        assertTrue(output.contains("No books are currently in the library."));
    }

    // This test more or less does the same thing as book's but with member.
    @Test
    void case4() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("New member has been successfully added."));
        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("john.doe@example.com"));
        assertTrue(output.contains("M001"));
    }

    // Likewise this also does the same as remove book but with members.
    @Test
    void case5() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n5\nM001\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("New member has been successfully added."));
        assertTrue(output.contains("Member removed successfully, and all borrowed books have been returned."));
        assertTrue(output.contains("No members are currently in the library."));
    }

    // This test checks out a book and confirms that it's no longer available and that it's checked out by John Doe.
    @Test
    void case6() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n4\nJohn Doe\njohn.doe@example.com\nM001\n6\n1234\nM001\n1\n9\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Available: No"));

        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("Divergent"));
    }

    // To counter that this test confirms that after returning a book that the member no longer has it and that it's back to being available.
    @Test
    void case7() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n4\nJohn Doe\njohn.doe@example.com\nM001\n6\n1234\nM001\n7\n1234\nM001\n1\n9\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book returned successfully."));

        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("Available: Yes"));

        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("No books currently borrowed."));
    }

    // This test just confirms that Divergent was found when searching for it.
    @Test
    void case8() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n8\nDivergent\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book found:"));
        assertTrue(output.contains("Divergent"));
    }

    // Case 9's core features get tested in other test so it's not needed here.

    // This test confirms that after adding a book and changing it that its info was actually changed.
    @Test
    void case10() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n10\n1234\nInsurgent\nVeronica Roth\n2012\nISBN-5678\nAction\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book information updated successfully."));
        assertTrue(output.contains("Insurgent"));
    }

    // Likewise this does the same with a members info.
    @Test
    void case11() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n11\nM001\nJane Doe\njane.doe@example.com\nM002\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Member information updated successfully."));
        assertTrue(output.contains("Jane Doe"));
    }

    // This just makes sure that trying to run exit works fine.
    @Test
    void case12() {
        String input = "12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }

    // These are just a series of all around null test.
    @Test
    void testNullChecks() {
        Library library = new Library();
        assertDoesNotThrow(() -> library.addBook(null));
        assertTrue(library.AllBooksInLibrary.isEmpty(), "Library should not contain any books.");
        assertTrue(library.AvailableBookIDs.isEmpty(), "No book IDs should be available.");
        assertDoesNotThrow(() -> library.addMember(null));
        assertTrue(library.AllMembers.isEmpty(), "Library should not contain any members.");
        assertTrue(library.MemberIDs.isEmpty(), "No member IDs should be available.");
        assertTrue(library.AllBooksInLibrary.isEmpty(), "Library should not contain any books after attempting to remove null.");
        assertTrue(library.AllMembers.isEmpty(), "Library should not contain any members after attempting to revoke null membership.");
        assertDoesNotThrow(() -> library.checkoutBook(null, null));
        assertTrue(library.LoanedBooks.isEmpty(), "No books should be loaned after attempting to checkout with null values.");
        assertDoesNotThrow(() -> library.returnBook(null, null));
        assertTrue(library.AvailableBookIDs.isEmpty(), "No books should be available after attempting to return with null values.");
    }
}