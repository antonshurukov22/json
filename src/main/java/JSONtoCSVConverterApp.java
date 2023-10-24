import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.File;

public class JSONtoCSVConverterApp extends Application {
    private File jsonFile;
    private File csvFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Конвертер JSON в CSV");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        Button selectJSONButton = new Button("Выбрать JSON файл");
        Button convertButton = new Button("Конвертировать");
        ProgressBar progressBar = new ProgressBar(0);
        Label progressLabel = new Label("0%");

        selectJSONButton.setOnAction(e -> {
            // Открываем диалог выбора JSON файла
            fileChooser.setTitle("Выбрать JSON файл");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            jsonFile = fileChooser.showOpenDialog(primaryStage); // Используйте primaryStage здесь
        });

        convertButton.setOnAction(e -> {
            if (jsonFile != null) {
                // Откройте диалоговое окно для выбора пути сохранения CSV файла
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                fileChooser.setTitle("Сохранить CSV файл");
                csvFile = fileChooser.showSaveDialog(primaryStage);

                if (csvFile != null) {
                    // Теперь csvFile содержит выбранный путь для сохранения CSV файла
                    // Вызовите конвертацию JSON в CSV с учетом выбранного пути
                    JSONtoCSV.convert(jsonFile.getAbsolutePath(), csvFile.getAbsolutePath());
                    showInfoDialog("Успех", "Конвертация и сохранение завершены успешно.");
                } else {
                    showInfoDialog("Ошибка", "Выберите путь для сохранения CSV файла.");
                }
            } else {
                showInfoDialog("Ошибка", "Выберите JSON файл перед конвертацией.");
            }
        });


        root.getChildren().addAll(selectJSONButton, convertButton, progressBar, progressLabel);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
}
