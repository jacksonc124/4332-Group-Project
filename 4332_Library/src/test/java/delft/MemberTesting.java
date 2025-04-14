package delft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;


public class MemberTesting {
    private Member member;
    private Book thebook;
    private List<Book> books2;
    private Member member2;

    @BeforeEach
    void setUp() {
    member = new Member("Eby" , "eby@gmail.com" ,"V1234", null);
    thebook = new Book("1984", "George Orwell", 1949, "123456789", "B001", "Dystopian");
    books2 = new ArrayList<>();
    }

    //--- Specification tests

    //tests the print member info to make sure that the object return something
    @Test
    void testPrintMemberInfo() {
        // Validate no exception is thrown
        //assertDoesNotThrow(() -> member.printMemberInfo());
    }

    //tests the update member info to make sure that the object match
    @Test
    void testUpdateMemberInfo() {
        member.updateMemberInfo("Sam", "vby@gmail.com", "V1235", books2);
        assertEquals("Sam", member.name);
        assertEquals("vby@gmail.com", member.email);
        assertEquals("V1235", member.memberID);
        assertEquals(books2, member.BorrowedBookList);
    }

    //checks list to make sure that it has increased after removal
    @Test
    void testAddBorrowedBooks(){
        member.addBorrowedBook(thebook);
        assertEquals(1, member.BorrowedBookList.size());

    }

    //checks list to make sure that it has decreased after removal
    @Test
    void testRemoveBorrowedBooks(){
        member.addBorrowedBook(thebook);
        member.removeBorrowedBook(thebook);
        assertEquals(0, member.BorrowedBookList.size());
    }


    //checks list to be the same as the return from method
    @Test
    void testgetBorrowedbookList(){
        List<Book> checker = member.getBorrowedbookList();
        assertEquals(new ArrayList<Book>(), checker);
    }

    //null test, makes sure that if null makes array list
    @Test
    void testgetBorrowedbookListNull(){
        member2 = new Member("Eby" , "eby@gmail.com" ,"V1234", null);
        assertEquals(new ArrayList<Book>(), member2.BorrowedBookList);
    }

    //null test, makes sure that if not null matches correct list
    @Test
    void testgetBorrowedListNullBranch(){
        books2.add(thebook);
        member2 = new Member("Eby" , "eby@gmail.com" ,"V1234", books2);
        assertEquals(books2, member2.BorrowedBookList);
    }


}
