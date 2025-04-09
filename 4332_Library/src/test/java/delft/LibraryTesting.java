package delft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jqwik.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryTesting {
    private Library library;

    @BeforeEach
    void setup() {
        library = new Library();
    }

    //--- Specification tests

    // Library(books, members) - populating constructor
    @Test
    void makeLibraryWithNullBooks() { // books are null
        // make sure book variables aren't initialized as null
        Member member = new Member(null, null, null, null);
        List<Member> memberList = new ArrayList<Member>(Arrays.asList(member, member, member));

        Library nullBooksLibrary = new Library(null, memberList);

        assertEquals(nullBooksLibrary.AvailableBookIDs, new ArrayList<String>());
        assertEquals(nullBooksLibrary.AllBooksInLibrary, new ArrayList<Book>());
    }

    @Test
    void makeLibraryWithNullMembers() { // members are null
        // make sure member variable not initialized as null

        Book book = new Book(null, null, 0, null, null, null);
        List<Book> bookList = new ArrayList<>(Arrays.asList(book, book, book));

        Library nullMembersLibrary = new Library(bookList, null);

        assertEquals(nullMembersLibrary.MemberIDs, new ArrayList<String>());
    }

    // addBook()
    @Test
    void addBook() { // add a book
        // ensure correct variables update
        Book book = new Book(null, null, 0, null, null, null);
        library.addBook(book);
        assertEquals(library.AllBooksInLibrary.size(), 1);
        assertEquals(library.AvailableBookIDs.size(), 1);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    @Test
    void addNullBook() { // attempt to add a null object as a book
        // variables should not update
        library.addBook(null);
        assertEquals(library.AllBooksInLibrary.size(), 0);
        assertEquals(library.AvailableBookIDs.size(), 0);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    // removeBook()
    @Test
    void removeAvailableBook() { // remove a book that is available
        // book should be removed from AllBooksInLibrary, AvailableBookIDs
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.AvailableBookIDs.add(book.bookID); 
        library.removeBook(book);

        assertEquals(library.AllBooksInLibrary.size(), 0);
        assertEquals(library.AvailableBookIDs.size(), 0);
    }

    @Test
    void removeLoanedBook() { // remove a book that is currently on loan
        // book should be removed from AllBooksInLibrary, LoanedBooks
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.LoanedBooks.put(book.bookID, ""); 
        library.removeBook(book);

        assertEquals(library.AllBooksInLibrary.size(), 0);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    // checkoutBook()
    @Test
    void checkoutBook() { // both bookID, memberID are valid
        // make sure relevant variables update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.AvailableBookIDs.add(book.bookID); 
        library.MemberIDs.add("0");

        library.checkoutBook(book.bookID, "0");

        assertEquals(library.AvailableBookIDs.size(), 0);
        assertEquals(library.LoanedBooks.size(), 1);
    }

    @Test
    void checkoutBookAsNonMember() { // memberID is invalid
        // make sure variables do not update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.AvailableBookIDs.add(book.bookID); 
        library.MemberIDs.add("0");

        library.checkoutBook(book.bookID, "1");
        
        assertEquals(library.AvailableBookIDs.size(), 1);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    @Test
    void checkoutUnavailableBook() { // bookID is invalid, either it is loaned or doesn't exist
        // make sure variables do not update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.AvailableBookIDs.add(book.bookID); 
        library.MemberIDs.add("0");

        library.checkoutBook("0", "0");
        
        assertEquals(library.AvailableBookIDs.size(), 1);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    // returnBook()
    @Test
    void returnBook() { // book is loaned to this member
        // variables should update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.LoanedBooks.put(book.bookID, "0"); 
        library.MemberIDs.add("0");

        library.returnBook(book.bookID, "0");

        assertEquals(library.AvailableBookIDs.size(), 1);
        assertEquals(library.LoanedBooks.size(), 0);
    }
    
    @Test
    void returnUnloanedBook() { // book is not loaned
        // variables should not update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.AvailableBookIDs.add(book.bookID); 
        library.MemberIDs.add("0");

        library.returnBook(book.bookID, "0");

        assertEquals(library.AvailableBookIDs.size(), 1);
        assertEquals(library.LoanedBooks.size(), 0);
    }

    @Test
    void returnBookAsWrongMember() { // member did not borrow the book
        // variables should not update
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.LoanedBooks.put(book.bookID, "0"); 
        library.MemberIDs.add("0");

        library.returnBook(book.bookID, "1");

        assertEquals(library.AvailableBookIDs.size(), 0);
        assertEquals(library.LoanedBooks.size(), 1);
    }
    
    // bookAvailability()
    @Test
    void bookAvailabilityTrue() { // book is available
        // should return true
        library.AvailableBookIDs.add("0");
        assertTrue(library.bookAvailability("0"));
    }

    @Test
    void bookAvailabilityFalse() { // book is not available
        // should return false
        assertFalse(library.bookAvailability("0"));
    }

    // findBookIdByName()
    @Test
    void findBookIdByName() { // book exists
        // should return bookId
        Book book = new Book("booyah", null, 0, null, "123", null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        assertEquals(library.findBookIdByName("booyah"), book.bookID);
    }

    @Test
    void findNoBookIdByName() { // book doesn't exist
        // should return null
        Book book = new Book("booyah", null, 0, null, "123", null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        assertEquals(library.findBookIdByName("bork"), null);
    }

    // whoHasBook()
    @Test
    void whoHasBook() { // someone has book
        // should return the memberID
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.LoanedBooks.put(book.bookID, "0"); 
        library.MemberIDs.add("0");

        assertEquals(library.whoHasBook(null), "0");
    }

    @Test
    void noOneHasBook() { // no one has book, either because it is available or does not exist
        // should return null
        Book book = new Book(null, null, 0, null, null, null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods

        assertEquals(library.whoHasBook("0"), null);
    }

    // addMember()
    @Test
    void addMember() { // add a member
        // should update library variable
        Member member = new Member(null, null, null, null);
        library.addMember(member);
        assertEquals(library.MemberIDs.size(), 1);
    }

    @Test
    void addNullMember() { // attempt to add a null object as a member
        // should not update library variable
        library.addMember(null);
        assertEquals(library.MemberIDs.size(), 0);
    }

    @Test
    void addDupeMember() { // attempt to add a duplicate member
        // should not update library variable
        Member member = new Member(null, null, null, null);
        library.addMember(member);
        library.addMember(member);
        assertEquals(library.MemberIDs.size(), 1);
    }

    // revokeMembership()
    @Test
    void revokeMembership() { // member exists
        // ensure library variable updates
        Member member = new Member(null, null, null, null);
        library.MemberIDs.add(member.memberID);

        library.revokeMembership(member);
        assertEquals(library.MemberIDs.size(), 0);
    }

    @Test
    void revokeMembershipForNonMember() { // member is not in library
        // ensure library variable does not update
        Member member = new Member(null, null, null, null);
        Member nonMember = new Member(null, null, "0", null);
        library.MemberIDs.add(member.memberID);

        library.revokeMembership(nonMember);
        assertEquals(library.MemberIDs.size(), 1);
    }

    // getAllMembers()
    @Test
    void getAllMembers() { // checking the correct variable is retrieved
        Member member = new Member(null, null, null, null);
        library.MemberIDs.add(member.memberID);

        assertEquals(library.getAllMembers(), library.MemberIDs);
    }

    //--- Structural tests

    //removeBook()
    @Test
    void removeNonExtantBook() { // book doesn't exist at all
        // no variables should update
        // exercising condition check: book is not a book in LoanedBooks
        Book book = new Book(null, null, 0, null, null, null);
        Book otherBook = new Book(null, null, 0, null, "0", null);

        library.AllBooksInLibrary.add(book); // interacting directly with variables to not rely on inner methods
        library.LoanedBooks.put(book.bookID, "");
        library.removeBook(otherBook);

        assertEquals(library.AllBooksInLibrary.size(), 1);
        assertEquals(library.LoanedBooks.size(), 1);
    }
}