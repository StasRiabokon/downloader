import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class GUI extends Application {


    private static File selectedDir;
    private static File selectedFile;
    private static TextField urlTextField;
    private static TextField nameTextField;
    private static Label currentDirToSave;
    private static TextField countThreadsTextField;


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Downloader");
        VBox vBox = new VBox();

        GridPane gridPane = new GridPane();
        vBox.getChildren().addAll(gridPane);

        Label urlLabel = new Label("URL:");
        Label nameLabel = new Label("Name:");
        Label curDirLabel = new Label("Dir to save:");
        Label curFile = new Label("File with links:");
        Label curFileWithLinksLabel = new Label("Not selected");
        currentDirToSave = new Label(System.getProperty("user.dir"));
        Label countThreadsLabel = new Label("Count of threads:");


        countThreadsTextField = new TextField();
        countThreadsTextField.setPromptText("1, 2, 3...");
        urlTextField = new TextField();
        urlTextField.setPromptText("http://www.example.com/file.txt");
        nameTextField = new TextField();
        nameTextField.setPromptText("Name for saving");

        Button downloadButton = new Button("Download");
        downloadButton.setPrefWidth(100);
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (urlTextField.getText().isEmpty()) {
                    errURL();
                    return;
                }
                if (nameTextField.getText().isEmpty()) {
                    errName();
                    return;
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            download();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        Button openDirChooser = new Button("Change Dir");
        openDirChooser.setPrefWidth(100);
        openDirChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                selectedDir = directoryChooser.showDialog(primaryStage);
                if (selectedDir != null) {
                    currentDirToSave.setText(selectedDir.getAbsolutePath());
                }
            }
        });
        Button chooseFileWithLinks = new Button("Choose file");
        chooseFileWithLinks.setPrefWidth(100);
        chooseFileWithLinks.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    curFileWithLinksLabel.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        Button downloadFromFileButton = new Button("Download");
        downloadFromFileButton.setPrefWidth(100);
        downloadFromFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (countThreadsTextField.getText().isEmpty()) {
                    errThreadCount();
                    return;
                }
                if (!isDigitMoreZero()) {
                    return;
                }
                if (curFileWithLinksLabel.getText().equals("Not selected")) {
                    return;
                }
                try {
                    Downloader.map = Downloader.readLinksFromFile(selectedFile + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int n = Integer.parseInt(countThreadsTextField.getText());
                for (int i = 0; i < n; i++) {
                    Multidownloading multidownloading = new Multidownloading();
                    Thread thread = new Thread(multidownloading);
                    thread.start();
                }
            }
        });

        gridPane.setPadding(new Insets(15));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(curDirLabel, 0, 0);
        gridPane.add(currentDirToSave, 1, 0);
        gridPane.add(openDirChooser, 2, 0);

        gridPane.add(new Label(""), 0, 1);

        gridPane.add(curFile, 0, 2);
        gridPane.add(curFileWithLinksLabel, 1, 2);
        gridPane.add(chooseFileWithLinks, 2, 2);
        gridPane.add(countThreadsLabel, 0, 3);
        gridPane.add(countThreadsTextField, 1, 3);
        gridPane.add(downloadFromFileButton, 2, 3);


        gridPane.add(new Label(""), 0, 4);

        gridPane.add(urlLabel, 0, 5);
        gridPane.add(urlTextField, 1, 5);
        gridPane.add(nameLabel, 0, 6);
        gridPane.add(nameTextField, 1, 6);
        gridPane.add(downloadButton, 2, 6);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(110);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(300);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(100);
        gridPane.getColumnConstraints().addAll(col1, col2, col3);

        Scene scene = new Scene(vBox, 510, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Alert errURL() {
        Alert erroralert1 = new Alert(Alert.AlertType.ERROR);
        erroralert1.setTitle("Empty field");
        erroralert1.setHeaderText(null);
        erroralert1.setContentText("URL field is empty");
        erroralert1.initStyle(StageStyle.UTILITY);
        erroralert1.showAndWait();
        return erroralert1;
    }

    public static Alert errBadURL() {
        Alert erroralert1 = new Alert(Alert.AlertType.ERROR);
        erroralert1.setTitle("Bad URL");
        erroralert1.setHeaderText(null);
        erroralert1.setContentText("URL is not correct");
        erroralert1.initStyle(StageStyle.UTILITY);
        erroralert1.showAndWait();
        return erroralert1;
    }

    private Alert errName() {
        Alert erroralert1 = new Alert(Alert.AlertType.ERROR);
        erroralert1.setTitle("Empty field");
        erroralert1.setHeaderText(null);
        erroralert1.setContentText("Name field is empty");
        erroralert1.initStyle(StageStyle.UTILITY);
        erroralert1.showAndWait();
        return erroralert1;
    }

    private Alert errThreadCount() {
        Alert erroralert1 = new Alert(Alert.AlertType.ERROR);
        erroralert1.setTitle("Empty field");
        erroralert1.setHeaderText(null);
        erroralert1.setContentText("Thread count field is empty");
        erroralert1.initStyle(StageStyle.UTILITY);
        erroralert1.showAndWait();
        return erroralert1;
    }

    private static boolean isDigitMoreZero() {
        try {
            if (Integer.parseInt(countThreadsTextField.getText()) > 0) {
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static Label getCurrentDirToSave() {
        return currentDirToSave;
    }

    private static void download() throws IOException {


        String stringUrl = urlTextField.getText();
        String filename = nameTextField.getText();
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (Exception e) {
            errBadURL();
            return;
        }
        String path = GUI.getCurrentDirToSave().getText();
        try (ReadableByteChannel byteChannel = Channels.newChannel(url.openStream())) {
            try (FileOutputStream outputStream = new FileOutputStream(new File(path + File.separator + filename))) {
                outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);

            }
        }
    }
}
