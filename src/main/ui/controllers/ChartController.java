package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Categories;
import model.Category;
import model.SpendingList;
import ui.controllers.enums.SceneEnum;
import ui.controllers.holders.SceneHolder;
import ui.controllers.holders.SpendingListHolder;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// Represents a controller for Chart scene
// INVARIANT: ui.controllers.Controller is loaded before this
public class ChartController implements Initializable {

    private final String emptyDatesError = "Dates cannot be blank";

    @FXML TableView<Category> categoriesTable;
    @FXML TableColumn<Category, String> categoriesColumn;
    @FXML TableColumn<Category, Boolean> isShownColumn;

    @FXML Tab monthlyTab;
    @FXML Tab customPeriodTab;

    @FXML ComboBox<LocalDate> dateComboBox;

    @FXML DatePicker fromDatePicker;
    @FXML DatePicker toDatePicker;

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
        spendingList = spendingListHolder.getSpendingList();
        categories = spendingList.getCategories();

        chartSetUpHelper = new ChartSetUpHelper(this);
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
        isStacked.setDisable(true);
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: if isStacked selected, makes stacked chart visible and bar chart hidden
    //          otherwise, makes bar chart visible and stacked chart hidden,
    //          enable isStaked option,
    //          and clicks plotChart if isReadyToPlot
    @FXML
    public void chartDateTypeSelected() {
        if (isStacked.isSelected()) {
            barChart.setVisible(false);
            stackedBarChart.setVisible(true);
        } else {
            barChart.setVisible(true);
            stackedBarChart.setVisible(false);
        }
        isStacked.setDisable(false);
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: makes bar chart hidden and stacked chart visible,
    //          and clicks plotChart if isReadyToPlot
    @FXML
    public void chartStackedSelected() {
        if (isStacked.isSelected()) {
            barChart.setVisible(false);
            stackedBarChart.setVisible(true);
        } else {
            barChart.setVisible(true);
            stackedBarChart.setVisible(false);
            chartDateTypeSelected();
        }
        if (isReadyToPlot()) {
            plotChart();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes view to main scene (one with records table),
    //          and saves this scene to sceneHolder
    @FXML
    public void backToSpendingList() {
        sceneHolder.getSceneMap().put(SceneEnum.CHART, categoriesTable.getScene());
        Stage window = (Stage) barChart.getScene().getWindow();
        window.setScene(sceneHolder.getSceneMap().get(SceneEnum.MAIN));
    }

    // MODIFIES: this
    // EFFECTS: repopulates dates combobox if spending list contains
    //          a record with month that isn't available in the combobox
    @FXML
    public void monthComboboxEntered() {
        LocalDate latestRecord = spendingList.getRecords().get(0).getTimeAdded().toLocalDate().withDayOfMonth(1);
        LocalDate latestComboBoxMonth = dateComboBox.getItems().get(1);
        if (!latestRecord.equals(latestComboBoxMonth)) {
            chartSetUpHelper.repopulateDateComboBox();
        }
    }

    // MODIFIES: this
    // EFFECTS: plots a new chart as soon as a month from combobox is selected
    @FXML
    public void monthSelected() {
        plotChart();
    }

    // MODIFIES: this
    // EFFECTS: plots barchart based on following parameters:
    //          #1: (1) categoryToggle, (2) monthlyTab
    //          #2: (1) categoryToggle, (2) dataTab
    //          #3: (1) dataToggle, (2) isStacked, (3) monthlyTab
    //          #4: (1) dataToggle, (2) isStacked, (3) dataTab
    //          #5: (1) dataToggle, (2) not Stacked, (3) monthlyTab
    //          #6: (1) dataToggle, (2) not Stacked, (3) dataTab
    @FXML
    public void plotChart() {
        if (categoryToggle.isSelected()) {
            if (monthlyTab.isSelected()) {
                plotCategoryMonthly();
            } else {
                plotCategoryCustom();
            }
        } else {
            if (isStacked.isSelected()) {
                if (monthlyTab.isSelected()) {
                    plotDateMonthly(stackedBarChart, stackedCategoryAxis);
                } else {
                    plotDateCustom(stackedBarChart, stackedCategoryAxis);
                }
            } else {
                if (monthlyTab.isSelected()) {
                    plotDateMonthly(barChart, barCategoryAxis);
                } else {
                    plotDateCustom(barChart, barCategoryAxis);
                }
            }
        }
    }

    // EFFECTS: plots barchart by category in selected month,
    //          or uses all records if selected month is LocalDate.MIN
    private void plotCategoryMonthly() {
        setUpCategoryChart("");
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        LocalDate selectedDate = dateComboBox.getSelectionModel().getSelectedItem();
        Map<String, Double> map = spendingList.groupByCategory(selectedDate);

        setUpCategoryChartXAxis(map);

        map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
        barChart.getData().add(series);
    }

    // EFFECTS: plot barchart by category in selected time period,
    //          shows error if datePicker is/are empty
    private void plotCategoryCustom() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        if (Objects.isNull(from) || Objects.isNull(to)) {
            chartSetUpHelper.showErrorMessage(emptyDatesError);
        } else {
            setUpCategoryChart("Custom Period");
            Map<String, Double> map = spendingList.groupByCategory(from, to);
            setUpCategoryChartXAxis(map);
            map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));
            barChart.getData().add(series);
        }
    }

