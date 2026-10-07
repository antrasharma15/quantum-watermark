package quantum;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * JavaFX user interface for the quantum watermarking system (images + video).
 * The UI only handles buttons, previews and progress. All watermarking logic is in
 * ImageWatermarker / VideoWatermarker / QuantumWatermarker (separation of concerns).
 * Long video jobs run on a background thread (javafx.concurrent.Task) so the window never freezes.
 */
public class Watermarkapp extends Application {

    private BufferedImage original;      // image chosen by the user
    private BufferedImage watermarked;   // result after embedding
    private File videoFile;              // video chosen by the user

    private final ImageView originalView = new ImageView();
    private final ImageView markedView   = new ImageView();
    private final TextField messageField = new TextField();
    private final Label infoLabel   = new Label("Capacity: -");
    private final Label statusLabel = new Label("Open an image or a video to begin.");

    // image buttons
    private final Button openBtn    = new Button("Open Image");
    private final Button embedBtn   = new Button("Embed Watermark");
    private final Button saveBtn    = new Button("Save as PNG");
    private final Button extractBtn = new Button("Extract from Image...");

    // video buttons
    private final Button openVideoBtn    = new Button("Open Video");
    private final Button embedVideoBtn   = new Button("Embed in Video");
    private final Button extractVideoBtn = new Button("Extract from Video...");
    private final Spinner<Integer> intervalSpinner = new Spinner<>(1, 30, 5);

