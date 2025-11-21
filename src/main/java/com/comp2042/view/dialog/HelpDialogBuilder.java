package com.comp2042.view.dialog;

import com.comp2042.service.audio.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Builder class for creating the help dialog UI.
 * 
 * <p>This class is responsible for building all UI components of the help dialog,
 * including sections for game modes, basics, randomizer, scoring, ghost brick,
 * endless rules, and two-player rules.
 * 
 * <p>The dialog lifecycle (opening, closing, state management) is handled by
 * the caller through the provided callback interface.
 */
public class HelpDialogBuilder {

    private static final int HELP_DIALOG_WIDTH = 720;
    private static final int HELP_DIALOG_HEIGHT = 560;
    private static final int SCROLL_PANE_HEIGHT = 520;
    private static final int MAIN_CONTAINER_SPACING = 20;
    private static final int MAIN_CONTAINER_PADDING = 30;

    /**
     * Callback interface for handling help dialog lifecycle events.
     */
    public interface HelpDialogCallbacks {
        /**
         * Called when the help dialog is closed.
         * Implementations should handle game state restoration.
         */
        void onDialogClosed();
    }

    /**
     * Creates and configures the help dialog stage.
     * 
     * @return configured Stage for the help dialog
     */
    public Stage createHelpDialog() {
        Stage helpStage = new Stage();
        helpStage.setTitle("Gameplay Guide");
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setResizable(false);
        return helpStage;
    }

