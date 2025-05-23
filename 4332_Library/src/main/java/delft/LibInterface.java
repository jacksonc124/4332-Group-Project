package delft;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class LibInterface {
    private final Library library;
    private final Scanner scanner;
    private final PrintStream out;
    private final Librarians librarians;
    private final LibraryAccounts accounts;
    // Lists what options require authentication to be used (i.e. only full-time librarians can access)
    private final ArrayList<Integer> optionNeedsAuthentication = new ArrayList<>(List.of(5));
    private String authenticationCode;

    public LibInterface(Library library, Librarians librarians, LibraryAccounts accounts, Scanner scanner, PrintStream out) {
        this.library = library;
        this.librarians = librarians;
        this.accounts = accounts;
        this.scanner = scanner;
        this.out = out;
    }

    public void run() {

        out.println("\nGroup members: Alex Purcell, Arabelle Betzwieser, Eberechi Nwosu, Jack Cogan");

        // This is what the user will first see when starting. Containing all the options of how to proceed.
        while (true) {
            out.println("\nLSU Group I Library System");
            out.println("1. Display All");
            out.println("2. Add Book");
            out.println("3. Remove Book");
            out.println("4. Add Member");
            out.println("5. Remove Member");
            out.println("6. Checkout");
            out.println("7. Return");
            out.println("8. Search for Book");
            out.println("9. Full Member List");
            out.println("10. Edit Book Information");
            out.println("11. Edit Member Information");
            out.println("12. Exit");
            out.print("Enter your choice: ");
            out.print("\n");

            int options = scanner.nextInt();
            scanner.nextLine();

            // Request authentication if action is restricted
            while (optionNeedsAuthentication.contains(options) && authenticationCode == null) {
                authenticationCode = authenticate();    // Store code so if successful, user shouldn't have to reauthenticate this session
                if (authenticationCode == null) {
                    out.println("Could not perform the action. Please select another option: ");
                    options = scanner.nextInt();
                    scanner.nextLine();
                }
            }

            // For the UI I went with the simple approach of using switch statements.
            // At first all of the methods were coupled in with the switches but that made it super difficult to test so I split them.
            switch (options) {
                // I thought it would make sense to have an option to list all books. Like what if you wanna
                // just see what the options are.
                case 1:
                    displayAllBooks();
                    break;

                // This is everything needed to added a book. Simply asking the user for info then adding it to library.addBook.
                case 2:
                    addBook(null, null, -1, null, null, null);
                    break;

                // This case is for removing books. Using the books specific ID to search the list for the matching ID.
                // Then removing it.
                case 3:
                    removeBook();
                    break;

                // This case is for adding members. It more or less does the same thing as adding books just saving it to library.addMember.
                case 4:
                    addMember();
                    break;

                // This just removes a member from the list. It also more or less does the same thing as removing books just with a different list.
                case 5:
                    removeMember();
                    break;

                // This case if for checking out a book. It just finds the book ID entered while also making 
                // sure the memeber ID exist. The other part is checking if the book's avalibility is set to true and making
                // it false after the checkout.
                case 6:
                    checkoutBook();
                    break;

                // This case is for returning a book. I mostly made it just to have a way
                // to change a book's availability back to true so its code is bascally the same as case 6's. 
                case 7:
                    returnBook();
                    break;

                // This case is for searching a book up and displaying its information.
                case 8:
                    searchBook();
                    break;

                // This case is to list all current members and if that member has a booked checked out then list that with them.
                case 9:
                    displayAllMembers();
                    break;

                // This case deals with editing the information of a book.    
                case 10:
                    editBookInfo();
                    break;

                // This case deals with editing the information of any memebrs. It works the same as case 10 so its code
                // is borderline the same as it.
                case 11:
                    editMemberInfo();
                    break;

                // This case just deals with exiting out.
                case 12:
                    out.println("Exiting system. Goodbye!");
                    return;

                // The default just deals with if the user trys to enter a choice that doesn't exist.
                default:
                    out.println("Invalid option.");
            }
        }
    }
    public static void main(String[] args) {
        Library library = new Library();
        LibraryAccounts accounts = new LibraryAccounts();
        Librarians librarians = new Librarians();
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;

        LibInterface libInterface = new LibInterface(library, librarians, accounts, scanner, out);
        libInterface.run();
    }

    // Returns a Librarian's authentication code if it's valid, returns null if invalid
    private String authenticate() {
        out.println("Please enter your authentication code: ");
        String code = scanner.nextLine();

        if (librarians.isValidAuth(code)) {
            out.println("Thank you for authenticating. You may proceed with your action.");
        }
        else {
            out.println("Authentication failed.");
            code = null;    // We want to null out anything that's not a valid authentication code
        }

        return code;
    }

    private void displayAllBooks() {
        // If there are no books, say that, otherwise print the list.
        if (library.AllBooksInLibrary.isEmpty()) {
            out.println("No books are currently in the library.");
        } else {
            out.println("Books currently in the library:");
            for (Book book : library.AllBooksInLibrary) {
                book.getBookInfo(out);
                // Gotta make the list look pretty.
                out.println("--------------------");
            }
        }
    }

    // Modified method from previous version - it now takes in any pre-defined variables and asks for missing variables needed to add book
    // Pass null for variables that aren't pre-defined, except for year
    // Pass negative numbers -> year to signal it's not pre-defined, since you can't check for null ints
    private void addBook(String name, String author, int year, String isbn, String bookID, String genre) {
        if (name == null) {
            out.print("Enter book name: ");
            name = scanner.nextLine();
        }

        if (author == null) {
            out.print("Enter author: ");
            author = scanner.nextLine();
        }

        
        if (year < 0) {
            // This is different due to the whole program crashing if you enter a non int for the year and we didn't want that now did we?
            while (true) {
                out.print("Enter year: ");
                try {
                    year = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    out.println("Invalid input. Please enter a valid year as an integer.");
                }
            }
        }

        if (isbn == null) {
            out.print("Enter ISBN: ");
            isbn = scanner.nextLine();
        }

        if (bookID == null) {
            out.print("Enter book ID: ");
            bookID = scanner.nextLine();
        }

        if (genre == null) {
            out.print("Enter genre: ");
            genre = scanner.nextLine();
        }

        library.addBook(new Book(name, author, year, isbn, bookID, genre));
        out.println("Book added successfully.");
    }

    private void removeBook() {
        out.print("Enter book ID to remove: ");
        String bookID = scanner.nextLine();
        // This goes through the list filtering for the book's ID and since only one book should have its ID
        // then it goes after the first it finds. And if it finds nothing then it's null.
        Book bookToRemove = library.AllBooksInLibrary.stream()
                .filter(book -> book.bookID.equals(bookID))
                .findFirst()
                .orElse(null);
        // The book gets removed if it's not null and if it is then the book doesn't exist.
        if (bookToRemove != null) {
            library.removeBook(bookToRemove);
            out.println("Book removed successfully.");
        } else {
            out.println("Book not found.");
        }
    }

    private void addMember() {
        out.print("Enter new member's name: ");
        String memberName = scanner.nextLine();

        out.print("Enter new member's email: ");
        String email = scanner.nextLine();

        out.print("Enter new member's ID: ");
        String memberID = scanner.nextLine();

        library.addMember(new Member(memberName, email, memberID, null));
        out.println("New member has been successfully added.");
    }

    private void removeMember() {
        out.print("Enter current member ID to remove: ");
        String memberID = scanner.nextLine();

        // Another search
        Member memberToRemove = library.getAllMembers().stream()
                .filter(member -> member.memberID.equals(memberID))
                .findFirst()
                .orElse(null);

        if (memberToRemove != null) {
            // This case also needs to return any books that member had.
            for (Book book : memberToRemove.getBorrowedbookList()) {
                book.isAvailable = true;
                library.LoanedBooks.remove(book.bookID);
            }

            // And finally revoke their membership
            library.revokeMembership(memberToRemove);
            out.println("Member removed successfully, and all borrowed books have been returned.");
        } else {
            out.println("Member not found.");
        }
    }

    private void checkoutBook() {
        out.print("Enter book ID to checkout: ");
        String bookID = scanner.nextLine();
        out.print("Enter member ID: ");
        String memberID = scanner.nextLine();

        boolean canCheckout = true;

        // First check if the member exists - no point allowing checkout or book purchase for a member that doesn't exist
        if (!library.MemberIDs.contains(memberID)) {
            canCheckout = false;
            out.println("Member not found.");
            return;
        }

        // Now check if the book is available
        if (!library.bookAvailability(bookID)) {        // Is the book available? If not, it may not exist or is being borrowed
            canCheckout = false;
            if (library.whoHasBook(bookID) == null) {   // No one is borrowing book, it doesn't exist in library
                out.println("The library does not currently own this book.");
                while (true) {                          // Prompting user for purchase + checkout
                    out.println("Would you like to purchase this book and proceed to checkout?");
                    out.println("Enter 1 (yes) or 2 (no)");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // to consume the enter token

                    if (choice == 1) {  // 1 = yes
                        if (authenticationCode == null) {               // If no authentication on record, allow user to attempt to authenticate
                            authenticationCode = authenticate();
                        }
                        
                        if (authenticationCode != null) {               // Only full-time librarians are allowed to purchase books
                            out.print("Enter book name for purchase: ");
                            String bookName = scanner.nextLine();
                            canCheckout = buyBook(bookName, bookID);    // Only set to true if purchase was successful
                        } else {
                            out.println("Please call a full-time librarian for approval if you would like to purchase this book.");
                            // Nothing happens here, just informing the user
                        }
                        break;
                    }
                    else if (choice == 2) { // 2 = no
                        break;
                    }
                    else {
                        out.println("Invalid option.");
                    }
                }
            }
            else {
                out.println("This book is already on loan.");
            }
        }

        if (canCheckout) {  // If book is available (not on loan or was just purchased)
            library.checkoutBook(bookID, memberID);
            // Set the book's availability to false after checkout
            Book bookToCheckout = library.AllBooksInLibrary.stream()
                    .filter(book -> book.bookID.equals(bookID))
                    .findFirst()
                    .orElse(null);
            if (bookToCheckout != null) {
                bookToCheckout.isAvailable = false;

                // Update the member's BorrowedBookList so case 9 can list it.
                Member member = library.getAllMembers().stream()
                        .filter(m -> m.memberID.equals(memberID))
                        .findFirst()
                        .orElse(null);
                if (member != null) {
                    member.addBorrowedBook(bookToCheckout);
                }

                // Update the Librarian's purchase history 
                librarians.recordBookPurchase(authenticationCode, bookToCheckout.name);
            }
            out.println("Book checked out successfully.");
        }
    }

    // This should only be called after a successful authenticate()
    // since only full-time librarians are allowed to touch LibraryAccounts (accounts)
    private boolean buyBook(String bookName, String bookID) {
        if (accounts.buyBooks(bookName)) {
            addBook(bookName, null, -1, null, bookID, null);
            out.printf("Successfully purchased book: %s\n", bookName);
            return true;
        }
        else {
            out.printf("Failed to purchase book: %s\n", bookName);
            return false;
        }
    }

    private void returnBook() {
        out.print("Enter book ID to return: ");
        String bookID = scanner.nextLine();
        out.print("Enter member ID: ");
        String memberID = scanner.nextLine();
        Book bookToReturn = library.AllBooksInLibrary.stream()
                .filter(book -> book.bookID.equals(bookID))
                .findFirst()
                .orElse(null);

        if (bookToReturn == null) {
            out.println("Book not found.");
            return;
        }
        if (bookToReturn.isAvailable) {
            out.println("Book is already available and cannot be returned.");
            return;
        }

        // Check if the member exists for testing's sake.
        Member member = library.getAllMembers().stream()
                .filter(m -> m.memberID.equals(memberID))
                .findFirst()
                .orElse(null);


        // Check if the book is loaned to the specified member
        if (!library.LoanedBooks.containsKey(bookID) || !library.LoanedBooks.get(bookID).equals(memberID)) {
            out.println("This book is not loaned to the specified member.");
            return;
        }

        // Process the return
        library.returnBook(bookID, memberID);
        bookToReturn.isAvailable = true;
        member.removeBorrowedBook(bookToReturn);
        out.println("Book returned successfully.");
    }

    private void searchBook() {
        out.print("Enter book name to search: ");
        String searchName = scanner.nextLine();
        String foundBookID = library.findBookIdByName(searchName);

        if (foundBookID != null) {
            Book foundBook = library.AllBooksInLibrary.stream()
                    .filter(book -> book.bookID.equals(foundBookID))
                    .findFirst()
                    .orElse(null);

            // If the book exist then give all its info.
            if (foundBook != null) {
                out.println("Book found:");
                foundBook.getBookInfo(out);

                // If it is, availability is set to true and say that otherwise say who has it.
                if (foundBook.isAvailable) {
                    out.println("The book is currently available.");
                } else {
                    String borrowerID = library.whoHasBook(foundBook.bookID);
                    if (borrowerID != null) {
                        out.println("Currently checked out by Member ID: " + borrowerID);
                    }
                }
            }
        } else {
            // If the book doesn't exist then say so.
            out.println("Book not found.");
        }
    }

    private void displayAllMembers() {
        if (library.getAllMembers().isEmpty()) {
            out.println("No members are currently in the library.");
        } else {
            out.println("Members currently in the library:");
            // This just list all the current members.
            for (Member member : library.getAllMembers()) {
                member.printMemberInfo(out);
                // And this prints their borrowed book list.
                out.println("Borrowed Books:");
                if (member.getBorrowedbookList().isEmpty()) {
                    out.println("  No books currently borrowed.");
                } else {
                    for (Book book : member.getBorrowedbookList()) {
                        out.println("  - " + book.name + " (ID: " + book.bookID + ")");
                    }
                }
                // Gotta keep it looking sleek.
                out.println("--------------------");
            }
        }
    }

    private void editBookInfo() {
        out.print("Enter book ID you'd like to edit: ");
        String bookID = scanner.nextLine();
        // Usual search for the book
        Book bookToEdit = library.AllBooksInLibrary.stream()
                .filter(book -> book.bookID.equals(bookID))
                .findFirst()
                .orElse(null);

        // If found then the user is prompted with a series of edits
        if (bookToEdit != null) {
            out.print("Enter new title (leave blank to keep current): ");
            String newTitle = scanner.nextLine();
            out.print("Enter new author (leave blank to keep current): ");
            String newAuthor = scanner.nextLine();
            out.print("Enter new year (leave blank to keep current): ");
            String newYear = scanner.nextLine();
            out.print("Enter new ISBN (leave blank to keep current): ");
            String newISBN = scanner.nextLine();
            out.print("Enter new genre (leave blank to keep current): ");
            String newGenre = scanner.nextLine();

            // Then it's information is updated only if something was actually imputed
            bookToEdit.updateBookInfo(
                    newTitle.isEmpty() ? bookToEdit.name : newTitle,
                    newAuthor.isEmpty() ? bookToEdit.author : newAuthor,
                    newYear.isEmpty() ? bookToEdit.year : Integer.parseInt(newYear),
                    newISBN.isEmpty() ? bookToEdit.ISBN : newISBN,
                    newGenre.isEmpty() ? bookToEdit.genre : newGenre
            );
            out.println("Book information updated successfully.");
        } else {
            out.println("Book not found.");
        }
    }

    private void editMemberInfo() {
        out.print("Enter member ID you'd like to edit: ");
        String memberID = scanner.nextLine();
        Member memberToEdit = library.getAllMembers().stream()
                .filter(member -> member.memberID.equals(memberID))
                .findFirst()
                .orElse(null);

        if (memberToEdit != null) {
            out.print("Enter new name (leave blank to keep current): ");
            String newName = scanner.nextLine();
            out.print("Enter new email (leave blank to keep current): ");
            String newEmail = scanner.nextLine();
            out.print("Enter new member ID (leave blank to keep current): ");
            String newMemberID = scanner.nextLine();

            memberToEdit.updateMemberInfo(
                    newName.isEmpty() ? memberToEdit.name : newName,
                    newEmail.isEmpty() ? memberToEdit.email : newEmail,
                    newMemberID.isEmpty() ? memberToEdit.memberID : newMemberID,
                    memberToEdit.getBorrowedbookList()
            );
            out.println("Member information updated successfully.");
        } else {
            out.println("Member not found.");
        }
    }
}
