package delft;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {

    private String runCLIWithInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Library library = new Library();
        LibInterface cli = new LibInterface(library, new Scanner(in), new PrintStream(out));
        cli.run();
        return out.toString();
    }

    @Test
    void testDisplayEmptyLibrary() {
        String input = "1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("No books are currently in the library."));
    }

    @Test
    void testAddBook() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book added successfully."));
        assertTrue(output.contains("Divergent"));
    }

    @Test
    void testRemoveBook() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n3\n1234\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book removed successfully."));
        assertTrue(output.contains("No books are currently in the library."));
    }

    @Test
    void testAddMember() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("New member has been successfully added."));
        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
    }

    @Test
    void testRemoveMember() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n5\nM001\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Member removed successfully, and all borrowed books have been returned."));
        assertTrue(output.contains("No members are currently in the library."));
    }

    @Test
    void testCheckoutBook() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n4\nJohn Doe\njohn.doe@example.com\nM001\n6\n1234\nM001\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book checked out successfully."));
        assertTrue(output.contains("No books are currently in the library."));
    }

    @Test
    void testReturnBook() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n4\nJohn Doe\njohn.doe@example.com\nM001\n6\n1234\nM001\n7\n1234\nM001\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book returned successfully."));
        assertTrue(output.contains("Divergent"));
    }

    @Test
    void testSearchBook() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n8\nDivergent\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book found:"));
        assertTrue(output.contains("Divergent"));
    }

    @Test
    void testDisplayAllMembers() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Members currently in the library:"));
        assertTrue(output.contains("John Doe"));
    }

    @Test
    void testEditBookInfo() {
        String input = "2\nDivergent\nVeronica Roth\n2011\nISBN-1234\n1234\nDystopian\n10\n1234\nInsurgent\nVeronica Roth\n2012\nISBN-5678\nAction\n1\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Book information updated successfully."));
        assertTrue(output.contains("Insurgent"));
    }

    @Test
    void testEditMemberInfo() {
        String input = "4\nJohn Doe\njohn.doe@example.com\nM001\n11\nM001\nJane Doe\njane.doe@example.com\nM002\n9\n12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Member information updated successfully."));
        assertTrue(output.contains("Jane Doe"));
    }

    @Test
    void testExit() {
        String input = "12\n";
        String output = runCLIWithInput(input);
        assertTrue(output.contains("Exiting system. Goodbye!"));
    }
}