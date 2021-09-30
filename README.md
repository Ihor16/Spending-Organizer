# Spending List

## JavaFx application for everyday use

- **What will the application do?**

This application allows user to store and categorize their spending. 
User can add corresponding spending entries to a list and refer to it later to see how much they spent each month.
Additionally, user can filter the spending entries by categories and/or sort them by the amount spent.

- **Who will use it?**

I'll use it, and in general the target is students who just moved on campus and need a tool for managing their finances. 
But this application can be used by other people as well.  

- **Why is this project of interest to you?**

It'll be interesting to see how good software design makes adding new features less problematic.
And I'd love to spice up my implementations by using Java Streams.

## User Stories

1. As a user, I want to be able to add new spending entry

For example, spending entry is a class *SpendingEntry* and spending list is a class *SpendingList*. 
When user adds a new spending entry in the app, we want to create a new instance of the *SpendingEntry* class
and add it to the *SpendingList* class (class that stores multiple *SpendingEntries*).

2. As a user, I want to be able to remove an existing entry
3. As a user, I want to be able to define new entry category
4. As a user, I want to be able to change an entry's category after creating the entry
