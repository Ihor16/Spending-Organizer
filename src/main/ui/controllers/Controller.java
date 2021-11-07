package ui.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.Categories;
import model.Category;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import persistence.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

// Class that handles events and loads data to the GUI
public class Controller implements Initializable {

    final String fieldsInitError = "Couldn't initialize data";
    final String fileError = "Choose a file";
    final String defaultFilePath = "./data/emptyFile.json";

    @FXML TextField titleFieldAdd;
    @FXML TextField amountFieldAdd;
    @FXML ComboBox<String> categoriesBoxAdd;

    @FXML ToggleGroup addGroup;
    @FXML RadioButton recordToggleAdd;
    @FXML RadioButton categoryToggleAdd;

    @FXML ToggleGroup removeGroup;
    @FXML RadioButton recordToggleRemove;
    @FXML RadioButton categoryToggleRemove;

    @FXML TableView<Record> recordTable;
    @FXML TableColumn<Record, String> titleColumn;
    @FXML TableColumn<Record, Double> amountColumn;
    @FXML TableColumn<Record, Category> categoryColumn;
    @FXML TableColumn<Record, LocalDateTime> dateColumn;

    @FXML TableView<Category> categoriesTable;
    @FXML TableColumn<Category, String> categoriesColumn;
    @FXML TableColumn<Category, Boolean> isShownColumn;

    @FXML MenuItem saveMenuItem;
    @FXML Label filenameLabel;
    Categories categories;
    SpendingList spendingList;

    SimpleBooleanProperty isChanged;
    SimpleStringProperty currentFilePath;

    private SetUpHelper setUpHelper;

    // MODIFIES: this
    // EFFECTS: initializes the data
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isChanged = new SimpleBooleanProperty(false);
        setUpHelper = new SetUpHelper(this);

