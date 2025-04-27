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
    DefaultMember member;
    InterfaceOptions options;

    private class DefaultBook {
        // default book values for testing cases where the particular values don't matter
        String name = "Divergent";
        String author = "Veronica Roth";
        String year = "2011";
        String isbn = "ISBN-1234";
        String bookID = "1234";
        String genre = "Dystopian";
    }

    private class DefaultMember {
        // default member values for testing cases where specific values are irrelevant
        String name = "John Doe";
        String email = "john.doe@gmail.com";
        String memberID = "M001";
    }

    private class InterfaceOptions {
        // predefined String variables for all possible valid LibInterface options
        String delimiter = "\n";
        String displayAll = "1" + delimiter;    // this lists all books in the library
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
        // instantiating a default book, member for testing cases where values don't matter
        book = new DefaultBook();
        member = new DefaultMember();

        // similarly, making object with pre-defined variables for the interface options for readability
        options = new InterfaceOptions();
    }

    // This is just testing the main method for code coverage's sake.
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
        assertTrue(output.contains(systemExit()));
    }

    // This test just confirms that before anything is done that the library is empty.
    @Test
    void case1() {
        String input = options.displayAll   // display all books - shouldn't be any listed in output
                + options.exit;             // always need to signal exit at end of test, otherwise program will crash
        String output = runCLIWithInput(input);
        assertTrue(output.contains(booksInLibrary(false)));
    }

    // This test adds a book then runs list all books to confirm that the book is actually there.
    // It also checks the non-number year catch.
    @Test
    void case2() {
        String input = options.addBook
                + constructBookInput(book.name, book.author, null, null, null, null)
                + "nah\n"   // invalid input to year; it must be interpretable as an integer -> should display error message but allow continued input
                + constructBookInput(null, null, book.year, book.isbn, book.bookID, book.genre)
                + options.displayAll
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Invalid input. Please enter a valid year as an integer.")); // error message for invalid year input
        assertTrue(output.contains("Book added successfully."));

        // displayAll message check
        assertTrue(output.contains(booksInLibrary(true)));
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
        String input = options.removeBook + "9999\n"  // attempting to remove a book that doesn't exist
                + options.addBook + constructBookInput()   // add default book -> Library, this book should be available to remove
                + options.removeBook + book.bookID + "\n"  // attempting to remove default book from Library -> should succeed
                + options.displayAll
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains(bookWasFound(false))); // error message for attempting to remove non-existent book

        // verifying default book was added & removed properly
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Book removed successfully."));
        assertTrue(output.contains(booksInLibrary(false)));  // displayAll message check
    }

    // This test more or less does the same thing as book's but with member.
    @Test
    void case4() {
        String input = options.addMember + constructMemberInput()
            + options.listMembers
            + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("New member has been successfully added."));

        // checking listMembers displays the right info for our new member
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains(member.email));
        assertTrue(output.contains(member.memberID));
    }

    // Likewise this also does the same as remove book but with members. Where it changes is where it also tests to confirm
    // that after removing a member that their checked out books are available again. Also testing the member not found check.
    @Test
    void case5() {
        String input = options.addBook + constructBookInput()
                + options.addMember + constructMemberInput()
                + options.removeMember + "373737\nM999\n"   // attempt to remove a non-existent member (373737 is the auth code for a librarian, required to revoke membership)
                + options.checkoutBook + "1234\nM001\n"     // checkout default book for default member
                + options.removeMember + "M001\n"           // 373737 auth code doesn't need to be passed again here because the library doesn't ask if you've already auth'd
                + options.displayAll    // displaying all library books; should show the default book as available
                + options.listMembers   // to check our removed member is no longer listed
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Member not found."));   // message from when we attempt to remove non-existent member
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("Member removed successfully, and all borrowed books have been returned."));

        // verifying displayAll has correct availability displayed
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(true)));

        // verifying listMembers shows no members in library
        assertTrue(output.contains(membersInLibrary(false)));
    }

    // This test checks out a book and confirms that it's no longer available and that it's checked out by John Doe.
    // Along with the added checks for invalid book and user ID inputs.
    @Test
    void case6() {
        String input = options.addBook + constructBookInput()
                + options.addMember + constructMemberInput()
                + options.checkoutBook + "9999\nM001\n2\n" // 2\n is for the no to the purchase checkout thing; attempting to checkout invalid book for default member
                + options.checkoutBook + "1234\nM999\n" // attempting to checkout default book for invalid member
                + options.checkoutBook + "1234\nM001\n" // checkout default book for default member
                + options.displayAll
                + options.listMembers
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Invalid member ID."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(false)));
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains(book.name));
    }

    // To counter that this test confirms that after returning a book that the member no longer has it and that it's back to being available.
    // Along with the check for if a member tries to return an already available book. Or if a member tries to return a book and someone
    // else has checked out. It looks convoluted though due to the kinda long branching paths.
    @Test
    void case7() {
        String updatedName = "Jane Smith";
        String updatedEmail = "jane.smith@example.com";
        String updatedMemberID = "M002";

        String input = options.addBook + constructBookInput()
                + options.addMember + constructMemberInput()
                + options.addMember + constructMemberInput(updatedName, updatedEmail, updatedMemberID)
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

        assertTrue(output.contains(bookWasFound(false)));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("This book is not loaned to the specified member."));
        assertTrue(output.contains("Book returned successfully."));
        assertTrue(output.contains("Book is already available and cannot be returned."));
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(true)));
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("No books currently borrowed."));
        assertTrue(output.contains(updatedName));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains("No books currently borrowed."));
    }

    // This test just confirms that Divergent was found when searching for it.
    // Along with confirming it says who owns it if someone does and if it doesn't exist.
    @Test
    void case8() {
        String input = options.addBook + constructBookInput()
                + options.addMember + constructMemberInput()
                + options.searchForBook + book.name + "\n"
                + options.checkoutBook + "1234\nM001\n"
                + options.searchForBook + book.name + "\n"
                + options.searchForBook + "NonExistentBook" + "\n"
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains(bookWasFound(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains("The book is currently available."));
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains(bookIsAvailable(false)));
        assertTrue(output.contains("Currently checked out by Member ID: M001"));
        assertTrue(output.contains(bookWasFound(false)));
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
                + options.addBook + constructBookInput()
                + options.editBookInfo + book.bookID + "\n" + constructBookInput(updatedTitle, book.author, updatedYear, updatedISBN, null, updatedGenre)
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

    // Likewise this does the same with a member's info.
    @Test
    void case11() {
        String updatedName = "Jane Smith";
        String updatedEmail = "jane.smith@example.com";
        String updatedMemberID = "M002";

        String input = options.editMemberAccount + "M999\n"
                + options.addMember + constructMemberInput()
                + options.editMemberAccount + member.memberID + "\n" + constructMemberInput(updatedName, updatedEmail, updatedMemberID)
                + options.listMembers
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains("Member not found."));
        assertTrue(output.contains("Member information updated successfully."));
        assertTrue(output.contains(updatedName));
        assertTrue(output.contains(updatedEmail));
        assertTrue(output.contains(updatedMemberID));
    }

    // This just makes sure that trying to run exit works fine.
    @Test
    void case12() {
        String input = options.exit;
        String output = runCLIWithInput(input);
        assertTrue(output.contains(systemExit()));
    }

    // This test is just to confirm that the default works.
    @Test
    void testDefaultCase() {
        String input = "99\n"   // invalid input
                + options.exit;
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Invalid option."));
        assertTrue(output.contains(systemExit()));
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
    private String constructBookInput(String name, String author, String year, String isbn, String bookID, String genre) {
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
    // if you need to check for the books values, use the DefaultBook book's variables
    private String constructBookInput() {
        String delimiter = "\n";
        String input = "";

        DefaultBook book = new DefaultBook();

        ArrayList<String> bookValues = new ArrayList<>(List.of(book.name, book.author, book.year, book.isbn, book.bookID, book.genre));
        for (String value: bookValues) {
            input += value + delimiter;
        }

        return input;
    }

    private String constructMemberInput(String name, String email, String memberID) {
        String delimiter = "\n";
        String input = "";

        ArrayList<String> memberValues = new ArrayList<>(
                Stream.of(name, email, memberID)
                        .filter(s -> s != null) // have to filter out null objects because you can't use List.of() on nulls
                        .toList()
        );

        for (String value: memberValues) {
            input += value + delimiter;
        }
        return input;
    }

    private String constructMemberInput() {
        String delimiter = "\n";
        String input = "";

        DefaultMember member = new DefaultMember();

        ArrayList<String> memberValues = new ArrayList<>(List.of(member.name, member.email, member.memberID));
        for (String value: memberValues) {
            input += value + delimiter;
        }
        return input;
    }

    //--- Assertion Helpers
    // Since we were never actually taught how to use AssertJ + I don't know if you want us trying to use it for this project,
    // I decided to make helpers that return the string we expect to decouple the strings a bit, trying to make the tests a bit less sensitive
    // or at least easier to update, as well as try to follow the textbook's example with how he tested his interface
    private String systemExit() {
        return "Exiting system. Goodbye!";
    }

    private String bookIsAvailable(boolean yes) {
        String output = "Available: ";
        if (yes) {
            return output + "Yes";
        }
        else {
            return output + "No";
        }
    }

    private String bookWasFound(boolean yes) {
        if (yes) {
            return "Book found:";
        }
        else {
            return "Book not found.";
        }
    }

    private String booksInLibrary(boolean yes) {
        if (yes) {
            return "Books currently in the library:";
        }
        else {
            return "No books are currently in the library.";
        }
    }

    private String membersInLibrary(boolean yes) {
        if (yes) {
            return "Members currently in the library:";
        }
        else {
            return "No members are currently in the library.";
        }
    }


}
