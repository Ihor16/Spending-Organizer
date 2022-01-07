package ui.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import persistence.JsonWriter;
import ui.controllers.enums.SceneEnum;
import ui.controllers.holders.SceneHolder;
import ui.controllers.holders.SpendingListHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

// Class that handles events in the main window
public class Controller implements Initializable {

    private final String fileError = "Choose a file";
    // File path to load from when user does File -> New,
    // file at this path is never modified
    private final String defaultFilePath = "./data/emptyFile.json";
    // File user selected during SaveAs operation
    // At the start of SaveAs operation it is set to null, and if user didn't choose a file, it remains null
    private File selectedFileDuringSaveAs;

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
    @FXML MenuItem changeViewMenuItem;
    @FXML Label filenameLabel;

    SpendingList spendingList;

    // True if data has been changed (if it's been changed, save pop-up menu is displayed)
    SimpleBooleanProperty isChanged;
    // File path to load from (to load from a new file, change this variable and call setUpHelper)
    SimpleStringProperty currentFilePath;

    private SetUpHelper setUpHelper;
    final SceneHolder sceneHolder = SceneHolder.getInstance();
    final SpendingListHolder spendingListHolder = SpendingListHolder.getInstance();

    // MODIFIES: this
    // EFFECTS: initializes application
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpHelper = new SetUpHelper(this);
        isChanged = new SimpleBooleanProperty(false);
        currentFilePath = new SimpleStringProperty("./data/emptyFile.json");
        setUpHelper.setUpUI();
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to save changes, and opens an empty file
    @FXML
    void newMenuItemClicked() {
        if (isChanged.get()) {
            if (showSavePopup()) {
                newFile();
            }
        } else {
            newFile();
        }
    }

    // MODIFIES: this
    // EFFECTS: opens an empty file, and
    //          disables chart view
    private void newFile() {
        currentFilePath.set(defaultFilePath);
        setUpHelper.setUpUI();
        isChanged.set(false);
        sceneHolder.getSceneMap().remove(SceneEnum.CHART);
        changeViewMenuItem.setDisable(true);
    }

    // MODIFIES: this
    // EFFECTS: asks user if they want to save changes, and opens chosen file
    @FXML
    void openMenuItemClicked() {
        if (isChanged.get()) {
            if (showSavePopup()) {
                openChosenFile();
            }
        } else {
            openChosenFile();
        }
    }

