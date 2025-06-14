package it.unicam.cs.mpgc.jbudget123039.view;

import it.unicam.cs.mpgc.jbudget123039.controller.StatisticsController;
import it.unicam.cs.mpgc.jbudget123039.util.SceneSwitcherUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static it.unicam.cs.mpgc.jbudget123039.util.DialogUtils.showError;

/**
 * Controller for the statistics comparison view.
 * <p>
 * Allows users to select two date ranges and compare financial statistics by category
 * between these two periods.
 * Displays the comparison results in a table and a bar chart.
 * Handles asynchronous data retrieval and updates the UI accordingly.
 * </p>
 */
public class StatisticsView {

    @FXML private DatePicker startDate1, endDate1, startDate2, endDate2;
    @FXML private TableView<StatEntry> tableComparison;
    @FXML private TableColumn<StatEntry, String> colCategory;
    @FXML private TableColumn<StatEntry, BigDecimal> colPeriod1, colPeriod2;
    @FXML private BarChart<String, Number> barChart;
    private final StatisticsController controller = new StatisticsController();

    /**
     * Initializes the controller by setting up the table columns
     * to show category and amounts for the two periods.
     */
    @FXML
    public void initialize() {
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().category()));
        colPeriod1.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().amount1()));
        colPeriod2.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().amount2()));
    }

    /**
     * Handles the action of comparing two selected periods.
     * Validates that all date fields are filled.
     * Calls the controller to perform the comparison asynchronously,
     * then updates the table and bar chart with the results.
     * Shows an error dialog if validation or comparison fails.
     */
    @FXML
    private void handleCompare() {
        LocalDate from1 = startDate1.getValue();
        LocalDate to1 = endDate1.getValue();
        LocalDate from2 = startDate2.getValue();
        LocalDate to2 = endDate2.getValue();

        if (from1 == null || to1 == null || from2 == null || to2 == null) {
            showError("Inserisci tutte le date.");
            return;
        }

        controller.comparePeriods(from1, to1, from2, to2)
                .thenAccept(result -> Platform.runLater(() -> {
                    tableComparison.setItems(FXCollections.observableArrayList(result.entrySet().stream()
                            .map(entry -> new StatEntry(
                                    entry.getKey(),
                                    entry.getValue().amount1(),
                                    entry.getValue().amount2()))
                            .toList()));
                    updateBarChart(result);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showError("Errore durante il confronto."));
                    return null;
                });
    }

    /**
     * Navigates back to the main view.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) startDate1.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }

    /**
     * Updates the bar chart with the comparison data.
     * Each category is shown with amounts from the two periods side by side.
     * Categories longer than 12 characters are truncated with an ellipsis.
     *
     * @param data a map of category names to their comparison data
     */
    private void updateBarChart(Map<String, StatisticsController.StatComparison> data) {
        barChart.getData().clear();

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Periodo 1");
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Periodo 2");

        data.forEach((category, comparison) -> {
            String shortCategory = category.length() > 12 ? category.substring(0, 12) + "…" : category;
            series1.getData().add(new XYChart.Data<>(shortCategory, comparison.amount1()));
            series2.getData().add(new XYChart.Data<>(shortCategory, comparison.amount2()));
        });

        barChart.getData().addAll(series1, series2);

        if (barChart.getXAxis() instanceof javafx.scene.chart.CategoryAxis xAxis) {
            xAxis.setTickLabelRotation(-45);
            xAxis.setTickLabelGap(10);
            xAxis.setCategories(FXCollections.observableArrayList(
                    data.keySet().stream()
                            .map(cat -> cat.length() > 12 ? cat.substring(0, 12) + "…" : cat)
                            .toList()
            ));
        }
    }

    /**
     * Immutable record representing a row in the comparison table,
     * containing the category name and amounts for the two periods.
     *
     * @param category the category name
     * @param amount1 the amount for period 1
     * @param amount2 the amount for period 2
     */
    public record StatEntry(String category, BigDecimal amount1, BigDecimal amount2) {}
}