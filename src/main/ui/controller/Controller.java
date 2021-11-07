package ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import model.Categories;
import model.Category;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import persistence.JsonReader;
import persistence.JsonWriter;
import ui.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Class that handles events and loads data to the GUI
public class Controller extends Main implements Initializable {

    private final String fieldsInitError = "Couldn't initialize data";
    private final String fileError = "Choose a file";

    @FXML private TextField titleFieldAdd;
    @FXML private TextField amountFieldAdd;
    @FXML private ComboBox<String> categoriesBoxAdd;

    @FXML private ToggleGroup addGroup;
    @FXML private RadioButton recordToggleAdd;
    @FXML private RadioButton categoryToggleAdd;

    @FXML private ToggleGroup removeGroup;
    @FXML private RadioButton recordToggleRemove;
    @FXML private RadioButton categoryToggleRemove;

    @FXML private TableView<Record> recordTable;
    @FXML private TableColumn<Record, String> titleColumn;
    @FXML private TableColumn<Record, Double> amountColumn;
    @FXML private TableColumn<Record, Category> categoryColumn;
    @FXML private TableColumn<Record, LocalDateTime> dateColumn;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> categoriesColumn;
    @FXML private TableColumn<Category, Boolean> isShownColumn;

    @FXML private BorderPane borderPane;

    private ObservableList<String> categoriesNamesList;
    private Categories categories;
    private SpendingList spendingList;

    private Category travelCategory;
    private Category groceriesCategory;
    private Category clothingCategory;

    private boolean isChanged = false;
    private String currentFile = "";

