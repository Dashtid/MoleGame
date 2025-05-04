# MoleGame

MoleGame is a simple and fun Java-based game where you control a mole digging through soil to reach a goal while avoiding obstacles and collecting power-ups. The game becomes progressively more challenging as you advance through levels.

---

## Features

* **Dynamic Levels** : Each level increases in difficulty with a larger grid, more obstacles, and a shorter time limit.
* **Obstacles** : Avoid red blocks that block your path.
* **Power-Ups** : Collect yellow blocks to gain a speed boost.
* **Goal** : Reach the green block to complete the level.
* **Score System** : Earn points for completing levels.

---

## How to Play

1. **Start the Game** : Run the program to begin.
2. **Control the Mole** :

* Use the following keys to move the mole:
  * **`W`** : Move up.
  * **`A`** : Move left.
  * **`S`** : Move down.
  * **`D`** : Move right.
* The mole leaves a tunnel behind as it moves.

1. **Objective** :

* Reach the green goal block to complete the level.
* Avoid red obstacle blocks.
* Collect yellow power-ups for a speed boost.

1. **Progression** :

* Each level increases in difficulty with more obstacles and a shorter time limit.
* The game ends if you fail to complete a level or run out of time.

---

## Requirements

* **Java Development Kit (JDK)** : Version 8 or higher.



## Installation

1. Clone the repository or download the project files.
2. Ensure the following files are in the same directory:
   * [Mole.java](vscode-file://vscode-app/c:/Users/david.dashti/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html)
   * [Graphics.java](vscode-file://vscode-app/c:/Users/david.dashti/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html)
   * [ColorConstants.java](vscode-file://vscode-app/c:/Users/david.dashti/AppData/Local/Programs/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.html)

---

## Running the Game

1. Open a terminal or command prompt.
2. Navigate to the directory containing the project files.
3. Compile the project:

   ```
   javac Mole.java Graphics.java ColorConstants.java
   ```
4. Run the game:

   ```
   java Mole
   ```

## Project Structure

**MoleGame/**

```
├── Mole.java           # Main game logic**
├── Graphics.java       # Handles rendering and **user input**
├── ColorConstants.java # Defines color constants **for the game
└── README.md           # Project documentation
```

---

## Gameplay Example

* **Level 1** :
* Grid size: 30x50
* Obstacles: 50
* Power-ups: 5
* Time limit: 60 seconds
* **Level 2** :
* Grid size: 35x55
* Obstacles: 60
* Power-ups: 5
* Time limit: 55 seconds

---

## Future Improvements

* Add more types of power-ups (e.g., obstacle destroyer, time extender).
* Introduce enemies that chase the mole.
* Add a leaderboard to track high scores.
* Implement a pause menu.
