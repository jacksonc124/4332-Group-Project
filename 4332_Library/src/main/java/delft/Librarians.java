package delft;
import java.util.*;

public class Librarians 
{
    public static class Librarian 
    {
        String name;
        String authCode; //6 Digit auth code
        double totalSalaryWithdrawn; //total cash withdrawn for salary
        List<String> booksPurchased; //record of books purchased by each librarian

        public Librarian(String name, String authCode)
        {
            this.name = name;
            this.authCode = authCode;
            this.totalSalaryWithdrawn = 0.0;
            this.booksPurchased = new ArrayList<>();
        }

        public void withdrawSalary(double amount) 
        {
            this.totalSalaryWithdrawn += amount;
        }

        public void purchaseBook(String bookTitle) 
        {
            booksPurchased.add(bookTitle);
        }
    }

    private Map<String, Librarian> librarianMap; //map for all librarians

    public Librarians() 
    {
        librarianMap = new HashMap<>();
        // Three full-time librarians (pre defined)
        librarianMap.put("373737", new Librarian("Janice", "373737"));
        librarianMap.put("234567", new Librarian("Marty", "234567"));
        librarianMap.put("162813", new Librarian("John", "162813"));
        // Example part-time librarian
        librarianMap.put("111111", new Librarian("Eve (Part-Time)", "111111"));
    }

    //checks if librarian exists based on code in hashmap
    public boolean isValidAuth(String code) 
    {
        return librarianMap.containsKey(code);
    }

    //returns librarian name from code
    public String getLibrarianName(String code) 
    {
        return librarianMap.containsKey(code) ? librarianMap.get(code).name : null;
    }

    //can withdraw additional salary for librarian by their code
    public void recordSalary(String code, double amount) 
    {
        if (librarianMap.containsKey(code)) 
        {
            librarianMap.get(code).withdrawSalary(amount);
        }
    }

    //purchases book for librarian based upon code
    public void recordBookPurchase(String code, String bookTitle) 
    {
        if (librarianMap.containsKey(code)) 
        {
            librarianMap.get(code).purchaseBook(bookTitle);
        }
    }

    //returns total salary withdrawn by code
    public double getTotalSalaryWithdrawn(String code) 
    {
        return librarianMap.containsKey(code) ? librarianMap.get(code).totalSalaryWithdrawn : 0.0;
    }

    //returns list of books purchased by a librarian from their code
    public List<String> getBooksPurchased(String code) 
    {
        return librarianMap.containsKey(code) ? librarianMap.get(code).booksPurchased : new ArrayList<>();
    }
}
