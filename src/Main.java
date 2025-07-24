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
        
        System.out.println("\n--- Loop Demo: Counting Books ---");
        for (int bookCount = 1; bookCount <= 5; bookCount++) {
            System.out.println("Processing book #" + bookCount);
        }
        
        System.out.println("\nSetup complete! Ready for the next step.");
    }
}