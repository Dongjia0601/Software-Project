package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main entry point for the Tetris application.
 * Initializes the JavaFX application and displays the main menu.
 * 
 * @author Dong, Jia.
 */
public class Main extends Application {

    /**
     * Initializes the primary stage with the main menu.
     * Loads the FXML layout and configures the window as a fixed-size, centered interface.
     * 
     * @param primaryStage The primary stage for this application
     * @throws Exception if FXML loading or scene configuration fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main menu FXML file from the classpath
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        if (location == null) {
            throw new IllegalStateException("Cannot find mainMenu.fxml resource. Application cannot start.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        // Configure the primary stage
        primaryStage.setTitle("Tetris - Main Menu");
        Scene scene = new Scene(root, 900, 800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Fixed window size for consistent UI
        
        // Center window on current screen (not forced to primary screen)
        primaryStage.centerOnScreen();
        
        primaryStage.show();
        
    }

    /**
     * Application entry point that launches the JavaFX runtime.
     * <p>
     * Note: When running from IDE, ensure VM options are configured with:
     * --module-path [JavaFX jars path] --add-modules javafx.controls,javafx.fxml,javafx.media
     * <p>
     * Alternatively, use Maven command: mvn javafx:run
     * 
     * @param args Command line arguments (currently unused)
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("JavaFX runtime components are missing")) {
                System.err.println("================================================");
                System.err.println("Error: JavaFX runtime components are missing");
                System.err.println("================================================");
                System.err.println("Solutions:");
                System.err.println("1. Configure VM options in IDE");
                System.err.println("2. Use Maven command: mvn javafx:run");
                System.err.println("3. Use run script: runGame.bat");
                System.err.println("================================================");
            }
            throw e;
        }
    }
}
