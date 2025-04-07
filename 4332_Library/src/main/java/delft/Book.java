package delft;

public class Book {
    private String name;
    private String author;
    private int year;
    private String ISBN;
    private String bookID;
    private boolean isAvailable;
    private String genre;

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

    // Getters
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getBookID() {
        return bookID;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getGenre() {
        return genre;
    }

    // Setters and Update Methods
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

    // Other Methods
    public boolean checkAvailability() {
        return isAvailable;
    }

    public void getBookInfo() {
        System.out.println("java.Book ID: " + bookID);
        System.out.println("Title: " + name);
        System.out.println("Author: " + author);
        System.out.println("Year: " + year);
        System.out.println("ISBN: " + ISBN);
        System.out.println("Genre: " + genre);
        System.out.println("Availability: " + (isAvailable ? "Available" : "Not Available"));
    }
}
