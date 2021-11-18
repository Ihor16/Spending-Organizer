package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import model.Categories;
import model.Category;
import model.Record;
import model.SpendingList;
import model.exceptions.NameException;
import model.exceptions.NegativeAmountException;
import persistence.JsonReader;
import ui.controllers.enums.SceneEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// Helper class that loads data to the GUI and formats GUI components in the main scene
public class SetUpHelper {

    private Controller cl;
    private final String defaultCellStyle = "-fx-background-color: skyblue";
    protected final String dateTimeFormat = "MMM. dd, yyyy - HH:mm";
    protected final String monthFormat = "MMMM yyyy";
    protected final String prettyDateFormat = "MMM. dd, yyyy";
    protected final String standardDateFormat = "MM/dd/yyyy";

    // Is used for all controllers apart from ui.controllers.Controller
    public SetUpHelper() {
    }

    public SetUpHelper(Controller controller) {
        this.cl = controller;
    }

    // MODIFIES: cl
    // EFFECTS: sets up ui and binds it with application data,
    //          cat be used to refresh app state based on the cl.currentFile value
    void setUpUI() {
        try {
            readSpendingList();
            addListeners();
            bindDataWithComponents();
            setUpUIComponents();
        } catch (NegativeAmountException | NameException e) {
            cl.showErrorMessage("Couldn't initialize data");
        }
    }

