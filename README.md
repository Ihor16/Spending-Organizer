# Spending List

## JavaFX Application for Everyday Use

- **What will the application do?**

This application allows user to store and categorize their spending. 
User can add spending records to a list and refer to it later to see how much they spend every month.
Spending record consists of "Title", "Amount Spent", and "Category of spending" (e.g. "Went to NoFrills" | 70 CAD | Groceries).
Additionally, user can filter the spending records by categories and/or sort them by the amount spent.

- **Who will use it?**

I will, and in general, the target is students who just moved on campus and need a tool for managing their finances. 
But this application can be used by other people as well.  

- **Why is this project of interest to you?**

It'll be interesting to see how good software design makes adding new features less problematic.
And I'd love to spice up my implementations by using Java Streams.

## User Stories

As a user, I want to be able to:
1. Add new spending records

For example, spending record is a class *Record* and spending list is a class *SpendingList*. 
When user adds a new spending record in the app, we want to create a new instance of the *Record* class
and add it to the *SpendingList* class (class that stores multiple *Records*).

2. Remove an existing record
3. View all records added
4. Sort my records by:
   1. Amount spent in descending order
   2. Category in alphabetic order
   3. Date in descending order (from new to old)
5. Edit my current records, e.g., change:
   1. Title
   2. Amount spent
   3. Category
---
6. Save my financial list to a file (before exiting the app, I want the program to ask me if I want to save or not)
7. Open my saved financial list and continue working on it
---
8. Use all previous functionality but now in a nice GUI
9. See a pop-up windows if I enter something incorrectly
10. Remove multiple records or categories at once
11. Rename my categories and automatically see changes in the whole app
12. Remove my categories and automatically see changes in the whole app
13. See my default category highlighted so that I can remember this category is default after renaming it
14. See all my records with default category highlighted so that I see I should assign them
15. Exit or open a new file without saving my current one if I didn't make any changes to it
16. See if I saved my changes by checking my current filename (if I see * in front of it, my changes are not saved)
17. See file I'm editing
18. Use shortcuts to edit my spending list quickly
19. Build a bar chart of my spending records:
    1. Amount Spent by Category: per month and for custom period
    2. Amount Spent by Month: per month and for custom period
    3. Amount Spent by Category (Stacked): per month and for custom period
    4. Amount Spent by Month (Stacked): per month and for custom period
20. Enter dates in "Month.day, year" and "MM/dd/yyyy" text formats
21. Keep my settings in each view unchanged when I change views