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

    // Helper method that returns output stream from program for assertion purposes
    // and so output can be verified
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
        // instantiating a default book & member for testing cases where specific values don't matter
        book = new DefaultBook();
        member = new DefaultMember();

        // instantiating structure with pre-defined variables for the interface options for readability
        options = new InterfaceOptions();
    }

    // This is just testing the main method for code coverage's sake. (structural)
    @Test
    void testMainMethod() {
        String input = options.exit;

        // initializing input/output streams
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());   // initializing input with exit option
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        LibInterface.main(new String[]{});  // call LibInterface main explicitly for the code coverage, since the other tests don't exercise the main

        // setting the program's input/output streams to the ones we just made
        System.setIn(System.in);
        System.setOut(System.out);

        // assert that the input produced the correct output & that the input/output streams are working as expected
        String output = out.toString();
        assertTrue(output.contains(systemExit()));
    }

    // This test just confirms that before anything is done that the library is empty.
    @Test
    void libraryStartsEmpty() {
        String input = options.displayAll   // display all books - shouldn't be any listed in output
                + options.exit;             // always need to signal exit at end of test, otherwise program will crash
        String output = runCLIWithInput(input);

        assertTrue(output.contains(booksInLibrary(false)));
        assertFalse(output.contains(booksInLibrary(true)));
    }

    // This test adds a book then runs list all books to confirm that the book is actually there.
    // It also checks the non-number year catch. (specification)
    @Test
    void libraryContainsAddedBook() {
        String input = options.addBook
                + constructBookInput(book.name, book.author, null, null, null, null)
                + "nah\n"   // invalid input to year; it must be interpretable as an integer -> should display error message but allow continued input
                + constructBookInput(null, null, book.year, book.isbn, book.bookID, book.genre) // finish input for adding default book to Library
                + options.displayAll    // display books in library, so we can check if the book was added
                + options.exit;
        String output = runCLIWithInput(input);

        // [add book] message check
        assertTrue(output.contains(invalidYearError())); // error message for invalid year input
        assertTrue(output.contains(bookAdded()));

        // [display books] message check: should list all attributes of our newly added book
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(book.author));
        assertTrue(output.contains(book.year));
        assertTrue(output.contains(book.isbn));
        assertTrue(output.contains(book.bookID));
        assertTrue(output.contains(book.genre));

        assertFalse(output.contains(booksInLibrary(false)));
    }

    // This test adds, then removes a book then runs list all books to confirm that the book is no longer there (specification)
    @Test
    void libraryDoesNotContainRemovedBook() {
        String invalidBookID = "9999";

        String input = options.removeBook + invalidBookID + "\n"    // attempting to remove a book that doesn't exist
                + options.addBook + constructBookInput()    // add default book -> Library, this book should be available to remove
                + options.removeBook + book.bookID + "\n"   // attempting to remove default book from Library -> should succeed
                + options.displayAll                        // should say there are no books to display
                + options.exit;
        String output = runCLIWithInput(input);

        // [remove book] check: make sure we get an error for attempting to remove a non-existent book
        assertTrue(output.contains(bookWasFound(false)));

        // [add book, remove book] message checks: verify default book was added & removed
        assertTrue(output.contains(bookAdded()));
        assertTrue(output.contains(bookRemoved()));

        // [display books] message check: should show that there are no books in library
        assertTrue(output.contains(booksInLibrary(false)));

        assertFalse(output.contains(booksInLibrary(true)));
        assertFalse(output.contains(book.name));    // an additional check to make sure that book attributes aren't displayed
    }

    // This test adds a member then runs list all members to confirm the member was added. (specification)
    @Test
    void libraryContainsAddedMember() {
        String input = options.addMember + constructMemberInput()   // add default member
            + options.listMembers   // use to verify that member was added correctly
            + options.exit;
        String output = runCLIWithInput(input);

        // [add member] check: should show we succeeded in adding member
        assertTrue(output.contains(memberAdded()));

        // [list members] check: should say there are members & display the info for our new member
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains(member.email));
        assertTrue(output.contains(member.memberID));

        assertFalse(output.contains(membersInLibrary(false)));
    }

    // Add, then remove a member, using list all members to verify they were removed. Also tests that removing a member
    // makes their checked out books available again. Also tests the member not found check. (specification)
    @Test
    void libraryDoesNotContainRemovedMember() {
        String invalidMemberID = "M999";
        String validAuthCode = "373737";    // valid auth code we need to pass when revoking membership since it is reserved for full-time librarians

        String input = options.addBook + constructBookInput()   // add default book
                + options.addMember + constructMemberInput()    // add default member
                + options.removeMember + validAuthCode + "\n" + invalidMemberID + "\n"  // attempt to remove a non-existent member
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // checkout default book for default member
                + options.removeMember + member.memberID + "\n" // auth code doesn't need to be passed again here because librarian stores if you've authenticated
                + options.displayAll    // display all library books; should show the default book as available
                + options.listMembers   // display all members; to check our removed member is no longer listed
                + options.exit;
        String output = runCLIWithInput(input);

        // [remove member (fail, success), checkout book] checks: verify actions
        assertTrue(output.contains(memberNotFound()));   // message from when we attempt to remove non-existent member
        assertTrue(output.contains(bookCheckedOut()));
        assertTrue(output.contains(memberRemoved()));

        // [display books] checks: verify default book is listed as available
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(true)));

        assertFalse(output.contains(booksInLibrary(false)));
        assertFalse(output.contains(bookIsAvailable(false)));

        // [list members] checks: should show no members in library
        assertTrue(output.contains(membersInLibrary(false)));
        assertFalse(output.contains(membersInLibrary(true)));
    }

    // This test checks out a book and confirms that it's no longer available and that it's checked out by John Doe.
    // Along with the added checks for invalid book and user ID inputs. (specification)
    @Test
    void checkoutBookAndCheckAvailability() {
        String invalidBookID = "9999";
        String invalidMemberID = "M999";

        String input = options.addBook + constructBookInput()   // add default book
                + options.addMember + constructMemberInput()    // add default member
                // the following line of input contains "2" for the no to the checkout->purchase book option when a book isn't in the library
                + options.checkoutBook + invalidBookID + "\n" + member.memberID + "\n" + "2" + "\n" // attempt check out of invalid book for default member
                + options.checkoutBook + book.bookID + "\n" + invalidMemberID + "\n" // attempt to check out default book for invalid member
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n" // checkout default book for default member
                + options.displayAll
                + options.listMembers
                + options.exit;
        String output = runCLIWithInput(input);

        // [check out book] checks:
        assertTrue(output.contains(memberNotFound()));  // error message for invalid member ID
        assertTrue(output.contains("Book checked out successfully."));

        // [display books] checks:
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(false)));

        assertFalse(output.contains(booksInLibrary(false)));
        assertFalse(output.contains(bookIsAvailable(true)));

        // [list members] checks:
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains("Borrowed Books:"));
        assertTrue(output.contains(book.name));
    }

    // To counter that this test confirms that after returning a book that the member no longer has it and that it's back to being available.
    // Along with the check for if a member tries to return an already available book. Or if a member tries to return a book and someone
    // else has checked out. It looks convoluted though due to the kinda long branching paths. (specification)
    @Test
    void checkoutAndReturnBookAndCheckAvailability() {
        String otherMemberName = "Jane Smith";
        String otherMemberEmail = "jane.smith@example.com";
        String otherMemberMemberID = "M002";

        String invalidBookID = "9999";
        String invalidMemberID = "M999";

        String input = options.addBook + constructBookInput()   // add default book
                + options.addMember + constructMemberInput()    // add default member
                + options.addMember + constructMemberInput(otherMemberName, otherMemberEmail, otherMemberMemberID)  // add another member
                + options.returnBook + book.bookID + "\n" + invalidMemberID + "\n"  // try to check out default book for invalid member
                + options.returnBook + invalidBookID + "\n" + member.memberID + "\n"    // try to check out invalid book for valid member
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // checkout default book for default member
                + options.returnBook + book.bookID + "\n" + otherMemberMemberID + "\n"  // try to return default book for member who did not borrow it
                + options.returnBook + book.bookID + "\n" + member.memberID + "\n"      // return default book for default member, who did borrow it
                + options.returnBook + book.bookID + "\n" + member.memberID + "\n"      // try to return book again
                + options.displayAll    // list book and make sure it's status is correct
                + options.listMembers   // list members and make sure list of borrowed book is correct
                + options.exit;
        String output = runCLIWithInput(input);

        // [checkout book] checks:
        assertTrue(output.contains(bookWasFound(false)));
        assertTrue(output.contains(bookCheckedOut()));

        // [return book] checks:
        assertTrue(output.contains(bookIsNotLoanedToMember())); // for when we try to return book for the wrong member
        assertTrue(output.contains(bookReturned()));
        assertTrue(output.contains(bookIsAlreadyReturned()));   // for when we try to return book a 2nd time

        // [list books] checks:
        assertTrue(output.contains(booksInLibrary(true)));
        assertTrue(output.contains(book.name));
        assertTrue(output.contains(bookIsAvailable(true)));

        assertFalse(output.contains(booksInLibrary(false)));
        assertFalse(output.contains(bookIsAvailable(false)));

        // [list members] checks:
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.name));
        assertTrue(output.contains(memberHasNoBooksOnBorrow()));
        assertTrue(output.contains(otherMemberName));
        assertTrue(output.contains(memberHasNoBooksOnBorrow()));

        assertFalse(output.contains(membersInLibrary(false)));
    }

    // This test just confirms that Divergent was found when searching for it.
    // Along with confirming it says who owns it if someone does and if it doesn't exist. (specification)
    @Test
    void searchForBookAndCheckAvailability() {
        String invalidBookName = "NonExistentBook";

        String input = options.addBook + constructBookInput()   // add default book
                + options.addMember + constructMemberInput()    // add default member
                + options.searchForBook + book.name + "\n"      // search for default book
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // check out default book for default member
                + options.searchForBook + book.name + "\n"      // search for default book again, after check out
                + options.searchForBook + invalidBookName + "\n"    // search for invalid book
                + options.exit;
        String output = runCLIWithInput(input);

        // [search book] checks:
        assertTrue(output.contains(bookWasFound(true))); // from when we search for default book
        assertTrue(output.contains(searchedBookIsAvailable())); // make sure it says it's available before checkout
        assertTrue(output.contains(bookIsAvailable(false)));    // make sure it says it's unavailable after checkout
        assertTrue(output.contains(searchedBookCheckedOutBy(member.memberID))); // make sure if unavailable, it says who checked it out
        assertTrue(output.contains(bookWasFound(false))); // make sure we get a failure for searching for an invalid book

        // [check out book] check:
        assertTrue(output.contains(bookCheckedOut()));
    }

    // Case 9's core features (list members) get tested in other test so it's not needed here.

    // This test confirms that after adding a book and changing it that its info was actually changed.
    // Along with the non-valid book input check. (specification)
    @Test
    void checkBookInfoUpdates() {
        String updatedTitle = "Insurgent";
        String updatedYear = "2012";
        String updatedISBN = "ISBN-5678";
        String updatedGenre = "Action";

        String invalidBookID = "9999";

        String input = options.editBookInfo + invalidBookID + "\n"  // try to edit info for an invalid book
                + options.addBook + constructBookInput()    // add default book
                // edit the info for the default book
                + options.editBookInfo + book.bookID + "\n" + constructBookInput(updatedTitle, book.author, updatedYear, updatedISBN, null, updatedGenre)
                + options.displayAll    // list books
                + options.exit;
        String output = runCLIWithInput(input);

        // [edit book] checks:
        assertTrue(output.contains(bookWasFound(false)));   // error message for trying to edit a book that doesn't exist
        assertTrue(output.contains(bookInfoUpdated())); // success message when we edit the default book

        // [list books] checks: should not show the old information for the default book
        assertTrue(output.contains(updatedTitle));
        assertTrue(output.contains(book.author));
        assertTrue(output.contains(updatedYear));
        assertTrue(output.contains(updatedISBN));
        assertTrue(output.contains(updatedGenre));

        assertFalse(output.contains(book.name));
        assertFalse(output.contains(book.year));
        assertFalse(output.contains(book.isbn));
        assertFalse(output.contains(book.genre));
    }

    // Like above, this test confirms that after adding a member and changing it that its info was actually changed.
    // Along with the non-valid member input check. (specification)
    @Test
    void checkMemberInfoUpdates() {
        String updatedName = "Jane Smith";
        String updatedEmail = "jane.smith@example.com";
        String updatedMemberID = "M002";

        String invalidMemberID = "M999";

        String input = options.editMemberAccount + invalidMemberID + "\n"   // try to edit info for invalid member
                + options.addMember + constructMemberInput()    // add default member
                // edit info for default member
                + options.editMemberAccount + member.memberID + "\n" + constructMemberInput(updatedName, updatedEmail, updatedMemberID)
                + options.listMembers   // list member info
                + options.exit;
        String output = runCLIWithInput(input);

        // [edit member] checks:
        assertTrue(output.contains(memberNotFound()));  // error message for trying to edit invalid member
        assertTrue(output.contains(memberInfoUpdated()));   // success message for editing default member

        // [list members] checks: should not show old member info for the default member
        assertTrue(output.contains(updatedName));
        assertTrue(output.contains(updatedEmail));
        assertTrue(output.contains(updatedMemberID));

        assertFalse(output.contains(member.name));
        assertFalse(output.contains(member.email));
        assertFalse(output.contains(member.memberID));
    }

    // This just makes sure that trying to run exit works fine. (specification)
    @Test
    void exitOptionWorks() {
        String input = options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains(systemExit()));
    }

    // This test is just to confirm that the default switch case works, 
    // i.e. that entering invalid input gives an error message. (specification)
    @Test
    void testDefaultCase() {
        String invalidOption = "99";

        String input = invalidOption + "\n"
                + options.exit;
        String output = runCLIWithInput(input);

        assertTrue(output.contains(invalidOption()));   // error message for entering an invalid option
        assertTrue(output.contains(systemExit()));
    }

    // These are just a series of all around null test. (specification)
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

    // --- PROJECT 2 NEW TESTS --- //

    // This tests that attempting a restricted action (remove a member) & 
    // failing authentication does not allow you to remove a member. (specification)
    @Test
    void authenticationFailDoesNotAllowAction() {
        String invalidAuthCode = "999999";

        String input = options.addMember + constructMemberInput() // add default member
                + options.removeMember // revoking membership should require authentication
                + invalidAuthCode + "\n"    // passing invalid authentication code to authentication prompt
                + options.listMembers   // list members and make sure there is still a member in the library
                + options.exit;

        String output = runCLIWithInput(input);

        // [authenticate] checks:
        assertTrue(output.contains(authenticated(false)));
        assertFalse(output.contains(authenticated(true)));

        // [list members] checks: library should say it has the default member & display their info
        assertTrue(output.contains(membersInLibrary(true)));
        assertTrue(output.contains(member.memberID));

        assertFalse(output.contains(membersInLibrary(false)));
    }

    // This tests that attempting a restricted action (remove a member) and 
    // passing authentication allows you to complete the action. (specification)
    @Test
    void authenticationSuccessAllowsAction() {
        String validAuthCode = "373737";
        String input = options.addMember + constructMemberInput() // add default member
                + options.removeMember // this option should trigger authentication prompt
                + validAuthCode + "\n"      // passing our authentication code to authentication prompt
                + member.memberID + "\n"    // once authenticated, passing member ID to remove member prompt
                + options.listMembers   // check that the library no longer has any members
                + options.exit;

        String output = runCLIWithInput(input);

        // [authenticate] checks:
        assertTrue(output.contains(authenticated(true)));
        assertFalse(output.contains(authenticated(false)));

        // [list members] checks: make sure library shows it has no members
        assertTrue(output.contains(membersInLibrary(false)));

        assertFalse(output.contains(membersInLibrary(true)));
        assertFalse(output.contains(member.memberID));
    }

    // Tests that attempting to check out a non-existent book prompts for purchase & allows checkout when
    // authenticating successfully. (specification)
    @Test
    void fulltimeLibrarianCanBuyBookForCheckout() {
        String validAuthCode = "373737";

        String input = options.addMember + constructMemberInput()   // add a default member to check out book for
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // default book doesn't exist, so this should prompt for purchase
                + "1" + "\n" // purchase prompt takes 1 for "yes"; this should lead to authentication prompt since we haven't authenticated before
                + validAuthCode + "\n"  // since we should successfully authenticate, after this should be prompt for name of book to purchase
                + book.name + "\n"      // entering book for purchase; this should lead to prompts for adding a book to library
                + constructBookInput(null, book.author, book.year, book.isbn, null, book.genre)  // add book inputs; skipping name/bookID since it was already given
                + options.exit;         // purchase should lead to automatic checkout, so we're done now

        String output = runCLIWithInput(input);

        // [authenticate] checks:
        assertTrue(output.contains(authenticated(true)));
        assertFalse(output.contains(authenticated(false)));

        // [buy book] checks:
        assertTrue(output.contains(boughtBook(true, book.name)));
        assertFalse(output.contains(boughtBook(false, book.name)));

        // [check out book] check:
        assertTrue(output.contains(bookCheckedOut()));
    }

    // Tests that attempting to check out a non-existent book prompts for purchase & prevents it
    // when failing authentication (specification)
    @Test
    void parttimeLibrarianCannotBuyBookForCheckout() {
        String invalidAuthCode = "999999";

        String input = options.addMember + constructMemberInput()   // add default member
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // try to check out book that doesn't exist
                + "1" + "\n"    // 1 = yes, we want to purchase this book
                + invalidAuthCode + "\n"    // should fail authentication
                + options.exit;

        String output = runCLIWithInput(input);

        // [authenticate] checks:
        assertTrue(output.contains(authenticated(false)));
        assertFalse(output.contains(authenticated(true)));

        // [purchase book] checks: we shouldn't see any messaging relating to purchasing a book at all b/c we didn't pass authentication
        assertFalse(output.contains(boughtBook(true, book.name)));
        assertFalse(output.contains(boughtBook(false, book.name)));

        // [check out book] check: check out shouldn't happen b/c without purchase, there's no book to check out
        assertFalse(output.contains(bookCheckedOut()));
    }

    // Tests that attempting to check out a non-existent book prompts for purchase & that it doesn't
    // force purchase if you say no. Also tests that the invalid option check works. (specification)
    @Test
    void checkoutBookDoesNotForcePurchase() {
        String invalidOption = "3";

        String input = options.addMember + constructMemberInput()   // add default member
                +  options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"   // try to check out invalid book for default member
                + invalidOption + "\n"  // passing an invalid option should prompt user to input again
                + "2" + "\n"    // 2 = no, we don't want to purchase this book
                + options.exit;

        String output = runCLIWithInput(input);

        // [authenticate] checks: shouldn't see any authentication pass/fail because we didn't try to purchase
        assertFalse(output.contains(authenticated(false)));
        assertFalse(output.contains(authenticated(true)));

        // [error] check: we should get an error message for passing an invalid option
        assertTrue(output.contains(invalidOption()));

        // [check out book] check: check out shouldn't happen b/c without purchase, there's no book to check out
        assertFalse(output.contains(bookCheckedOut()));
    }

    // Tests that after first check out, a book cannot be checked out by a different person (structural - was missing from specification)
    @Test
    void cannotCheckoutABookAlreadyCheckedOut() {
        String otherMemberName = "Jane Smith";
        String otherMemberEmail = "jane.smith@example.com";
        String otherMemberMemberID = "M002";

        String input = options.addMember + constructMemberInput()   // add default member
                + options.addMember + constructMemberInput(otherMemberName, otherMemberEmail, otherMemberMemberID)  // add another member
                + options.addBook + constructBookInput()    // add default book
                + options.checkoutBook + book.bookID + "\n" + member.memberID + "\n"    // checkout default book -> default member
                + options.checkoutBook + book.bookID + "\n" + otherMemberMemberID + "\n"    // try to check out default book -> other member
                + options.searchForBook + book.name + "\n"    // so we can verify book is still checked out to default member
                + options.exit;

        String output = runCLIWithInput(input);

        // [search book] checks: make sure book is checked out by default member, not the other member
        assertTrue(output.contains(searchedBookCheckedOutBy(member.memberID)));
        assertFalse(output.contains(searchedBookCheckedOutBy(otherMemberMemberID)));
    }


    // --- HELPER METHODS --- //

    // Constructs book input using given values
    // If values are passed as null, the input construction skips those values so you can construct partially valid input
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

    // Constructs book input using the default book values (if you don't care about specific values)
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

    // Constructs member input using given values
    // If values are passed as null, the input construction skips those values so you can construct partially valid input
    // but still customize for invalid cases
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

    // Constructs member input using the default member values (if you don't care about specific values)
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

    // message displayed if you enter option: exit to the interface
    private String systemExit() {
        return "Exiting system. Goodbye!";
    }

    // message displayed if you enter an invalid option to the interface
    private String invalidOption() {
        return "Invalid option.";
    }

    // message displayed if you enter an invalid year when trying to add a book
    private String invalidYearError() {
        return "Invalid input. Please enter a valid year as an integer.";
    }

    // messages displayed upon passing/failing authentication for an action
    private String authenticated(boolean yes) {
        if (yes) {
            return "Thank you for authenticating. You may proceed with your action.";
        }
        else {
            return "Authentication failed.";
        }
    }

    // BOOK ASSERTIONS
    // message displayed by displayAll (lists all books) if a book is un/available
    private String bookIsAvailable(boolean yes) {
        String output = "Available: ";
        if (yes) {
            return output + "Yes";
        }
        else {
            return output + "No";
        }
    }

    // message displayed if a book is not in the library and thus can't be searched/edited/etc
    private String bookWasFound(boolean yes) {
        if (yes) {
            return "Book found:";
        }
        else {
            return "Book not found.";
        }
    }

    // message displayed by displayAll (lists all books) if there are/n't books in the library
    private String booksInLibrary(boolean yes) {
        if (yes) {
            return "Books currently in the library:";
        }
        else {
            return "No books are currently in the library.";
        }
    }

    private String bookAdded() {
        return "Book added successfully.";
    }

    private String bookRemoved() {
        return "Book removed successfully.";
    }

    private String bookReturned() {
        return "Book returned successfully.";
    }

    // message displayed if a member tries to return a book they have not borrowed
    private String bookIsNotLoanedToMember() {
        return "This book is not loaned to the specified member.";
    }

    // message displayed if a member tries to return a book that is already available/returned
    private String bookIsAlreadyReturned() {
        return "Book is already available and cannot be returned.";
    }

    private String bookCheckedOut() {
        return "Book checked out successfully.";
    }

    private String searchedBookIsAvailable() {
        return "The book is currently available.";
    }

    private String searchedBookCheckedOutBy(String memberID) {
        return "Currently checked out by Member ID: " + memberID;
    }

    private String bookInfoUpdated() {
        return "Book information updated successfully.";
    }

    private String boughtBook(boolean yes, String name) {
        if (yes) {
            return "Successfully purchased book: " + name;
        }
        else {
            return "Failed to purchase book: " + name;
        }
    }

    // MEMBER ASSERTIONS
    // message displayed by full member list / list members options if there are/n't members in the library
    private String membersInLibrary(boolean yes) {
        if (yes) {
            return "Members currently in the library:";
        }
        else {
            return "No members are currently in the library.";
        }
    }

    // message displayed by full member list/ list members options if a member doesn't have books on borrow
    private String memberHasNoBooksOnBorrow() {
        return "No books currently borrowed.";
    }

    private String memberAdded() {
        return "New member has been successfully added.";
    }

    private String memberRemoved() {
        return "Member removed successfully, and all borrowed books have been returned.";
    }

    // message displayed if a member is not in the library and hence can't be searched/edited/etc
    private String memberNotFound() {
        return "Member not found.";
    }

    private String memberInfoUpdated() {
        return "Member information updated successfully.";
    }










}
