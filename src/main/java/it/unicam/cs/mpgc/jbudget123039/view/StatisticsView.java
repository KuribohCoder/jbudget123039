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

public class StatisticsView {

    @FXML private DatePicker startDate1, endDate1, startDate2, endDate2;
    @FXML private TableView<StatEntry> tableComparison;
    @FXML private TableColumn<StatEntry, String> colCategory;
    @FXML private TableColumn<StatEntry, BigDecimal> colPeriod1, colPeriod2;
    @FXML private BarChart<String, Number> barChart;
    private final StatisticsController controller = new StatisticsController();

    @FXML
    public void initialize() {
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().category()));
        colPeriod1.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().amount1()));
        colPeriod2.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().amount2()));
    }

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

    @FXML
    private void handleBack() {
        Stage stage = (Stage) startDate1.getScene().getWindow();
        SceneSwitcherUtils.switchScene(stage, "/ui/main.fxml");
    }

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

    public record StatEntry(String category, BigDecimal amount1, BigDecimal amount2) {}
}