    // MODIFIES: cl
    // EFFECTS: reads spending list from cl.currentFilePath
    //          throws Exception if file is corrupted
    private void readSpendingList() throws NegativeAmountException, NameException {
        cl.categories = new Categories();
        cl.spendingList = new SpendingList(cl.categories);
        try {
            JsonReader reader = new JsonReader(cl.currentFilePath.get());
            cl.spendingList = reader.read();
            cl.categories = cl.spendingList.getCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: cl
    // EFFECTS: adds listeners for categories, spending list, isChanged, and currentFilePath
    //          to perform operations when they're changed
    private void addListeners() {
        addCategoriesListener();
        addSpendingListListener();
        addIsChangedListener();
        addFilePathListener();

    }

    // MODIFIES: this
    // EFFECTS: Changes isChanged property when categories are added or removed
    private void addCategoriesListener() {
        cl.categories.getCategories()
                .addListener((ListChangeListener<Category>) c -> {
                    repopulateCategoriesComboBox();
                    cl.isChanged.set(true);
                });
    }

    // MODIFIES: this
    // EFFECTS: changes isChanged property when records are added or removed,
    //          if spendingList doesn't have records, disables possibility of switching to chart menu
    //          and removes chart scene from sceneHolder
    private void addSpendingListListener() {
        cl.spendingList.getRecords()
                .addListener((ListChangeListener<Record>) c -> {
                    cl.isChanged.set(true);
                    if (cl.spendingList.getRecords().isEmpty()) {
                        cl.changeViewMenuItem.setDisable(true);
                        cl.sceneHolder.getSceneMap().remove(SceneEnum.CHART);
                    } else {
                        cl.changeViewMenuItem.setDisable(false);
                    }
                });
    }

    // MODIFIES: this
    // EFFECTS: changes filenameLabel and enables/disables save button to show if changes are saved
    private void addIsChangedListener() {
        cl.isChanged.addListener(c -> {
            cl.saveMenuItem.setDisable(!(cl.isChanged.get()));
            if (cl.isChanged.get()) {
                cl.saveMenuItem.setDisable(false);
                cl.filenameLabel.setText("*" + parseFileName(cl.currentFilePath.get()));
            } else {
                cl.saveMenuItem.setDisable(true);
                cl.filenameLabel.setText(parseFileName(cl.currentFilePath.get()));
            }
        });
    }

    // EFFECTS: changes filename label to current file path
    private void addFilePathListener() {
        cl.currentFilePath.addListener(c -> cl.filenameLabel.setText(parseFileName(cl.currentFilePath.get())));
    }

    // MODIFIES: cl
    // EFFECTS: populates tables and combo boxes with spending list data
    private void bindDataWithComponents() {
        populateRecordsTable();
        populateCategoriesTable();
        repopulateCategoriesComboBox();
    }

    // MODIFIES: cl
    // EFFECTS: populates combobox with categories names,
    //          sets first element of the combo box to the first element of categories
    void repopulateCategoriesComboBox() {
        cl.categoriesBoxAdd.getItems().clear();
        cl.categoriesBoxAdd
                .setItems(FXCollections.observableArrayList(cl.categories.getCategoriesNames()));
        cl.categoriesBoxAdd.getSelectionModel().selectFirst();
    }

    // MODIFIES: cl
    // EFFECTS: populates records table
    // Implementation is based on https://youtu.be/uh5R7D_vFto?list=PLoodc-fmtJNYbs-gYCdd5MYS4CKVbGHv2&t=786
    private void populateRecordsTable() {
        cl.titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        cl.amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        cl.categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        cl.dateColumn.setCellValueFactory(new PropertyValueFactory<>("timeAdded"));
        formatDateColumn(cl.dateColumn);

        cl.recordTable.setItems(cl.spendingList.getRecords());
    }

    // MODIFIES: cl
    // EFFECTS: populates categories table
    // Based on: https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
    private void populateCategoriesTable() {
        cl.categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cl.isShownColumn.setCellValueFactory(new PropertyValueFactory<>("isShown"));
        formatIsShownColumn(cl.isShownColumn);
        cl.categoriesTable.setItems(cl.categories.getCategories());
    }

    // MODIFIES: cl
    // EFFECTS: formats date cells
    // Based on https://stackoverflow.com/a/50224259
    private void formatDateColumn(TableColumn<Record, LocalDateTime> dateColumn) {
        dateColumn.setCellFactory(tableColumn -> new TableCell<Record, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(DateTimeFormatter.ofPattern(dateTimeFormat).format(item));
                }
            }
        });
    }

    // MODIFIES: cl
    // EFFECTS: formats isShown cells
    // Based on https://stackoverflow.com/a/50224259
    protected void formatIsShownColumn(TableColumn<Category, Boolean> isShownColumn) {
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


    // MODIFIES: cl
    // EFFECTS: specifies GUI parameters
    private void setUpUIComponents() {
        cl.filenameLabel.setText(parseFileName(cl.currentFilePath.get()));
        enableMultipleCellsSelection();
        colorDefaultCategoryInCategoriesTable();
        colorRecordsWithDefaultCategory();
        makeCellsEditable();
        Platform.runLater(() -> cl.recordTable.requestFocus());
    }

    // MODIFIES: cl
    // EFFECTS: enables multiple cell selection in records and categories tables
    private void enableMultipleCellsSelection() {
        cl.recordTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cl.recordTable.getSelectionModel().setCellSelectionEnabled(true);

        cl.categoriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cl.categoriesTable.getSelectionModel().setCellSelectionEnabled(true);

    }

    // MODIFIES: cl
    // EFFECTS: colors records in records table that have default category
    private void colorRecordsWithDefaultCategory() {
        cl.recordTable.setRowFactory(callable -> new TableRow<Record>() {
            @Override
            protected void updateItem(Record record, boolean empty) {
                super.updateItem(record, empty);
                if (Objects.nonNull(record)
                        && record.getCategory().equals(cl.categories.getDefaultCategory())) {
                    setStyle(defaultCellStyle);
                } else {
                    setStyle("");
                }
            }
        });
        cl.categoriesTable.refresh();
    }

    // MODIFIES: cl
    // EFFECTS: colors default category in the categories table
    private void colorDefaultCategoryInCategoriesTable() {
        // implementation is based on: https://stackoverflow.com/a/56309916
        cl.categoriesTable.setRowFactory(callable -> new TableRow<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (Objects.nonNull(category) && category.equals(cl.categories.getDefaultCategory())) {
                    setStyle(defaultCellStyle);
                } else {
                    setStyle("");
                }
            }
        });
    }

    // MODIFIES: categoriesTable
    // EFFECTS: colors default category in categoriesTable
    protected void colorDefaultCategoryInCategoriesTable(TableView<Category> categoriesTable, Categories categories) {
        // implementation is based on: https://stackoverflow.com/a/56309916
        categoriesTable.setRowFactory(callable -> new TableRow<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (Objects.nonNull(category) && category.equals(categories.getDefaultCategory())) {
                    setStyle(defaultCellStyle);
                } else {
                    setStyle("");
                }
            }
        });
    }

    // MODIFIES: cl
    // EFFECTS: makes cells in records and categories tables editable
    private void makeCellsEditable() {
        cl.titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cl.amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cl.categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object.getName();
            }

            @Override
            public Category fromString(String string) {
                Category category;
                try {
                    category = new Category(string, cl.categories);
                    return category;
                } catch (NameException e) {
                    cl.showErrorMessage(e.getMessage());
                }
                return null;
            }
        }, cl.categories.getCategories()));

        cl.categoriesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    // EFFECTS: returns a filename from given filepath
    private String parseFileName(String filePath) {
        List<String> chunks;
        if (cl.currentFilePath.get().contains("\\")) {
            chunks = Arrays.asList(filePath.split("\\\\"));
        } else {
            chunks = Arrays.asList(filePath.split("/"));
        }
        return chunks.get(chunks.size() - 1);
    }

    // EFFECTS: shows a pop-up error window with a given message
    // Implementation is based on https://stackoverflow.com/a/39151264
    void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input not valid");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

}