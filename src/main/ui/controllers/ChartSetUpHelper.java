package ui.controllers;


import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ChartSetUpHelper extends SetUpHelper {

    private final ChartController cl;

    public ChartSetUpHelper(ChartController cl) {
        this.cl = cl;
    }

    void setUpChartController() {
        populateCategoriesTable();
        populateDateComboBox();
        formatUI();
    }

    private void formatUI() {
        formatDateComboBox();
        formatDatePickerText(cl.fromDateField);
        formatDatePickerText(cl.toDateField);
        cl.barCategoryAxis = new CategoryAxis();
        cl.barNumberAxis = new NumberAxis();
        cl.stackedCategoryAxis = new CategoryAxis();
        cl.stackedNumberAxis = new NumberAxis();
    }

    // MODIFIES: datePicker
    // EFFECTS: formats text in datePickers
    // Regex implementation is based on: https://stackoverflow.com/a/2876934
    private void formatDatePickerText(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(prettyDateFormat);
            @Override
            public String toString(LocalDate date) {
                return Objects.isNull(date) ? ("") : (formatter.format(date));
            }

            @Override
            public LocalDate fromString(String string) {
                if (Objects.isNull(string) || string.isEmpty()) {
                    return null;
                } else if (string.matches(".*[a-zA-Z]+.*")) {
                    return LocalDate.parse(string, DateTimeFormatter.ofPattern(prettyDateFormat));
                } else {
                    return LocalDate.parse(string, DateTimeFormatter.ofPattern(standardDateFormat));
                }
            }
        });
    }


    // MODIFIES: this
    // EFFECTS: formats dates in dateComboBox
    private void formatDateComboBox() {
        cl.dateComboBox.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(monthFormat);
            @Override
            public String toString(LocalDate date) {
                if (Objects.isNull(date)) {
                    return "";
                } else if (date.equals(LocalDate.MIN)) {
                    return "All";
                } else {
                    return formatter.format(date);
                }
            }

            @Override
            public LocalDate fromString(String string) {
                return (Objects.isNull(string) || string.isEmpty()) ? null : LocalDate.parse(string);
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: populates categories table and colors default category in categoriesTable
    private void populateCategoriesTable() {
        cl.categoriesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cl.isShownColumn.setCellValueFactory(new PropertyValueFactory<>("isShown"));
        super.formatIsShownColumn(cl.isShownColumn);
        cl.categoriesTable.setItems(cl.categories.getCategories());
        super.colorDefaultCategoryInCategoriesTable(cl.categoriesTable, cl.categories);
    }

    // MODIFIES: this
    // EFFECTS: populates dateComboBox and adds a LocalDate.MIN (used to display all months)
    private void populateDateComboBox() {
        cl.dateComboBox.getItems().add(LocalDate.MIN);
        cl.dateComboBox.getItems().addAll(FXCollections.observableArrayList(cl.spendingList.getDates()));
        cl.dateComboBox.getSelectionModel().selectFirst();
    }
}