    private final ProgressBar progress = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);

    @Override
    public void start(Stage stage) {
        // ----- button rows -----
        HBox imageRow = new HBox(10, new Label("Image:"), openBtn, embedBtn, saveBtn, extractBtn);
        imageRow.setAlignment(Pos.CENTER_LEFT);
        intervalSpinner.setPrefWidth(75);
        HBox videoRow = new HBox(10, new Label("Video:"), openVideoBtn,
                new Label("Watermark every Nth frame:"), intervalSpinner, embedVideoBtn, extractVideoBtn);
        videoRow.setAlignment(Pos.CENTER_LEFT);

        // ----- message input -----
        messageField.setPromptText("Type the watermark text to hide");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        HBox messageRow = new HBox(10, new Label("Watermark text:"), messageField);
        messageRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(10, imageRow, videoRow, messageRow);
        top.setPadding(new Insets(12));

        // ----- centre: before / after previews -----
        HBox previews = new HBox(16, previewBox("Original", originalView),
                previewBox("Watermarked (quantum LSB)", markedView));
        previews.setPadding(new Insets(0, 12, 0, 12));
        previews.setAlignment(Pos.CENTER);

        // ----- bottom: info, progress, status -----
        progress.setVisible(false);
        progress.setPrefWidth(300);
        VBox bottom = new VBox(4, infoLabel, progress, statusLabel);
        bottom.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(previews);
        root.setBottom(bottom);

        openBtn.setOnAction(e -> openImage(stage));
        embedBtn.setOnAction(e -> embed());
        saveBtn.setOnAction(e -> savePng(stage));
        extractBtn.setOnAction(e -> extractFromFile(stage));
        openVideoBtn.setOnAction(e -> openVideo(stage));
        embedVideoBtn.setOnAction(e -> embedVideo(stage));
        extractVideoBtn.setOnAction(e -> extractVideo(stage));

        refreshButtons(false);

        stage.setTitle("Quantum Watermarking - Images and Video (simulated)");
        stage.setScene(new Scene(root, 1150, 680));
        stage.show();
    }

    private VBox previewBox(String title, ImageView view) {
        view.setPreserveRatio(true);
        view.setFitWidth(500);
        view.setFitHeight(400);
        StackPane frame = new StackPane(view);
        frame.setMinSize(500, 400);
        frame.setStyle("-fx-border-color: #999999; -fx-background-color: #f4f4f4;");
        VBox box = new VBox(6, new Label(title), frame);
        box.setAlignment(Pos.TOP_CENTER);
        return box;
    }

    // ------------------------------------------------------------ image actions

    private void openImage(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose an image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) { status("Could not read that file as an image."); return; }
            original = img;
            watermarked = null;
            showImage(originalView, original);
            markedView.setImage(null);
            infoLabel.setText("Image: " + img.getWidth() + " x " + img.getHeight()
                    + "   |   Capacity: " + ImageWatermarker.capacityBytes(img) + " characters");
            refreshButtons(false);
            status("Loaded " + file.getName() + ". Type a message and click Embed Watermark.");
        } catch (IOException ex) {
            status("Error reading image: " + ex.getMessage());
        }
    }

    private void embed() {
        String text = messageField.getText();
        if (original == null) { status("Open an image first."); return; }
        if (text == null || text.isEmpty()) { status("Type some watermark text first."); return; }
        try {
            watermarked = ImageWatermarker.embed(original, text);
            showImage(markedView, watermarked);
            double psnr = ImageWatermarker.psnr(original, watermarked);
            refreshButtons(false);
            status(String.format("Embedded %d characters. PSNR = %.2f dB (above 40 dB = invisible). "
                    + "Save as PNG to keep the watermark.", text.length(), psnr));
        } catch (IllegalArgumentException ex) {
            status("Cannot embed: " + ex.getMessage());
        }
    }

    private void savePng(Stage stage) {
        if (watermarked == null) { status("Embed a watermark first."); return; }
        FileChooser fc = new FileChooser();
        fc.setTitle("Save watermarked image (PNG)");
        fc.setInitialFileName("watermarked.png");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG image", "*.png"));
        File file = fc.showSaveDialog(stage);
        if (file == null) return;
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        try {
            ImageIO.write(watermarked, "png", file);
            status("Saved " + file.getAbsolutePath());
        } catch (IOException ex) {
            status("Error saving: " + ex.getMessage());
        }
    }

    private void extractFromFile(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a watermarked image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) { status("Could not read that file as an image."); return; }
            String message = ImageWatermarker.extract(img);
            showMessage(message);
            status("Extracted " + message.length() + " characters from " + file.getName());
        } catch (IOException ex) {
            status("Error reading image: " + ex.getMessage());
        } catch (RuntimeException ex) {
            status("No valid watermark found in this image (" + ex.getMessage() + ")");
        }
    }

    // ------------------------------------------------------------ video actions

    private void openVideo(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a video");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.mkv", "*.avi", "*.mov"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;
        videoFile = file;
        infoLabel.setText("Video: " + file.getName());
        refreshButtons(false);
        status("Loaded " + file.getName() + ". Type a message and click Embed in Video.");
    }

    private void embedVideo(Stage stage) {
        String text = messageField.getText();
        if (videoFile == null) { status("Open a video first."); return; }
        if (text == null || text.isEmpty()) { status("Type some watermark text first."); return; }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save watermarked video (lossless MKV)");
        fc.setInitialFileName("watermarked_video.mkv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Matroska video", "*.mkv"));
        File chosen = fc.showSaveDialog(stage);
        if (chosen == null) return;
        final File output = chosen.getName().toLowerCase().endsWith(".mkv")
                ? chosen : new File(chosen.getParentFile(), chosen.getName() + ".mkv");

        final File input = videoFile;
        final int interval = intervalSpinner.getValue();

        // Runs on a background thread so the window stays responsive.
        Task<Videowatermarker.Result> task = new Task<>() {
            @Override
            protected Videowatermarker.Result call() throws Exception {
                return Videowatermarker.embed(input, output, text, interval, this::updateMessage);
            }
        };
        task.setOnSucceeded(e -> {
            finishTask();
            Videowatermarker.Result r = task.getValue();
            status("Done. Watermarked " + r.markedFrames() + " of " + r.totalFrames()
                    + " frames. Saved " + output.getAbsolutePath());
        });
        task.setOnFailed(e -> {
            finishTask();
            status("Video embedding failed: " + task.getException().getMessage());
        });
        startTask(task);
    }

    private void extractVideo(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a watermarked video");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Videos", "*.mkv", "*.mp4", "*.avi", "*.mov"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return Videowatermarker.extract(file, this::updateMessage);
            }
        };
        task.setOnSucceeded(e -> {
            finishTask();
            showMessage(task.getValue());
            status("Extracted " + task.getValue().length() + " characters from " + file.getName());
        });
        task.setOnFailed(e -> {
            finishTask();
            status("Extraction failed: " + task.getException().getMessage());
        });
        startTask(task);
    }

    /** Show progress, lock the buttons, and run the task on a background thread. */
    private void startTask(Task<?> task) {
        refreshButtons(true);
        progress.setVisible(true);
        statusLabel.textProperty().bind(task.messageProperty());   // live messages from the task
        Thread worker = new Thread(task);
        worker.setDaemon(true);
        worker.start();
    }

    /** Called when a background task ends (success or failure). */
    private void finishTask() {
        statusLabel.textProperty().unbind();
        progress.setVisible(false);
        refreshButtons(false);
    }

    // ------------------------------------------------------------ helpers

    /** Enable or disable buttons depending on what is loaded and whether a task is running. */
    private void refreshButtons(boolean busy) {
        openBtn.setDisable(busy);
        embedBtn.setDisable(busy || original == null);
        saveBtn.setDisable(busy || watermarked == null);
        extractBtn.setDisable(busy);
        openVideoBtn.setDisable(busy);
        embedVideoBtn.setDisable(busy || videoFile == null);
        extractVideoBtn.setDisable(busy);
        intervalSpinner.setDisable(busy);
    }

    private void status(String text) {
        statusLabel.setText(text);
    }

    private void showMessage(String message) {
        TextArea area = new TextArea(message);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(420, 160);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Extracted watermark");
        alert.setHeaderText("Hidden message found:");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    /** Convert a BufferedImage to a JavaFX image and show it. */
    private void showImage(ImageView view, BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
        WritableImage fxImage = new WritableImage(w, h);
        fxImage.getPixelWriter().setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), pixels, 0, w);
        view.setImage(fxImage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}