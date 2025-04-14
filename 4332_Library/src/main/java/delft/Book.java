package delft;

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
    public void getBookInfo() {
        System.out.println("Book ID: " + bookID);
        System.out.println("Title: " + name);
        System.out.println("Author: " + author);
        System.out.println("Year: " + year);
        System.out.println("ISBN: " + ISBN);
        System.out.println("Genre: " + genre);
        System.out.println("Availability: " + (isAvailable ? "Available" : "Not Available"));
    }

}
