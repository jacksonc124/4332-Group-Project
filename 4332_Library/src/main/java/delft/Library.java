package delft;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Library {
    public List<String> AvailableBookIDs;
    public HashMap<String, String> LoanedBooks; // HashMap of BookID, MemberID
    public List<Book> AllBooksInLibrary;
    public List<String> MemberIDs;

    //--- Constructors

    // default constructor - no existing books or members
    public Library() { 
        this.AllBooksInLibrary = new ArrayList<>();
        this.AvailableBookIDs = new ArrayList<>();
        this.LoanedBooks = new HashMap<>();
        this.MemberIDs = new ArrayList<>();
    }

    // populate library w/ existing books & members
    // would normally implement helper methods for grabbing the book/memberIDs, but
    // UML does not list these so just dumping everything in the constructor
    public Library(List<Book> books, List<Member> members) { 
        this.LoanedBooks = new HashMap<>();

        // library methods assume class variables are initialized, hence null checks
        // of course, this relies on the promise that the Library variables are not modified externally...
        // for the purposes of this, assuming that we can pretend that the public vars are "private"
        this.AvailableBookIDs = new ArrayList<>();
        if (books == null) { this.AllBooksInLibrary = new ArrayList<>(); }
        else {
            this.AllBooksInLibrary = books;
            for (Book book : books) {
                this.AvailableBookIDs.add(book.bookID); // by default, all books are available
            }
        }

        this.MemberIDs = new ArrayList<>();
        if (members != null) {
            for (Member member: members) {
                this.MemberIDs.add(member.memberID);
            }
        }
    }

    //--- Book handling
    // Ordinarily, Member BorrowedBookList would be updated with certain actions 
    // (i.e., removeBook, checkoutBook, returnBook, etc) but strictly following the UML, the
    // library does not appear to require functionality for handling this, so it's not included

    public void addBook(Book book) {
        if (book != null) {
            this.AllBooksInLibrary.add(book);
            this.AvailableBookIDs.add(book.bookID);
        }
    }

    public void removeBook(Book book) {
        this.AllBooksInLibrary.remove(book);

        // handling book removal based on whether it is currently available/loaned
        if (this.AvailableBookIDs.contains(book.bookID)) { // remove from available
             this.AvailableBookIDs.remove(book.bookID);
        }
        else if (this.LoanedBooks.containsKey(book.bookID)) { // remove from loaned
            this.LoanedBooks.remove(book.bookID);
        }
    }

    public void checkoutBook(String bookID, String memberID) {
        if (this.MemberIDs.contains(memberID)) {
            if (this.AvailableBookIDs.contains(bookID)) {
                this.LoanedBooks.put(bookID, memberID);
                this.AvailableBookIDs.remove(bookID);
            }
        }
    }

    public void returnBook(String bookID, String memberID) {
        if (this.MemberIDs.contains(memberID)) {
            if (this.LoanedBooks.get(bookID) == memberID) {
                this.LoanedBooks.remove(bookID);
                this.AvailableBookIDs.add(bookID);
            }
        }
    }

    // note: does not check if book even exists in library
    public boolean bookAvailability(String bookID) {
        if (this.AvailableBookIDs.contains(bookID) ) {
            return true;
        }
        else { 
            return false; 
        }
    }

    // return bookID associated with name, return null if it doesn't exist
    // case insensitive
    public String findBookIdByName(String name) {
        for (Book book : this.AllBooksInLibrary) {
            if (book.name.toLowerCase() == name.toLowerCase()) {
                return book.bookID;
            }
        }
        return null;
    }

    // return memberID of member who has the book, return null if no one has it
    public String whoHasBook(String bookID) {
        if (this.LoanedBooks.containsKey(bookID)) {
            return this.LoanedBooks.get(bookID);
        }
        else {
            return null;
        }
    }

    //--- Member handling

    public void addMember(Member member) {
        if (member != null) {
            this.MemberIDs.add(member.memberID);
        }
    }

    public void revokeMembership(Member member) {
        this.MemberIDs.remove(member.memberID);
    }

    public List<String> getAllMembers() { 
        return this.MemberIDs;
    }
    
}