    // MODIFIES: this
    // EFFECTS: opens chosen file, and disables chart view if opened file has no records
    //          shows error message if used didn't choose a file
    // Implementation is based on: https://youtu.be/hNz8Xf4tMI4?t=345
    private void openChosenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./data"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (Objects.nonNull(selectedFile)) {
            currentFilePath.set(selectedFile.getPath());
            setUpHelper.setUpUI();
            isChanged.set(false);
            sceneHolder.getSceneMap().remove(SceneEnum.CHART);
            changeViewMenuItem.setDisable(spendingList.getRecords().isEmpty());
        } else {
            setUpHelper.showErrorMessage(fileError);
        }
    }

    // MODIFIES: this
    // EFFECTS: saves app state to the file with currentFilePath path,
    //          shows error message if used didn't choose a file,
    //          if user is in default empty file, treat as if they clicked SaveAs button
    @FXML
    void saveMenuItemClicked() {
        if (currentFilePath.get().equals(defaultFilePath)) {
            saveAsMenuItemClicked();
            if (Objects.isNull(selectedFileDuringSaveAs)) {
                focusOnRecordsOrCategories();
            }
        } else {
            try (JsonWriter writer = new JsonWriter(currentFilePath.get())) {
                writer.open();
                writer.write(spendingList);
                isChanged.set(false);
            } catch (FileNotFoundException e) {
                setUpHelper.showErrorMessage(e.getMessage());
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: saves app state to a file that user chooses,
    //          shows error message if user didn't choose a file
    // INVARIANT: if user didn't choose a file, selectedFileDuringSaveAs is null after this methods
    // Implementation is based on: http://java-buddy.blogspot.com/2015/03/javafx-example-save-textarea-to-file.html
    @FXML
    void saveAsMenuItemClicked() {
        selectedFileDuringSaveAs = null;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Spending Organizer (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(new File("./data"));

        selectedFileDuringSaveAs = fileChooser.showSaveDialog(null);

        if (Objects.nonNull(selectedFileDuringSaveAs)) {
            currentFilePath.set(selectedFileDuringSaveAs.getPath());
            try (JsonWriter writer = new JsonWriter(currentFilePath.get())) {
                writer.open();
                writer.write(spendingList);
                isChanged.set(false);
            } catch (FileNotFoundException e) {
                setUpHelper.showErrorMessage(e.getMessage());
                selectedFileDuringSaveAs = null;
            }
        } else {
            setUpHelper.showErrorMessage(fileError);
        }
    }

    // EFFECTS: asks user if they want to save changes and closes the app
    @FXML
    public void closeMenuItemClicked() {
        if (isChanged.get()) {
            if (showSavePopup()) {
                Platform.exit();
                EventLog.getInstance().forEach(System.out::println);
            }
        } else {
            Platform.exit();
            EventLog.getInstance().forEach(System.out::println);
        }
    }

    // MODIFIES: this
    // EFFECTS: if addGroup has record toggle selected, adds new record
    //          if addGroup has category toggle selected, adds new category
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
            selectedCategories.forEach(c -> spendingList.getCategories().remove(c, spendingList));
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
            setUpHelper.showErrorMessage(e.getMessage());
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
            setUpHelper.showErrorMessage(e.getMessage());
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
        Category category = spendingList.getCategories().getCategoryByName(editedCell.getOldValue());
        try {
            category.setName(editedCell.getNewValue(), spendingList.getCategories());
            isChanged.set(true);
        } catch (NameException e) {
            setUpHelper.showErrorMessage(e.getMessage());
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
    // EFFECTS: switches focus from records to categories table and vice versa
    @FXML
    void focusOnRecordsOrCategories() {
        if (recordTable.isFocused()) {
            categoriesTable.requestFocus();
        } else {
            recordTable.requestFocus();
        }
    }

    // MODIFIES: this
    // EFFECTS: focuses on add menu to add a new record
    @FXML
    public void focusOnAddRecord() {
        titleFieldAdd.requestFocus();
        recordToggleAdd.setSelected(true);
        recordRBPressedInAddMenu();
    }

    // MODIFIES: this
    // EFFECTS: focuses on add menu to add a new category
    @FXML
    public void focusOnAddCategory() {
        titleFieldAdd.requestFocus();
        categoryToggleAdd.setSelected(true);
        categoryRBPressedInAddMenu();
    }

    // MODIFIES: this
    // EFFECTS: removes selected records or categories depending which table user is focused on
    @FXML
    public void removeSelectedRecordsOrCategories() {
        if (recordTable.isFocused()) {
            recordToggleRemove.setSelected(true);
            removeSelected();
        } else if (categoriesTable.isFocused()) {
            categoryToggleRemove.setSelected(true);
            removeSelected();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes scene to bar chart scene
    //          if sceneHolder already contains Chart scene, simply sets it,
    //          otherwise, loads a new FXML file
    // Implementation is based on https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8
    @FXML
    public void changeSceneToChart() {
        sceneHolder.getSceneMap().put(SceneEnum.MAIN, recordTable.getScene());
        Stage window = (Stage) recordTable.getScene().getWindow();
        if (sceneHolder.getSceneMap().containsKey(SceneEnum.CHART)) {
            window.setScene(sceneHolder.getSceneMap().get(SceneEnum.CHART));
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/resources/chart.fxml"));
                spendingListHolder.setSpendingList(spendingList);
                Parent chartViewParent = loader.load();

                window.setScene(new Scene(chartViewParent));
                window.show();

            } catch (IOException e) {
                setUpHelper.showErrorMessage("Couldn't load the chart view");
                e.printStackTrace();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new category to the categories table,
    //          shows error message if entered name is invalid
    private void addNewCategory() {
        try {
            String name = titleFieldAdd.getText();
            new Category(name, spendingList.getCategories());
        } catch (NameException e) {
            setUpHelper.showErrorMessage(e.getMessage());
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
            Record record = new Record(title, amount, spendingList.getCategories().getCategoryByName(categoryName));
            spendingList.addRecord(record);
        } catch (NameException | NegativeAmountException | NumberFormatException e) {
            setUpHelper.showErrorMessage(e.getMessage());
        }
    }

    // EFFECTS: shows a pop-up window asking user if they want to save changes and does a chosen operation,
    //          returns false if user chooses to cancel this pop-up window
    // Implementation is based on https://code.makery.ch/blog/javafx-dialogs-official/
    boolean showSavePopup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save changes dialog");
        alert.setHeaderText("Would you like to save changes?");
        alert.getDialogPane().setMinWidth(470);

        ButtonType save = new ButtonType("Save");
        ButtonType saveAs = new ButtonType("Save As");
        ButtonType dontSave = new ButtonType("Don't Save");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (currentFilePath.get().equals(defaultFilePath)) {
            alert.getButtonTypes().setAll(saveAs, dontSave, cancel);
        } else {
            alert.getButtonTypes().setAll(save, saveAs, dontSave, cancel);
        }

        Optional<ButtonType> chosenButton = alert.showAndWait();
        return chosenButton.map(buttonType -> didChooseASaveOption(buttonType, save, saveAs, dontSave))
                .orElse(false);
    }

    // EFFECTS: returns false if (1) user chooses none of save, saveAs, don't save buttons
    //          OR (2) user chose saveAs option but didn't specify a file to save to
    private boolean didChooseASaveOption(ButtonType chosenButton, ButtonType save,
                                         ButtonType saveAs, ButtonType dontSave) {
        if (chosenButton.equals(save)) {
            saveMenuItemClicked();
            return true;
        } else if (chosenButton.equals(saveAs)) {
            saveAsMenuItemClicked();
            // If user didn't choose file during SaveAs (i.e., if selectedFileDuringSaveAs is null),
            // treat as if they chose to cancel pop-up window
            return selectedFileDuringSaveAs != null;
        } else {
            return chosenButton.equals(dontSave);
        }
    }
}