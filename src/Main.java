public class Main {
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  Welcome to Library Management  ");
        System.out.println("=================================");
        System.out.println();
        
        System.out.printf("Hello and welcome to our Library Management System!%n");
        System.out.println("This system will help you manage books, users, and transactions.");
        System.out.println();
        
        System.out.println("Features we'll be building:");
        String[] features = {
            "Book Management (Add, Update, Remove, Search)",
            "User Registration and Management", 
            "Borrowing and Returning Books",
            "Transaction Tracking",
            "Library Statistics and Reporting",
            "Graphical User Interface"
        };
        
        for (int i = 0; i < features.length; i++) {
            System.out.printf("%d. %s%n", i + 1, features[i]);
        }
        
        System.out.println();
        System.out.println("Let's start building this step by step!");
        
        System.out.println("\n--- Book Class Demo ---");
        
        Book book1 = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", "Programming", 2008);
        Book book2 = new Book("978-0-201-61622-4", "The Pragmatic Programmer", "Andy Hunt");
        Book book3 = new Book("978-0-321-35668-0", "Effective Java", "Joshua Bloch", "Programming", 2017);
        
        System.out.println("📖 Sample Books in our Library:");
        System.out.println("1. " + book1.getFormattedInfo());
        System.out.println("2. " + book2.getFormattedInfo());
        System.out.println("3. " + book3.getFormattedInfo());
        
        System.out.println("\n🔄 Testing Book Status Changes:");
        System.out.println("Before: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        book1.markAsBorrowed();
        System.out.println("After borrowing: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        book1.markAsAvailable();
        System.out.println("After returning: " + book1.getTitle() + " is " + 
                          (book1.isAvailable() ? "available" : "not available"));
        
        System.out.println("\nStep 3 complete! We now have a working Book class.");
    }
}