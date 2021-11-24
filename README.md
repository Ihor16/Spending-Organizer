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

---
### Phase 4: Task 2
````
Mon Nov 22 19:04:59 PST 2021
New Category added: default
Mon Nov 22 19:04:59 PST 2021
New categories list created: Categories[categories=[Category[name=default]]]
Mon Nov 22 19:04:59 PST 2021
New SpendingList created: SpendingList[records=[], categories=Categories[categories=[Category[name=default]]]]
Mon Nov 22 19:05:09 PST 2021
New Category added: Groceries
Mon Nov 22 19:05:14 PST 2021
New Category added: Travel
Mon Nov 22 19:05:25 PST 2021
New Record added: Record[title=Save On Foods, amount=80.0, category=Category[name=Groceries], timeAdded=2021-11-22T19:05:25.372]
Mon Nov 22 19:05:37 PST 2021
New Record added: Record[title=Went to Montreal, amount=500.0, category=Category[name=Groceries], timeAdded=2021-11-22T19:05:37.771]
Mon Nov 22 19:05:40 PST 2021
Record's category set to: Category[name=Travel]
Mon Nov 22 19:05:50 PST 2021
New Record added: Record[title=Went to Toronto, amount=700.0, category=Category[name=Travel], timeAdded=2021-11-22T19:05:50.590]
Mon Nov 22 19:05:55 PST 2021
NameException thrown because new Record's name is blank
Mon Nov 22 19:06:04 PST 2021
Record's title set to: SaveOn
Mon Nov 22 19:06:11 PST 2021
NegativeAmountException thrown because new Record's amount is < 0
Mon Nov 22 19:06:17 PST 2021
Record's amount set to: 84.14
Mon Nov 22 19:06:32 PST 2021
New Category added: Cloting
Mon Nov 22 19:06:37 PST 2021
Category's name changed to Clothing
Mon Nov 22 19:06:50 PST 2021
New Record added: Record[title=T-Shirt, amount=30.0, category=Category[name=Clothing], timeAdded=2021-11-22T19:06:50.673]
Mon Nov 22 19:06:54 PST 2021
Record's category set to: Category[name=default]
Mon Nov 22 19:06:57 PST 2021
Record's category set to: Category[name=Clothing]
Mon Nov 22 19:07:03 PST 2021
New Record added: Record[title=Shorts, amount=80.0, category=Category[name=Clothing], timeAdded=2021-11-22T19:07:03.843]
Mon Nov 22 19:07:14 PST 2021
New Record added: Record[title=Jacket, amount=100.0, category=Category[name=Clothing], timeAdded=2021-11-22T19:07:14.260]
Mon Nov 22 19:07:16 PST 2021
Removed this record: Record[title=Shorts, amount=80.0, category=Category[name=Clothing], timeAdded=2021-11-22T19:07:03.843]
Mon Nov 22 19:07:20 PST 2021
Record's category set to: Category[name=default]
Mon Nov 22 19:07:20 PST 2021
Record's category set to: Category[name=default]
Mon Nov 22 19:07:20 PST 2021
Removed this category: Clothing
Mon Nov 22 19:07:27 PST 2021
Removed this record: Record[title=Jacket, amount=100.0, category=Category[name=default], timeAdded=2021-11-22T19:07:14.260]
Mon Nov 22 19:07:27 PST 2021
Removed this record: Record[title=T-Shirt, amount=30.0, category=Category[name=default], timeAdded=2021-11-22T19:06:50.673]
Mon Nov 22 19:07:48 PST 2021
NameException thrown because new Category's name is blank
Mon Nov 22 19:08:13 PST 2021
Category's name changed to Supermarket groceries
````
---
### Phase 4: Task 3
In my design all the classes revolve around SpendingList class, 
which holds a list of Records and a list of available Categories.
Therefore, it's very convenient to write to JSON just one class and save the whole application automatically. 

Also, it was very nice to use interfaces WritableObject and WritableArray since classes that implement them can
clearly show how their fields are going to be presented in the JSON file. 

Additionally, I utilized a Singleton pattern for holding SpendingList and Scene references.
Using this pattern allowed me to prevent data loss while switching between scenes, 
which made my application more convenient.

If I had more time, I'd like to
* implement an Observer pattern: make SpendingList an Observer and Categories — Subject.
  This would make it more convenient to edit SpendingList when a Category is removed
* make a helper class for SpendingList because SpendingList has a verbose implementation of grouping
  required only for drawing charts. And I think it would make SpendingList more readable if this behaviour
  is abstracter to another class
* rethink the relationships between controller classes.
  Since both of them handle multiple similar components,
  I think it would be better to create some abstract class that would implement common behaviour and then make
  Controller and ChartController subclasses of that abstract class.
