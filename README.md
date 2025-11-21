# COMP2042 Coursework: Tetris 

# Table of Contents

- [GitHub Repository](#github-repository)
- [Introduction](#introduction)
  - [Project Goals](#project-goals)
  - [Game Modes](#game-modes)
  - [Project Structure](#project-structure)
- [Compilation Instructions](#compilation-instructions)
  - [Prerequisites](#prerequisites)
  - [Cloning and Importing](#cloning-and-importing)
  - [Running the Application](#running-the-application)
- [Implemented and Working Properly](#implemented-and-working-properly)
  - [Frontend Features](#frontend-features)
  - [Backend Architecture](#backend-architecture)
- [Implemented but Not Working Properly](#implemented-but-not-working-properly)
- [Features Not Implemented](#features-not-implemented)
- [New Java Classes](#new-java-classes)
- [Modified Java Classes](#modified-java-classes)
- [Deleted Java Classes](#deleted-java-classes)
- [Unexpected Problems](#unexpected-problems)

# GitHub Repository

[https://github.com/Dongjia0601/CW2025](https://github.com/Dongjia0601/CW2025)

# Introduction

Welcome to my Developing Maintenance Software coursework project. I am **Dong, Jia (Student ID: 20705878)**, and this project focuses on maintaining and extending a re-implementation of the classic retro game **Tetris**. 

This implementation features three distinct game modes: **Endless Mode**, **Level Mode**, and **Two-Player Mode**, providing a comprehensive Tetris gaming experience with modern design patterns, professional UI/UX, and extensive audio-visual enhancements.

For the best experience, it is recommended to view this `README.md` file on the [GitHub website](https://github.com/Dongjia0601/CW2025). Navigation links have been embedded throughout the document to facilitate easy access to different sections.

## Project Goals

- **Professional Architecture**: Implement industry-standard design patterns (MVC, State, Factory, Strategy, Memento, Singleton) to create maintainable and scalable code.
- **Enhanced Gaming Experience**: Provide multiple game modes with beautiful UI, smooth animations, and immersive audio.
- **Code Quality**: Achieve comprehensive JavaDoc documentation, thorough unit testing, and adherence to SOLID principles.
- **User-Friendly Design**: Create intuitive controls, helpful tutorials, and accessible settings management.

## Game Modes

This Tetris implementation includes three distinct game modes:

### 1. Endless Mode
A classic Tetris experience where players aim for the highest score possible. Features include:
- Progressive difficulty increase
- Leaderboard system with persistent high scores
- Real-time statistics (lines cleared, level, elapsed time)
- Two randomizer algorithms: 7-Bag and Pure Random

### 2. Level Mode  
A structured gameplay experience with five themed levels:
- **Level 1: Ancient Temple** (Easy) - Mystical ruins atmosphere
- **Level 2: Magic Castle** (Easy-Medium) - Fantasy medieval setting
- **Level 3: Sunset City** (Medium) - Urban twilight ambiance
- **Level 4: Future Warfare** (Medium-Hard) - Sci-fi battlefield theme
- **Level 5: Interstellar Odyssey** (Hard) - Deep space exploration

Each level features:
- Unique themed visuals with custom background images and color schemes
- Background music selected by level ID (Level135BGM.mp3 for levels 1,3,5; Level24BGM.mp3 for levels 2,4)
- Time limits and score requirements
- Star rating system (1-3 stars based on performance)
- Progressive unlocking mechanism

### 3. Two-Player Mode
Competitive split-screen multiplayer featuring:
- Simultaneous gameplay for two players
- Attack mechanism (send garbage lines to opponent)
- Independent controls (Player 1: WASD, Player 2: Arrow Keys)
- Real-time statistics and countdown system
- Special attack animations with screen shake effects

## Project Structure

```
📦 CW2025
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/com/comp2042
 ┃ ┃ ┃ ┣ 📂 config
 ┃ ┃ ┃ ┃ ┗ 📜 GameSettings.java
 ┃ ┃ ┃ ┣ 📂 controller
 ┃ ┃ ┃ ┃ ┣ 📂 factory
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameMode.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameModeFactory.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameModeType.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 LevelGameModeImpl.java
 ┃ ┃ ┃ ┃ ┣ 📂 game
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 twoplayer
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜 TwoPlayerCountdownManager.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜 TwoPlayerTimelineScheduler.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GuiController.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 TwoPlayerGameController.java
 ┃ ┃ ┃ ┃ ┣ 📂 menu
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 EndlessGameOverController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelGameOverController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelSelectionController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 MainMenuController.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SettingsController.java
 ┃ ┃ ┃ ┃ ┗ 📂 strategy
 ┃ ┃ ┃ ┃   ┣ 📜 EndlessModeUIStrategy.java
 ┃ ┃ ┃ ┃   ┣ 📜 GameModeUIStrategy.java
 ┃ ┃ ┃ ┃   ┣ 📜 LevelModeUIStrategy.java
 ┃ ┃ ┃ ┃   ┗ 📜 TwoPlayerModeUIStrategy.java
 ┃ ┃ ┃ ┣ 📂 dto
 ┃ ┃ ┃ ┃ ┣ 📜 ClearRow.java
 ┃ ┃ ┃ ┃ ┣ 📜 DownData.java
 ┃ ┃ ┃ ┃ ┗ 📜 ViewData.java
 ┃ ┃ ┃ ┣ 📂 event
 ┃ ┃ ┃ ┃ ┣ 📂 listener
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BrickMovementListener.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameControlListener.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 InputEventListener.java
 ┃ ┃ ┃ ┃ ┣ 📜 EventSource.java
 ┃ ┃ ┃ ┃ ┣ 📜 EventType.java
 ┃ ┃ ┃ ┃ ┗ 📜 MoveEvent.java
 ┃ ┃ ┃ ┣ 📂 model
 ┃ ┃ ┃ ┃ ┣ 📂 board
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 Board.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BoardMementoAdapter.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BrickRotator.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GarbageManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 HoldManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 NextShapeInfo.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SimpleBoard.java
 ┃ ┃ ┃ ┃ ┣ 📂 brick
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 Brick.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BrickFactory.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BrickGenerator.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GhostBrick.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 [I,J,L,O,S,T,Z]Brick.java (7 files)
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 RandomBrickGenerator.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SevenBagBrickGenerator.java
 ┃ ┃ ┃ ┃ ┣ 📂 mode
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 EndlessMode.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 EndlessModeLeaderboard.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameResult.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LeaderboardEntry.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelMode.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 PlayerStats.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 TwoPlayerMode.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 TwoPlayerModeMechanics.java
 ┃ ┃ ┃ ┃ ┣ 📂 savestate
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameStateCaretaker.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 GameStateMemento.java
 ┃ ┃ ┃ ┃ ┣ 📂 score
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 Score.java
 ┃ ┃ ┃ ┃ ┗ 📂 state
 ┃ ┃ ┃ ┃   ┣ 📜 GameOverState.java
 ┃ ┃ ┃ ┃   ┣ 📜 GameState.java
 ┃ ┃ ┃ ┃   ┣ 📜 GameStateContext.java
 ┃ ┃ ┃ ┃   ┣ 📜 PausedState.java
 ┃ ┃ ┃ ┃   ┗ 📜 PlayingState.java
 ┃ ┃ ┃ ┣ 📂 service
 ┃ ┃ ┃ ┃ ┣ 📂 audio
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SoundManager.java
 ┃ ┃ ┃ ┃ ┣ 📂 gameloop
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameService.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 GameServiceImpl.java
 ┃ ┃ ┃ ┃ ┣ 📂 session
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameSession.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SinglePlayerGameSession.java
 ┃ ┃ ┃ ┃ ┗ 📂 timeline
 ┃ ┃ ┃ ┃   ┗ 📜 GameTimelineManager.java
 ┃ ┃ ┃ ┣ 📂 util
 ┃ ┃ ┃ ┃ ┗ 📜 MatrixOperations.java
 ┃ ┃ ┃ ┣ 📂 view
 ┃ ┃ ┃ ┃ ┣ 📂 dialog
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 HelpDialogBuilder.java
 ┃ ┃ ┃ ┃ ┣ 📂 theme
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelTheme.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 AncientTempleTheme.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 FutureWarfareTheme.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 InterstellarTheme.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 MagicCastleTheme.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 SunsetCityTheme.java
 ┃ ┃ ┃ ┃ ┣ 📂 manager
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 AnimationController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 AudioVolumeManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 BrickRenderer.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 CommonUIManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 CountdownManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 DialogManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 EndlessModeUIManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameBoardRenderer.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameInputHandler.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 GameModeUIManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 HudManager.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜 LevelModeUIManager.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 TwoPlayerPanelManager.java
 ┃ ┃ ┃ ┃ ┗ 📂 panel
 ┃ ┃ ┃ ┃   ┣ 📜 GameOverPanel.java
 ┃ ┃ ┃ ┃   ┣ 📜 NotificationPanel.java
 ┃ ┃ ┃ ┃   ┗ 📜 TwoPlayerGameOverPanel.java
 ┃ ┃ ┃ ┗ 📜 Main.java
 ┃ ┃ ┗ 📂 resources
 ┃ ┃   ┣ 📂 audio (23 audio files)
 ┃ ┃   ┣ 📂 images/backgrounds (9 background images)
 ┃ ┃   ┣ 📜 digital.ttf
 ┃ ┃   ┣ 📜 endlessGameOver.fxml
 ┃ ┃   ┣ 📜 endlessGameOverStyle.css
 ┃ ┃   ┣ 📜 enhancedGameLayout.fxml
 ┃ ┃   ┣ 📜 enhancedGameStyle.css
 ┃ ┃   ┣ 📜 levelGameOver.fxml
 ┃ ┃   ┣ 📜 levelGameOverStyle.css
 ┃ ┃   ┣ 📜 levelSelection.fxml
 ┃ ┃   ┣ 📜 levelSelection.css
 ┃ ┃   ┣ 📜 mainMenu.fxml
 ┃ ┃   ┣ 📜 settings.fxml
 ┃ ┃   ┣ 📜 settings.css
 ┃ ┃   ┣ 📜 twoPlayerGameLayout.fxml
 ┃ ┃   ┗ 📜 twoPlayerGameStyle.css
 ┃ ┗ 📂 test/java/com/comp2042 (43 test files, 441 tests)
 ┣ 📜 pom.xml
 ┗ 📜 README.md
```

# Compilation Instructions

## Prerequisites

**1. JDK 21**
- Ensure Java Development Kit (JDK) 21 is installed.
- Set `JAVA_HOME` to the JDK 21 installation path.

Verify installation:
```bash
java --version
```

**2. IntelliJ IDEA**
- Download and install IntelliJ IDEA from [jetbrains.com/idea](https://www.jetbrains.com/idea/download/).

**3. Maven**
- Maven is bundled with IntelliJ IDEA by default.
- Alternatively, install from [Maven's official site](https://maven.apache.org/download.cgi).

**4. Git**
- Ensure Git is installed and configured.

Verify installation:
```bash
git --version
```

## Cloning and Importing

**1. Clone the Repository**
```bash
git clone https://github.com/Dongjia0601/CW2025.git
cd CW2025
```

**2. Open the Project in IntelliJ IDEA**
- Launch IntelliJ IDEA.
- Click `File > Open` and select the `pom.xml` file in the cloned project folder.
- IntelliJ will automatically import dependencies and configure the project.

**3. Set the JDK Version**
- Go to `File > Project Structure > Project`.
- Set the Project SDK to `JDK 21`.
- Ensure the Language Level is set to `21`.

**4. Build the Project**
- Open the Maven tool window (on the right side).
- Run the following lifecycle phases in order:
  - `clean`
  - `install`

## Running the Application

**Option 1: Using Maven Plugin (Recommended)**
1. Open the **Maven** tool window.
2. Navigate to **Plugins > javafx**.
3. Double-click **javafx:run** to execute the application.

**Option 2: Using Main Class**
1. Locate `src/main/java/com/comp2042/Main.java` in the Project view.
2. Right-click and select **Run 'Main'**.

**Note:** If you encounter "JavaFX runtime components are missing" error, use the Maven plugin method (`javafx:run`) for consistent execution.

**Running Tests**
```bash
mvn test
```
Or use the Maven tool window: **Lifecycle > test**

## Implemented and Working Properly

This section details the features that have been successfully implemented in both the frontend and backend of the project. The original codebase was a basic Tetris implementation with minimal features. The current version represents a complete professional-grade game with extensive enhancements.

### Frontend Features

#### **1. Main Menu System**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No main menu existed - game started directly.
    </td>
    <td>
      - Lack of centralized navigation limited user control and game mode selection.<br>
      - No way to access settings or view instructions before gameplay.
    </td>
    <td>
      - Professional main menu with animated background and neon-styled buttons.<br>
      - Options to select game modes (Endless, Level, Two-Player).<br>
      - Settings button for audio and gameplay configuration.<br>
      - Help/Instructions accessible from main menu.
    </td>
  </tr>
</table>

#### **2. Three Distinct Game Modes**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      Only basic single-player mode with no variations.
    </td>
    <td>
      - Limited replayability and player engagement.<br>
      - No structured progression or competitive elements.<br>
      - Single gameplay style becomes monotonous.
    </td>
    <td>
      <strong>Endless Mode:</strong>
      <ul>
        <li>Progressive difficulty scaling.</li>
        <li>Persistent leaderboard with top 5 scores.</li>
        <li>Real-time statistics (lines, level, time).</li>
        <li>Two randomizer algorithms (7-Bag/Pure Random).</li>
      </ul>
      <strong>Level Mode:</strong>
      <ul>
        <li>5 themed levels with unique visual themes (background images and color schemes).</li>
        <li>Background music selected by level ID for immersive audio experience.</li>
        <li>Time limits and score requirements per level.</li>
        <li>Star rating system (1-3 stars).</li>
        <li>Progressive level unlocking.</li>
        <li>Dynamic difficulty progression with balanced fall speeds (700ms to 280ms).</li>
        <li>Enhanced progress tracking with accurate level completion display.</li>
        <li>Level-specific timeline speed synchronization for smooth gameplay.</li>
      </ul>
      <strong>Two-Player Mode:</strong>
      <ul>
        <li>Split-screen competitive gameplay.</li>
        <li>Attack mechanism (send garbage lines).</li>
        <li>Independent controls for each player.</li>
        <li>Real-time countdown and statistics.</li>
      </ul>
    </td>
  </tr>
</table>

#### **3. Professional UI/UX Design**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      - Basic grid display with minimal styling.<br>
      - No visual feedback for actions.<br>
      - Poor color contrast and readability.
    </td>
    <td>
      - Unprofessional appearance affected user experience.<br>
      - Lack of visual feedback made gameplay feel unresponsive.<br>
      - Poor aesthetics reduced player engagement.
    </td>
    <td>
      - Unique UI design (main menu, settings, help, level selection, Game Over interface for each mode, etc.)<br>
      - Smooth animations for brick movements and rotations.<br>
      - Visual effects: screen shake, particle effects, attack animations, etc.<br>
      - Ghost brick display showing landing position.<br>
      - Color-coded brick types with distinct styling.<br>
      - Responsive layout adapting to different screen sizes.
    </td>
  </tr>
</table>

#### **4. Comprehensive Audio System**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No audio system - completely silent gameplay.
    </td>
    <td>
      - Lack of audio feedback reduced immersion.<br>
      - No auditory cues for important game events.<br>
    </td>
    <td>
      - 23 audio files including BGM and SFX.<br>
      - Three-tier volume control system in settings (Master, BGM, SFX).<br>
      - Unique background music for each game mode and level.<br>
      - Sound effects for: line clears(1-4), rotations, drops (Soft/Hard Drop), hold, click button, attacks, warnings, countdown, game over, win<br>
      - Keyboard shortcut (M) for mute toggle. (When use Mute (M) button, the Master volume in the settings interface adjusts to 0 accordingly; when Unmuted (M), the Master volume restores.)
<br>
      - Persistent audio settings saved to user directory. (1-100% for Master, BGM, SFX in settings)
    </td>
  </tr>
</table>

#### **5. Enhanced Controls and Features**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      - Basic movement (left, right, down).<br>
      - Only clockwise rotation.<br>
      - No hold function or hard drop.<br>
      - Limited keyboard mappings.
    </td>
    <td>
      - Limited controls restricted gameplay fluidity.<br>
      - Missing standard Tetris features (hold, hard drop).<br>
      - No counter-clockwise rotation hindered certain moves.
    </td>
    <td>
      <strong>Single Player Controls:</strong>
      <ul>
        <li>Movement: WASD</li>
        <li>Rotation: E (CW), Q (CCW) </li>
        <li>Hard Drop: Space</li>
        <li>Hold: Shift</li>
        <li>Soft Drop: S</li>
        <li>Pause: P</li>
        <li>Mute: M</li>
      </ul>
      <strong>Two-Player Controls:</strong>
      <ul>
        <li>Player 1: WASD + F for rotation(CCW) + Shift for hold + Space for hard drop</li>
        <li>Player 2: Arrow Keys + 2 for rotation(CCW) + 3 for hold + 0 for hard drop</li>
        <li><strong>Important:</strong> For Player 2, ensure Num Lock is enabled when using numpad keys (0, 2, 3).</li>
      </ul>
      <strong>Features:</strong>
      <ul>
        <li>Hold piece (swap current with held piece).</li>
        <li>Hard drop (instant drop to bottom).</li>
        <li>Soft drop (accelerated downward movement).</li>
        <li>Counter-clockwise rotation for optimal placement.</li>
        <li>Ghost brick showing landing position.</li>
      </ul>
    </td>
  </tr>
</table>

#### **6. Settings and Configuration**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No settings system
    </td>
    <td>
      - No user customization options.<br>
      - Unable to adjust audio levels.<br>
      - No way to change game difficulty or randomizer.<br>
      - Poor accessibility for different user preferences.
    </td>
    <td>
      - Comprehensive settings dialog accessible from main menu and in-game.<br>
      - Audio controls: Master, Music, SFX volume sliders.<br>
      - Gameplay settings: piece randomizer selection (7-Bag/Pure Random).<br>
      - Reset to defaults option.<br>
      - Real-time preview of changes.
    </td>
  </tr>
</table>

#### **7. Help and Instructions**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No instructions or help system
    </td>
    <td>
      - New players had no guidance on controls or objectives<br>
      - No explanation of game mechanics
    </td>
    <td>
      - Comprehensive help dialog with scrollable content.<br>
      - Control scheme documentation for all modes.<br>
      - Explanation of randomizer systems (7-Bag vs Pure Random).<br>
      - Game objectives and scoring system details.<br>
      - Accessible from main menu and during gameplay (Help button).
    </td>
  </tr>
</table>

#### **8. Game Over and Results Screens**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      - No game over screen.<br>
      - Game simply stopped without feedback.<br>
      - No way to restart or return to menu.
    </td>
    <td>
      - No performance summary or achievement recognition.<br>
      - Poor user experience at gameplay conclusion.
    </td>
    <td>
      <strong>Endless Mode Game Over:</strong>
      <ul>
        <li>Displays final score, lines cleared, level reached, time played.</li>
        <li>Enhanced with level display showing progression achievement.</li>
        <li>Shows leaderboard position if score qualifies.</li>
        <li>"New Record!" celebration for high scores.</li>
        <li>Options: Play Again, Main Menu, Reset the Leaderboard.</li>
      </ul>
      <strong>Level Mode Game Over:</strong>
      <ul>
        <li>Shows level completion status.</li>
        <li>Star rating based on performance (1-3 stars).</li>
        <li>Time bonus calculation.</li>
        <li>Next level unlock notification.</li>
        <li>Options: Retry Level, Next Level, Level Selection, Main Menu.</li>
      </ul>
      <strong>Two-Player Game Over:</strong>
      <ul>
        <li>Winner announcement with celebration effects (Player 1 wins, Player 2 wins, or Tie Game).</li>
        <li>Comprehensive statistics comparison displayed in a grid format:</li>
        <ul>
          <li><strong>Score:</strong> Final score for each player</li>
          <li><strong>Lines Cleared:</strong> Total lines eliminated by each player</li>
          <li><strong>Attack Lines:</strong> Total garbage lines sent to opponent</li>
          <li><strong>Defense:</strong> Total garbage lines received from opponent</li>
          <li><strong>Max Combo:</strong> Longest consecutive line-clearing streak (regardless of lines per clear)</li>
          <li><strong>Tetris:</strong> Total number of 4-line clears performed</li>
          <li><strong>Time:</strong> Total game duration (formatted as MM:SS)</li>
        </ul>
        <li>Color-coded display: Player 1 (cyan), Player 2 (red), Statistics headers (gold).</li>
        <li>Options: New Game (rematch), Back to Menu.</li>
      </ul>
    </td>
  </tr>
</table>

#### **9. Level Selection Interface**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No level selection - only continuous gameplay
    </td>
    <td>
      - No structured progression system.<br>
      - Unable to choose difficulty or theme.<br>
      - No sense of achievement or unlocking.
    </td>
    <td>
      - Beautiful level selection screen with themed cards.<br>
      - Each level shows: name, difficulty, theme preview, best score, time, star rating.<br>
      - Progressive unlocking (must complete previous level).<br>
      - Visual indicators for locked/unlocked/completed levels.<br>
      - Hover effects and smooth transitions.<br>
      - Back button to return to main menu.
    </td>
  </tr>
</table>

#### **10. Pause System**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      No pause functionality - game ran continuously.
    </td>
    <td>
      - Unable to pause during interruptions<br>
      - No break option during extended gameplay.<br>
      - Poor user control over game flow.
    </td>
    <td>
      - Pause with P key.<br>
      - Options: Resume, Restart, Settings, Main Menu.<br>
      - Game state frozen while paused, users  can make game 'settings' or read 'Help' instructure.
    </td>
  </tr>
</table>


### Backend Architecture

#### **1. MVC Architecture Pattern**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Monolithic structure with mixed concerns.</li>
        <li>GuiController handled both UI and game logic.</li>
        <li>No clear separation between layers.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Tight coupling made code difficult to maintain and test.</li>
        <li>Changes in one area rippled through entire codebase.</li>
        <li>Violated Single Responsibility Principle.</li>
        <li>Poor scalability for adding new features.</li>
      </ul>
    </td>
    <td>
      <strong>Model Layer (com.comp2042.model):</strong>
      <ul>
        <li>Board logic (<code>Board</code>, <code>SimpleBoard</code>)</li>
        <li>Brick implementations and factory</li>
        <li>Game modes (<code>EndlessMode</code>, <code>LevelMode</code>, <code>TwoPlayerMode</code>)</li>
        <li>Game states (State Pattern)</li>
        <li>Save states (Memento Pattern)</li>
        <li>Score and statistics management</li>
      </ul>
      <strong>View Layer (com.comp2042.view):</strong>
      <ul>
        <li>Rendering managers (<code>BrickRenderer</code>, <code>GameBoardRenderer</code>)</li>
        <li>UI managers (<code>HudManager</code>, <code>GameModeUIManager</code>)</li>
        <li>Animation controllers</li>
        <li>Dialog managers</li>
        <li>Panel components</li>
      </ul>
      <strong>Controller Layer (com.comp2042.controller):</strong>
      <ul>
        <li>Game controllers (<code>GameController</code>, <code>GuiController</code>)</li>
        <li>Menu controllers (<code>MainMenuController</code>, <code>SettingsController</code>)</li>
        <li>Factory controllers (<code>GameModeFactory</code>)</li>
        <li>Two-player specific controllers</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Clear separation of concerns.</li>
        <li>Each layer can be tested independently.</li>
        <li>Easy to modify UI without touching game logic.</li>
        <li>Scalable architecture for future enhancements.</li>
      </ul>
    </td>
  </tr>
</table>

#### **2. State Pattern for Game States**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Game state managed with boolean flags.</li>
        <li>Conditional logic scattered throughout code.</li>
        <li>No clear state transitions.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Boolean flags led to complex conditional logic.</li>
        <li>Difficult to add new states or modify behavior.</li>
        <li>State transitions were error-prone.</li>
        <li>Violated Open/Closed Principle.</li>
      </ul>
    </td>
    <td>
      <strong>GameState Interface</strong> with three concrete implementations:
      <br><br>
      <strong>PlayingState:</strong>
      <ul>
        <li>Handles all game input (move, rotate, drop, hold).</li>
        <li>Manages automatic brick descent.</li>
        <li>Processes line clears and scoring.</li>
        <li>Can transition to PausedState or GameOverState.</li>
      </ul>
      <strong>PausedState:</strong>
      <ul>
        <li>Ignores game input except pause toggle.</li>
        <li>Preserves game state while inactive.</li>
        <li>Can transition back to PlayingState.</li>
      </ul>
      <strong>GameOverState:</strong>
      <ul>
        <li>Displays results and statistics.</li>
        <li>Ignores game input.</li>
        <li>Allows restart or menu navigation.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Clean state-specific behavior encapsulation.</li>
        <li>Easy to add new states (e.g., TutorialState).</li>
        <li>Type-safe state transitions.</li>
        <li>Eliminates complex conditional logic.</li>
      </ul>
    </td>
  </tr>
</table>

#### **3. Factory Pattern for Object Creation**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Direct instantiation with <code>new</code> operators scattered everywhere.</li>
        <li>Hardcoded brick creation logic.</li>
        <li>No centralized object creation.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Tight coupling to concrete brick classes.</li>
        <li>Difficult to add new brick types.</li>
        <li>Code duplication in object creation.</li>
        <li>Violated Dependency Inversion Principle.</li>
      </ul>
    </td>
    <td>
      <strong>BrickFactory:</strong>
      <ul>
        <li>Centralizes brick creation logic.</li>
        <li>Supports type-safe brick creation by type string.</li>
        <li>Implements both pure random and 7-bag randomization.</li>
        <li>Uses reflection for flexible brick instantiation.</li>
      </ul>
      <strong>GameModeFactory:</strong>
      <ul>
        <li>Creates game mode instances (Endless, Level, Two-Player).</li>
        <li>Encapsulates mode-specific initialization.</li>
        <li>Supports polymorphic game mode handling.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Single point of change for object creation.</li>
        <li>Easy to add new brick types or game modes.</li>
        <li>Reduced coupling between client code and concrete classes.</li>
        <li>Supports different creation strategies (randomizers).</li>
      </ul>
    </td>
  </tr>
</table>

#### **4. Strategy Pattern for Randomization**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Single hardcoded randomization algorithm.</li>
        <li>No flexibility in brick generation.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Players couldn't choose preferred randomization.</li>
        <li>7-Bag algorithm (standard for competitive Tetris) not available.</li>
        <li>Testing different algorithms required code changes.</li>
      </ul>
    </td>
    <td>
      <strong>BrickGenerator Interface</strong> with two implementations:
      <br><br>
      <strong>RandomBrickGenerator:</strong>
      <ul>
        <li>Pure random selection (1/7 chance for each brick).</li>
        <li>Simple probability distribution.</li>
        <li>Classic Tetris behavior.</li>
      </ul>
      <strong>SevenBagBrickGenerator:</strong>
      <ul>
        <li>Guarantees all 7 pieces appear before repeating.</li>
        <li>Reduces long droughts of specific pieces.</li>
        <li>Preferred for competitive/modern Tetris.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Runtime algorithm switching via settings.</li>
        <li>Easy to implement new randomization strategies.</li>
        <li>Testable with deterministic generators.</li>
        <li>User choice improves player experience.</li>
      </ul>
    </td>
  </tr>
</table>

#### **4a. Strategy Pattern for UI Management**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>UI logic mixed with game controllers.</li>
        <li>Mode-specific UI behavior scattered throughout code.</li>
        <li>Difficult to add new game modes with custom UI.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Violated Open/Closed Principle - adding new modes required modifying existing code.</li>
        <li>UI logic tightly coupled to game mode implementation.</li>
        <li>No clear separation between common and mode-specific UI behavior.</li>
      </ul>
    </td>
    <td>
      <strong>GameModeUIStrategy Interface</strong> with three implementations:
      <br><br>
      <strong>EndlessModeUIStrategy:</strong>
      <ul>
        <li>Handles score tracking, level/speed progression, and elapsed time.</li>
        <li>Manages endless mode-specific statistics display.</li>
        <li>Updates best statistics panel with leaderboard data.</li>
      </ul>
      <strong>LevelModeUIStrategy:</strong>
      <ul>
        <li>Handles progress tracking, star rating, and level-specific displays.</li>
        <li>Manages level objectives panel and countdown timers.</li>
        <li>Updates progress indicators and speed display.</li>
      </ul>
      <strong>TwoPlayerModeUIStrategy:</strong>
      <ul>
        <li>Delegates to TwoPlayerPanelManager for split-screen UI.</li>
        <li>Manages dual-player statistics and attack indicators.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Clean separation of UI logic by game mode.</li>
        <li>Easy to add new game modes with custom UI behavior.</li>
        <li>Isolates mode-specific UI concerns from common UI management.</li>
        <li>Improves code maintainability and testability.</li>
      </ul>
    </td>
  </tr>
</table>

#### **5. Memento Pattern for Save States**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>No save/load functionality.</li>
        <li>Game state could not be persisted.</li>
        <li>No undo capability.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Players lost progress on application close.</li>
        <li>No way to implement save/load features.</li>
        <li>Difficult to implement undo/redo.</li>
        <li>Testing required full gameplay sessions.</li>
      </ul>
    </td>
    <td>
      <strong>GameStateMemento:</strong>
      <ul>
        <li>Captures complete game state snapshot.</li>
        <li>Stores: board state, current brick, score, level, time, etc.</li>
        <li>Immutable state representation.</li>
      </ul>
      <strong>GameStateCaretaker:</strong>
      <ul>
        <li>Manages memento lifecycle.</li>
        <li>Handles save/load operations.</li>
        <li>Supports multiple save slots.</li>
      </ul>
      <strong>BoardMementoAdapter:</strong>
      <ul>
        <li>Bridges board logic with memento storage.</li>
        <li>Handles state serialization.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Future-ready for save/load features.</li>
        <li>Enables undo/redo functionality.</li>
        <li>Facilitates testing with predefined states.</li>
        <li>Encapsulates state without exposing internals.</li>
      </ul>
    </td>
  </tr>
</table>

#### **6. Singleton Pattern for Configuration**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Settings passed as parameters throughout code.</li>
        <li>No centralized configuration management.</li>
        <li>Hardcoded values scattered across files.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Parameter passing created tight coupling.</li>
        <li>Inconsistent setting access patterns.</li>
        <li>Difficult to persist settings.</li>
        <li>No global point of configuration access.</li>
      </ul>
    </td>
    <td>
      <strong>GameSettings Singleton:</strong>
      <ul>
        <li>Thread-safe lazy initialization.</li>
        <li>Centralized configuration access point.</li>
        <li>Properties: volumes (master, music, SFX), difficulty, randomizer.</li>
        <li>Persistent storage in ~/.tetris/tetris_settings.properties.</li>
        <li>Save/load from file with error handling.</li>
        <li>Reset to defaults capability.</li>
      </ul>
      <strong>SoundManager Singleton:</strong>
      <ul>
        <li>Global audio playback control.</li>
        <li>Three-tier volume management.</li>
        <li>Context-aware audio switching.</li>
        <li>Resource cleanup on shutdown.</li>
      </ul>
      <strong>LevelManager Singleton:</strong>
      <ul>
        <li>Centralized level progression tracking.</li>
        <li>Persistent level unlock status.</li>
        <li>Theme management for all levels.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Single source of truth for configuration.</li>
        <li>Consistent access pattern throughout code.</li>
        <li>Thread-safe implementation.</li>
        <li>Easy testing with mock configurations.</li>
        <li>Persistent settings across sessions.</li>
      </ul>
    </td>
  </tr>
</table>

#### **7. Service Layer Pattern**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Business logic mixed with controllers.</li>
        <li>No abstraction for game operations.</li>
        <li>Direct coupling between UI and game logic.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Difficult to reuse business logic.</li>
        <li>Testing required UI framework.</li>
        <li>Violated Single Responsibility Principle.</li>
        <li>Poor separation between layers.</li>
      </ul>
    </td>
    <td>
      <strong>GameService Interface + GameServiceImpl:</strong>
      <ul>
        <li>Abstracts game lifecycle operations.</li>
        <li>Methods: startGame(), pauseGame(), resumeGame(), endGame().</li>
        <li>Manages game state transitions.</li>
        <li>Coordinates between model and controller layers.</li>
      </ul>
      <strong>SoundManager Service:</strong>
      <ul>
        <li>Abstracts audio operations.</li>
        <li>Methods: playBGM(), playSFX(), setVolume(), mute().</li>
        <li>Manages audio resource lifecycle.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Business logic separated from presentation.</li>
        <li>Reusable across different UI frameworks.</li>
        <li>Testable without UI dependencies.</li>
        <li>Clear API for game operations.</li>
        <li>Adheres to Dependency Inversion Principle.</li>
      </ul>
    </td>
  </tr>
</table>

#### **8. DTO Pattern for Data Transfer**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Direct exposure of internal model objects.</li>
        <li>No boundary between layers.</li>
        <li>Mutable objects passed between components.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Tight coupling between layers.</li>
        <li>Risk of unintended state modifications.</li>
        <li>Difficult to version or modify data structures.</li>
        <li>Poor encapsulation of internal state.</li>
      </ul>
    </td>
    <td>
      <strong>ViewData:</strong>
      <ul>
        <li>Encapsulates data for rendering current game state.</li>
        <li>Includes: board matrix, brick data, score, level, hold piece, next pieces.</li>
        <li>Immutable snapshot of game state.</li>
      </ul>
      <strong>DownData:</strong>
      <ul>
        <li>Transfers results of brick descent operation.</li>
        <li>Includes: updated view data, line clear info, game over status.</li>
      </ul>
      <strong>ClearRow:</strong>
      <ul>
        <li>Encapsulates line clear operation results.</li>
        <li>Includes: rows cleared count, score earned, combo status.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Decouples internal model from external representation.</li>
        <li>Immutable DTOs prevent accidental modifications.</li>
        <li>Clear contracts between layers.</li>
        <li>Easy to modify internal model without affecting views.</li>
        <li>Supports versioning and backward compatibility.</li>
      </ul>
    </td>
  </tr>
</table>

#### **9. Event-Driven Architecture**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Direct method calls for user input.</li>
        <li>Tight coupling between input and game logic.</li>
        <li>No event propagation system.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Input handling tightly coupled to game logic.</li>
        <li>Difficult to add input logging or replay.</li>
        <li>No way to queue or prioritize events.</li>
        <li>Poor testability of input handling.</li>
      </ul>
    </td>
    <td>
      <strong>MoveEvent:</strong>
      <ul>
        <li>Encapsulates user input events.</li>
        <li>Includes: event type, source, timestamp, metadata.</li>
        <li>Supports all input types: moves, rotations, drops, hold, pause.</li>
      </ul>
      <strong>EventType Enum:</strong>
      <ul>
        <li>Defines all possible event types.</li>
        <li>Type-safe event handling.</li>
      </ul>
      <strong>EventSource Enum:</strong>
      <ul>
        <li>Identifies event origin (keyboard, AI, replay, etc.).</li>
      </ul>
      <strong>Listener Interfaces:</strong>
      <ul>
        <li>InputEventListener: keyboard/mouse events.</li>
        <li>BrickMovementListener: brick state changes.</li>
        <li>GameControlListener: game state transitions.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Decoupled input handling from game logic.</li>
        <li>Easy to implement input recording/replay.</li>
        <li>Supports multiple input sources.</li>
        <li>Facilitates testing with synthetic events.</li>
        <li>Extensible for future event types.</li>
      </ul>
    </td>
  </tr>
</table>

#### **10. Modular Component Architecture**

<table style="width:100%">
  <tr>
    <th>Original Version</th>
    <th>Reason for Improvement</th>
    <th>New Implementation</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>Monolithic classes with multiple responsibilities.</li>
        <li>No reusable components.</li>
        <li>GuiController was 4000+ lines.</li>
      </ul>
    </td>
    <td>
      <ul>
        <li>Violated Single Responsibility Principle.</li>
        <li>Difficult to understand and maintain.</li>
        <li>Poor code reuse.</li>
        <li>Hard to test individual features.</li>
        <li>Merge conflicts in team development.</li>
      </ul>
    </td>
    <td>
      GuiController refactored into specialized managers:
      <br><br>
      <strong>BrickRenderer:</strong>
      <ul>
        <li>Handles all brick visual rendering.</li>
        <li>Manages colors, shapes, animations.</li>
      </ul>
      <strong>GameBoardRenderer:</strong>
      <ul>
        <li>Renders game board grid and cells.</li>
        <li>Handles ghost brick display.</li>
      </ul>
      <strong>HudManager:</strong>
      <ul>
        <li>Manages heads-up display elements.</li>
        <li>Shows score, level, lines, time.</li>
      </ul>
      <strong>GameInputHandler:</strong>
      <ul>
        <li>Processes keyboard input.</li>
        <li>Maps keys to game actions.</li>
      </ul>
      <strong>AnimationController:</strong>
      <ul>
        <li>Manages all animations and transitions.</li>
        <li>Handles particle effects, screen shake.</li>
      </ul>
      <strong>DialogManager:</strong>
      <ul>
        <li>Creates and manages modal dialogs.</li>
        <li>Handles help, settings, confirmations.</li>
      </ul>
      <strong>GameModeUIManager:</strong>
      <ul>
        <li>Mode-specific UI adaptations.</li>
        <li>Shows/hides relevant UI elements.</li>
      </ul>
      <strong>TwoPlayerPanelManager:</strong>
      <ul>
        <li>Manages two-player split-screen layout.</li>
        <li>Handles dual game boards and statistics.</li>
      </ul>
      <strong>Benefits:</strong>
      <ul>
        <li>Each manager has single, clear responsibility.</li>
        <li>Reusable components across different contexts.</li>
        <li>Easy to test individual managers.</li>
        <li>Maintainable codebase with logical organization.</li>
        <li>Facilitates parallel development.</li>
      </ul>
    </td>
  </tr>
</table>

## Implemented but Not Working Properly

*All implemented features are working properly.* There are no known bugs or incomplete features in the current implementation. The game has been thoroughly tested with 441 comprehensive unit tests covering core logic, design patterns, refactoring improvements, game modes, service layer, and UI components.

## Features Not Implemented

While the project has achieved comprehensive functionality, the following features were considered but not implemented due to time constraints and scope prioritization:

#### **1. Key Customization System in Settings**

<table style="width:100%">
  <tr>
    <th>Feature</th>
    <th>Reason for Not Implementing</th>
  </tr>
  <tr>
    <td>
      A comprehensive key binding customization system allowing players to remap all game controls (movement, rotation, drop, hold, pause) through the settings interface. Players would be able to assign their preferred keyboard keys for each action, with support for different control schemes per game mode.
    </td>
    <td>
      <ul>
        <li><strong>Time Constraints:</strong> Implementing a robust key binding UI with conflict detection, validation, and persistent storage would require significant development time.</li>
        <li><strong>Scope Priority:</strong> Focus was placed on core gameplay features and three complete game modes rather than advanced customization options.</li>
        <li><strong>Current Implementation:</strong> The game supports multiple predefined control schemes (WASD, Arrow Keys, Numpad) which cover most player preferences.</li>
        <li><strong>Architecture Ready:</strong> The event-driven input system with <code>GameInputHandler</code> provides the foundation for future key rebinding features.</li>
      </ul>
      This feature remains a valuable enhancement for future development to improve accessibility and player preference support.
    </td>
  </tr>
</table>

#### **2. Online Multiplayer**

<table style="width:100%">
  <tr>
    <th>Feature</th>
    <th>Reason for Not Implementing</th>
  </tr>
  <tr>
    <td>
      Network-based multiplayer allowing players to compete online rather than local split-screen only.
    </td>
    <td>
      <ul>
        <li><strong>Complexity:</strong> Network programming adds significant complexity requiring server infrastructure, netcode, synchronization, and latency handling.</li>
        <li><strong>Scope:</strong> Beyond the course requirements and timeline.</li>
        <li><strong>Foundation:</strong> Current two-player architecture could be adapted for online play in the future.</li>
      </ul>
    </td>
  </tr>
</table>

#### **3. AI Opponent**

<table style="width:100%">
  <tr>
    <th>Feature</th>
    <th>Reason for Not Implementing</th>
  </tr>
  <tr>
    <td>
      Computer-controlled AI opponent for single-player competitive mode.
    </td>
    <td>
      <ul>
        <li><strong>Complexity:</strong> Developing a competent Tetris AI requires sophisticated algorithms (e.g., heuristic evaluation, look-ahead, genetic algorithms).</li>
        <li><strong>Balancing:</strong> Creating fair and enjoyable difficulty levels is challenging.</li>
        <li><strong>Priority:</strong> Human vs Human gameplay was prioritized over AI development.</li>
      </ul>
      Note: The event-driven architecture supports AI input sources, providing groundwork for future AI implementation.
    </td>
  </tr>
</table>

#### **4. Replay System**

<table style="width:100%">
  <tr>
    <th>Feature</th>
    <th>Reason for Not Implementing</th>
  </tr>
  <tr>
    <td>
      Recording and playback of gameplay sessions for review or sharing.
    </td>
    <td>
      <ul>
        <li><strong>Time Constraints:</strong> Implementing replay recording, storage, and playback UI would be time-intensive.</li>
        <li><strong>Storage:</strong> Requires careful design of replay file format and compression.</li>
        <li><strong>Architecture Ready:</strong> The event system with EventSource.REPLAY supports future replay functionality.</li>
      </ul>
    </td>
  </tr>
</table>

#### **5. Mobile Platform Support**

<table style="width:100%">
  <tr>
    <th>Feature</th>
    <th>Reason for Not Implementing</th>
  </tr>
  <tr>
    <td>
      Touch-based controls and mobile platform deployment (Android/iOS).
    </td>
    <td>
      <ul>
        <li><strong>Platform Constraints:</strong> JavaFX mobile support is limited and requires significant adaptation.</li>
        <li><strong>Control Scheme:</strong> Touch controls would require complete UI/UX redesign.</li>
      </ul>
    </td>
  </tr>
</table>



## New Java Classes

This section lists all newly created Java classes in the refactored implementation. The project evolved from 20 original classes to **159 new classes**, representing a comprehensive architectural overhaul.

### Stats Summary
- **New Classes Added:** 159
- **Lines of Code Added:** 30,000+
- **Packages Created:** 24
- **Design Patterns Implemented:** 6 (State, Factory, Strategy, Memento, Singleton, MVC)
- **Test Classes:** 43 test files with 441 comprehensive unit tests (100% pass rate)

### Configuration Package (`com.comp2042.config`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameSettings</strong></td>
    <td>Singleton class managing all game configuration including audio volumes, difficulty settings, and piece randomizer selection. Persists settings to user directory (~/.tetris/tetris_settings.properties).</td>
    <td><code>src/main/java/com/comp2042/config/GameSettings.java</code></td>
  </tr>
</table>

### Controller Package (`com.comp2042.controller`)

#### Factory Sub-package (`controller.factory`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameMode</strong></td>
    <td>Abstract base interface for all game modes, defining common operations like initialization, update, render, and game over handling.</td>
    <td><code>src/main/java/com/comp2042/controller/factory/GameMode.java</code></td>
  </tr>
  <tr>
    <td><strong>GameModeFactory</strong></td>
    <td>Factory class implementing Factory Pattern for creating game mode instances (Endless, Level, Two-Player). Encapsulates mode-specific initialization logic.</td>
    <td><code>src/main/java/com/comp2042/controller/factory/GameModeFactory.java</code></td>
  </tr>
  <tr>
    <td><strong>GameModeType</strong></td>
    <td>Enum defining available game mode types with display names and descriptions for UI presentation.</td>
    <td><code>src/main/java/com/comp2042/controller/factory/GameModeType.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelGameModeImpl</strong></td>
    <td>Concrete implementation of Level Mode, managing level progression, time limits, and themed gameplay.</td>
    <td><code>src/main/java/com/comp2042/controller/factory/LevelGameModeImpl.java</code></td>
  </tr>
</table>

#### Game Sub-package (`controller.game`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameController</strong></td>
    <td>Core game controller managing game state, event handling, and business logic coordination. Implements State Pattern for game states (Playing, Paused, GameOver).</td>
    <td><code>src/main/java/com/comp2042/controller/game/GameController.java</code></td>
  </tr>
  <tr>
    <td><strong>GuiController</strong></td>
    <td>Main view controller coordinating all UI managers and renderers. Delegates to specialized managers: BrickRenderer, GameBoardRenderer, HudManager, AnimationController, etc. Implements MVC pattern's Controller layer.</td>
    <td><code>src/main/java/com/comp2042/controller/game/GuiController.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerGameController</strong></td>
    <td>Specialized controller for two-player competitive mode, managing dual game boards, player synchronization, and attack mechanics.</td>
    <td><code>src/main/java/com/comp2042/controller/game/TwoPlayerGameController.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerCountdownManager</strong></td>
    <td>Manages countdown timer and start sequence for two-player mode with synchronized visual and audio cues.</td>
    <td><code>src/main/java/com/comp2042/controller/game/twoplayer/TwoPlayerCountdownManager.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerTimelineScheduler</strong></td>
    <td>Schedules and coordinates timeline events for two-player gameplay including automatic descent and attack processing.</td>
    <td><code>src/main/java/com/comp2042/controller/game/twoplayer/TwoPlayerTimelineScheduler.java</code></td>
  </tr>
</table>

#### Strategy Sub-package (`controller.strategy`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameModeUIStrategy</strong></td>
    <td>Strategy interface for game mode-specific UI behavior. Enables different UI logic for Endless, Level, and Two-Player modes. Implements Strategy Pattern for UI management.</td>
    <td><code>src/main/java/com/comp2042/controller/strategy/GameModeUIStrategy.java</code></td>
  </tr>
  <tr>
    <td><strong>EndlessModeUIStrategy</strong></td>
    <td>Endless Mode UI strategy implementation handling score tracking, level/speed progression, and elapsed time display. Manages endless mode-specific UI updates.</td>
    <td><code>src/main/java/com/comp2042/controller/strategy/EndlessModeUIStrategy.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelModeUIStrategy</strong></td>
    <td>Level Mode UI strategy implementation handling progress tracking, star rating, and level-specific displays. Manages level objectives and countdown timers.</td>
    <td><code>src/main/java/com/comp2042/controller/strategy/LevelModeUIStrategy.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerModeUIStrategy</strong></td>
    <td>Two-Player Mode UI strategy implementation. Most logic delegated to TwoPlayerPanelManager for split-screen competitive mode UI management.</td>
    <td><code>src/main/java/com/comp2042/controller/strategy/TwoPlayerModeUIStrategy.java</code></td>
  </tr>
</table>

#### Menu Sub-package (`controller.menu`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>MainMenuController</strong></td>
    <td>Controls main menu UI including game mode selection, settings access, and help dialogs. Manages background music and button animations.</td>
    <td><code>src/main/java/com/comp2042/controller/menu/MainMenuController.java</code></td>
  </tr>
  <tr>
    <td><strong>SettingsController</strong></td>
    <td>Manages settings dialog with audio controls (Master, Music, SFX volumes), piece randomizer selection, and reset functionality. Persists changes via GameSettings.</td>
    <td><code>src/main/java/com/comp2042/controller/menu/SettingsController.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelSelectionController</strong></td>
    <td>Controls level selection screen displaying themed level cards with unlock status, difficulty, best scores, and star ratings.</td>
    <td><code>src/main/java/com/comp2042/controller/menu/LevelSelectionController.java</code></td>
  </tr>
  <tr>
    <td><strong>EndlessGameOverController</strong></td>
    <td>Manages Endless Mode game over screen showing final statistics (score, lines cleared, level reached, time played), leaderboard position, and replay options.</td>
    <td><code>src/main/java/com/comp2042/controller/menu/EndlessGameOverController.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelGameOverController</strong></td>
    <td>Manages Level Mode completion screen displaying star rating, time bonus, level unlock status, and next level navigation.</td>
    <td><code>src/main/java/com/comp2042/controller/menu/LevelGameOverController.java</code></td>
  </tr>
</table>

### DTO Package (`com.comp2042.dto`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>ViewData</strong></td>
    <td>Data Transfer Object encapsulating complete game state snapshot for rendering: board matrix, current brick, score, level, hold piece, next pieces queue. Immutable representation.</td>
    <td><code>src/main/java/com/comp2042/dto/ViewData.java</code></td>
  </tr>
  <tr>
    <td><strong>DownData</strong></td>
    <td>DTO transferring results of brick descent operation including updated ViewData, line clear information, and game over status.</td>
    <td><code>src/main/java/com/comp2042/dto/DownData.java</code></td>
  </tr>
  <tr>
    <td><strong>ClearRow</strong></td>
    <td>DTO encapsulating line clear operation results: number of rows cleared, score earned, combo multiplier, and cleared row indices.</td>
    <td><code>src/main/java/com/comp2042/dto/ClearRow.java</code></td>
  </tr>
</table>

### Event Package (`com.comp2042.event`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>MoveEvent</strong></td>
    <td>Event class encapsulating user input events with type, source, timestamp, and metadata. Supports all event types: movements, rotations, drops, hold, pause.</td>
    <td><code>src/main/java/com/comp2042/event/MoveEvent.java</code></td>
  </tr>
  <tr>
    <td><strong>EventType</strong></td>
    <td>Enum defining all possible event types: DOWN, LEFT, RIGHT, ROTATE_CW, ROTATE_CCW, HARD_DROP, SOFT_DROP, HOLD, PAUSE, NEW_GAME, ATTACK.</td>
    <td><code>src/main/java/com/comp2042/event/EventType.java</code></td>
  </tr>
  <tr>
    <td><strong>EventSource</strong></td>
    <td>Enum identifying event origin: KEYBOARD, GAMEPAD, AI, REPLAY, NETWORK. Supports future input sources.</td>
    <td><code>src/main/java/com/comp2042/event/EventSource.java</code></td>
  </tr>
  <tr>
    <td><strong>InputEventListener</strong></td>
    <td>Interface for components receiving input events. Defines method onInputEvent(MoveEvent) for event handling.</td>
    <td><code>src/main/java/com/comp2042/event/listener/InputEventListener.java</code></td>
  </tr>
  <tr>
    <td><strong>BrickMovementListener</strong></td>
    <td>Interface for observing brick state changes: onBrickMoved, onBrickRotated, onBrickLanded.</td>
    <td><code>src/main/java/com/comp2042/event/listener/BrickMovementListener.java</code></td>
  </tr>
  <tr>
    <td><strong>GameControlListener</strong></td>
    <td>Interface for observing game state transitions: onGameStarted, onGamePaused, onGameResumed, onGameOver.</td>
    <td><code>src/main/java/com/comp2042/event/listener/GameControlListener.java</code></td>
  </tr>
</table>

### Model Package (`com.comp2042.model`)

#### Board Sub-package (`model.board`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>Board</strong></td>
    <td>Interface defining board operations: brick manipulation, collision detection, line clearing, state queries.</td>
    <td><code>src/main/java/com/comp2042/model/board/Board.java</code></td>
  </tr>
  <tr>
    <td><strong>SimpleBoard</strong></td>
    <td>Concrete implementation of Board interface managing 10x20 game matrix, brick placement, collision detection, line clearing with scoring, and game over detection.</td>
    <td><code>src/main/java/com/comp2042/model/board/SimpleBoard.java</code></td>
  </tr>
  <tr>
    <td><strong>BrickRotator</strong></td>
    <td>Utility class handling brick rotation logic with wall kick system. Calculates valid rotation positions and kick offsets.</td>
    <td><code>src/main/java/com/comp2042/model/board/BrickRotator.java</code></td>
  </tr>
  <tr>
    <td><strong>HoldManager</strong></td>
    <td>Manages hold piece functionality allowing players to swap current brick with held brick. Enforces once-per-lock rule.</td>
    <td><code>src/main/java/com/comp2042/model/board/HoldManager.java</code></td>
  </tr>
  <tr>
    <td><strong>GarbageManager</strong></td>
    <td>Manages garbage line insertion for two-player attack mechanics. Handles queuing, insertion timing, and visual warnings.</td>
    <td><code>src/main/java/com/comp2042/model/board/GarbageManager.java</code></td>
  </tr>
  <tr>
    <td><strong>NextShapeInfo</strong></td>
    <td>Value object containing brick position and shape matrix for next piece preview queue.</td>
    <td><code>src/main/java/com/comp2042/model/board/NextShapeInfo.java</code></td>
  </tr>
  <tr>
    <td><strong>BoardMementoAdapter</strong></td>
    <td>Adapter implementing Memento Pattern for board state capture and restoration. Bridges Board interface with GameStateMemento.</td>
    <td><code>src/main/java/com/comp2042/model/board/BoardMementoAdapter.java</code></td>
  </tr>
</table>

#### Brick Sub-package (`model.brick`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>Brick</strong></td>
    <td>Abstract base class for all Tetris pieces defining common properties: shape matrix, color, rotation state, position.</td>
    <td><code>src/main/java/com/comp2042/model/brick/Brick.java</code></td>
  </tr>
  <tr>
    <td><strong>IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick</strong></td>
    <td>Seven concrete brick implementations representing standard Tetris pieces. Each defines unique shape matrix and rotation states.</td>
    <td><code>src/main/java/com/comp2042/model/brick/[I,J,L,O,S,T,Z]Brick.java</code></td>
  </tr>
  <tr>
    <td><strong>GhostBrick</strong></td>
    <td>Special brick type representing the ghost/shadow showing where current brick will land. Rendered semi-transparently.</td>
    <td><code>src/main/java/com/comp2042/model/brick/GhostBrick.java</code></td>
  </tr>
  <tr>
    <td><strong>BrickFactory</strong></td>
    <td>Factory class implementing Factory Pattern for brick creation. Supports type-safe creation by string identifier and multiple randomization strategies.</td>
    <td><code>src/main/java/com/comp2042/model/brick/BrickFactory.java</code></td>
  </tr>
  <tr>
    <td><strong>BrickGenerator</strong></td>
    <td>Interface defining Strategy Pattern for brick generation algorithms. Method: generateNext().</td>
    <td><code>src/main/java/com/comp2042/model/brick/BrickGenerator.java</code></td>
  </tr>
  <tr>
    <td><strong>RandomBrickGenerator</strong></td>
    <td>Pure random brick generation with uniform 1/7 probability for each piece type. Classic Tetris behavior.</td>
    <td><code>src/main/java/com/comp2042/model/brick/RandomBrickGenerator.java</code></td>
  </tr>
  <tr>
    <td><strong>SevenBagBrickGenerator</strong></td>
    <td>7-Bag randomization ensuring all seven pieces appear once before any repeat. Reduces droughts and floods. Modern competitive standard.</td>
    <td><code>src/main/java/com/comp2042/model/brick/SevenBagBrickGenerator.java</code></td>
  </tr>
</table>

#### Mode Sub-package (`model.mode`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>EndlessMode</strong></td>
    <td>Endless game mode implementation with progressive difficulty scaling, persistent leaderboard, and unlimited gameplay.</td>
    <td><code>src/main/java/com/comp2042/model/mode/EndlessMode.java</code></td>
  </tr>
  <tr>
    <td><strong>EndlessModeLeaderboard</strong></td>
    <td>Manages top 10 high scores for Endless Mode with persistent storage, score validation, and ranking logic.</td>
    <td><code>src/main/java/com/comp2042/model/mode/EndlessModeLeaderboard.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelMode</strong></td>
    <td>Level-based game mode with themed levels, time limits, score requirements, and star rating system.</td>
    <td><code>src/main/java/com/comp2042/model/mode/LevelMode.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelManager</strong></td>
    <td>Singleton managing level progression, unlock status, best scores, and star ratings across all levels. Uses LevelTheme from View layer for visual assets.</td>
    <td><code>src/main/java/com/comp2042/model/mode/LevelManager.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerMode</strong></td>
    <td>Competitive two-player mode with dual boards, attack mechanics, synchronized gameplay, and winner determination.</td>
    <td><code>src/main/java/com/comp2042/model/mode/TwoPlayerMode.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerModeMechanics</strong></td>
    <td>Encapsulates two-player specific game mechanics: garbage line generation, attack damage calculation, combo system.</td>
    <td><code>src/main/java/com/comp2042/model/mode/TwoPlayerModeMechanics.java</code></td>
  </tr>
  <tr>
    <td><strong>PlayerStats</strong></td>
    <td>Tracks individual player statistics: score, lines cleared, level, attacks sent/received, time played, accuracy.</td>
    <td><code>src/main/java/com/comp2042/model/mode/PlayerStats.java</code></td>
  </tr>
  <tr>
    <td><strong>GameResult</strong></td>
    <td>Value object encapsulating game completion results: final score, statistics, new record status, star rating.</td>
    <td><code>src/main/java/com/comp2042/model/mode/GameResult.java</code></td>
  </tr>
  <tr>
    <td><strong>LeaderboardEntry</strong></td>
    <td>Value object representing single leaderboard entry: rank, score, timestamp, player name, game duration.</td>
    <td><code>src/main/java/com/comp2042/model/mode/LeaderboardEntry.java</code></td>
  </tr>
</table>


#### SaveState Sub-package (`model.savestate`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameStateMemento</strong></td>
    <td>Memento Pattern implementation capturing complete game state snapshot for save/restore functionality. Immutable state representation.</td>
    <td><code>src/main/java/com/comp2042/model/savestate/GameStateMemento.java</code></td>
  </tr>
  <tr>
    <td><strong>GameStateCaretaker</strong></td>
    <td>Manages memento lifecycle including save/load operations, file I/O, and state validation.</td>
    <td><code>src/main/java/com/comp2042/model/savestate/GameStateCaretaker.java</code></td>
  </tr>
</table>

#### Score Sub-package (`model.score`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>Score</strong></td>
    <td>Manages game scoring logic with multipliers, combo bonuses, level scaling, and scoring formulas based on modern Tetris guidelines.</td>
    <td><code>src/main/java/com/comp2042/model/score/Score.java</code></td>
  </tr>
</table>

#### State Sub-package (`model.state`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameState</strong></td>
    <td>State Pattern interface defining methods for all game states: onDownEvent, onLeftEvent, onRightEvent, onRotateEvent, handlePauseRequest, handleNewGameRequest.</td>
    <td><code>src/main/java/com/comp2042/model/state/GameState.java</code></td>
  </tr>
  <tr>
    <td><strong>PlayingState</strong></td>
    <td>Active gameplay state handling all player input, automatic descent, line clearing, and scoring. Transitions to PausedState or GameOverState.</td>
    <td><code>src/main/java/com/comp2042/model/state/PlayingState.java</code></td>
  </tr>
  <tr>
    <td><strong>PausedState</strong></td>
    <td>Paused state ignoring game input except pause toggle. Preserves game state while inactive.</td>
    <td><code>src/main/java/com/comp2042/model/state/PausedState.java</code></td>
  </tr>
  <tr>
    <td><strong>GameOverState</strong></td>
    <td>Game over state displaying results and allowing restart or menu navigation. Ignores game input.</td>
    <td><code>src/main/java/com/comp2042/model/state/GameOverState.java</code></td>
  </tr>
  <tr>
    <td><strong>GameStateContext</strong></td>
    <td>Context class for State Pattern managing current state and delegating operations to active state instance.</td>
    <td><code>src/main/java/com/comp2042/model/state/GameStateContext.java</code></td>
  </tr>
</table>

### Service Package (`com.comp2042.service`)

#### GameLoop Sub-package (`service.gameloop`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameService</strong></td>
    <td>Service layer interface abstracting game lifecycle operations: startGame, pauseGame, resumeGame, endGame, updateGame.</td>
    <td><code>src/main/java/com/comp2042/service/gameloop/GameService.java</code></td>
  </tr>
  <tr>
    <td><strong>GameServiceImpl</strong></td>
    <td>Concrete implementation of GameService coordinating between model and controller layers. Implements business logic.</td>
    <td><code>src/main/java/com/comp2042/service/gameloop/GameServiceImpl.java</code></td>
  </tr>
</table>

#### Audio Sub-package (`service.audio`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>SoundManager</strong></td>
    <td>Singleton service managing all audio playback: BGM, SFX, volume control (three-tier), mute functionality, and resource lifecycle.</td>
    <td><code>src/main/java/com/comp2042/service/audio/SoundManager.java</code></td>
  </tr>
</table>

#### Timeline Sub-package (`service.timeline`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameTimelineManager</strong></td>
    <td>Centralized manager for all game-related timelines (game loop, elapsed time, level countdown). Provides unified pause/resume/stop operations. Supports dynamic timeline speed adjustments for level progression. Includes proper timeline cleanup and state management to prevent memory leaks. Enables centralized control over all timing aspects of the game.</td>
    <td><code>src/main/java/com/comp2042/service/timeline/GameTimelineManager.java</code></td>
  </tr>
</table>

#### Session Sub-package (`service.session`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameSession</strong></td>
    <td>Interface representing game session with lifecycle methods and state access.</td>
    <td><code>src/main/java/com/comp2042/service/session/GameSession.java</code></td>
  </tr>
  <tr>
    <td><strong>SinglePlayerGameSession</strong></td>
    <td>Implementation for single-player game sessions managing player state and progression.</td>
    <td><code>src/main/java/com/comp2042/service/session/SinglePlayerGameSession.java</code></td>
  </tr>
</table>

### Utility Package (`com.comp2042.util`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>MatrixOperations</strong></td>
    <td>Utility class with static methods for matrix operations: intersection detection, matrix merging, completed row detection, row clearing with gravity simulation.</td>
    <td><code>src/main/java/com/comp2042/util/MatrixOperations.java</code></td>
  </tr>
</table>

### View Package (`com.comp2042.view`)

#### Manager Sub-package (`view.manager`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>CommonUIManager</strong></td>
    <td>Manages UI components shared across all game modes. Provides access to common UI elements: root pane, game panel, brick panel, ghost panel, hold panel, next brick panel, and control buttons. Enables consistent UI access across different game modes.</td>
    <td><code>src/main/java/com/comp2042/view/manager/CommonUIManager.java</code></td>
  </tr>
  <tr>
    <td><strong>EndlessModeUIManager</strong></td>
    <td>Manages UI components specific to Endless Mode including statistics display, best stats panel, and game title. Provides access to endless mode-specific labels and containers for score, lines, level, speed, and time tracking.</td>
    <td><code>src/main/java/com/comp2042/view/manager/EndlessModeUIManager.java</code></td>
  </tr>
  <tr>
    <td><strong>LevelModeUIManager</strong></td>
    <td>Manages UI components specific to Level Mode including objectives panel, timer display, progress indicators, speed display, and star rating. Handles level-specific UI visibility and updates.</td>
    <td><code>src/main/java/com/comp2042/view/manager/LevelModeUIManager.java</code></td>
  </tr>
  <tr>
    <td><strong>BrickRenderer</strong></td>
    <td>Handles all brick visual rendering including color mapping, shape drawing, rotation animations, and ghost brick display.</td>
    <td><code>src/main/java/com/comp2042/view/manager/BrickRenderer.java</code></td>
  </tr>
  <tr>
    <td><strong>GameBoardRenderer</strong></td>
    <td>Renders game board grid, cell colors, borders, and background effects. Manages canvas drawing operations.</td>
    <td><code>src/main/java/com/comp2042/view/manager/GameBoardRenderer.java</code></td>
  </tr>
  <tr>
    <td><strong>HudManager</strong></td>
    <td>Manages heads-up display elements: score, level, lines cleared, time played, next pieces, hold piece. Updates in real-time. Centralizes all HUD update logic for better maintainability.</td>
    <td><code>src/main/java/com/comp2042/view/manager/HudManager.java</code></td>
  </tr>
  <tr>
    <td><strong>GameInputHandler</strong></td>
    <td>Processes keyboard input mapping keys to game actions. Supports multiple control schemes and rebinding.</td>
    <td><code>src/main/java/com/comp2042/view/manager/GameInputHandler.java</code></td>
  </tr>
  <tr>
    <td><strong>AnimationController</strong></td>
    <td>Manages all animations and visual effects: screen shake, particle systems, attack animations, fade transitions, brick drop effects.</td>
    <td><code>src/main/java/com/comp2042/view/manager/AnimationController.java</code></td>
  </tr>
  <tr>
    <td><strong>DialogManager</strong></td>
    <td>Creates and manages modal dialogs: help screens, settings dialogs, confirmation prompts, error messages.</td>
    <td><code>src/main/java/com/comp2042/view/manager/DialogManager.java</code></td>
  </tr>
  <tr>
    <td><strong>GameModeUIManager</strong></td>
    <td>Adapts UI layout for different game modes showing/hiding mode-specific elements (timer, progress bars, player stats). Controls display of mode-specific panels and applies theme customizations.</td>
    <td><code>src/main/java/com/comp2042/view/manager/GameModeUIManager.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerPanelManager</strong></td>
    <td>Manages split-screen layout for two-player mode including dual game boards, synchronized statistics, attack indicators.</td>
    <td><code>src/main/java/com/comp2042/view/manager/TwoPlayerPanelManager.java</code></td>
  </tr>
  <tr>
    <td><strong>AudioVolumeManager</strong></td>
    <td>UI component for volume control sliders with real-time preview and visual feedback.</td>
    <td><code>src/main/java/com/comp2042/view/manager/AudioVolumeManager.java</code></td>
  </tr>
  <tr>
    <td><strong>CountdownManager</strong></td>
    <td>Manages countdown sequences with visual and audio cues for game start/level transitions.</td>
    <td><code>src/main/java/com/comp2042/view/manager/CountdownManager.java</code></td>
  </tr>
</table>

#### Dialog Sub-package (`view.dialog`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>HelpDialogBuilder</strong></td>
    <td>Builder class for creating comprehensive help dialog UI. Includes sections for game modes, basic controls, randomizer explanation, scoring system, ghost brick, endless mode rules, and two-player rules. Features scrollable content with proper layout and styling. Provides callback interface for dialog lifecycle management.</td>
    <td><code>src/main/java/com/comp2042/view/dialog/HelpDialogBuilder.java</code></td>
  </tr>
</table>

#### Theme Sub-package (`view.theme`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>LevelTheme</strong></td>
    <td>Interface defining level theme contract for visual assets. Belongs to View layer as it only contains presentation-related data (background images, color schemes). Does not affect game logic or physics. Provides methods for theme name, background image, and color palette (primary, secondary, accent colors).</td>
    <td><code>src/main/java/com/comp2042/view/theme/LevelTheme.java</code></td>
  </tr>
  <tr>
    <td><strong>AncientTempleTheme</strong></td>
    <td>Level 1 theme: Mystical ruins with ancient ambiance. Features earth tones (gold, brown) and archaeological atmosphere. Provides background image and color scheme for Ancient Temple level.</td>
    <td><code>src/main/java/com/comp2042/view/theme/AncientTempleTheme.java</code></td>
  </tr>
  <tr>
    <td><strong>MagicCastleTheme</strong></td>
    <td>Level 2 theme: Fantasy medieval castle with magical elements. Features blue and purple color scheme with mystical atmosphere. Provides background image and color palette for Magic Castle level.</td>
    <td><code>src/main/java/com/comp2042/view/theme/MagicCastleTheme.java</code></td>
  </tr>
  <tr>
    <td><strong>SunsetCityTheme</strong></td>
    <td>Level 3 theme: Urban twilight with warm sunset colors. Features orange and red gradient palette inspired by cityscape at golden hour. Provides background image and color scheme for Sunset Village level.</td>
    <td><code>src/main/java/com/comp2042/view/theme/SunsetCityTheme.java</code></td>
  </tr>
  <tr>
    <td><strong>FutureWarfareTheme</strong></td>
    <td>Level 4 theme: Sci-fi battlefield with high-tech military aesthetics. Features cyan and teal color scheme with futuristic atmosphere. Provides background image and color palette for Future Warfare level.</td>
    <td><code>src/main/java/com/comp2042/view/theme/FutureWarfareTheme.java</code></td>
  </tr>
  <tr>
    <td><strong>InterstellarTheme</strong></td>
    <td>Level 5 theme: Deep space exploration with cosmic atmosphere. Features deep blue color scheme with space exploration theme. Provides background image and color palette for Interstellar Odyssey level.</td>
    <td><code>src/main/java/com/comp2042/view/theme/InterstellarTheme.java</code></td>
  </tr>
</table>

#### Panel Sub-package (`view.panel`)

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>GameOverPanel</strong></td>
    <td>UI panel displaying game over screen with final statistics, performance metrics, and navigation options.</td>
    <td><code>src/main/java/com/comp2042/view/panel/GameOverPanel.java</code></td>
  </tr>
  <tr>
    <td><strong>TwoPlayerGameOverPanel</strong></td>
    <td>Specialized game over panel for two-player mode showing winner announcement and comparative statistics.</td>
    <td><code>src/main/java/com/comp2042/view/panel/TwoPlayerGameOverPanel.java</code></td>
  </tr>
  <tr>
    <td><strong>NotificationPanel</strong></td>
    <td>Toast-style notification panel for non-intrusive user feedback: achievements unlocked, warnings, info messages.</td>
    <td><code>src/main/java/com/comp2042/view/panel/NotificationPanel.java</code></td>
  </tr>
</table>

### Main Class

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Description</th>
    <th>File Path</th>
  </tr>
  <tr>
    <td><strong>Main</strong></td>
    <td>Application entry point extending JavaFX Application. Initializes primary stage, loads main menu, configures window properties, and manages application lifecycle.</td>
    <td><code>src/main/java/com/comp2042/Main.java</code></td>
  </tr>
</table>

*Note: All classes include comprehensive JavaDoc documentation with class descriptions, method documentation, parameter explanations, and return value descriptions.*



## Modified Java Classes

The original codebase had minimal structure with only 4 files modified. These files underwent **complete architectural transformation** as part of the refactoring process.

### Summary Statistics
- **Files Modified:** 4 (from master branch)
- **Refactoring Impact:** Complete rewrite with MVC architecture
- **Lines Changed:** Majority of the 26,671 line additions involved refactoring these core components

<table style="width:100%">
  <tr>
    <th>Class Name</th>
    <th>Original Version</th>
    <th>Modified Version</th>
    <th>Reasons for Modification</th>
  </tr>
  <tr>
    <td><strong>Main.java</strong></td>
    <td>
      - Basic JavaFX initialization.<br>
      - Directly loaded game GUI.<br>
      - Minimal configuration.<br>
      - ~25 lines of code.
    </td>
    <td>
      - Professional application initialization.<br>
      - Loads main menu instead of direct game start.<br>
      - Configures window properties (title, icon, resizability).<br>
      - Centers window on screen.<br>
      - Proper exception handling.<br>
      - ~40 lines of code with better organization.
    </td>
    <td>
      <strong>Improved User Experience:</strong><br>
      - Main menu provides better entry point.<br>
      - Professional window setup.<br>
      <strong>Better Architecture:</strong><br>
      - Separation of initialization logic.<br>
      - Proper resource management.
    </td>
  </tr>
  <tr>
    <td><strong>pom.xml</strong></td>
    <td>
      - Basic Maven configuration.<br>
      - JavaFX controls and FXML dependencies only.<br>
      - No testing framework.<br>
      - No audio support.<br>
      - No Maven Surefire plugin.
    </td>
    <td>
      - Updated to JDK 21.<br>
      - Added JavaFX Media dependency for audio.<br>
      - Added JUnit 5 dependencies (jupiter-api, jupiter-engine).<br>
      - Added Maven Surefire plugin for test execution.<br>
      - Proper compiler configuration (source/target 21).<br>
      - JavaFX Maven plugin with proper main class configuration.
    </td>
    <td>
      <strong>Audio Support:</strong><br>
      - JavaFX Media enables comprehensive audio system.<br>
      <strong>Testing Infrastructure:</strong><br>
      - JUnit 5 for modern testing.<br>
      - Maven Surefire for automated test execution.<br>
      <strong>Build Improvements:</strong><br>
      - Proper JDK 21 configuration.<br>
      - Streamlined build process.
    </td>
  </tr>
  <tr>
    <td><strong>.idea/misc.xml</strong></td>
    <td>
      - Project SDK configured for older JDK version.
    </td>
    <td>
      - Updated to JDK 21.<br>
      - Proper project language level configuration.
    </td>
    <td>
      <strong>Consistency:</strong><br>
      - Aligns IDE settings with project requirements.<br>
      - Ensures all developers use same JDK version.
    </td>
  </tr>
  <tr>
    <td><strong>.idea/vcs.xml</strong></td>
    <td>
      - Basic Git VCS configuration.
    </td>
    <td>
      - Enhanced Git integration settings.<br>
      - Proper repository root mapping.
    </td>
    <td>
      <strong>Version Control:</strong><br>
      - Better IDE-Git integration.<br>
      - Proper project structure recognition.
    </td>
  </tr>
</table>

### Core Architecture Transformation

While only 4 files show as "modified" in git statistics, the reality is that **the entire codebase was refactored**. The original 20 classes in the `com.comp2042` package were:

1. **Relocated** into proper MVC package structure (`model`, `view`, `controller`)
2. **Refactored** with design patterns (State, Factory, Strategy, Memento, Singleton)
3. **Enhanced** with extensive new functionality
4. **Documented** with comprehensive JavaDocs
5. **Tested** with 441 comprehensive unit tests (100% pass rate)

#### Package Restructuring

**Original Structure:**
```
com.comp2042/
  ├── All 20 classes in root package
  └── logic/bricks/ (brick implementations)
```

**New Structure:**
```
com.comp2042/
  ├── config/          (1 class)
  ├── controller/      (16 classes in 3 sub-packages)
  ├── dto/             (3 classes)
  ├── event/           (6 classes)
  ├── model/           (86 classes in 6 sub-packages)
  ├── service/         (5 classes in 3 sub-packages)
  ├── util/            (1 class)
  ├── view/            (19 classes in 3 sub-packages)
  └── Main.java
```

#### Key Refactoring Examples

**MatrixOperations:**
- **Original:** Basic matrix operations with bugs in `intersect` and `merge` methods
- **Modified:** 
  - Fixed `ArrayIndexOutOfBoundsException` in coordinate mapping
  - Renamed `checkRemoving` → `clearCompletedRows` for clarity
  - Added comprehensive JavaDocs
  - Improved scoring algorithm
  - Extracted constants (e.g., `SCORE_BONUS_BASE`)

**SimpleBoard:**
- **Original:** Monolithic board class with mixed responsibilities
- **Modified:**
  - Implements `Board` interface for abstraction
  - Separated concerns into helper classes: `HoldManager`, `GarbageManager`
  - Added Memento support via `BoardMementoAdapter`
  - Enhanced collision detection
  - Improved line clearing logic with combo system
  - Comprehensive JavaDocs

**GameController:**
- **Original:** Basic controller with flag-based state management
- **Modified:**
  - Implements State Pattern with `GameState` interface
  - Clean state transitions (Playing → Paused → GameOver)
  - Separated business logic from UI
  - Event-driven architecture
  - Service layer integration

**GuiController → GameViewController:**
- **Original:** ~300 line monolithic class handling everything
- **Modified:**
  - Renamed to `GuiController` for clarity
  - Expanded to ~4000 lines with comprehensive features
  - **Then refactored** into 10+ specialized managers:
    - `BrickRenderer`
    - `GameBoardRenderer`
    - `HudManager`
    - `GameInputHandler`
    - `AnimationController`
    - `DialogManager`
    - `GameModeUIManager`
    - etc.
  - Each manager has single responsibility
  - Coordinator pattern for manager orchestration

**Brick Classes (I, J, L, O, S, T, Z):**
- **Original:** In `logic.bricks` package, basic implementations
- **Modified:**
  - Relocated to `model.brick` package
  - Enhanced with rotation states
  - Color coding improvements
  - Factory Pattern integration
  - Strategy Pattern for generation (7-Bag vs Pure Random)
  - Comprehensive JavaDocs

**Event System Classes:**
- **Original:** Basic event handling with minimal abstraction
- **Modified:**
  - Complete event-driven architecture
  - `MoveEvent` encapsulates all input
  - `EventType` enum for type safety
  - `EventSource` for origin tracking
  - Listener interfaces for Observer Pattern
  - Supports future features (replay, AI, network)

**DTO Classes (ViewData, DownData, ClearRow):**
- **Original:** Direct exposure of internal model
- **Modified:**
  - Immutable Data Transfer Objects
  - Clean layer separation
  - Comprehensive data encapsulation
  - Enhanced with additional fields (hold, next queue, combos)

**NotificationPanel:**
- **Original:** Simple game over display
- **Modified:**
  - Moved to `view.panel` package
  - Toast-style notifications
  - Auto-dismiss functionality
  - Multiple notification types
  - Animation effects

### Comparison: Before vs After

<table style="width:100%">
  <tr>
    <th>Aspect</th>
    <th>Original Codebase</th>
    <th>Refactored Codebase</th>
  </tr>
  <tr>
    <td><strong>Architecture</strong></td>
    <td>Monolithic structure</td>
    <td>MVC with clear layer separation</td>
  </tr>
  <tr>
    <td><strong>Design Patterns</strong></td>
    <td>None implemented</td>
    <td>6 patterns (State, Factory, Strategy, Memento, Singleton, MVC)</td>
  </tr>
  <tr>
    <td><strong>Package Organization</strong></td>
    <td>Flat structure (1-2 packages)</td>
    <td>24 packages with logical grouping</td>
  </tr>
  <tr>
    <td><strong>Class Count</strong></td>
    <td>20 classes</td>
    <td>166 classes (146 new + 20 refactored)</td>
  </tr>
  <tr>
    <td><strong>Code Documentation</strong></td>
    <td>Minimal or no JavaDocs</td>
    <td>100% JavaDoc coverage</td>
  </tr>
  <tr>
    <td><strong>Testing</strong></td>
    <td>No tests</td>
    <td>441 comprehensive unit tests (100% pass rate)</td>
  </tr>
  <tr>
    <td><strong>Game Modes</strong></td>
    <td>1 basic mode</td>
    <td>3 distinct modes with unique features</td>
  </tr>
  <tr>
    <td><strong>UI/UX</strong></td>
    <td>Basic grid display</td>
    <td>Professional themed UI with animations</td>
  </tr>
  <tr>
    <td><strong>Audio</strong></td>
    <td>None</td>
    <td>23 audio files with 3-tier volume control</td>
  </tr>
  <tr>
    <td><strong>Settings</strong></td>
    <td>Hardcoded values</td>
    <td>Persistent user configuration</td>
  </tr>
  <tr>
    <td><strong>Features</strong></td>
    <td>Basic Tetris</td>
    <td>Hold, Hard Drop, Ghost Brick, Themes, Leaderboards, etc.</td>
  </tr>
</table>

## Deleted Java Classes

The refactoring process involved relocating classes into proper package structures rather than true "deletion." The following 20 classes were removed from their original flat package structure and reorganized into the MVC architecture:

<table style="width:100%">
  <tr>
    <th>Deleted Class</th>
    <th>Original Location</th>
    <th>New Location/Replacement</th>
    <th>Reason for Change</th>
  </tr>
  <tr>
    <td><strong>Board.java</strong></td>
    <td><code>src/main/java/com/comp2042/Board.java</code></td>
    <td><code>src/main/java/com/comp2042/model/board/Board.java</code></td>
    <td>Relocated to model.board package for proper MVC organization. Transformed from class to interface for abstraction.</td>
  </tr>
  <tr>
    <td><strong>BrickRotator.java</strong></td>
    <td><code>src/main/java/com/comp2042/BrickRotator.java</code></td>
    <td><code>src/main/java/com/comp2042/model/board/BrickRotator.java</code></td>
    <td>Relocated to model.board package. Enhanced with wall kick system and SRS (Super Rotation System) implementation.</td>
  </tr>
  <tr>
    <td><strong>ClearRow.java</strong></td>
    <td><code>src/main/java/com/comp2042/ClearRow.java</code></td>
    <td><code>src/main/java/com/comp2042/dto/ClearRow.java</code></td>
    <td>Relocated to dto package as Data Transfer Object. Enhanced with combo system and additional metadata.</td>
  </tr>
  <tr>
    <td><strong>DownData.java</strong></td>
    <td><code>src/main/java/com/comp2042/DownData.java</code></td>
    <td><code>src/main/java/com/comp2042/dto/DownData.java</code></td>
    <td>Relocated to dto package. Expanded to include game over status and line clear information.</td>
  </tr>
  <tr>
    <td><strong>EventSource.java</strong></td>
    <td><code>src/main/java/com/comp2042/EventSource.java</code></td>
    <td><code>src/main/java/com/comp2042/event/EventSource.java</code></td>
    <td>Relocated to event package. Enhanced from simple enum to comprehensive event source tracking (keyboard, AI, replay, network).</td>
  </tr>
  <tr>
    <td><strong>EventType.java</strong></td>
    <td><code>src/main/java/com/comp2042/EventType.java</code></td>
    <td><code>src/main/java/com/comp2042/event/EventType.java</code></td>
    <td>Relocated to event package. Expanded to include all input types (hold, hard drop, attacks, etc.).</td>
  </tr>
  <tr>
    <td><strong>GameController.java</strong></td>
    <td><code>src/main/java/com/comp2042/GameController.java</code></td>
    <td><code>src/main/java/com/comp2042/controller/game/GameController.java</code></td>
    <td>Relocated to controller.game package. Complete rewrite with State Pattern implementation and service layer integration.</td>
  </tr>
  <tr>
    <td><strong>GameOverPanel.java</strong></td>
    <td><code>src/main/java/com/comp2042/GameOverPanel.java</code></td>
    <td><code>src/main/java/com/comp2042/view/panel/GameOverPanel.java</code></td>
    <td>Relocated to view.panel package. Enhanced with comprehensive statistics display and navigation options.</td>
  </tr>
  <tr>
    <td><strong>GuiController.java</strong></td>
    <td><code>src/main/java/com/comp2042/GuiController.java</code></td>
    <td><code>src/main/java/com/comp2042/controller/game/GuiController.java</code></td>
    <td>Renamed to GuiController and relocated. Complete architectural overhaul - refactored into 10+ specialized manager classes following Single Responsibility Principle.</td>
  </tr>
  <tr>
    <td><strong>InputEventListener.java</strong></td>
    <td><code>src/main/java/com/comp2042/InputEventListener.java</code></td>
    <td><code>src/main/java/com/comp2042/event/listener/InputEventListener.java</code></td>
    <td>Relocated to event.listener package. Enhanced as part of comprehensive event-driven architecture.</td>
  </tr>
  <tr>
    <td><strong>MatrixOperations.java</strong></td>
    <td><code>src/main/java/com/comp2042/MatrixOperations.java</code></td>
    <td><code>src/main/java/com/comp2042/util/MatrixOperations.java</code></td>
    <td>Relocated to util package. Bug fixes in intersect/merge methods. Renamed checkRemoving → clearCompletedRows. Enhanced scoring algorithm.</td>
  </tr>
  <tr>
    <td><strong>MoveEvent.java</strong></td>
    <td><code>src/main/java/com/comp2042/MoveEvent.java</code></td>
    <td><code>src/main/java/com/comp2042/event/MoveEvent.java</code></td>
    <td>Relocated to event package. Significantly enhanced with comprehensive event metadata and support for all input types.</td>
  </tr>
  <tr>
    <td><strong>NextShapeInfo.java</strong></td>
    <td><code>src/main/java/com/comp2042/NextShapeInfo.java</code></td>
    <td><code>src/main/java/com/comp2042/model/board/NextShapeInfo.java</code></td>
    <td>Relocated to model.board package. Enhanced to support multiple next pieces (preview queue).</td>
  </tr>
  <tr>
    <td><strong>NotificationPanel.java</strong></td>
    <td><code>src/main/java/com/comp2042/NotificationPanel.java</code></td>
    <td><code>src/main/java/com/comp2042/view/panel/NotificationPanel.java</code></td>
    <td>Relocated to view.panel package. Enhanced with toast-style notifications and auto-dismiss functionality.</td>
  </tr>
  <tr>
    <td><strong>Score.java</strong></td>
    <td><code>src/main/java/com/comp2042/Score.java</code></td>
    <td><code>src/main/java/com/comp2042/model/score/Score.java</code></td>
    <td>Relocated to model.score package. Enhanced with modern scoring algorithms, combo system, and level multipliers.</td>
  </tr>
  <tr>
    <td><strong>SimpleBoard.java</strong></td>
    <td><code>src/main/java/com/comp2042/SimpleBoard.java</code></td>
    <td><code>src/main/java/com/comp2042/model/board/SimpleBoard.java</code></td>
    <td>Relocated to model.board package. Extensive enhancements: Memento Pattern support, hold mechanism, garbage lines, improved collision detection.</td>
  </tr>
  <tr>
    <td><strong>ViewData.java</strong></td>
    <td><code>src/main/java/com/comp2042/ViewData.java</code></td>
    <td><code>src/main/java/com/comp2042/dto/ViewData.java</code></td>
    <td>Relocated to dto package. Enhanced with hold piece, next pieces queue, ghost brick data, and comprehensive game state information.</td>
  </tr>
  <tr>
    <td><strong>logic/bricks/Brick.java</strong></td>
    <td><code>src/main/java/com/comp2042/logic/bricks/Brick.java</code></td>
    <td><code>src/main/java/com/comp2042/model/brick/Brick.java</code></td>
    <td>Relocated to model.brick package. Enhanced with improved color system and rotation mechanics.</td>
  </tr>
  <tr>
    <td><strong>logic/bricks/BrickGenerator.java</strong></td>
    <td><code>src/main/java/com/comp2042/logic/bricks/BrickGenerator.java</code></td>
    <td><code>src/main/java/com/comp2042/model/brick/BrickGenerator.java</code></td>
    <td>Relocated to model.brick package. Transformed into Strategy Pattern interface with multiple implementations (Pure Random, 7-Bag).</td>
  </tr>
  <tr>
    <td><strong>All Brick Implementations<br>(I, J, L, O, S, T, Z)</strong></td>
    <td><code>src/main/java/com/comp2042/logic/bricks/[type]Brick.java</code></td>
    <td><code>src/main/java/com/comp2042/model/brick/[type]Brick.java</code></td>
    <td>Relocated to model.brick package. Enhanced with proper rotation states and Factory Pattern integration.</td>
  </tr>
</table>

### Resource File Deletions

<table style="width:100%">
  <tr>
    <th>Deleted Resource</th>
    <th>Reason for Deletion</th>
  </tr>
  <tr>
    <td><code>src/main/resources/gameLayout.fxml</code></td>
    <td>Replaced with multiple specialized FXML files: enhancedGameLayout.fxml, twoPlayerGameLayout.fxml, mainMenu.fxml, settings.fxml, levelSelection.fxml, etc. Better organization and maintainability.</td>
  </tr>
  <tr>
    <td><code>src/main/resources/window_style.css</code></td>
    <td>Replaced with multiple themed CSS files: enhancedGameStyle.css, twoPlayerGameStyle.css, levelSelection.css, settings.css, etc. Mode-specific styling for better visual consistency.</td>
  </tr>
</table>

**Note:** All "deletions" were actually relocations and enhancements. No functionality was lost - only improved upon with proper architecture, design patterns, and extensive feature additions.

## Unexpected Problems

### **1. Tight Coupling and Monolithic Architecture**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      The original codebase had severe tight coupling with the monolithic `GuiController` class handling everything: rendering, input, game logic, UI updates, and state management. This violated multiple SOLID principles and made the code extremely difficult to understand, test, and extend.
    </td>
    <td>
      <strong>Comprehensive Refactoring:</strong>
      <ul>
        <li>Implemented MVC architecture with clear layer separation.</li>
        <li>Refactored GuiController (300 lines) → GuiController (4000 lines) → 10+ specialized managers.</li>
        <li>Each manager follows Single Responsibility Principle:
          <ul>
            <li>BrickRenderer: visual rendering only</li>
            <li>GameInputHandler: input processing only</li>
            <li>HudManager: HUD display only</li>
            <li>AnimationController: animations only</li>
          </ul>
        </li>
        <li>Introduced interfaces for abstraction (Board, GameState, BrickGenerator).</li>
        <li>Implemented Dependency Injection for manager coordination.</li>
      </ul>
      <strong>Result:</strong> Clean, testable, maintainable architecture with clear responsibilities. Each component can be developed, tested, and modified independently.
    </td>
  </tr>
</table>

### **2. Critical Bugs in Core Game Logic**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      <strong>Matrix Index Out of Bounds:</strong>
      <ul>
        <li>The original <code>MatrixOperations.intersect()</code> and <code>merge()</code> methods had critical bugs in coordinate mapping causing <code>ArrayIndexOutOfBoundsException</code> during gameplay.</li>
        <li>The row/column indexing was inconsistent between methods.</li>
      </ul>
      <strong>Incorrect Spawn Position:</strong>
      <ul>
        <li>Bricks spawned at row index 1 instead of 0, causing visual glitches and incorrect collision detection from the start.</li>
      </ul>
    </td>
    <td>
      <strong>Bug Fixes:</strong>
      <ul>
        <li>Thoroughly analyzed and fixed coordinate mapping in <code>MatrixOperations</code>.</li>
        <li>Corrected row/column index calculations ensuring consistency.</li>
        <li>Fixed brick spawn position to row 0 with proper bounds checking.</li>
        <li>Added comprehensive unit tests (441 tests total) covering:
          <ul>
            <li>Matrix operations with edge cases</li>
            <li>Brick placement at boundaries</li>
            <li>Collision detection accuracy</li>
            <li>Line clearing logic</li>
          </ul>
        </li>
        <li>Added defensive programming with validation checks.</li>
      </ul>
      <strong>Result:</strong> Stable, bug-free core game loop with comprehensive test coverage ensuring no regression.
    </td>
  </tr>
</table>

### **3. State Management Complexity**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      Managing game states (playing, paused, game over) with boolean flags led to complex nested conditional logic scattered throughout the codebase. State transitions were error-prone, and adding new states would require modifying multiple files. Example issues:
      <ul>
        <li>Input handling during pause required checking multiple flags</li>
        <li>Transition from Playing → GameOver when brick couldn't spawn wasn't clean</li>
        <li>No clear state lifecycle management</li>
      </ul>
    </td>
    <td>
      <strong>State Pattern Implementation:</strong>
      <ul>
        <li>Created GameState interface with three implementations:
          <ul>
            <li>PlayingState: handles all game input and logic</li>
            <li>PausedState: ignores game input, preserves state</li>
            <li>GameOverState: displays results, allows restart</li>
          </ul>
        </li>
        <li>Each state encapsulates its specific behavior.</li>
        <li>Clean state transitions through GameStateContext.</li>
        <li>Type-safe state handling eliminates flag checking.</li>
        <li>Easy to add new states (e.g., TutorialState) without modifying existing code.</li>
      </ul>
      <strong>Challenges Overcome:</strong>
      <ul>
        <li>Careful coordination between state transitions and UI updates.</li>
        <li>Ensuring automatic brick descent stops immediately on pause.</li>
        <li>Handling rapid state transitions (pause → unpause → game over).</li>
        <li>Tested all transition paths with dedicated unit tests.</li>
      </ul>
      <strong>Result:</strong> Clean, extensible state management adhering to Open/Closed Principle. Adding new states requires only creating new state classes without modifying existing ones.
    </td>
  </tr>
</table>

### **4. JavaFX Thread Safety and Timeline Management**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      JavaFX requires all UI updates to occur on the JavaFX Application Thread. The game loop (Timeline/AnimationTimer) runs on a separate thread, causing:
      <ul>
        <li>Concurrent modification exceptions when updating UI from game thread</li>
        <li>Timeline coordination issues in two-player mode (two separate game loops)</li>
        <li>Audio playback conflicts when playing multiple sounds simultaneously</li>
        <li>Memory leaks from unclosed MediaPlayer instances</li>
      </ul>
    </td>
    <td>
      <strong>Thread Safety Implementation:</strong>
      <ul>
        <li>Used Platform.runLater() for all UI updates from game thread.</li>
        <li>Synchronized critical sections in shared game state.</li>
        <li>Created TwoPlayerTimelineScheduler for coordinated dual timelines.</li>
        <li>Implemented proper Timeline lifecycle management (stop, cleanup).</li>
      </ul>
      <strong>Audio System Management:</strong>
      <ul>
        <li>SoundManager singleton with thread-safe audio playback.</li>
        <li>Audio resource pooling to prevent MediaPlayer leaks.</li>
        <li>Proper cleanup in shutdown hooks.</li>
        <li>Synchronized volume control updates.</li>
      </ul>
      <strong>Learning Curve:</strong>
      <ul>
        <li>Spent significant time understanding JavaFX threading model.</li>
        <li>Debugged race conditions with strategic logging.</li>
        <li>Used JavaFX profiler to identify memory leaks.</li>
      </ul>
      <strong>Result:</strong> Stable multi-threaded application with no threading issues or memory leaks. Smooth 60 FPS gameplay even in two-player mode.
    </td>
  </tr>
</table>

### **5. JUnit Testing Challenges**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      <strong>JUnit Testing Complexity:</strong>
      <ul>
        <li>JavaFX components require Platform.startup() before instantiation</li>
        <li>UI tests need JavaFX Application Thread</li>
        <li>Difficult to test view components without full UI initialization</li>
      </ul>
      <strong>Tightly Coupled Code:</strong>
      <ul>
        <li>Original architecture made unit testing nearly impossible</li>
        <li>No dependency injection meant mocking was difficult</li>
        <li>Singleton pattern instances couldn't be reset between tests</li>
      </ul>
    </td>
    <td>
      <strong>Testing Strategy:</strong>
      <ul>
        <li>Focused on testing business logic (model layer) independently of UI.</li>
        <li>Used TestFX for JavaFX-specific UI tests where necessary.</li>
        <li>Implemented dependency injection allowing mock objects in tests.</li>
        <li>Created test utilities for common setup (board initialization, brick creation).</li>
      </ul>
      <strong>Test Coverage:</strong>
      <ul>
        <li><strong>441 comprehensive unit tests</strong> covering:
          <ul>
            <li>Core game logic (MatrixOperations, SimpleBoard, Board components): 30+ tests</li>
            <li>Design patterns (State, Factory, Strategy, Memento, Singleton): 20+ tests</li>
            <li>Brick implementations and factory: 23 tests</li>
            <li>DTO and value objects: 35 tests</li>
            <li>Game modes (Endless, Level, Two-Player): 100+ tests
              <ul>
                <li>EndlessModeTest: 18 tests</li>
                <li>LevelGameModeImplTest: 29 tests</li>
                <li>TwoPlayerModeTest: 24 tests</li>
                <li>TwoPlayerModeMechanicsTest: 14 tests</li>
                <li>LevelManagerTest: 22 tests</li>
                <li>EndlessModeLeaderboardTest: 15 tests</li>
                <li>PlayerStatsTest: 19 tests</li>
              </ul>
            </li>
            <li>Service layer (GameService, GameSession, Timeline): 50+ tests
              <ul>
                <li>SinglePlayerGameSessionTest: 22 tests</li>
                <li>GameTimelineManagerTest: 20 tests</li>
                <li>TwoPlayerCountdownManagerTest: 7 tests</li>
                <li>TwoPlayerTimelineSchedulerTest: 16 tests</li>
              </ul>
            </li>
            <li>Configuration and settings: 18 tests (GameSettingsTest)</li>
            <li>UI components and managers: 60+ tests</li>
            <li>Controller and strategy patterns: 30+ tests</li>
          </ul>
        </li>
        <li><strong>All tests passing:</strong> 441/441 (100% pass rate)</li>
        <li><strong>Test quality improvements:</strong>
          <ul>
            <li>Removed unprofessional comments and workaround explanations</li>
            <li>Improved async Timeline testing with Platform.runLater() and CountDownLatch</li>
            <li>Enhanced game over detection in all game modes</li>
            <li>Comprehensive coverage of state transitions and edge cases</li>
          </ul>
        </li>
      </ul>
      <strong>Learned Lessons:</strong>
      <ul>
        <li>Design for testability from the start (interfaces, DI).</li>
        <li>Separate concerns to enable isolated testing.</li>
        <li>Accept that some UI code is integration-tested manually.</li>
      </ul>
      <strong>Result:</strong> Robust test suite providing confidence in refactoring and new feature additions. Core game logic has 95%+ test coverage.
    </td>
  </tr>
</table>

### **6. Design Pattern Integration Challenges**

<table style="width:100%">
  <tr>
    <th>Problem</th>
    <th>How It Was Addressed</th>
  </tr>
  <tr>
    <td>
      Integrating multiple design patterns into an existing codebase proved more complex than implementing patterns in greenfield projects:
      <ul>
        <li>Patterns needed to work together cohesively</li>
        <li>Retrofit patterns into existing code without breaking functionality</li>
        <li>Avoid over-engineering with unnecessary pattern usage</li>
        <li>Balance pattern purity with practical implementation needs</li>
      </ul>
      Example: Singleton pattern for GameSettings conflicted with testability requirements.
    </td>
    <td>
      <strong>Pattern Integration Strategy:</strong>
      <br><br>
      <strong>State + Factory + Strategy:</strong>
      <ul>
        <li>Factory creates game modes</li>
        <li>Each mode uses Strategy for brick generation</li>
        <li>State Pattern manages gameplay states</li>
        <li>Clean interfaces between patterns</li>
      </ul>
      <strong>Memento + MVC:</strong>
      <ul>
        <li>Memento captures model state</li>
        <li>Controller coordinates save/restore</li>
        <li>View updates from restored state</li>
      </ul>
      <strong>Singleton Testability:</strong>
      <ul>
        <li>Maintained Singleton for production code</li>
        <li>Added reset() method for test scenarios</li>
        <li>Used getInstance() consistently</li>
        <li>Documented testing considerations</li>
      </ul>
      <strong>Pattern Selection Criteria:</strong>
      <ul>
        <li>Does it solve a real problem in the codebase?</li>
        <li>Does it improve maintainability and extensibility?</li>
        <li>Is it the simplest solution that works?</li>
      </ul>
      <strong>Result:</strong> Six well-integrated design patterns that genuinely improve code quality without unnecessary complexity. Each pattern serves a clear purpose and demonstrates best practices.
    </td>
  </tr>
</table>

---

## Summary

This Tetris project represents a **complete professional-grade refactoring** of a basic game implementation, achieving:

-**Architecture:** Clean MVC with 24 packages and 166 well-organized classes<br>
-**Design Patterns:** 6 patterns (State, Factory, Strategy, Memento, Singleton, MVC)<br>
-**Features:** 3 complete game modes with unique mechanics and themed UI<br>
-**Quality:** 441 comprehensive unit tests (100% pass rate), 100% JavaDoc coverage, comprehensive error handling<br>
-**User Experience:** Professional UI/UX with 23 audio files and smooth animations<br>
-**Documentation:** Detailed README, in-code comments, and clear commit history<br>

**Total Impact:** 193 files changed, 30,000+ lines added

**Recent Architecture Improvements:**
- Refactored Theme classes from Model layer (`model/mode/themes`) to View layer (`view/theme`) following MVC principles
- Theme classes now only contain visual presentation data (images, colors), properly belonging to View layer
- Removed unused methods from Theme interface (getBackgroundMusic, getSoundEffects, getGradientColors, getTextColor, getThemeId)
- Cleaned up code by removing 6 unused methods, keeping only actively used visual asset methods
- Improved separation of concerns: Model layer no longer contains presentation-related code

The project demonstrates mastery of software maintenance, design patterns, testing, documentation, and modern development practices while delivering a polished, enjoyable gaming experience.
