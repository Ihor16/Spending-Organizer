package ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Class that handles events and loads data to the GUI
public class Controller implements Initializable {

    private final String categoryTitleError = "Category title cannot be blank";
    private final String amountError = "Amount must be a positive number";
    private final String recordTitleError = "Record title cannot be blank";
    private final String fieldsInitError = "Couldn't initialize fields";
    private final String recordOrCategoryTitleError = "Record title or category cannot be blank";

    @FXML private TextField titleFieldAdd;
    @FXML private TextField amountFieldAdd;
    @FXML private ComboBox<String> categoriesBoxAdd;
    @FXML private RadioButton recordToggleAdd;
    @FXML private RadioButton categoryToggleAdd;

    @FXML private ToggleGroup addGroup;
    @FXML private ToggleGroup removeGroup;

    @FXML private TableView<Record> recordTable;
    @FXML private TableColumn<Record, String> titleColumn;
    @FXML private TableColumn<Record, Double> amountColumn;
    @FXML private TableColumn<Record, String> categoryColumn;
    @FXML private TableColumn<Record, LocalDateTime> dateColumn;

    @FXML private TableView<Map<String, String>> categoriesTable;
    @FXML private TableColumn<Map, String> categoriesColumn;

    private SpendingList spendingList;

    // EFFECTS: initializes the data
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initSpendingList();
            bindDataWithComponents();
            setUpUIComponents();
        } catch (NegativeAmountException | NameException e) {
            showErrorMessage(fieldsInitError);
        }
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
        List<Record> selectedRecords = new ArrayList<>(recordTable.getSelectionModel().getSelectedItems());
        selectedRecords.forEach(spendingList::removeRecord);
    }

    // MODIFIES: this
    // EFFECTS: changes title of the selected record
    @FXML
    void changeRecordTitleCell(TableColumn.CellEditEvent<Record, String> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setTitle(editedCell.getNewValue());
        } catch (NameException e) {
            showErrorMessage(recordTitleError);
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
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
            showErrorMessage(amountError);
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes category of the selected record
    @FXML
    void changeRecordCategoryCell(TableColumn.CellEditEvent<Record, String> editedCell) {
        Record record = recordTable.getSelectionModel().getSelectedItem();
        try {
            record.setCategory(editedCell.getNewValue(), spendingList.getCategories());
        } catch (NameException e) {
            showErrorMessage(categoryTitleError);
        } finally {
            recordTable.refresh();
            recordTable.requestFocus();
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
        makeTableCellsEditable();
        Platform.runLater(() -> recordTable.requestFocus());
    }

    // MODIFIES: this
    // EFFECTS: populates tables and combo boxes with spending list data
    private void bindDataWithComponents() {
        populateRecordsTable();
        populateCategoriesTable();
        populateCategoriesComboBox();
    }

    // MODIFIES: this
    // EFFECTS: populates records table
    // Implementation is based on https://youtu.be/uh5R7D_vFto?list=PLoodc-fmtJNYbs-gYCdd5MYS4CKVbGHv2&t=786
    private void populateRecordsTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(data -> data.getValue().getTimeAddedAsProperty());
        formatDateColumn(dateColumn);

        recordTable.setItems(spendingList.getRecords());
    }

    // MODIFIES: this
    // EFFECTS: populates categories table
    // Based on: https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    private void populateCategoriesTable() {
        String mapKey = "A";
        categoriesColumn.setCellValueFactory(new MapValueFactory<>(mapKey));

        // TODO: probably remove
//        Callback<TableColumn<Map, String>, TableCell<Map, String>> cellFactory =
//                param -> new TextFieldTableCell<>(new StringConverter<String>() {
//                    @Override
//                    public String toString(String object) {
//                        return object;
//                    }
//
//                    @Override
//                    public String fromString(String string) {
//                        return string;
//                    }
//                });
//        categoriesColumn.setCellFactory(cellFactory);

        categoriesTable.setItems(spendingList.getCategoriesAsObservableList(mapKey));
    }

    // MODIFIES: this
    // EFFECTS: adds new category to the table
    private void addNewCategory() {
        try {
            String title = titleFieldAdd.getText();
            spendingList.addCategory(title);

            // Have to repopulate the categories GUI components because
            // they don't update dynamically
            populateCategoriesTable();
            populateCategoriesComboBox();
        } catch (NameException e) {
            showErrorMessage(recordOrCategoryTitleError);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new record to the table
    private void addNewRecord() {
        try {
            String title = titleFieldAdd.getText();
            double amount = Double.parseDouble(amountFieldAdd.getText());
            String category = categoriesBoxAdd.getValue();
            Record record = new Record(title, amount, category);
            spendingList.addRecord(record);
        } catch (NameException e) {
            showErrorMessage(recordOrCategoryTitleError);
        } catch (NegativeAmountException | NumberFormatException e) {
            showErrorMessage(amountError);
        }
    }

    // MODIFIES: this
    // EFFECTS: clears categories combo box and then populates it with categories,
    //          if categories is not empty, set's first elements of the combo box to the first element
    //          of the categories set
    private void populateCategoriesComboBox() {
        categoriesBoxAdd.getItems().clear();
        Set<String> categories = spendingList.getCategories();
        categoriesBoxAdd.getItems().addAll(categories);
        if (!categories.isEmpty()) {
            categoriesBoxAdd.getSelectionModel().select(0);
        }
    }

    // MODIFIES: dateColumn
    // EFFECTS: formats date cells
    // Based on https://stackoverflow.com/questions/50224091/getting-localdate-to-display-in-a-tableview-in-javafx
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
        spendingList = new SpendingList();
        try {
            spendingList.addRecord(new Record("Went to Montreal", 507.68, "Travel"));
            Thread.sleep(10);
            spendingList.addRecord(new Record("Bought jeans", 60.68, "Clothing"));
            Thread.sleep(10);
            spendingList.addRecord(new Record("Went to NoFrills", 70.67, "Groceries"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: makes records table cells editable
    private void makeTableCellsEditable() {
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.addAll(spendingList.getCategories());
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
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