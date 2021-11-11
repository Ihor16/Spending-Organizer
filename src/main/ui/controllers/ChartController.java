package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Category;
import model.SpendingList;

import java.net.URL;
import java.util.ResourceBundle;

// Represents a controller for Chart scene
// INVARIANT: ui.controllers.Controller is loaded before this
public class ChartController implements Initializable {

    @FXML private StackedBarChart<?, ?> barChart;
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
    }

    // MODIFIES: this
    // EFFECTS: changes scene view to main scene (one with records table)
    @FXML
    public void changeSceneToSpendingList() {
        Stage window = (Stage) barChart.getScene().getWindow();
        window.setScene(sceneHolder.getSceneMap().get(SceneEnum.MAIN));
    }
}

