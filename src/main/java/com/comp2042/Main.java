package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main entry point for the Tetris application.
 * 
 * <p>This class serves as the application launcher and initializes the main menu interface.
 * It follows the JavaFX Application lifecycle and sets up the primary stage with the main menu.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Initialize the JavaFX application</li>
 *   <li>Load the main menu FXML layout</li>
 *   <li>Configure the primary stage with appropriate settings</li>
 *   <li>Display the main menu to the user</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class Main extends Application {

    /**
     * The main entry point for the JavaFX application.
     * 
     * <p>This method is called after the JavaFX runtime has been initialized.
     * It sets up the primary stage with the main menu interface and configures
     * the application window properties.</p>
     * 
     * <p>The method performs the following operations:</p>
     * <ul>
     *   <li>Loads the main menu FXML file from resources</li>
     *   <li>Creates a new scene with the loaded FXML content</li>
     *   <li>Configures the stage title and window properties</li>
     *   <li>Displays the main menu to the user</li>
     * </ul>
     * 
     * @param primaryStage The primary stage for this application, onto which
     *                    the application scene can be set. Applications may create other stages,
     *                    if needed, but they will not be primary stages.
     * @throws Exception if the FXML file cannot be loaded or if there are issues
     *                   with scene creation or stage configuration.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main menu FXML file from the classpath
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null; // No internationalization needed for this project
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        // Configure the primary stage
        primaryStage.setTitle("Tetris - Main Menu");
        Scene scene = new Scene(root, 900, 800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Fixed window size for consistent UI
        primaryStage.show();
        
        // Log successful initialization
        System.out.println("Main menu loaded successfully");
    }

    /**
     * The main method that launches the JavaFX application.
     * 
     * <p>This method serves as the entry point for the application and delegates
     * control to the JavaFX Application.launch() method, which handles the
     * initialization of the JavaFX runtime and calls the start() method.</p>
     * 
     * @param args Command line arguments passed to the application.
     *             Currently not used, but maintained for future extensibility.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
