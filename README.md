# COMP2042 Coursework: Tetris Refactoring and Extension

## GitHub

[https://github.com/Dongjia0601/CW2025](https://github.com/Dongjia0601/CW2025)

## Compilation Instructions

1.  Ensure Java Development Kit (JDK) 21 (or a compatible version) is installed.
2.  Ensure JavaFX SDK 21.0.8 (or a compatible version) is installed and configured in your IDE.
3.  Import the project as a Maven project into your Integrated Development Environment (IDE), such as IntelliJ IDEA or Eclipse. The project root folder is `CW2025`.
4.  The project uses Maven for dependency management. The `pom.xml` file contains the necessary JavaFX dependencies.
5.  Run the `Main` class (`com.comp2042.Main`) to start the application.

## Implemented and Working Properly

*   **Bug Fixes:** Corrected critical bugs in `MatrixOperations.java` related to matrix indexing and coordinate mapping in the `intersect` and `merge` methods. This resolved `ArrayIndexOutOfBoundsException` errors during gameplay.
*   **State Pattern Implementation:** Implemented the State Pattern for managing game states (Playing, Paused, GameOver). This refactored the core game logic, improving modularity and separation of concerns within `GameController` and `GuiController`. The 'P' key now correctly pauses and unpauses the game.
*   **Code Renaming:** Renamed the method `MatrixOperations.checkRemoving` to `clearCompletedRows` for improved clarity and accuracy.
*   **Javadoc Documentation:** Added comprehensive Javadoc comments to all major classes, interfaces, enums, and methods across the codebase, enhancing code readability and maintainability.
*   **Constants Extraction:** Extracted hardcoded values into named constants for improved maintainability. Examples include `BRICK_SIZE` in `GuiController`, `SCORE_BONUS_BASE` in `MatrixOperations`, and spawn position constants in `SimpleBoard`.
*   **JUnit Tests:** Added JUnit 5 tests for core logic classes (`MatrixOperations`, `SimpleBoard`, `Score`, `ClearRow`, `NextShapeInfo`, `ViewData`, `GameState` implementations) to verify functionality and support future maintenance.

## Implemented but Not Working Properly

*  

## Features Not Implemented

*   

## New Java Classes

*   `com.comp2042.gameplay.GameState.java`: Interface defining methods for different game states.
*   `com.comp2042.gameplay.PlayingState.java`: Implements the logic for the active gameplay state.
*   `com.comp2042.gameplay.PausedState.java`: Implements the logic for the paused state.
*   `com.comp2042.gameplay.GameOverState.java`: Implements the logic for the game over state.

## Modified Java Classes

*   `com.comp2042.MatrixOperations.java`: Fixed bugs in `intersect` and `merge`. Renamed `checkRemoving` to `clearCompletedRows`. Added Javadocs.
*   `com.comp2042.SimpleBoard.java`: Added Javadocs. Potentially added constants for spawn position.
*   `com.comp2042.GameController.java`: Integrated State Pattern. Added methods for state management (`transitionToState`, `requestPause`). Added Javadocs.
*   `com.comp2042.GuiController.java`: Integrated State Pattern for pause logic. Added Javadocs. Added constant `BRICK_SIZE`.
*   `com.comp2042.Score.java`: Added Javadocs.
*   `com.comp2042.ClearRow.java`: Added Javadocs.
*   `com.comp2042.ViewData.java`: Added Javadocs.
*   `com.comp2042.NextShapeInfo.java`: Added Javadocs.
*   `com.comp2042.NotificationPanel.java`: Added Javadocs.
*   `com.comp2042.Board.java`: Added Javadocs.
*   `com.comp2042.InputEventListener.java`: Added Javadocs.
*   `com.comp2042.EventSource.java`: Added Javadocs.
*   `com.comp2042.EventType.java`: Added Javadocs.
*   `com.comp2042.MoveEvent.java`: Added Javadocs.
*   `com.comp2042.logic.bricks.Brick.java`: Added Javadocs.
*   `com.comp2042.logic.bricks.BrickGenerator.java`: Added Javadocs.
*   `com.comp2042.logic.bricks.RandomBrickGenerator.java`: Added Javadocs.
*   `com.comp2042.logic.bricks.IBrick.java`, `JBrick.java`, `LBrick.java`, `OBrick.java`, `SBrick.java`, `TBrick.java`, `ZBrick.java`: Added Javadocs.
*   `com.comp2042.BrickRotator.java`: Updated Javadocs to reflect method rename (`calculateNextShapeInfo`).

## Unexpected Problems

*   Integrating the State Pattern required careful management of state transitions, particularly from `PlayingState` to `GameOverState` when a new brick cannot be placed immediately after landing. This was handled by having the `PlayingState` notify the `GameController` of the transition via the `transitionToState` method.
*   Ensuring `GuiController` correctly reflected the logical state (e.g., stopping the automatic `moveDown` call when paused) based on the `GameController`'s state required coordination between the GUI and the game logic controllers.