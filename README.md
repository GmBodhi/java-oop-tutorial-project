# Library Management System Tutorial

## Step 1: Project Setup & Git Initialization

Welcome to the Java Library Management System tutorial! In this step, you'll learn how to set up a new Java project and initialize version control.

### What You'll Learn
- Project structure organization
- Git repository initialization
- Basic git commands
- IntelliJ IDEA project setup

### Prerequisites
- Java JDK 11 or higher installed
- IntelliJ IDEA Community Edition
- Git installed on your system

### Step-by-Step Instructions

#### 1. Create Project Structure
```
library-management/
├── src/
├── out/
├── README.md
└── .gitignore
```

#### 2. Initialize Git Repository
```bash
git init
git add .
git commit -m "Initial project setup with basic structure"
```

#### 3. Understanding Git Basics
- `git init`: Creates a new Git repository
- `git add`: Stages files for commit
- `git commit`: Saves changes to repository history
- `git status`: Shows current repository state

### Learning Objectives Achieved
✅ Created proper Java project structure  
✅ Initialized version control system  
✅ Learned basic Git workflow  
✅ Set up development environment  

### Next Steps
In Step 2, we'll create our first Java program with a simple "Hello World" application and learn about the main method and basic output formatting.

### Exercise
Try these git commands to explore your repository:
```bash
git status
git log
git log --oneline
```

### Common Issues & Solutions
- **Permission denied**: Make sure you have write permissions in the directory
- **Git not found**: Ensure Git is installed and added to PATH
- **Empty repository**: Use `git status` to check if files are staged properly