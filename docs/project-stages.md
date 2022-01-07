## Project Stages
Below you can find a list of features that were implemented during each stage of the project development so far

### Stage 1: Console implementation with basic functionality
User can:

1. Add new spending records
2. Remove an existing record
3. View all records added
4. Sort records by:
    1. Amount spent in descending order
    2. Category in alphabetic order
    3. Date in descending order (from new to old)
5. Edit current records, e.g., change:
    1. Title
    2. Amount spent
    3. Category
---

### Stage 2: Data Persistence
User can:
1. Save financial list to a file (before exiting, the program asks them if they want to save or not)
2. Open saved financial list and continue working on it
---

### Stage 3: GUI and Charts
User can:

1. Use all previous functionality but now in a nice GUI
2. See pop-up windows if they enter something incorrectly
3. Remove multiple records or categories at once
4. Rename categories and automatically see changes in the whole app
5. Remove categories and automatically see changes in the whole app
6. See default category highlighted (it stays highlighter even after they rename it)
7. See all records with default category highlighted (so they know which records need to be assigned)
8. Exit or open a new file without saving a current one if they didn't make any changes to it
9. See a filename they're editing
10. See if they saved the changes (if they see a * in front of the filename, the changes are not saved)
11. Use shortcuts to navigate the app quickly
12. Build a bar chart of the spending records:
    1. Amount Spent by Category (Normal & Stacked)
    2. Amount Spent by Month (Normal & Stacked)
13. Enter dates in "Month.day, year" and "MM/dd/yyyy" text formats
14. Keep settings in each view unchanged when they change views

---
### Phase 4: Logging
1. Added simple logging

Console output example after using the app
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
