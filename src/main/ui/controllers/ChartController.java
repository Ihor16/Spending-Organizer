package ui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Categories;
import model.Category;
import model.SpendingList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Represents a controller for Chart scene
// INVARIANT: ui.controllers.Controller is loaded before this
public class ChartController implements Initializable {

    private final String chartDrawingError = "Drawing error";
    private final String emptyDatesError = "Dates cannot be blank";

    @FXML TableView<Category> categoriesTable;
    @FXML TableColumn<Category, String> categoriesColumn;
    @FXML TableColumn<Category, Boolean> isShownColumn;

    @FXML Tab monthlyTab;
    @FXML Tab customPeriodTab;

    @FXML ComboBox<LocalDate> dateComboBox;

    @FXML DatePicker fromDateField;
    @FXML DatePicker toDateField;

    @FXML BarChart<String, Number> barChart;
    @FXML CategoryAxis barCategoryAxis;
    @FXML NumberAxis barNumberAxis;

    @FXML StackedBarChart<String, Number> stackedBarChart;
    @FXML CategoryAxis stackedCategoryAxis;
    @FXML NumberAxis stackedNumberAxis;

    @FXML RadioButton categoryToggle;
    @FXML RadioButton dateToggle;
    @FXML ToggleGroup chartGroup;
    @FXML CheckBox isStacked;
    @FXML ToggleGroup periodGroup;

    ChartSetUpHelper chartSetUpHelper;
    Categories categories;
    SpendingList spendingList;
    final SceneHolder sceneHolder = SceneHolder.getInstance();
    final SpendingListHolder spendingListHolder = SpendingListHolder.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartSetUpHelper = new ChartSetUpHelper(this);
        spendingList = spendingListHolder.getSpendingList();
        categories = spendingList.getCategories();

        chartSetUpHelper.setUpChartController();

        if (isReadyToPlot()) {
            plotChart();
        }

    }

    // MODIFIES: this
    // EFFECTS: makes bar chart visible and stacked chart hidden,
    //          disables isStaked option,
    //          and clicks plotChart if isReadyToPlot
    @FXML
    public void chartCategoryTypeSelected() {
        barChart.setVisible(true);
        stackedBarChart.setVisible(false);
        isStacked.setSelected(false);
        isStacked.setDisable(true);
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: makes bar chart visible and stacked chart hidden,
    //          enable isStaked option,
    //          and clicks plotChart if isReadyToPlot
    @FXML
    public void chartDateTypeSelected() {
        barChart.setVisible(true);
        stackedBarChart.setVisible(false);
        isStacked.setDisable(false);
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: makes bar chart hidden and stacked chart visible
    //          and clicks plotChart if isReadyToPlot
    @FXML
    public void chartStackedSelected() {
        barChart.setVisible(false);
        stackedBarChart.setVisible(true);
        if (!isStacked.isSelected()) {
            chartDateTypeSelected();
        }
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes scene view to main scene (one with records table)
    //          and clicks plot chart button
    @FXML
    public void backToSpendingList() {
        sceneHolder.getSceneMap().put(SceneEnum.CHART, barChart.getScene());
        Stage window = (Stage) barChart.getScene().getWindow();
        window.setScene(sceneHolder.getSceneMap().get(SceneEnum.MAIN));
    }

    @FXML
    public void monthSelected() {
        plotChart();
    }

    @FXML
    public void plotChart() {
        barChart.getData().clear();
        barChart.setAnimated(true);

        if (categoryToggle.isSelected()) {
            if (monthlyTab.isSelected()) {
                plotByCategoryMonthly();
            } else {
                plotByCategoryCustomPeriod();
            }
        } else {
            if (isStacked.isSelected()) {
                if (monthlyTab.isSelected()) {
                    plotByDateMonthlyStacked();
                } else {
//                    plotByDateCustomPeriodStacked();
                }
            } else {
                if (monthlyTab.isSelected()) {
                    plotByDateMonthly();
                } else {
//                    plotByDateCustomPeriod();
                }
            }
        }

    }

    // EFFECTS:
    private void plotByCategoryCustomPeriod() {
        barCategoryAxis.setLabel("Categories");
        barNumberAxis.setLabel("Amount Spent");
        barChart.getData().clear();
        barChart.setAnimated(false);
        LocalDate from = fromDateField.getValue();
        LocalDate to = toDateField.getValue();

        if (Objects.isNull(from) || Objects.isNull(to)) {
            chartSetUpHelper.showErrorMessage(emptyDatesError);
        } else {
            barChart.setAnimated(false);
            barChart.setTitle("Total Amount Spent by Category");
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            Map<String, Double> map = spendingList.groupByCategory(from, to);
            try {
                map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
                barChart.getData().add(series);
            } catch (NullPointerException e) {
                chartSetUpHelper.showErrorMessage(chartDrawingError);
            }
        }
    }

    // EFFECTS: creates a bar chart by category in a one-month period
    private void plotByCategoryMonthly() {
        barCategoryAxis.setLabel("Categories");
        barNumberAxis.setLabel("Amount Spent");
        barChart.getData().clear();
        barChart.setAnimated(false);
        barChart.setTitle("Total Amount Spent by Category");
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        LocalDate selectedDate = dateComboBox.getSelectionModel().getSelectedItem();
        Map<String, Double> map = spendingList.groupByCategory(selectedDate);

        map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
        barChart.getData().add(series);
    }

    // EFFECTS:
    private void plotByDateMonthly() {
        barCategoryAxis.setLabel("Month");
        barNumberAxis.setLabel("Amount Spent");
        barChart.getData().clear();
        barChart.setAnimated(false);
        barChart.setTitle("Total Amount by Month");
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        LocalDate selectedDate = dateComboBox.getSelectionModel().getSelectedItem();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(chartSetUpHelper.prettyDateFormat);
        Map<String, Map<LocalDate, Double>> map = spendingList.groupByCategoryAndDate(selectedDate);

        for (Map.Entry<String, Map<LocalDate, Double>> entry : map.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            entry.getValue().forEach((key, value) -> series.getData()
                    .add(new XYChart.Data<>(formatter.format(key), value)));
            seriesList.add(series);
        }
        barChart.getData().addAll(seriesList);
    }

    private void plotByDateMonthlyStacked() {
        stackedCategoryAxis.setLabel("Month");
        stackedNumberAxis.setLabel("Amount Spent");
        stackedBarChart.getData().clear();
        stackedBarChart.setAnimated(false);
        stackedBarChart.setTitle("Total Amount by Month");
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        LocalDate selectedDate = dateComboBox.getSelectionModel().getSelectedItem();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(chartSetUpHelper.prettyDateFormat);
        Map<String, Map<LocalDate, Double>> map = spendingList.groupByCategoryAndDate(selectedDate);

        for (Map.Entry<String, Map<LocalDate, Double>> entry : map.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            entry.getValue().forEach((key, value) -> series.getData()
                    .add(new XYChart.Data<>(formatter.format(key), value)));
            seriesList.add(series);
        }

        stackedBarChart.getData().addAll(seriesList);
    }

    // EFFECTS: returns true is use is in the Monthly menu,
    //          or if they are in custom period menu and date fields values there are not empty
    private boolean isReadyToPlot() {
        return monthlyTab.isSelected()
                || (customPeriodTab.isSelected() && Objects.nonNull(fromDateField.getValue())
                && Objects.nonNull(toDateField.getValue()));
    }

}

