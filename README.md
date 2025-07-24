# Library Management System Tutorial

## Current Step: Step 3 - Book Class & Object-Oriented Programming

## Step History

### ✅ Step 1: Project Setup & Git Initialization (Completed)

**What You Learned:**
- Project structure organization
- Git repository initialization  
- Basic git commands
- Development environment setup

**Files Created:**
- Project structure (src/, out/)
- .gitignore
- README.md
- Git repository initialization

### ✅ Step 2: Hello World & Basic I/O (Completed)

**What You Learned:**
- Main method structure and purpose
- System.out.println() vs System.out.printf()
- String formatting and arrays
- Basic loops and iteration

**Files Created:**
- src/Main.java with welcome program
- Updated README with instructions

---

## Step 3: Book Class & Object-Oriented Programming

Welcome to Step 3! Now we'll create our first custom class and dive into object-oriented programming concepts.

### What You'll Learn
- Class definition and structure
- Constructors and method overloading
- Encapsulation with private fields and public methods
- Enums for representing constants
- Input validation and error handling
- Object methods: toString(), equals(), hashCode()

### Learning Objectives
- Create a complete Book class with proper encapsulation
- Understand constructors and method overloading
- Implement enums for status management
- Apply input validation and defensive programming
- Use getters, setters, and utility methods effectively

### Step-by-Step Instructions

#### 1. Book Class Structure
The `src/Book.java` file contains:
- **Private fields**: isbn, title, author, genre, publicationYear, status
- **BookStatus enum**: AVAILABLE, BORROWED, RESERVED, MAINTENANCE
- **Multiple constructors**: Full constructor and simplified version
- **Getter/Setter methods**: With validation
- **Utility methods**: isAvailable(), markAsBorrowed(), etc.
- **Object methods**: toString(), equals(), hashCode()

#### 2. Key OOP Concepts Demonstrated

**Encapsulation:**
```java
private String title;  // Private field
public String getTitle() { return title; }  // Public accessor
```

**Constructor Overloading:**
```java
Book(String isbn, String title, String author, String genre, int year)
Book(String isbn, String title, String author)  // Simplified version
```

**Enum Usage:**
```java
public enum BookStatus { AVAILABLE, BORROWED, RESERVED }
private BookStatus status;
```

#### 3. Updated Main.java
- Creates sample Book objects using different constructors
- Demonstrates getter methods and formatted output
- Shows status change functionality
- Tests object methods in action

### Learning Objectives Achieved
✅ Created complete Book class with encapsulation  
✅ Implemented constructors and method overloading  
✅ Added enums for type-safe constants  
✅ Applied input validation and error handling  
✅ Overrode Object methods (toString, equals, hashCode)  
✅ Created utility methods for common operations  

### Next Steps
In Step 4, we'll create a User class that can interact with Books, introducing concepts like object relationships and collections within objects.

### Exercise
1. Add a `pages` field to the Book class with appropriate getter/setter
2. Create a method `isClassic()` that returns true if publication year < 1970
3. Add validation to ensure publication year is reasonable (between 1450-current year)
4. Create a `Category` enum and replace the String genre field

### Common Issues & Solutions
- **Null pointer exceptions**: Check our validation in constructors and setters
- **Enum not found errors**: Make sure enum is defined within the class
- **Access modifiers**: Remember private fields need public getters/setters
- **Constructor chaining**: Use `this()` to call other constructors