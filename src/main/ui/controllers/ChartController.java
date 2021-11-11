package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Category;
import model.Record;
import model.SpendingList;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;

// Represents a controller for Chart scene
// INVARIANT: ui.controllers.Controller is loaded before this
public class ChartController implements Initializable {

    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;
    @FXML private DatePicker fromDateField;
    @FXML private DatePicker toDateField;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> categoriesColumn;
    @FXML private TableColumn<Category, Boolean> isShownColumn;

    @FXML private ToggleGroup chartGroup;
    @FXML private ToggleGroup periodGroup;

    private SetUpHelper setUpHelper = new SetUpHelper();
    private SpendingList spendingList;
    private final SceneHolder sceneHolder = SceneHolder.getInstance();
    private final SpendingListHolder spendingListHolder = SpendingListHolder.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spendingList = spendingListHolder.getSpendingList();
        setUpHelper.populateCategoriesTable(categoriesTable, categoriesColumn,
                isShownColumn, spendingList.getCategories());
        sceneHolder.getSceneMap().put(SceneEnum.CHART, barChart.getScene());

        fromDateField.setValue(spendingList.getRecords().get(0).getTimeAdded().toLocalDate());
        fromDateField.setValue(spendingList.getRecords().get(spendingList.getRecords().size() - 1)
                .getTimeAdded().toLocalDate());
    }

    // MODIFIES: this
    // EFFECTS: changes scene view to main scene (one with records table)
    @FXML
    public void changeSceneToSpendingList() {
        Stage window = (Stage) barChart.getScene().getWindow();
        window.setScene(sceneHolder.getSceneMap().get(SceneEnum.MAIN));
    }

    @FXML
    public void plotChart() {
        barChart.setTitle("Records by Categories");
        categoryAxis = new CategoryAxis();
        numberAxis = new NumberAxis();
        categoryAxis.setLabel("Categories");
        numberAxis.setLabel("Sum of amount spent");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        try {
            Map<String, Double> map = spendingList
                    .groupByCategoryInSelectedDates(fromDateField.getValue(), toDateField.getValue());

            map.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));

            barChart.getData().add(series);
        } catch (NullPointerException e) {
            setUpHelper.showErrorMessage(e.getMessage());
        }
    }

}