    // EFFECTS: initializes the data
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            currentFile = "./data/spendingList.json";
            initSpendingList();
            bindDataWithComponents();
            setUpUIComponents();
        } catch (NegativeAmountException | NameException e) {
            showErrorMessage(fieldsInitError);
        }
    }

    @FXML
    void newMenuItemClicked() {
        if (isChanged) {
            // TODO: ask if they want to save
        }
        currentFile = "./data/emptyFile.json";
        try {
            initSpendingList();
            bindDataWithComponents();
            setUpUIComponents();
        } catch (NegativeAmountException | NameException e) {
            showErrorMessage(fieldsInitError);
        }
    }

    @FXML
    // Implementation is based on: https://www.youtube.com/watch?v=hNz8Xf4tMI4
    void openMenuItemClicked() {
        if (isChanged) {
            // TODO: ask if they want to save
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./data"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (Objects.nonNull(selectedFile)) {
            currentFile = selectedFile.getPath();
            try {
                initSpendingList();
                bindDataWithComponents();
                setUpUIComponents();
            } catch (NegativeAmountException | NameException e) {
                showErrorMessage(fieldsInitError);
            }
        } else {
            showErrorMessage(fileError);
        }
    }

    @FXML
    void saveMenuItemClicked() {
        try (JsonWriter writer = new JsonWriter(currentFile)) {
            writer.open();
            writer.write(spendingList);
        } catch (FileNotFoundException e) {
            showErrorMessage(e.getMessage());
        }
        isChanged = false;
    }

    @FXML
    // Implementation is based on: http://java-buddy.blogspot.com/2015/03/javafx-example-save-textarea-to-file.html
    void saveAsMenuItemClicked() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Spending List (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        fileChooser.setInitialDirectory(new File("./data"));
        File file = fileChooser.showSaveDialog(null);

        if (Objects.nonNull(file)) {
            currentFile = file.getPath();
            try (JsonWriter writer = new JsonWriter(currentFile)) {
                writer.open();
                writer.write(spendingList);
            } catch (FileNotFoundException e) {
                showErrorMessage(e.getMessage());
            }
        } else {
            showErrorMessage(fileError);
        }
        isChanged = false;
    }

    @FXML
    void closeMenuItemClicked() {
        if (isChanged) {
            // TODO: ask if they want to save
        }
        getPrimaryStage().close();
    }

    // MODIFIES: this
    // EFFECTS: adds record or category depending on user's choice
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
    // EFFECTS: removes selected row from records table
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
    // EFFECTS: changes title of the selected record
    @FXML
    void changeRecordTitleCell(TableColumn.CellEditEvent<Record, String> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setTitle(editedCell.getNewValue());
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
            isChanged = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes amount of the selected record
    @FXML
    void changeRecordAmountCell(TableColumn.CellEditEvent<Record, Double> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setAmount(editedCell.getNewValue());
        } catch (NegativeAmountException | NumberFormatException e) {
            showErrorMessage(e.getMessage());
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
            isChanged = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes category of the selected record
    @FXML
    void changeRecordCategoryCell(TableColumn.CellEditEvent<Record, Category> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        record.setCategory(editedCell.getNewValue());
        recordTable.refresh();
        recordTable.requestFocus();
        isChanged = true;
    }

    // MODIFIES: this
    // EFFECTS: changes name of the selected category in the categories table
    @FXML
    void changeCategoriesCategoryCell(TableColumn.CellEditEvent<Category, String> editedCell) {
        Category category = categories.getCategoryByName(editedCell.getOldValue());
        try {
            category.setName(editedCell.getNewValue());
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        } finally {
            categoriesTable.refresh();
            recordTable.refresh();
            categoriesTable.requestFocus();
            repopulateCategoriesComboBox();
            isChanged = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: enables amount and category fields
    @FXML
    void recordRadioButtonPressedAdd() {
        amountFieldAdd.setDisable(false);
        categoriesBoxAdd.setDisable(false);
    }

    // MODIFIES: this
    // EFFECTS: disables amount and category fields
    @FXML
    void categoryRadioButtonPressedAdd() {
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
    // EFFECTS: focuses on add menu
    @FXML
    void focusOnRecords()  {
        recordTable.requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: specifies GUI parameters
    private void setUpUIComponents() {
        recordTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        recordTable.getSelectionModel().setCellSelectionEnabled(true);
        categoriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoriesTable.getSelectionModel().setCellSelectionEnabled(true);

        // implementation is based on: https://stackoverflow.com/a/56309916
        categoriesTable.setRowFactory(callable -> new TableRow<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (Objects.nonNull(category) && category.equals(categories.getDefaultCategory())) {
                    setStyle("-fx-background-color: skyblue");
                } else {
                    setStyle("");
                }
            }
        });

        recordTable.setRowFactory(callable -> new TableRow<Record>() {
            @Override
            protected void updateItem(Record record, boolean empty) {
                super.updateItem(record, empty);
                if (Objects.nonNull(record) && record.getCategory().equals(categories.getDefaultCategory())) {
                    setStyle("-fx-background-color: skyblue");
                } else {
                    setStyle("");
                }
            }
        });

        categoriesTable.refresh();
        makeTableCellsEditable();
        Platform.runLater(() -> recordTable.requestFocus());
    }

    // MODIFIES: this
    // EFFECTS: populates tables and combo boxes with spending list data
    private void bindDataWithComponents() {
        populateRecordsTable();
        populateCategoriesTable();
        repopulateCategoriesComboBox();
    }

    // MODIFIES: this
    // EFFECTS: populates records table
    // Implementation is based on https://youtu.be/uh5R7D_vFto?list=PLoodc-fmtJNYbs-gYCdd5MYS4CKVbGHv2&t=786
    private void populateRecordsTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        formatCategoryColumn(categoryColumn);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("timeAdded"));
        formatDateColumn(dateColumn);

        recordTable.setItems(spendingList.getRecords());
    }

    // MODIFIES: this
    // EFFECTS: populates categories table
    // Based on: https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    private void populateCategoriesTable() {
        categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        isShownColumn.setCellValueFactory(new PropertyValueFactory<>("isShown"));
        formatIsShownColumn(isShownColumn);

        categoriesTable.setItems(categories.getCategories());
    }

    // MODIFIES: this
    // EFFECTS: adds new category to the table
    private void addNewCategory() {
        try {
            String name = titleFieldAdd.getText();
            new Category(name, categories);
        } catch (NameException e) {
            showErrorMessage(e.getMessage());
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new record to the table
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

    // MODIFIES: this
    // EFFECTS: populates combobox with categories names,
    //          sets first element of the combo box to the first element of categories
    private void repopulateCategoriesComboBox() {
        categoriesBoxAdd.getItems().clear();
        categoriesBoxAdd.setItems(FXCollections.observableArrayList(categories.getCategoriesNames()));
        categoriesBoxAdd.getSelectionModel().select(0);
    }

    // MODIFIES: isShownColumn
    // EFFECTS: formats isShown cells
    // Based on https://stackoverflow.com/a/50224259
    private void formatIsShownColumn(TableColumn<Category, Boolean> isShownColumn) {
        isShownColumn.setCellFactory(tableColumn -> new TableCell<Category, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    CheckBox checkBox = new CheckBox("");
                    checkBox.setSelected(item);
                }
            }
        });
    }

    // MODIFIES: categoryColumn
    // EFFECTS: formats category cells
    // Based on https://stackoverflow.com/a/50224259
    private void formatCategoryColumn(TableColumn<Record, Category> categoryColumn) {
        categoryColumn.setCellFactory(tableColumn -> new TableCell<Record, Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    // MODIFIES: dateColumn
    // EFFECTS: formats date cells
    // Based on https://stackoverflow.com/a/50224259
    private void formatDateColumn(TableColumn<Record, LocalDateTime> dateColumn) {
        String pattern = "MMM. dd - HH:mm";
        dateColumn.setCellFactory(tableColumn -> new TableCell<Record, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(DateTimeFormatter.ofPattern(pattern).format(item));
                }
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: inits records with slight delay between each one,
    //          throws NameException if title is blank
    //          throws NegativeAmountException if amount <= 0
    private void initSpendingList() throws NegativeAmountException, NameException {
        categories = new Categories();
        spendingList = new SpendingList(categories);
        try {
            JsonReader reader = new JsonReader(currentFile);
            spendingList = reader.read();
            categories = spendingList.getCategories();
            categories.getCategories().addListener((ListChangeListener<Category>) c -> repopulateCategoriesComboBox());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            spendingList.addRecord(new Record("Went to Montreal", 507.68, travelCategory));
//            Thread.sleep(10);
//            spendingList.addRecord(new Record("Bought jeans", 60.68, clothingCategory));
//            Thread.sleep(10);
//            spendingList.addRecord(new Record("Went to NoFrills", 70.67, groceriesCategory));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    // MODIFIES: this
    // EFFECTS: makes records table cells editable
    private void makeTableCellsEditable() {
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object.getName();
            }

            @Override
            public Category fromString(String string) {
                Category category;
                try {
                    category = new Category(string, categories);
                    return category;
                } catch (NameException e) {
                    showErrorMessage(e.getMessage());
                }
                return null;
            }
        }, categories.getCategories()));
        categoriesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    // EFFECTS: shows a pop-up error message window with a given message
    // Implementation is based on https://stackoverflow.com/questions/39149242/how-can-i-do-an-error-messages-in-javafx
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input not valid");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}