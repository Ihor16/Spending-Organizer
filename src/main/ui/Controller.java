package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {

    @FXML private TextField titleFieldAdd;
    @FXML private TextField amountFieldAdd;

    @FXML private ComboBox<String> categoriesBoxAdd;
    @FXML private ComboBox<String> categoriesBoxRemove;
    @FXML private TextField indexFieldRemove;

    @FXML private Button addRecordButton;
    @FXML private Button removeRecordButton;
    @FXML private ToggleGroup addGroup;
    @FXML private ToggleGroup removeGroup;

    @FXML private TableView<Record> recordTable;
    @FXML private TableColumn<Record, Integer> indexColumn;
    @FXML private TableColumn<Record, String> titleColumn;
    @FXML private TableColumn<Record, Double> amountColumn;
    @FXML private TableColumn<Record, String> categoryColumn;
    @FXML private TableColumn<Record, LocalDateTime> dateColumn;

    @FXML private TableView<String> categoriesTable;
    @FXML private TableColumn<String, String> categoriesColumn;


    private SpendingList spendingList;


    @FXML
    void addRecord(ActionEvent event) {

    }

    @FXML
    void removeRecord(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initSpendingList();
            bindSpendingListWithComponents();
        } catch (NegativeAmountException | NameException e) {
            showErrorMessage("Couldn't initialize fields");
        }
    }

    // TODO
    // MODIFIES: this
    // EFFECTS: populates tables and combo boxes with spending list data
    private void bindSpendingListWithComponents() {
        populateRecordsTable();
        populateCategoriesTable();
        populateComboBoxes();
    }

    private void populateCategoriesTable() {
        categoriesTable.setItems(spendingList.getCategoriesAsProperty());
    }

    private void populateComboBoxes() {
        categoriesBoxAdd.getItems().addAll(spendingList.getCategories());
        categoriesBoxRemove.getItems().addAll(spendingList.getCategories());
    }

    // EFFECTS: populates records table
    // Implementation is based on https://youtu.be/uh5R7D_vFto?list=PLoodc-fmtJNYbs-gYCdd5MYS4CKVbGHv2&t=786
    private void populateRecordsTable() {
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(data -> data.getValue().getTimeAddedAsProperty());

        // Implementation of formatting cells in table view is taken from
        // https://stackoverflow.com/questions/50224091/getting-localdate-to-display-in-a-tableview-in-javafx
        dateColumn.setCellFactory(col -> new TableCell<Record, LocalDateTime>() {

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(DateTimeFormatter.ofPattern("MMM. dd - HH:mm").format(item));
                }
            }
        });

        ObservableList<Record> records = FXCollections.observableArrayList();
        records.setAll(spendingList.getRecords());

        recordTable.setItems(records);
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

    // MODIFIES: shows a pop-up error message window with a given message
    // Implementation is based on https://stackoverflow.com/questions/39149242/how-can-i-do-an-error-messages-in-javafx
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input not valid");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}