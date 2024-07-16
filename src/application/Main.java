package application;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Main extends Application {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final double FACT_BOX_WIDTH = 600;
    private static final double FACT_BOX_HEIGHT = 100;

    private Image backgroundImage;
    private LunarPuzzle currentPuzzle;
    private int currentLevel = 1;
    private GameObject titleObject, factBoxObject, hintButtonObject;
    private boolean puzzleCompleted = false;
    private GraphicsContext gc;
    private Stage hintStage;
    private Image[] moonPhaseImages;
    private static final int NUM_PHASES = 8;

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            root.setCenter(canvas);

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            // Load resources
            backgroundImage = new Image(getClass().getResourceAsStream("/application/assets/background.gif"), 0, 0, true, true);
            Font.loadFont(getClass().getResourceAsStream("/application/assets/fonts/joystix monospace.otf"), 30);
            
            moonPhaseImages = new Image[NUM_PHASES];
            for (int i = 0; i < NUM_PHASES; i++) {
                moonPhaseImages[i] = new Image(getClass().getResourceAsStream("/application/assets/moon_phase_" + (i + 1) + ".png"), 0, 0, true, true);
            }

            // Initialize GameObjects
            titleObject = new GameObject(gc, WINDOW_WIDTH / 2, 50, "Lunar Explorer");
            titleObject.setFontSize(40);
            factBoxObject = new GameObject(gc, WINDOW_WIDTH / 2, (WINDOW_HEIGHT - FACT_BOX_HEIGHT / 2) - 50, FACT_BOX_WIDTH, FACT_BOX_HEIGHT, "Complete the puzzle to learn about the moon!");
            hintButtonObject = new GameObject(gc, WINDOW_WIDTH - 100, 50, "Hint");

            // Initialize hint stage
            hintStage = new Stage();
            hintStage.initOwner(primaryStage);
            hintStage.initStyle(StageStyle.UTILITY);
            hintStage.setTitle("Hint");


            // Initialize the first puzzle
            currentPuzzle = new LunarPuzzle(currentLevel);

            // Create an AnimationTimer to update the scene
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    draw();
                }
            }.start();
            
            // Set up user input handling
            canvas.setOnMouseClicked(e -> handleMouseClick(e.getX(), e.getY()));

            primaryStage.setTitle("Lunar Explorer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void draw() {
        drawBackground();
        titleObject.update();
        hintButtonObject.update();
        
        if (!puzzleCompleted) {
            currentPuzzle.draw(gc);
        } else {
            // Draw moon phase image
            Image moonPhase = moonPhaseImages[currentLevel - 1];
            double moonSize = 300; // Increased size for better visibility
            double moonX = (WINDOW_WIDTH - moonSize) / 2;
            double moonY = (WINDOW_HEIGHT - FACT_BOX_HEIGHT - moonSize) / 2;
            
            // Draw the moon phase image with transparency
            gc.setGlobalAlpha(1.0); // Ensure full opacity
            gc.drawImage(moonPhase, moonX, moonY, moonSize, moonSize);
        }
        
        factBoxObject.update();
    }

    private void drawBackground() {
        double imageAspectRatio = backgroundImage.getWidth() / backgroundImage.getHeight();
        double canvasAspectRatio = WINDOW_WIDTH / WINDOW_HEIGHT;

        double sx, sy, sw, sh;
        double dx = 0, dy = 0, dw = WINDOW_WIDTH, dh = WINDOW_HEIGHT;

        if (imageAspectRatio > canvasAspectRatio) {
            // Image is wider than canvas
            sh = backgroundImage.getHeight();
            sw = sh * canvasAspectRatio;
            sy = 0;
            sx = (backgroundImage.getWidth() - sw) / 2;
        } else {
            // Image is taller than canvas
            sw = backgroundImage.getWidth();
            sh = sw / canvasAspectRatio;
            sx = 0;
            sy = (backgroundImage.getHeight() - sh) / 2;
        }

        gc.drawImage(backgroundImage, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    private void handleMouseClick(double x, double y) {
        if (hintButtonObject.contains(x, y)) {
            showHintPopup();
        } else if (!puzzleCompleted) {
            boolean moved = currentPuzzle.handleClick(gc, x, y);
            if (moved) {
                puzzleCompleted = currentPuzzle.checkWin();
                if (puzzleCompleted) {
                    factBoxObject.setText(getLunarFact(currentLevel));
                    
                    // Add a small delay before showing the moon image
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                    pause.setOnFinished(event -> draw());
                    pause.play();
                }
            }
        } else {
            // Move to next level
            currentLevel++;
            if (currentLevel > NUM_PHASES) currentLevel = 1;
            puzzleCompleted = false;
            currentPuzzle = new LunarPuzzle(currentLevel);
            factBoxObject.setText("Complete the puzzle to learn about the moon!");
        }
    }
    
    private void showHintPopup() {
        ImageView hintImageView = new ImageView(currentPuzzle.getFullImage());
        hintImageView.setPreserveRatio(true);
        hintImageView.setFitWidth(300);

        StackPane hintLayout = new StackPane(hintImageView);
        hintLayout.setPadding(new Insets(10));

        Scene hintScene = new Scene(hintLayout);
        hintStage.setScene(hintScene);
        hintStage.show();
    }
    
    private String getLunarFact(int phase) {
        switch (phase) {
            case 1:
                return "New Moon: The moon is between Earth and the Sun. Its dark side faces us, making it nearly invisible in the night sky.";
            case 2:
                return "Waxing Crescent: A sliver of the moon becomes visible. 'Waxing' means it's getting larger.";
            case 3:
                return "First Quarter: Half of the moon's lit surface is visible from Earth. It looks like a 'half moon'.";
            case 4:
                return "Waxing Gibbous: More than half of the moon is illuminated. 'Gibbous' means bulging or convex.";
            case 5:
                return "Full Moon: The entire face of the moon is illuminated. This occurs when Earth is between the Sun and the Moon.";
            case 6:
                return "Waning Gibbous: The illuminated area starts to decrease. 'Waning' means it's getting smaller.";
            case 7:
                return "Last Quarter: Another half moon, but on the opposite side compared to the First Quarter.";
            case 8:
                return "Waning Crescent: The last sliver of light before the New Moon. Also called the 'old moon'.";
            default:
                return "Explore the moon's phases! Each cycle takes about 29.5 days to complete.";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}