    // EFFECTS: plots by date in selected time period
    private void plotDateCustom(XYChart<String, Number> chart, CategoryAxis categoryAxis) {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        if (Objects.isNull(from) || Objects.isNull(to)) {
            chartSetUpHelper.showErrorMessage(emptyDatesError);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(chartSetUpHelper.monthFormat);
            setUpDataChart(chart, categoryAxis);

            Map<String, Map<LocalDate, Double>> map = spendingList.groupByCategoryAndDate(from, to);
            List<LocalDate> allData = getDatesFromMap(map);
            setUpDateChartXAxis(categoryAxis, allData, formatter);

            List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
            parseMapForDateChart(formatter, map, seriesList);

            chart.getData().addAll(seriesList);
            setUpDateChartXAxis(categoryAxis, allData, formatter);
        }
    }

    // MODIFIES: this
    // EFFECTS: plots chart by date in selected month,
    //          or uses all records if selected month is LocalDate.MIN
    private void plotDateMonthly(XYChart<String, Number> chart, CategoryAxis categoryAxis) {
        setUpDataChart(chart, categoryAxis);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(chartSetUpHelper.monthFormat);

        LocalDate selectedDate = dateComboBox.getSelectionModel().getSelectedItem();

        List<LocalDate> allDates;
        if (selectedDate.equals(LocalDate.MIN)) {
            allDates = spendingList.getDates();
            Collections.sort(allDates);
        } else {
            allDates = Collections.singletonList(selectedDate);
        }

        setUpDateChartXAxis(categoryAxis, allDates, formatter);

        Map<String, Map<LocalDate, Double>> map = spendingList.groupByCategoryAndDate(selectedDate);
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        parseMapForDateChart(formatter, map, seriesList);

        chart.getData().addAll(seriesList);
        setUpDateChartXAxis(categoryAxis, allDates, formatter);
    }

    // MODIFIES: seriesList
    // EFFECTS: parses groupByCategoryAndDate map and populates a list of series from it
    private void parseMapForDateChart(DateTimeFormatter formatter, Map<String, Map<LocalDate, Double>> map,
                                      List<XYChart.Series<String, Number>> seriesList) {
        for (Map.Entry<String, Map<LocalDate, Double>> entry : map.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            entry.getValue().forEach((key, value) -> series.getData()
                    .add(new XYChart.Data<>(formatter.format(key), value)));
            seriesList.add(series);
        }
    }

    // EFFECTS: returns a list of distinct LocalDate extracted from map
    private List<LocalDate> getDatesFromMap(Map<String, Map<LocalDate, Double>> map) {
        List<LocalDate> list = new ArrayList<>();
        map.values().forEach(v -> list.addAll(v.keySet()));
        return list.stream().distinct().collect(Collectors.toList());
    }

    // MODIFIES: this
    // EFFECTS: sets up categories chart
    private void setUpCategoryChart(String titleAddition) {
        setUpBarChart(barChart);
        barCategoryAxis.setLabel("Categories");
        barChart.setTitle("Total Amount Spent by Category" + (titleAddition.isEmpty() ? ""
                : " (" + titleAddition + ")"));
    }

    // MODIFIES: this
    // EFFECTS: sets up categories of a data chart
    private void setUpDataChart(XYChart<String, Number> chart, CategoryAxis categoryAxis) {
        setUpBarChart(chart);
        chart.setTitle("Total Amount Spent by Date");
        categoryAxis.setLabel("Month");
    }

    // MODIFIES: this
    // EFFECTS: generally sets up any bar chart
    private void setUpBarChart(XYChart<String, Number> chart) {
        stackedNumberAxis.setLabel("Amount Spent");
        barNumberAxis.setLabel("Amount Spent");
        chart.getData().clear();
        chart.setAnimated(false);
    }

    // MODIFIES: this
    // EFFECTS: updates categories axis of chart to be strings
    private void setUpCategoryChartXAxis(Map<String, Double> map) {
        ObservableList<String> categories = FXCollections.observableArrayList(map.keySet());
        barCategoryAxis.setCategories(categories);
        barCategoryAxis.setAutoRanging(true);
    }

    // MODIFIES: this
    // EFFECTS: updates categories axis of chart to be dates
    private void setUpDateChartXAxis(CategoryAxis categoryAxis, List<LocalDate> allDates, DateTimeFormatter formatter) {
        List<String> formattedDates = allDates.stream().map(d -> d.format(formatter)).collect(Collectors.toList());
        ObservableList<String> c = FXCollections.observableArrayList(formattedDates);
        categoryAxis.setAutoRanging(true);
        categoryAxis.setCategories(c);
    }

    // EFFECTS: returns true is use is in the Monthly menu,
    //          or if they are in custom period menu and date fields values there are not empty
    private boolean isReadyToPlot() {
        return monthlyTab.isSelected()
                || (customPeriodTab.isSelected() && Objects.nonNull(fromDatePicker.getValue())
                && Objects.nonNull(toDatePicker.getValue()));
    }
}