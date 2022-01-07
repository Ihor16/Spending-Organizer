package ui.controllers.holders;

import model.SpendingList;

// Represents a Singleton holder of SpendingList
public final class SpendingListHolder {

    private SpendingList spendingList;
    private static final SpendingListHolder INSTANCE = new SpendingListHolder();

    private SpendingListHolder() {
    }

    // EFFECTS: returns instance of this
    public static SpendingListHolder getInstance() {
        return INSTANCE;
    }

    public SpendingList getSpendingList() {
        return spendingList;
    }

    public void setSpendingList(SpendingList spendingList) {
        this.spendingList = spendingList;
    }
}
