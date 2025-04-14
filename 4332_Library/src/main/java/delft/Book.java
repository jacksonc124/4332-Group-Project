package delft;

import java.io.PrintStream;

public class Book {
    public String name;
    public String author;
    public int year;
    public String ISBN;
    public String bookID;
    public boolean isAvailable;
    public String genre;

    // Constructor
    public Book(String name, String author, int year, String ISBN, String bookID, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.ISBN = ISBN;
        this.bookID = bookID;
        this.genre = genre;
        this.isAvailable = true; // By default, a new book is available
    }

    // Update Method
    public void updateBookInfo(String name, String author, int year, String ISBN, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.ISBN = ISBN;
        this.genre = genre;
    }

    public void setAvailability(boolean status) {
        this.isAvailable = status;
    }

    // Check Availability of Book
    public boolean checkAvailability() {
        return isAvailable;
    }

    // Book toString
public void getBookInfo(PrintStream out) {
    out.println("Title: " + name);
    out.println("Author: " + author);
    out.println("Year: " + year);
    out.println("ISBN: " + ISBN);
    out.println("Book ID: " + bookID);
    out.println("Genre: " + genre);
    out.println("Available: " + (isAvailable ? "Yes" : "No"));
}


}
