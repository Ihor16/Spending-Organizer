# Spending List

## JavaFX Application for Everyday Use

- **What will the application do?**

This application allows user to store and categorize their spending. 
User can add spending entries to a list and refer to it later to see how much they spend every month.
Spending record consists of "Title", "Amount Spent", and "Category of spending" (e.g. "Went to NoFrills" | 70 CAD | Groceries).
Additionally, user can filter the spending entries by categories and/or sort them by the amount spent.

- **Who will use it?**

I will, and in general, the target is students who just moved on campus and need a tool for managing their finances. 
But this application can be used by other people as well.  

- **Why is this project of interest to you?**

It'll be interesting to see how good software design makes adding new features less problematic.
And I'd love to spice up my implementations by using Java Streams.

## User Stories

1. As a user, I want to be able to add new spending entries

For example, spending record is a class *Entry* and spending list is a class *SpendingList*. 
When user adds a new spending record in the app, we want to create a new instance of the *Entry* class
and add it to the *SpendingList* class (class that stores multiple *Entries*).

2. As a user, I want to be able to remove an existing record
3. As a user, I want to be able to view all entries added
4. As a user, I want to be able to sort my entries by:
   1. Amount spent in descending order
   2. Category in alphabetic order
   3. Date in descending order (from new to old)
5. As a user, I want to be able to edit my current entries, e.g., change:
   1. Title
   2. Amount spent
   3. Category
6. As a user, I want to be able to save my financial list to a file
(before exiting the app, I want the program to ask me if I want to save or not)
7. As a user, I want to be able to open my saved financial list and continue working on it