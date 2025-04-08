package delft;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Library {
    public List<String> AvailableBookIDs;
    public HashMap<String, String> LoanedBooks; // HashMap of BookID, MemberID
    public List<Book> AllBooksInLibrary;
    public List<String> MemberIDs; // may have to change based on Member implementation

    // Constructors

    public Library() { // default constructor - no existing books or members
        this.AllBooksInLibrary = null;
        this.AvailableBookIDs = null;
        this.LoanedBooks = null;
        this.MemberIDs = null;
    }

    public Library(List<Book> books, List<Member> members) { // populate library w/ existing books & members
        this.AllBooksInLibrary = books;
        this.AvailableBookIDs = getBookIDs(books); // by default, all books are available
        this.LoanedBooks = null;
        this.MemberIDs = getMemberIDs(members);
    }

    // Helper methods

    private List<String> getBookIDs(List<Book> books) {
        if (books == null) return null;

        List<String> bookIDs = new ArrayList<String>();
        for (Book book : books) {
            bookIDs.add(book.bookID);
        }
        return bookIDs;
    }

    private List<Integer> getMemberIDs(List<Member> members) { // have to wait for Member implementation
        if (members == null) return null;

        List<Integer> memberIDs = new ArrayList<>();
        for (Member member: members) {
            // memberIDs.add(member.MemberID);
        }
        return memberIDs;
    }

    // Book handling
    // Ordinarily, Member BorrowedBookList would be updated with certain actions (i.e., removeBook, checkoutBook, returnBook, etc)
    // but strictly following the UML, the library does not appear to require functionality for handling this, so it is not included

    public void addBook(Book book) {
        this.AllBooksInLibrary.add(book);
        this.AvailableBookIds.add(book.bookID);
    }

    public void removeBook(Book book) {
        this.AllBooksInLibrary.remove(book);

        // handling book removal based on whether it is currently available/loaned
        if (this.AvailableBookIds.contains(book.bookID)) { // remove from available
             this.AvailableBookIds.remove(book.bookID);
        }
        else if (this.LoanedBooks.containsKey(book.bookID)) { // remove from loaned
            this.LoanedBooks.remove(book.bookID);
        }
    }

    public void checkoutBook(String bookID, String memberID) {
        // check if member exists
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
            }
        }
    }

    public boolean bookAvailability(String bookID) {
        if (this.AvailableBookIDs.contains(bookID) ) {
            return true;
        }
        else { 
            return false; 
        }
    }

    public String findBookIdByName(String name) {
        for (Book book : this.AllBooksInLibrary) {
            if (book.getName() == name) return book.bookID;
        }
        return null;
    }

    public String whoHasBook(String bookID) { // may have to adjust based on type of MemberID
        if (this.LoanedBooks.containsKey(bookID)) {
            return this.LoanedBooks.get(bookID);
        }
        else {
            return -1;
        }
    }

    // Member handling - may have to adjust based on Member implementation

    public void addMember(Member member) {   // may also desire overrides for adding using String memberID
        // this.MemberIDs.add(member.MemberID);
    }

    public void revokeMembership(Member member) {
        // this.MemberIDs.remove(member.MemberID);
    }

    public List<String> getAllMembers() { 
        return this.MemberIDs;
    }
    
}
