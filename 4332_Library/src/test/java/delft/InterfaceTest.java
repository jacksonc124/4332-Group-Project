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

    // This just test the main method for code coverage sake.
    @Test
    void testMainMethod() {
        String input = "12\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));
        LibInterface.main(new String[]{});
        System.setIn(System.in);
        System.setOut(System.out);
        String output = out.toString();
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }

    // This test just confirms that before anything is done that the library is empty.
    @Test
    void case1() {
        String input = "1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("No books are currently in the library."));
    }

    // This test adds a book then runs list all books to confirm that the book is actually there.
    // It also checks the non-number year catch.
    @Test
    void case2() {
        String input = "2\nDivergent\nVeronica Roth\nnah\n2011\nISBN-1234\n1234\nDystopian\n1\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Invalid input. Please enter a valid year as an integer."));
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
        String input = "3\n9999\n2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n3\n1234\n1\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book not found."));
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

    // Likewise this also does the same as remove book but with members. Where it changes is where it also tests to confirm
    // that after removing a member that their checked out books are available again. Also testing the member not found check.
    @Test
    void case5() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n" +
                "4\nJohn Doe\njohn.doe@example.com\nM001\n" +
                "5\nM999\n" + "6\n1234\nM001\n" + "5\nM001\n" + "1\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Member not found."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Member removed successfully, and all borrowed books have been returned."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("Available: Yes"));
        assertTrue(output.contains("No members are currently in the library."));
    }

    // This test checks out a book and confirms that it's no longer available and that it's checked out by John Doe.
    // Along with the added checks for invalid book and user ID inputs.
    @Test
    void case6() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n" +
                "4\nJohn Doe\njohn.doe@example.com\nM001\n" +
                "6\n9999\nM001\n" + "6\n1234\nM999\n" + "6\n1234\nM001\n" + "1\n9\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book is not available for checkout or does not exist."));
        assertTrue(output.contains("Invalid member ID."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("Available: No"));
        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("Divergent"));
    }

    // To counter that this test confirms that after returning a book that the member no longer has it and that it's back to being available.
    // Along with the check for if a member tries to return an already avalible book. Or if a member tries to return a book and someone
    // else has checked out. It looks conviluted though due to the kinda long branching paths.
    @Test
    void case7() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n" +
                "4\nJohn Doe\njohn.doe@example.com\nM001\n" +
                "4\nJane Smith\njane.smith@example.com\nM002\n" +
                "7\n1234\nM999\n" + "7\n9999\nM001\n" + "6\n1234\nM001\n" +
                "7\n1234\nM002\n" + "7\n1234\nM001\n" + "7\n1234\nM001\n" + "1\n9\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book not found."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("This book is not loaned to the specified member."));
        assertTrue(output.contains("Book returned successfully."));
        assertTrue(output.contains("Book is already available and cannot be returned."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("Available: Yes"));
        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("No books currently borrowed."));
        assertTrue(output.contains("Jane Smith"));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("No books currently borrowed."));
    }

    // This test just confirms that Divergent was found when searching for it.
    // Along with confirming it says who owns it if someone does and if it doesn't exist.
    @Test
    void case8() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n" +
                "4\nJohn Doe\njohn.doe@example.com\nM001\n" +
                "8\nDivergent\n" + "6\n1234\nM001\n" + "8\nDivergent\n" + "8\nNonExistentBook\n" + "12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book found:"));
        assertTrue(output.contains("Divergent"));
        assertTrue(output.contains("The book is currently available."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Available: No"));
        assertTrue(output.contains("Currently checked out by Member ID: M001"));
        assertTrue(output.contains("Book not found."));
    }

    // Case 9's core features get tested in other test so it's not needed here.

    // This test confirms that after adding a book and changing it that its info was actually changed.
    // Along with the non-valid book input check.
    @Test
    void case10() {
        String input = "10\n9999\n" +
                "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n" +
                "10\n1234\nInsurgent\nVeronica Roth\n2012\nISBN-5678\nAction\n" +
                "1\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book not found."));
        assertTrue(output.contains("Book information updated successfully."));
        assertTrue(output.contains("Insurgent"));
        assertTrue(output.contains("Veronica Roth"));
        assertTrue(output.contains("2012"));
        assertTrue(output.contains("ISBN-5678"));
        assertTrue(output.contains("Action"));
    }

    // Likewise this does the same with a members' info.
    @Test
    void case11() {
        String input = "11\nM999\n" +
                "4\nJohn Doe\njohn.doe@example.com\nM001\n" +
                "11\nM001\nJane Doe\njane.doe@example.com\nM002\n" + "9\n12\n";
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Member not found."));
        assertTrue(output.contains("Member information updated successfully."));
        assertTrue(output.contains("Jane Doe"));
        assertTrue(output.contains("jane.doe@example.com"));
        assertTrue(output.contains("M002"));
    }

    // This just makes sure that trying to run exit works fine.
    @Test
    void case12() {
        String input = "12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }

    // This test is just to confirm that the default works.
    @Test
    void testDefaultCase() {
        String input = "99\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Invalid option."));
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }

    // These are just a series of all around null test.
    @Test
    void testNullChecks() {
        Library library = new Library();
    
        // Test adding a null book
        assertDoesNotThrow(() -> library.addBook(null));
        assertTrue(library.AllBooksInLibrary.isEmpty(), "Library should not contain any books.");
        assertTrue(library.AvailableBookIDs.isEmpty(), "No book IDs should be available.");
    
        // Test adding a null member
        assertDoesNotThrow(() -> library.addMember(null));
        assertTrue(library.AllMembers.isEmpty(), "Library should not contain any members.");
        assertTrue(library.MemberIDs.isEmpty(), "No member IDs should be available.");

        // Test revoking membership for a null member
        assertTrue(library.AllMembers.isEmpty(), "Library should not contain any members after attempting to revoke null membership.");
        assertTrue(library.MemberIDs.isEmpty(), "No member IDs should be available after attempting to revoke null membership.");
    
        // Test checking out a null book or member
        assertDoesNotThrow(() -> library.checkoutBook(null, null));
        assertTrue(library.LoanedBooks.isEmpty(), "No books should be loaned after attempting to checkout with null values.");
    
        // Test returning a null book or member
        assertDoesNotThrow(() -> library.returnBook(null, null));
        assertTrue(library.AvailableBookIDs.isEmpty(), "No books should be available after attempting to return with null values.");
    }
}
