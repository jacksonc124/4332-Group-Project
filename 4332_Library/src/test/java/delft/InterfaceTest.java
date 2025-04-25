package delft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Default;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {

    private String runCLIWithInput(String input) {
        // This is used to more or less fake a stream of inputs going through the cases.
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Library library = new Library();
        Librarians librarians = new Librarians();
        LibraryAccounts accounts = new LibraryAccounts();
        LibInterface cli = new LibInterface(library, librarians, accounts, new Scanner(in), new PrintStream(out));
        cli.run();
        return out.toString();
    }

    DefaultBook book;
    InterfaceOptions options;

    private class DefaultBook {
        // default book values to be used with default input methods
        String name = "Divergent";
        String author = "Veronica Roth";
        String year = "2011";
        String isbn = "ISBN-1234";
        String bookID = "1234";
        String genre = "Dystopian";
    }

    private class InterfaceOptions {
        String delimiter = "\n";
        String displayAll = "1" + delimiter;
        String addBook = "2" + delimiter;
        String removeBook = "3" + delimiter;
        String addMember = "4" + delimiter;
        String removeMember = "5" + delimiter;
        String checkoutBook = "6" + delimiter;
        String returnBook = "7" + delimiter;
        String searchForBook = "8" + delimiter;
        String listMembers = "9" + delimiter;
        String editBookInfo = "10" + delimiter;
        String editMemberAccount = "11" + delimiter;
        String exit = "12" + delimiter;
    }


    @BeforeEach
    void setup() {
        // instantiating a default book we can just reference
        book = new DefaultBook();

        // similarly, making object with pre-defined variables with string input for the various interface options for readability
        options = new InterfaceOptions();
    }

    // This just testing the main method for code coverage's sake.
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
        String input = options.displayAll   // display all books - shouldn't be any listed in output
                + options.exit;             // always need to signal exit at end of test, otherwise program will crash
        String output = runCLIWithInput(input);
        assertTrue(output.contains("No books are currently in the library."));
    }

    // This test adds a book then runs list all books to confirm that the book is actually there.
    // It also checks the non-number year catch.
    @Test
    void case2() {
        String input = options.addBook
                + constructAddBookInput(book.name, book.author, null, null, null, null)
                + "nah\n"
                + constructAddBookInput(null, null, book.year, book.isbn, book.bookID, book.genre)
                + options.displayAll
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Invalid input. Please enter a valid year as an integer."));
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(book.author));
        assertTrue(output.contains(book.year));
        assertTrue(output.contains(book.isbn));
        assertTrue(output.contains(book.bookID));
        assertTrue(output.contains(book.genre));
    }

    // This test removes a book then runs list all books to confirm that the book is actually there.
    @Test
    void case3() {
        String input = options.removeBook + "9999\n"  // passing in book id to the remove book prompt
                + options.addBook + constructAddBookInput()   // add default book -> Library, this book should be available to remove
                + options.removeBook + book.bookID + "\n"  // again, passing in book id to the remove book prompt; this is the default book's bookID
                + options.displayAll
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book not found."));
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Book removed successfully."));
        assertTrue(output.contains("No books are currently in the library."));
    }

    // This test more or less does the same thing as book's but with member.
    @Test
    void case4() {
        String input = options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
            + options.listMembers
            + options.exit;
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
        String input = options.addBook + constructAddBookInput()
                + options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
                + options.removeMember + "373737\nM999\n"
                + options.checkoutBook + "1234\nM001\n"
                + options.removeMember + "M001\n"
                + options.displayAll
                + options.listMembers
                + options.exit;
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Member not found."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Member removed successfully, and all borrowed books have been returned."));
        assertTrue(output.contains("Books currently in the library:"));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains("Available: Yes"));
        assertTrue(output.contains("No members are currently in the library."));
    }

    // This test checks out a book and confirms that it's no longer available and that it's checked out by John Doe.
    // Along with the added checks for invalid book and user ID inputs.
    @Test
    void case6() {
        String input = options.addBook + constructAddBookInput()
                + options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
                + options.checkoutBook + "9999\nM001\n2\n" // 2\n is for the no to the purchase checkout thing
                + options.checkoutBook + "1234\nM999\n"
                + options.checkoutBook + "1234\nM001\n"
                + options.displayAll
                + options.listMembers
                + options.exit;
        String output = runCLIWithInput(input);

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
    // else has checked out. It looks convoluted though due to the kinda long branching paths.
    @Test
    void case7() {
        String input = options.addBook + constructAddBookInput()
                + options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
                + options.addMember + "Jane Smith\njane.smith@example.com\nM002\n"
                + options.returnBook + "1234\nM999\n"
                + options.returnBook + "9999\nM001\n"
                + options.checkoutBook + "1234\nM001\n"
                + options.returnBook + "1234\nM002\n"
                + options.returnBook + "1234\nM001\n"
                + options.returnBook + "1234\nM001\n"
                + options.displayAll
                + options.listMembers
                + options.exit;
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
        String input = options.addBook + constructAddBookInput()
                + options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
                + options.searchForBook + book.name + "\n"
                + options.checkoutBook + "1234\nM001\n"
                + options.searchForBook + book.name + "\n"
                + options.searchForBook + "NonExistentBook" + "\n"
                + options.exit;
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
        String updatedTitle = "Insurgent";
        String updatedYear = "2012";
        String updatedISBN = "ISBN-5678";
        String updatedGenre = "Action";

        String input = options.editBookInfo + "9999\n"
                + options.addBook + constructAddBookInput()
                + options.editBookInfo + book.bookID + "\n" + updatedTitle + "\n" + book.author + "\n" + updatedYear + "\n" + updatedISBN + "\n" + updatedGenre + "\n"
                + options.displayAll
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Book not found."));
        assertTrue(output.contains("Book information updated successfully."));
        assertTrue(output.contains(updatedTitle));
        assertTrue(output.contains(book.author));
        assertTrue(output.contains(updatedYear));
        assertTrue(output.contains(updatedISBN));
        assertTrue(output.contains(updatedGenre));
    }

    // Likewise this does the same with a members' info.
    @Test
    void case11() {
        String input = options.editMemberAccount + "M999\n"
                + options.addMember + "John Doe\njohn.doe@example.com\nM001\n"
                + options.editMemberAccount + "M001\n" + "Jane Doe\njane.doe@example.com\nM002\n"
                + options.listMembers
                + options.exit;
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
        String input = options.exit;
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }

    // This test is just to confirm that the default works.
    @Test
    void testDefaultCase() {
        String input = "99\n"   // invalid input
                + options.exit;
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

    // --- HELPER METHODS --- //

    // constructs book input using given values
    // if values are passed as null, the input construction skips those values so you can construct partially valid input
    // but still customize for invalid cases
    private String constructAddBookInput(String name, String author, String year, String isbn, String bookID, String genre) {
        String delimiter = "\n";
        String input = "";

        ArrayList<String> bookValues = new ArrayList<>(
                Stream.of(name, author, year, isbn, bookID, genre)
                    .filter(s -> s != null) // have to filter out null objects because you can't use List.of() on nulls
                    .toList()
            );

        for (String value: bookValues) {
            input += value + delimiter;
        }
        return input;
    }

    // method overload: constructs book input using the default book values
    // for when you just need some valid input
    // if you need to check for the books values, just instantiate a DefaultBook
    // and check for the relevant variables
    private String constructAddBookInput() {
        String delimiter = "\n";
        String input = "";

        DefaultBook book = new DefaultBook();

        ArrayList<String> bookValues = new ArrayList<>(List.of(book.name, book.author, book.year, book.isbn, book.bookID, book.genre));
        for (String value: bookValues) {
            input += value + delimiter;
        }

        return input;
    }

}
