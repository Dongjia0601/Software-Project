# COMP2042 Coursework: Tetris 

# Table of Contents

## GitHub Repository

[https://github.com/Dongjia0601/CW2025](https://github.com/Dongjia0601/CW2025)

## Introduction

Welcome to my Developing Maintenance Software coursework project. I am Dong, Jia (Student ID: 20705878), and this project focuses on maintaining and extending a re-implementation of the classic retro game (Tetris). This implementation features four distinct game modes: Endless Mode, Level Mode, AI Mode, and Two-Player Mode, providing a comprehensive Tetris gaming experience. For the best experience, it is recommended to view this README.md file on the [GitHub website](https://github.com/Dongjia0601/CW2025). Navigation links have been embedded throughout the document to facilitate easy access to different sections.

### Game Modes

This Tetris implementation includes four distinct game modes:

1. **Endless Mode**: A classic Tetris experience where players aim for the highest score possible without time limits or level restrictions.

2. **Level Mode**: A structured gameplay experience with themed levels, each featuring unique challenges, time limits, and objectives.

3. **AI Mode**: A single-player mode where players can compete against an AI opponent, providing an engaging challenge for solo gameplay.

4. **Two-Player Mode**: A competitive multiplayer mode where two players can play simultaneously, competing for the highest score or last player standing.

## Project Structure 

## Compilation Instructions

### Prerequisites
Before you begin, ensure the following components are installed and properly configured:

- **Java Development Kit (JDK) 21**  
  Required to compile and run the application.  
  [Download JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or Microsoft Build of OpenJDK.

- **IntelliJ IDEA** (or Eclipse)  
  Recommended IDE for building and running the project.  
  [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

- **Maven**  
  Used for dependency management and project builds. IntelliJ IDEA bundles Maven by default.
  Alternatively, install it from [Maven’s official site](https://maven.apache.org/download.cgi) and add it to your system PATH.

- **JavaFX SDK 21.0.9**  
  Required for running the JavaFX application.  
  [Download JavaFX](https://gluonhq.com/products/javafx/) and configure it in your IDE.
- 
- **Git**  
  Used to clone and manage the project repository.

---

### Cloning and Importing the Project
1. Clone the repository:
    ```bash
    [git clone https://github.com/Dongjia0601/CW2025]
    ```
2. Open **IntelliJ IDEA** → **File > Open...**
3. Navigate to the cloned project directory containing the `pom.xml` file.
4. Click **OK** to open it as a Maven project. IntelliJ will automatically import and configure dependencies.

---

### Configuring the JDK in IntelliJ IDEA
1. Go to **File > Project Structure > Project**
2. Set **Project SDK** to **JDK 21**
   - If JDK 21 is not listed, click **New...** and locate your JDK installation folder.
3. Click **Apply** and **OK**

---

### Setting Up JavaFX and Maven in IntelliJ IDEA
1. Ensure **JavaFX SDK** is properly configured in your project settings.
2. If Maven is not detected automatically:
   - Go to **File > Settings > Build, Execution, Deployment > Build Tools > Maven**
   - Set the **Maven home directory** to the bundled version or your installed Maven path.
3. IntelliJ IDEA will automatically download all dependencies defined in `pom.xml`.

---

### Running the Application

#### Option 1: Using Maven Plugin (Recommended)
1. Open the **Maven** tool window (click the Maven icon on the right sidebar).
2. Navigate to **Plugins > javafx**
3. Double-click **javafx:run** to execute the application.
   This automatically attaches required JavaFX modules.

#### Option 2: Using the Main Class
1. In the **Project** view, locate:
    ```
    src/main/java/com/comp2042/Main.java
    ```
2. Right-click `Main.java` → **Run 'Main'** to start the application.

Note:
> Running the application directly via `Run 'Main'` in IntelliJ may produce
> `JavaFX runtime components are missing` because IntelliJ does not automatically
> configure the JavaFX module path. Please use the Maven plugin (`javafx:run`)
> for consistent and portable execution.
---

### VM Options (if needed)
If JavaFX runtime errors occur, ensure the following VM options are added to your Run Configuration:
```bash
--module-path "path\to\javafx-sdk-21.0.9\lib" 
--add-modules javafx.controls,javafx.fxml
```
This should only be necessary when not using javafx:run.

## Implemented and Working Properly

*   **Bug Fixes:** Corrected critical bugs in `MatrixOperations.java` related to matrix indexing and coordinate mapping in the `intersect` and `merge` methods. This resolved `ArrayIndexOutOfBoundsException` errors during gameplay.
*   **State Pattern Implementation:** Implemented the State Pattern for managing game states (Playing, Paused, GameOver). This refactored the core game logic, improving modularity and separation of concerns within `GameController` and `GuiController`. The 'P' key now correctly pauses and unpauses the game.
*   **Code Renaming:** Renamed the method `MatrixOperations.checkRemoving` to `clearCompletedRows` for improved clarity and accuracy.
*   **Javadoc Documentation:** Added comprehensive Javadoc comments to all major classes, interfaces, enums, and methods across the codebase, enhancing code readability and maintainability.
*   **Constants Extraction:** Extracted hardcoded values into named constants for improved maintainability. Examples include `BRICK_SIZE` in `GuiController`, `SCORE_BONUS_BASE` in `MatrixOperations`, and spawn position constants in `SimpleBoard`.


## Implemented but Not Working Properly

*  

## Features Not Implemented

*   

## New Java Classes

*   `com.comp2042.gameplay.GameState.java`: Interface defining methods for different game states.
*   `com.comp2042.gameplay.PlayingState.java`: Implements the logic for the active gameplay state.
*   `com.comp2042.gameplay.PausedState.java`: Implements the logic for the paused state.
*   `com.comp2042.gameplay.GameOverState.java`: Implements the logic for the game over state.
*   `com.comp2042.game.LevelTheme.java`: Interface for defining level themes (planned for additions).
*   `com.comp2042.game.ThemeLevel.java `: Class for level configuration (planned for additions).
\

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
