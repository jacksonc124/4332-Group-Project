package delft;

import java.util.Scanner;

public class LibInterface {
    public static void main(String[] args) {

        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        // This is what the user will first see when starting. Containing all the options of how to proceed.
        while (true) {
            System.out.println("\nLSU Group I Library System");
            System.out.println("1. Display All");
            System.out.println("2. Add Book");
            System.out.println("3. Remove Book");
            System.out.println("4. Add Member");
            System.out.println("5. Remove Member");
            System.out.println("6. Checkout");
            System.out.println("7. Return");
            System.out.println("8. Search for Book");
            System.out.println("9. Full Member List");
            System.out.println("10. Edit Book Information");
            System.out.println("11. Edit Member Information");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");
            System.out.print("\n");

            int options = scanner.nextInt();
            scanner.nextLine();

            // For the UI I went with the simple aproach of using switch statements.
            switch (options) {
                // I thought it would make sense to have an option to list all books. Like what if you wanna
                // just see what the options are.
                case 1:
                // If there are no books say that other wise print the list.
                    System.out.println("Books currently in the library:");
                if (library.AllBooksInLibrary.isEmpty()) {
                    System.out.println("No books are currently in the library.");
                } else {
                    for (Book book : library.AllBooksInLibrary) {
                        book.getBookInfo();
                        // Gotta make the list look pretty.
                        System.out.println("--------------------");
                    }
                }
                    break;

                // This is everything needed to added a book. Simply asking the user for info then adding it to library.addBook.
                case 2:
                    System.out.print("Enter book name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();

                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();

                    System.out.print("Enter book ID: ");
                    String bookID = scanner.nextLine();

                    System.out.print("Enter genre: ");
                    String genre = scanner.nextLine();

                    library.addBook(new Book(name, author, year, isbn, bookID, genre));
                    System.out.println("Book added successfully.");
                    break;

                // This case is for removing books. Using the books specific ID to search the list for the matching ID.
                // Then removing it.
                case 3:
                    System.out.print("Enter book ID to remove: ");
                    bookID = scanner.nextLine();
                    // This goes through the list filtering for the book's ID and since only one book should have its ID
                    // then it goes after the first it finds. And if it finds nothing then it's null.
                    Book bookToRemove = library.AllBooksInLibrary.stream().filter(book -> book.bookID.equals(bookID))
                    .findFirst().orElse(null);
                    // The book gets removed if it's not null and if it is then the book doesn't exist.
                    if (bookToRemove != null) {
                        library.removeBook(bookToRemove);
                        System.out.println("Book removed successfully.");
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;

                // This case is for adding members. It more or less does the same thing as adding books just saving it to library.addMember.
                case 4:
                    System.out.print("Enter new member's name: ");
                    String memberName = scanner.nextLine();

                    System.out.print("Enter new member's email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter new member's ID: ");
                    String memberID = scanner.nextLine();

                    library.addMember(new Member(memberName, email, memberID, null));
                    System.out.println("New member has beed successfully added.");
                    break;

                // This just removes a member from the list. It also more or less does the same thing as removing books just with a different list.
                case 5:
                    System.out.print("Enter current member ID to remove: ");
                    memberID = scanner.nextLine();
            
                    // Another search
                    Member memberToRemove = library.getAllMembers().stream()
                        .filter(member -> member.memberID.equals(memberID))
                        .findFirst().orElse(null);
            
                    if (memberToRemove != null) {
                        // This case also needs to return any books that member had.
                        for (Book book : memberToRemove.getBorrowedbookList()) {
                        book.isAvailable = true;
                        library.LoanedBooks.remove(book.bookID);
                        }
            
                        // And finally revoke their membership
                        library.revokeMembership(memberToRemove);
                        System.out.println("Member removed successfully, and all borrowed books have been returned.");
                    } else {
                       System.out.println("Member not found.");
                    }
                    break;

                // This case if for checking out a book. It just finds the book ID entered while also making 
                // sure the memeber ID exist. The other part is checking if the book's avalibility is set to true and making
                // it false after the checkout.
                case 6:
                    System.out.print("Enter book ID to checkout: ");
                    bookID = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    memberID = scanner.nextLine();
            
                    // Check if the book exists and is available
                    if (!library.bookAvailability(bookID)) {
                        System.out.println("Book is not available for checkout or does not exist.");
                    } else if (!library.MemberIDs.contains(memberID)) {
                        System.out.println("Invalid member ID.");
                    } else {
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
                        }
                        System.out.println("Book checked out successfully.");
                    }
                    break;

                // This case is for returning a book. I mostly made it just to have a way
                // to change a book's availability back to true so its code is bascally the same as case 6's. 
                case 7:
                    System.out.print("Enter book ID to return: ");
                    bookID = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    memberID = scanner.nextLine();
            
                    if (library.bookAvailability(bookID)) {
                        System.out.println("Book is already available and cannot be returned.");
                    } else if (!library.LoanedBooks.containsKey(bookID) || !library.LoanedBooks.get(bookID).equals(memberID)) {
                        System.out.println("This book is not loaned to the specified member.");
                    } else {
                        library.returnBook(bookID, memberID);
                        Book bookToReturn = library.AllBooksInLibrary.stream()
                            .filter(book -> book.bookID.equals(bookID))
                            .findFirst()
                            .orElse(null);
                        if (bookToReturn != null) {
                            bookToReturn.isAvailable = true;
                            Member member = library.getAllMembers().stream()
                               .filter(m -> m.memberID.equals(memberID))
                               .findFirst()
                               .orElse(null);
                            if (member != null) {
                                member.removeBorrowedBook(bookToReturn);
                            }
                        }
                        System.out.println("Book returned successfully.");
                    }
                    break;

                // This case is for searching a book up and displaying its information.
                case 8:
                    // While checkout and return require the book's ID, search will use its name instead.
                    System.out.print("Enter book name to search: ");
                    String searchName = scanner.nextLine();
                    String foundBookID = library.findBookIdByName(searchName);
                
                    if (foundBookID != null) {
                        Book foundBook = library.AllBooksInLibrary.stream()
                            .filter(book -> book.bookID.equals(foundBookID))
                            .findFirst()
                            .orElse(null);
                
                        // If the book exist then give all its info.
                        if (foundBook != null) {
                            System.out.println("Book found:");
                            foundBook.getBookInfo();
                
                            // If it is availility is true and say that otherwise say who has it.
                            if (foundBook.isAvailable) {
                                System.out.println("The book is currently available.");
                            } else {
                                String borrowerID = library.whoHasBook(foundBook.bookID);
                                if (borrowerID != null) {
                                    System.out.println("Currently checked out by Member ID: " + borrowerID);
                                } else {
                                    // A little just in case line
                                    System.out.println("Error: Book is marked as unavailable but no borrower found.");
                                }
                            }
                        }
                    } else {
                        // If the book doesn't exist then say so.
                        System.out.println("Book not found.");
                    }
                    break;

                // This case is to list all current members and if that member has a booked checked out then list that with them.
                case 9:
                    System.out.println("Members currently in the library:");
                    if (library.getAllMembers().isEmpty()) {
                        System.out.println("No members are currently in the library.");
                    } else {
                        // This just list all the current members.
                        for (Member member : library.getAllMembers()) {
                            member.printMemberInfo();
                            // And this prints their borrowed book list.
                            System.out.println("Borrowed Books:");
                            if (member.getBorrowedbookList().isEmpty()) {
                                System.out.println("  No books currently borrowed.");
                            } else {
                                for (Book book : member.getBorrowedbookList()) {
                                    System.out.println("  - " + book.name + " (ID: " + book.bookID + ")");
                                }
                            }
                            // Gotta keep it looking sleek.
                            System.out.println("--------------------");
                        }
                    }
                    break;
                    
                // This case deals with editing the information of a book.    
                case 10:
                    System.out.print("Enter book ID you'd like to edit: ");
                    bookID = scanner.nextLine();
                    // Usual search for the book
                    Book bookToEdit = library.AllBooksInLibrary.stream()
                        .filter(book -> book.bookID.equals(bookID))
                        .findFirst().orElse(null);
                    
                    // If found then the user is prompted with a series of edits
                    if (bookToEdit != null) {
                        System.out.print("Enter new title (leave blank to keep current): ");
                        String newTitle = scanner.nextLine();
                        System.out.print("Enter new author (leave blank to keep current): ");
                        String newAuthor = scanner.nextLine();
                        System.out.print("Enter new year (leave blank to keep current): ");
                        String newYear = scanner.nextLine();
                        System.out.print("Enter new ISBN (leave blank to keep current): ");
                        String newISBN = scanner.nextLine();
                        System.out.print("Enter new genre (leave blank to keep current): ");
                        String newGenre = scanner.nextLine();
                
                        // Then it's infromation is updated only if something was actually imputed
                        bookToEdit.updateBookInfo(
                            newTitle.isEmpty() ? bookToEdit.name : newTitle,
                            newAuthor.isEmpty() ? bookToEdit.author : newAuthor,
                            newYear.isEmpty() ? bookToEdit.year : Integer.parseInt(newYear),
                            newISBN.isEmpty() ? bookToEdit.ISBN : newISBN,
                            newGenre.isEmpty() ? bookToEdit.genre : newGenre
                        );
                        System.out.println("Book information updated successfully.");
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;

                // This case deals with editing the information of any memebrs. It works the same as case 10 so its code
                // is borderline the same as it.
                case 11:
                    System.out.print("Enter member ID you'd like to edit: ");
                    memberID = scanner.nextLine();
                    Member memberToEdit = library.getAllMembers().stream()
                        .filter(member -> member.memberID.equals(memberID))
                        .findFirst()
                        .orElse(null);
                
                    if (memberToEdit != null) {
                        System.out.print("Enter new name (leave blank to keep current): ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new email (leave blank to keep current): ");
                        String newEmail = scanner.nextLine();
                        System.out.print("Enter new member ID (leave blank to keep current): ");
                        String newMemberID = scanner.nextLine();
                
                        memberToEdit.updateMemberInfo(
                            newName.isEmpty() ? memberToEdit.name : newName,
                            newEmail.isEmpty() ? memberToEdit.email : newEmail,
                            newMemberID.isEmpty() ? memberToEdit.memberID : newMemberID,
                            memberToEdit.getBorrowedbookList()
                        );
                        System.out.println("Member information updated successfully.");
                    } else {
                        System.out.println("Member not found.");
                    }
                    break;

                // This case just deals with exiting out.
                case 12:
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;

                // The default just deals with if the user trys to enter a choice that doesn't exist.
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}