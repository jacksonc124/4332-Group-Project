Requirements for new class in LibraryAccounts.java:
- Maintains an operating cash balance for the library starting at $39000. The librarian can
manually add cash to the balance at any point with incoming donations.
- Allows the librarian to withdraw a custom salary amount.
- Orders new books from Purchasing (below) when needed. Value of book is deducted from
Operating Cash Balance.

Requirements for new class Purchasing.java:
- Should be accessible to LibraryAccounts.
- Receives requests from Library accounts to purchase new books.
- Generates a randomized value between $10 and $100 for any new book.
- Returns the value of the book.

Requirements for new class Librarians.java:
- Maintains record of three distinct librarians (pre-defined) with diVerent names and 6-digit
authentication codes.
- Maintains record of total cash withdrawn as salary per librarian.
- Maintains a record of books purchased by each Librarian.

Requirements for modifying Interface.java
- Interface now interacts with LibraryAccounts and Librarians, in addition to Library.
- Other than the three full-time librarians. Library hires part-time volunteer librarians. They
can add/remove/checkout books but cannot access LibraryAccounts or revoke
memberships. Use authentication to limit sensitive functionalities to full-time Librarians.
- If a full-time librarian calls Library.checkoutBook() for a book that has not been added
through Library.addBook(), your interface must present them a simple option to purchase
the book through LibraryAccounts and then add the book to Library and proceed with
checkout. Part-time librarians must be asked to call a full-time librarian for approval.