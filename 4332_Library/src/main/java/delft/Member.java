package delft;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Member {
    public String name;
    public String email;
    public String memberID;
    public List<Book> BorrowedBookList;

    // Constructor
    public Member(String name, String email, String memberID, List<Book> BorrowedBookList) {
        this.name = name;
        this.email = email;
        this.memberID = memberID;
        this.BorrowedBookList = (BorrowedBookList != null) ? BorrowedBookList : new ArrayList<>();
        // By default, a new member is made
    }

    // project methods

    // prints out the memberID, name and email
public void printMemberInfo(PrintStream out) {
    out.println("Name: " + name);
    out.println("Email: " + email);
    out.println("Member ID: " + memberID);
}


    // returns the info of the book list
    public List<Book> getBorrowedbookList() {
        return BorrowedBookList;
    }

    // adds the book to the list
    public void addBorrowedBook(Book book) {
        this.BorrowedBookList.add(book);
    }

    // updates the member's name, email and Member ID
    public void updateMemberInfo(String name, String email, String memberID, List<Book> BorrowedBookList) {
        this.name = name;
        this.email = email;
        this.memberID = memberID;
        this.BorrowedBookList = BorrowedBookList;
    }

    // removes the specfic book from the list
    public void removeBorrowedBook(Book book) {
        this.BorrowedBookList.remove(book);

    }

}