    /**
     * Creates the scrollable pane for help content.
     * 
     * @return configured ScrollPane
     */
    public ScrollPane createHelpScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(SCROLL_PANE_HEIGHT);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.getStyleClass().add("help-scroll");
        return scrollPane;
    }

    /**
     * Creates the main help content container with all sections.
     * 
     * @return VBox containing all help sections
     */
    public VBox createHelpContent() {
        VBox mainContainer = new VBox(MAIN_CONTAINER_SPACING);
        mainContainer.setPadding(new Insets(MAIN_CONTAINER_PADDING));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1A0033, #2D1B69);");

        // Title
        Label titleLabel = new Label("Gameplay Guide");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4DFFFF; -fx-alignment: center;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        // Add all content sections
        mainContainer.getChildren().addAll(
            titleLabel,
            createGameModesSection(),
            createBasicsSection(),
            createRandomizerSection(),
            createScoringSection(),
            createGhostBrickSection(),
            createEndlessRulesSection(),
            createTwoPlayerRulesSection(),
            createCloseButton()
        );

        return mainContainer;
    }

    /**
     * Sets up the help dialog with all components and event handlers.
     * 
     * @param helpStage the dialog stage
     * @param scrollPane the scroll pane container
     * @param mainContainer the main content container
     * @param callbacks callback interface for dialog events
     * @param stylesheetPath path to the CSS stylesheet
     */
    public void setupHelpDialog(Stage helpStage, ScrollPane scrollPane, VBox mainContainer,
                                HelpDialogCallbacks callbacks, String stylesheetPath) {
        scrollPane.setContent(mainContainer);

        Scene helpScene = new Scene(scrollPane, HELP_DIALOG_WIDTH, HELP_DIALOG_HEIGHT);
        if (stylesheetPath != null) {
            helpScene.getStylesheets().add(stylesheetPath);
        }

        // Set up close button event handlers
        HBox buttonContainer = (HBox) mainContainer.getChildren().get(mainContainer.getChildren().size() - 1);
        Button closeButton = (Button) buttonContainer.getChildren().get(0);

        closeButton.setOnAction(e -> {
            SoundManager.getInstance().playButtonClickSound();
            helpStage.close();
            if (callbacks != null) {
                callbacks.onDialogClosed();
            }
        });

        helpStage.setOnCloseRequest(e -> {
            SoundManager.getInstance().playButtonClickSound();
            if (callbacks != null) {
                callbacks.onDialogClosed();
            }
        });

        helpStage.setScene(helpScene);
        helpStage.show();
    }

    /**
     * Creates the game modes section.
     */
    private VBox createGameModesSection() {
        VBox modesContainer = new VBox(12);
        modesContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");

        String[][] modeData = {
            {"Endless Mode", "Play endlessly and aim for the highest score."},
            {"Level Mode", "Clear levels with increasing difficulty and unlock new themes."},
            {"Two-Player Mode", "Challenge a friend in local two-player battle."}
        };

        for (String[] mode : modeData) {
            HBox modeRow = new HBox(20);
            modeRow.setAlignment(Pos.CENTER_LEFT);

            Label modeLabel = new Label(mode[0]);
            modeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-min-width: 150;");

            Label descLabel = new Label(mode[1]);
            descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            descLabel.setMaxWidth(500);
            descLabel.setWrapText(true);
            descLabel.setPrefWidth(500);

            modeRow.getChildren().addAll(modeLabel, descLabel);
            modesContainer.getChildren().add(modeRow);
        }

        return modesContainer;
    }

    /**
     * Creates the basics section with gameplay rules and controls.
     */
    private HBox createBasicsSection() {
        HBox basicsDual = new HBox(20);
        basicsDual.setAlignment(Pos.TOP_LEFT);

        // Left: Gameplay Basics & Rules
        VBox basicsLeftBox = new VBox(12);
        basicsLeftBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
        Label basicsLeftTitle = new Label("Gameplay Basics");
        basicsLeftTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        VBox basicsLeftCol = createBulletedColumn(new String[] {
            "Place falling tetrominoes to complete rows.",
            "Clear full horizontal lines to earn points.",
            "Clear multiple lines at once for higher points.",
            "Pieces fall faster as you clear more lines.",
            "Topping out (stack reaches top) ends the game."
        }, 330);
        basicsLeftBox.getChildren().addAll(basicsLeftTitle, basicsLeftCol);

        // Right: Sidebar Panels & Actions
        VBox basicsRightBox = new VBox(12);
        basicsRightBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 10; -fx-padding: 18;");
        Label basicsRightTitle = new Label("Side Panels & Actions");
        basicsRightTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        VBox basicsRightCol = createBulletedColumn(new String[] {
            "Next: preview upcoming pieces.",
            "Hold: store one piece to swap later (one swap per piece).",
            "Ghost Brick: semi-transparent preview showing where the piece will land.",
            "Statistics: shows Level, Lines cleared, Speed and Time.",
            "Score: real-time points and the Highest Score.",
            "Controls: Settings, Help, Back to Menu.",
            "Actions: New Game (N), Pause & Resume (P), Mute."
        }, 330);
        basicsRightBox.getChildren().addAll(basicsRightTitle, basicsRightCol);

        basicsDual.getChildren().addAll(basicsLeftBox, basicsRightBox);
        return basicsDual;
    }

    /**
     * Creates the piece randomizer help section.
     */
    private VBox createRandomizerSection() {
        VBox rngContainer = new VBox(10);
        rngContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

        Label rngTitle = new Label("Piece Randomizer Systems");
        rngTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
        rngTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rngTitle, Priority.ALWAYS);

        Label rngIntro = new Label(
            "Modern Tetris variants use a \"bag\" to distribute tetrominoes, while early games used pure random selection. Choose your system in Settings > Gameplay > Piece Randomizer. Default is 7‑Bag System.");
        rngIntro.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        rngIntro.setWrapText(true);

        Label bagHeader = new Label("7‑Bag System (Recommended)");
        bagHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        Label bagDesc = new Label(
            "• Each set of seven contains I, O, T, S, Z, J, L exactly once, then a new bag is shuffled.\n" +
            "• Guarantees fairness and predictability: no long droughts, no long streaks.\n" +
            "• Best for skill development and consistent difficulty.");
        bagDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        bagDesc.setWrapText(true);

        Label prHeader = new Label("Pure Random System (Classic)");
        prHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        Label prDesc = new Label(
            "• Each piece is chosen uniformly at random with replacement.\n" +
            "• Can produce streaks and droughts (harder and more volatile).\n" +
            "• Choose this if you prefer old-school variance and challenge.");
        prDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        prDesc.setWrapText(true);

        Label applyInfo = new Label(
            "Note: Changing the piece randomizer requires a game restart.\n" +
            "When you click Save in the settings page, the current game will reset with the selected system.");
        applyInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");
        applyInfo.setWrapText(true);

        rngContainer.getChildren().addAll(rngTitle, rngIntro, bagHeader, bagDesc, prHeader, prDesc, applyInfo);
        return rngContainer;
    }

    /**
     * Creates the scoring system help section.
     */
    private VBox createScoringSection() {
        VBox scoreContainer = new VBox(10);
        scoreContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

        Label scoreTitle = new Label("Score System");
        scoreTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
        scoreTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(scoreTitle, Priority.ALWAYS);

        Label lineScores = new Label(
            "Line Clears:\n" +
            "• Single (1 line): +100 pts\n" +
            "• Double (2 lines): +300 pts\n" +
            "• Triple (3 lines): +500 pts\n" +
            "• Tetris (4 lines): +800 pts");
        lineScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        lineScores.setWrapText(true);

        Label dropScores = new Label(
            "Drops:\n" +
            "• Soft Drop: +1 pt per row (accelerated)\n" +
            "• Hard Drop: +2 pts per row (instant)");
        dropScores.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        dropScores.setWrapText(true);

        HBox scoreRow = new HBox(40);
        scoreRow.setAlignment(Pos.TOP_LEFT);
        lineScores.setPrefWidth(300);
        dropScores.setPrefWidth(300);
        scoreRow.getChildren().addAll(lineScores, dropScores);

        scoreContainer.getChildren().addAll(scoreTitle, scoreRow);
        return scoreContainer;
    }

    /**
     * Creates the ghost brick system help section.
     */
    private VBox createGhostBrickSection() {
        VBox ghostContainer = new VBox(10);
        ghostContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

        Label ghostTitle = new Label("Ghost Brick System");
        ghostTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
        ghostTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(ghostTitle, Priority.ALWAYS);

        Label ghostDesc = new Label(
            "The Ghost Brick is a semi-transparent preview that shows where your current piece will land if dropped straight down. " +
            "It helps you plan your placement strategy and make precise drops.\n\n" +
            "Display Conditions:\n" +
            "• Endless Mode: Shown when level is less than 5 (levels 1-4). Not shown from level 5 onwards (up to level 15).\n" +
            "• Level Mode: Shown for Easy difficulty (Level 1 and 2)\n" +
            "• Two-Player Mode: Always shown");
        ghostDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        ghostDesc.setWrapText(true);

        ghostContainer.getChildren().addAll(ghostTitle, ghostDesc);
        return ghostContainer;
    }

    /**
     * Creates the endless mode rules section.
     */
    private VBox createEndlessRulesSection() {
        VBox endlessContainer = new VBox(10);
        endlessContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

        Label endlessTitle = new Label("Endless Mode Rules");
        endlessTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
        endlessTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(endlessTitle, Priority.ALWAYS);

        Label levelProgTitle = new Label("Level Progression");
        levelProgTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label levelProgText = new Label(
            "• Level increases by 1 for every 10 lines cleared\n" +
            "• Starting level: 1, Maximum level: 15\n" +
            "• Examples: 0-9 lines = Level 1, 10-19 lines = Level 2, ..., 140+ lines = Level 15");
        levelProgText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        levelProgText.setWrapText(true);

        Label speedTitle = new Label("Speed Progression");
        speedTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label speedText = new Label(
            "• Speed multiplier increases every 2 levels\n" +
            "• Starting speed: 1x, Maximum speed: 8x\n" +
            "• Examples: Level 1-2 = 1x, Level 3-4 = 2x, ..., Level 15 = 8x");
        speedText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        speedText.setWrapText(true);

        endlessContainer.getChildren().addAll(endlessTitle, levelProgTitle, levelProgText, speedTitle, speedText);
        return endlessContainer;
    }

    /**
     * Creates the two-player mode rules section.
     */
    private VBox createTwoPlayerRulesSection() {
        VBox twoPlayerContainer = new VBox(12);
        twoPlayerContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-padding: 20;");

        Label twoPlayerTitle = new Label("Two-Player Mode Rules");
        twoPlayerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: center;");
        twoPlayerTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(twoPlayerTitle, Priority.ALWAYS);

        Label objectiveTitle = new Label("Objective & Winning Condition");
        objectiveTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label objectiveText = new Label(
            "Clear lines to send garbage lines to your opponent. The last player standing wins!\n" +
            "The game ends when one player's board fills up. The player with the higher score wins!");
        objectiveText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        objectiveText.setWrapText(true);

        Label importantTitle = new Label("Important:");
        importantTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label importantText = new Label(
            "For Player 2, ensure Num Lock is ON when using numpad keys (0, 2, 3)");
        importantText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        importantText.setWrapText(true);

        Label attackTitle = new Label("Attack System");
        attackTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label attackText = new Label(
            "• 1 line cleared: No attack (0 garbage lines)\n" +
            "• 2 lines cleared: Send 1 garbage line\n" +
            "• 3 lines cleared: Send 2 garbage lines\n" +
            "• 4 lines cleared (Tetris): Send 4 garbage lines");
        attackText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        attackText.setWrapText(true);

        Label comboTitle = new Label("Combo Bonus");
        comboTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label comboText = new Label(
            "Build combos by clearing lines consecutively. Each combo above 1 eliminates 2 garbage lines from your board!\n" +
            "Example: Combo x3 = Eliminates 4 garbage lines (2 per combo above 1)");
        comboText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        comboText.setWrapText(true);

        Label garbageTitle = new Label("Garbage Lines");
        garbageTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label garbageText = new Label("Garbage lines appear as gray blocks with one random hole. Clear them quickly or they'll stack up!");
        garbageText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        garbageText.setWrapText(true);

        Label featuresTitle = new Label("Special Features");
        featuresTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label featuresText = new Label(
            "• Countdown timer before game starts (3-2-1)\n" +
            "• Visual attack animations when receiving attacks\n" +
            "• Real-time statistics tracking (combo, attacks, defense)\n" +
            "• Sound effects for attacks and line clears");
        featuresText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        featuresText.setWrapText(true);

        Label strategyTitle = new Label("Strategy Tips");
        strategyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 8 0 4 0;");
        Label strategyText = new Label(
            "• Build for Tetris (4-line clears) for maximum damage\n" +
            "• Maintain combos to clear incoming garbage lines\n" +
            "• Watch your opponent's board and adapt your strategy\n" +
            "• Use hold to save pieces for better setups");
        strategyText.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
        strategyText.setWrapText(true);

        twoPlayerContainer.getChildren().addAll(
            twoPlayerTitle,
            objectiveTitle, objectiveText,
            importantTitle, importantText,
            attackTitle, attackText,
            comboTitle, comboText,
            garbageTitle, garbageText,
            featuresTitle, featuresText,
            strategyTitle, strategyText
        );
        return twoPlayerContainer;
    }

    /**
     * Creates the close button for the help dialog.
     */
    private HBox createCloseButton() {
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #4DFFFF; -fx-text-fill: #1A0033; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        closeButton.setFocusTraversable(false);

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getChildren().add(closeButton);

        return buttonContainer;
    }

    /**
     * Creates a bulleted column of text items.
     * 
     * @param items array of text items to display
     * @param width preferred width of the column
     * @return VBox containing labeled items
     */
    private VBox createBulletedColumn(String[] items, double width) {
        VBox box = new VBox(6);
        for (String text : items) {
            Label lbl = new Label("• " + text);
            lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");
            lbl.setWrapText(true);
            lbl.setPrefWidth(width);
            box.getChildren().add(lbl);
        }
        return box;
    }
}

