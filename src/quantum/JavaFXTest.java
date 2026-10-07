package quantum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * JavaFX user interface for the quantum image watermarking system.
 * The UI only handles buttons and previews. All watermarking logic is in
 * ImageWatermarker / QuantumWatermarker (separation of concerns).
 */
public class JavaFXTest extends Application {

    private BufferedImage original;      // image chosen by the user
    private BufferedImage watermarked;   // result after embedding

    private final ImageView originalView = new ImageView();
    private final ImageView markedView   = new ImageView();
    private final TextField messageField = new TextField();
    private final Label infoLabel   = new Label("Capacity: -");
    private final Label statusLabel = new Label("Open an image to begin.");

    private final Button openBtn    = new Button("Open Image");
    private final Button embedBtn   = new Button("Embed Watermark");
    private final Button saveBtn    = new Button("Save as PNG");
    private final Button extractBtn = new Button("Extract from Image...");

    @Override
    public void start(Stage stage) {
        // ----- top bar: buttons -----
        HBox buttons = new HBox(10, openBtn, embedBtn, saveBtn, extractBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // ----- message input -----
        messageField.setPromptText("Type the watermark text to hide in the image");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        HBox messageRow = new HBox(10, new Label("Watermark text:"), messageField);
        messageRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(10, buttons, messageRow);
        top.setPadding(new Insets(12));

        // ----- centre: before / after previews -----
        HBox previews = new HBox(16, previewBox("Original", originalView),
                previewBox("Watermarked (quantum LSB)", markedView));
        previews.setPadding(new Insets(0, 12, 0, 12));
        previews.setAlignment(Pos.CENTER);

        // ----- bottom: info + status -----
        VBox bottom = new VBox(4, infoLabel, statusLabel);
        bottom.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(previews);
        root.setBottom(bottom);

        // Embed and Save are disabled until they make sense
        embedBtn.setDisable(true);
        saveBtn.setDisable(true);

        openBtn.setOnAction(e -> openImage(stage));
        embedBtn.setOnAction(e -> embed());
        saveBtn.setOnAction(e -> savePng(stage));
        extractBtn.setOnAction(e -> extractFromFile(stage));

        stage.setTitle("Quantum Image Watermarking (simulated)");
        stage.setScene(new Scene(root, 1100, 600));
        stage.show();
    }

    /** A titled box holding one preview image. */
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

    // ---------------------------------------------------------------- actions

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
            infoLabel.setText("Size: " + img.getWidth() + " x " + img.getHeight()
                    + "   |   Capacity: " + ImageWatermarker.capacityBytes(img) + " characters");
            embedBtn.setDisable(false);
            saveBtn.setDisable(true);
            status("Loaded " + file.getName() + ". Type a message and click Embed.");
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
            saveBtn.setDisable(false);
            status(String.format("Embedded %d characters. PSNR = %.2f dB (above 40 dB = invisible). "
                    + "Save as PNG to keep the watermark.", text.length(), psnr));
        } catch (IllegalArgumentException ex) {
            status("Cannot embed: " + ex.getMessage());
        }
    }
    private void savePng(Stage stage) {
        if (watermarked == null) {
            status("Embed a watermark first.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save watermarked image (PNG)");
        fc.setInitialFileName("watermarked.png");

        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG image", "*.png")
        );

        // Start the Save dialog on Desktop
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        if (desktop.exists()) {
            fc.setInitialDirectory(desktop);
        }

        File file = fc.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(
                    file.getParentFile(),
                    file.getName() + ".png"
            );
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
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

    // ---------------------------------------------------------------- helpers

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