import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class P2PFileSharingApp extends Application {
    private TextField sharedFolderPathField;
    private TextField secretKeyField;
    private ListView<String> filesListView;
    private ListView<String> connectedNodesListView;
    private ListView<Download> transfersListView;
    private ObservableList<Download> activeDownloads;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("P2P File Sharing App");

        // Setup Screen
        VBox setupLayout = new VBox(10);
        Label sharedFolderLabel = new Label("Shared Folder Path:");
        sharedFolderPathField = new TextField();
        Label secretKeyLabel = new Label("Secret Key:");
        secretKeyField = new TextField();
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> browseSharedFolder());
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> startP2P());
        setupLayout.getChildren().addAll(sharedFolderLabel, sharedFolderPathField, secretKeyLabel, secretKeyField, browseButton, startButton);

        // Main Screen
        HBox mainLayout = new HBox(10);
        filesListView = new ListView<>();
        connectedNodesListView = new ListView<>();
        transfersListView = new ListView<>();
        activeDownloads = FXCollections.observableArrayList();
        transfersListView.setItems(activeDownloads);
        transfersListView.setCellFactory(param -> new ListCell<Download>() {
            @Override
            protected void updateItem(Download item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        mainLayout.getChildren().addAll(filesListView, connectedNodesListView, transfersListView);

        // Set up the main scene
        Scene scene = new Scene(setupLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseSharedFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Shared Folder");
        File selectedFolder = directoryChooser.showDialog(new Stage());

        if (selectedFolder != null) {
            sharedFolderPathField.setText(selectedFolder.getAbsolutePath());
            updateFilesListView(selectedFolder);
        }
    }

    private void updateFilesListView(File folder) {
        filesListView.getItems().clear();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                filesListView.getItems().add(file.getName());
            }
        }
    }

    private void startP2P() {
        String sharedFolderPath = sharedFolderPathField.getText();
        String secretKey = secretKeyField.getText();
        discoverNodes(secretKey);
        shareFiles(sharedFolderPath);
        filesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            initiateDownload();
        });
    }

    private void discoverNodes(String secretKey) {
        connectedNodesListView.getItems().addAll("Node1", "Node2", "Node3");
    }

    private void shareFiles(String sharedFolderPath) {
        File sharedFolder = new File(sharedFolderPath);
        File[] files = sharedFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                sharedFiles.add(file.getName());
            }
        }
        updateFilesListView(sharedFolder);
    }

    private void initiateDownload() {
        String selectedFile = filesListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            startDownload(selectedFile);
        }
    }

    private void startDownload(String fileName) {
        Download download = new Download(fileName, 0, 1000); // Replace 1000 with the actual file size
        activeDownloads.add(download);

        Task<Void> downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (long i = 0; i <= download.getTotalBytes(); i += 100) {
                    Thread.sleep(50);
                    download.setBytesTransferred(i);
                    updateProgress(i, download.getTotalBytes());
                }
                return null;
            }
        };

        download.progressProperty().bind(downloadTask.progressProperty());
        new Thread(downloadTask).start();
    }

    public static class Download {
        private String fileName;
        private long bytesTransferred;
        private long totalBytes;

        public Download(String fileName, long bytesTransferred, long totalBytes) {
            this.fileName = fileName;
            this.bytesTransferred = bytesTransferred;
            this.totalBytes = totalBytes;
        }

        public String getFileName() {
            return fileName;
        }

        public long getBytesTransferred() {
            return bytesTransferred;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public double getPercentage() {
            return (double) bytesTransferred / totalBytes * 100;
        }

        @Override
        public String toString() {
            return String.format("%s - %.2f%%", fileName, getPercentage());
        }
    }
}
