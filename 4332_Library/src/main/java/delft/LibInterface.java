package delft;

public class LibInterface {
    public static void main(String[] args) {

        Book book1 = new Book("Moby Dick", "Roald Dahl", 2002, "Help", "A22", "Fiction");
        System.out.println(book1.getAuthor());
    }
}