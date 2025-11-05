package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameOverPanel extends BorderPane {
    
    private Label titleLabel;
    private Label subtitleLabel;

    public GameOverPanel() {
        titleLabel = new Label("GAME OVER");
        titleLabel.getStyleClass().add("game-over-title");
        
        subtitleLabel = new Label("");
        subtitleLabel.getStyleClass().add("game-over-subtitle");
        
        VBox container = new VBox(5);
        container.getStyleClass().add("game-over-container");
        container.getChildren().addAll(titleLabel, subtitleLabel);
        
        setCenter(container);
    }
    
    /**
     * Sets the title text for the game over panel.
     * 
     * @param title the title text to display
     */
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    /**
     * Sets the subtitle text for the game over panel.
     * 
     * @param subtitle the subtitle text to display
     */
    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }
}
