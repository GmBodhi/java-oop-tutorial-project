# Library Management System Tutorial

## Current Step: Step 2 - Hello World & Basic I/O

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

---

## Step 2: Hello World & Basic I/O

Welcome to Step 2! Now we'll create our first Java program and learn about basic input/output operations.

### What You'll Learn
- Main method structure and purpose
- `System.out.println()` vs `System.out.printf()`
- String formatting and output
- Basic loops for repetitive tasks
- Arrays and iteration

### Learning Objectives
- Understand the entry point of Java applications
- Master different output methods
- Practice with for loops and arrays
- Learn proper code formatting and commenting

### Step-by-Step Instructions

#### 1. Create Main.java
The `src/Main.java` file contains:
- Welcome message with formatted output
- Feature list using arrays and loops
- Demonstration of different print methods
- Loop example for future book processing

#### 2. Compile and Run
```bash
# Compile the Java file
javac -d out src/Main.java

# Run the compiled program  
java -cp out Main
```

#### 3. Understanding the Code
- **`public class Main`**: Defines our main class
- **`public static void main(String[] args)`**: Entry point for Java applications
- **`System.out.println()`**: Prints text with newline
- **`System.out.printf()`**: Formatted printing with placeholders
- **`%n`**: Platform-independent newline character
- **Arrays**: Used to store our feature list
- **For loops**: Iterate through arrays and count

### Learning Objectives Achieved
✅ Created first Java program with main method  
✅ Used different output methods (println, printf)  
✅ Implemented arrays and for loops  
✅ Applied string formatting techniques  
✅ Practiced proper code structure  

### Next Steps
In Step 3, we'll create our first custom class - the Book class - and learn about object-oriented programming concepts like encapsulation, constructors, and methods.

### Exercise
1. Modify the program to include your name in the welcome message
2. Add more features to the features array
3. Create a loop that counts backwards from 10 to 1
4. Experiment with different printf format specifiers

### Common Issues & Solutions
- **"javac not found"**: Ensure Java JDK is installed and in PATH
- **"Could not find or load main class"**: Check classpath and make sure you're in the right directory
- **Compilation errors**: Check for typos, missing semicolons, or bracket mismatches