        currentFilePath = new SimpleStringProperty("./data/spendingList.json");
        setUpHelper.setUpUI();
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to save changes, and opens an empty file
    @FXML
    void newMenuItemClicked() {
        if (isChanged.get()) {
            showSavePopup();
        }
        currentFilePath.set(defaultFilePath);
        setUpHelper.setUpUI();
        isChanged.set(false);
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to save changes, and opens chosen file
    //          shows error message if used didn't choose a file
    // Implementation is based on: https://www.youtube.com/watch?v=hNz8Xf4tMI4
    @FXML
    void openMenuItemClicked() {
        if (isChanged.get()) {
            showSavePopup();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./data"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (Objects.nonNull(selectedFile)) {
            currentFilePath.set(selectedFile.getPath());
            setUpHelper.setUpUI();
            isChanged.set(false);
        } else {
            showErrorMessage(fileError);
        }
    }

    // MODIFIES: this
    // EFFECTS: saves app state to the file with currentFilePath,
    //          shows error message if used didn't choose a file
    //          if user is in the default file, treat as if they clicked SaveAs button
    @FXML
    void saveMenuItemClicked() {
        if (currentFilePath.get().equals(defaultFilePath)) {
            saveAsMenuItemClicked();
        }
        try (JsonWriter writer = new JsonWriter(currentFilePath.get())) {
            writer.open();
            writer.write(spendingList);
            isChanged.set(false);
        } catch (FileNotFoundException e) {
            showErrorMessage(e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: saves app state to a file that user chooses,
    //          shows error message if user didn't choose a file
    // Implementation is based on: http://java-buddy.blogspot.com/2015/03/javafx-example-save-textarea-to-file.html
    @FXML
    void saveAsMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Spending List (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        fileChooser.setInitialDirectory(new File("./data"));
        File file = fileChooser.showSaveDialog(null);

        if (Objects.nonNull(file)) {
            currentFilePath.set(file.getPath());
            try (JsonWriter writer = new JsonWriter(currentFilePath.get())) {
                writer.open();
                writer.write(spendingList);
                isChanged.set(false);
            } catch (FileNotFoundException e) {
                showErrorMessage(e.getMessage());
            }
        } else {
            showErrorMessage(fileError);
        }
    }

    // TODO: document
    @FXML
    void closeMenuItemClicked() {
        if (isChanged.get()) {
            showSavePopup();
        }
//        getPrimaryStage().close();
    }

    // MODIFIES: this
    // EFFECTS: adds new record if addGroup has record toggle selected,
    //          adds new category if addGroup has category toggle selected
    @FXML
    void add() {
        Toggle selectedToggle = addGroup.getSelectedToggle();
        if (selectedToggle.equals(recordToggleAdd)) {
            addNewRecord();
        } else if (selectedToggle.equals(categoryToggleAdd)) {
            addNewCategory();
        }
    }

    // MODIFIES: this
    // EFFECTS: removes record if removeGroup has record toggle selected,
    //          removes category if removeGroup has category toggle selected
    @FXML
    void removeSelected() {
        Toggle selectedToggle = removeGroup.getSelectedToggle();
        if (selectedToggle.equals(recordToggleRemove)) {
            List<Record> selectedRecords = new ArrayList<>(recordTable.getSelectionModel().getSelectedItems());
            selectedRecords.forEach(spendingList::removeRecord);
        } else if (selectedToggle.equals(categoryToggleRemove)) {
            List<Category> selectedCategories = new ArrayList<>(categoriesTable.getSelectionModel().getSelectedItems());
            selectedCategories.forEach(c -> categories.remove(c, spendingList));
            recordTable.refresh();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes title of selected record
    @FXML
    void changeTitleInRecords(TableColumn.CellEditEvent<Record, String> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setTitle(editedCell.getNewValue());
            isChanged.set(true);
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes amount of selected record
    @FXML
    void changeAmountInRecords(TableColumn.CellEditEvent<Record, Double> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setAmount(editedCell.getNewValue());
            isChanged.set(true);
        } catch (NegativeAmountException | NumberFormatException e) {
            showErrorMessage(e.getMessage());
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes category of selected record
    @FXML
    void changeCategoryInRecords(TableColumn.CellEditEvent<Record, Category> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        record.setCategory(editedCell.getNewValue());
        recordTable.refresh();
        recordTable.requestFocus();
        isChanged.set(true);
    }

    // MODIFIES: this
    // EFFECTS: changes name of selected category in the categories table
    @FXML
    void changeCategoryInCategories(TableColumn.CellEditEvent<Category, String> editedCell) {
        Category category = categories.getCategoryByName(editedCell.getOldValue());
        try {
            category.setName(editedCell.getNewValue());
            isChanged.set(true);
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        } finally {
            categoriesTable.refresh();
            recordTable.refresh();
            categoriesTable.requestFocus();
            setUpHelper.repopulateCategoriesComboBox();
        }
    }

    // MODIFIES: this
    // EFFECTS: enables amount and category fields
    @FXML
    void recordRBPressedInAddMenu() {
        amountFieldAdd.setDisable(false);
        categoriesBoxAdd.setDisable(false);
    }

    // MODIFIES: this
    // EFFECTS: disables amount and category fields
    @FXML
    void categoryRBPressedInAddMenu() {
        amountFieldAdd.setDisable(true);
        categoriesBoxAdd.setDisable(true);
    }

    // MODIFIES: this
    // EFFECTS: focuses on add menu
    @FXML
    void focusOnAddMenu()  {
        titleFieldAdd.requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: focuses on records table
    @FXML
    void focusOnRecords()  {
        recordTable.requestFocus();
    }


    // MODIFIES: this
    // EFFECTS: adds new category to the categories table,
    //          shows error message if entered name is invalid
    private void addNewCategory() {
        try {
            String name = titleFieldAdd.getText();
            new Category(name, categories);
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new record to the records table,
    //          shows error message if entered record field(s) is/are invalid
    private void addNewRecord() {
        try {
            String title = titleFieldAdd.getText();
            double amount = Double.parseDouble(amountFieldAdd.getText());
            String categoryName = categoriesBoxAdd.getValue();
            Record record = new Record(title, amount, categories.getCategoryByName(categoryName));
            spendingList.addRecord(record);
        } catch (NameException | NegativeAmountException | NumberFormatException e) {
            showErrorMessage(e.getMessage());
        }
    }

    // EFFECTS: shows a pop-up window asking user if they want to save changes
    // Implementation is based on https://code.makery.ch/blog/javafx-dialogs-official/
    void showSavePopup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save changes dialog");
        alert.setHeaderText("Would you like to save changes?");

        ButtonType save = new ButtonType("Save");
        ButtonType saveAs = new ButtonType("Save As");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (currentFilePath.get().equals(defaultFilePath)) {
            alert.getButtonTypes().setAll(saveAs, cancel);
        } else {
            alert.getButtonTypes().setAll(save, saveAs, cancel);
        }

        Optional<ButtonType> chosenButton = alert.showAndWait();
        chosenButton.ifPresent(b -> {
            if (b.equals(save)) {
                saveMenuItemClicked();
            } else if (b.equals(saveAs)) {
                saveAsMenuItemClicked();
            }
        });
    }

    // EFFECTS: shows a pop-up error message window with a given message
    // Implementation is based on https://stackoverflow.com/a/39151264
    void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input not valid");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}