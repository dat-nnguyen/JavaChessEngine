# ♞Java Medieval Chess Engine

A fully functional, object-oriented Chess Engine built from scratch using Java 17 and JavaFX. This project features a robust MVC architecture, a custom Minimax AI opponent, and a polished medieval pixel-art interface

<img width="900" height="694" alt="Image" src="https://github.com/user-attachments/assets/4ab80409-433f-4b13-8c00-faf4374d3396" />
<img width="897" height="699" alt="Image" src="https://github.com/user-attachments/assets/e9a8aa5d-dce5-4651-b152-faf00fa7f040" />
<img width="897" height="698" alt="Image" src="https://github.com/user-attachments/assets/dc1e461f-e2bb-47d4-9c72-19b7d2403d68" />
<img width="896" height="705" alt="Image" src="https://github.com/user-attachments/assets/58942dce-39c9-45d4-87e6-45e23b5ce981" />

## 🚀 Features

### **Core Engine**
* **Complete Chess Rules:** Handles complex moves including **Castling** (King/Queen side), **En Passant**, and **Pawn Promotion**.
* **Move Validation:** Validates legal moves for all pieces, preventing illegal actions (e.g., moving a pinned piece, moving into check).
* **Game States:** Detects **Check**, **Checkmate**, and **Stalemate** conditions accurately.

### **Artificial Intelligence**
* **Minimax Algorithm:** Implements a recursive tree-search algorithm to determine the best move.
* **Heuristic Evaluation:** Scores board states based on material weight, mobility, and positional safety.
* **Concurrency:** Runs AI calculations on a background thread (`JavaFX Task`) to prevent UI freezing during deep searches.

### **User Interface (GUI)**
* **JavaFX Implementation:** Built with a clean, responsive UI using `StackPane`, `BorderPane`, and `GridPane` layouts.
* **Asset Management:** Features a "Medieval Pixel Art" theme with video backgrounds (`.mp4`) and custom sound effects.
* **Quality of Life:**
    * **Visual Feedback:** Highlights legal moves and selected pieces.
    * **Game Timer:** Functional countdown clock with "Flag Fall" logic.
    * **Menus:** Interactive Main Menu, Game Setup (Mode/Difficulty/Time), and Pause/Game Over screens.

---

## 🛠 Technology Stack

* **Language:** Java 17
* **Framework:** JavaFX 22.0.2 (Controls, FXML, Media, Graphics)
* **Build Tool:** Maven
* **Testing:** JUnit 5, Mockito
* **Design Patterns:**
    * **MVC:** Separates `Board` (Model), `BoardPanel` (View), and `GameEngine` (Controller).
    * **Strategy Pattern:** Used for AI Move selection (`MoveStrategy`).
    * **Factory Pattern:** Used for creating Moves (`MoveFactory`).
    * **Builder Pattern:** Used for constructing immutable `Board` states.
    * **Singleton/Static Utilities:** Used for `SoundManager` and `ImageCache`.

---

## ⚙️ Installation & Setup

### Prerequisites
* **Java JDK 17** or higher.
* **Maven** installed.

### Steps
1.  **Clone the repository**
    ```bash
    git clone [https://github.com/dat-nnguyen/java-chess-engine.git](https://github.com/dat-nnguyen/java-chess-engine.git)
    cd java-chess-engine
    ```

2.  **Build the project** (Downloads dependencies)
    ```bash
    mvn clean install
    ```

3.  **Run the Application**
    ```bash
    mvn javafx:run
    ```

---

## 🕹 Controls

* **Left Click:** Select a piece / Move a piece.
* **Esc / Menu Button:** Pause the game and open the in-game menu.
* **Drag Game Over Screen:** You can click and drag the victory popup to view the final board state underneath.

---

## 🏗 Architecture Overview

The project follows strict **Object-Oriented Principles**:

* **`entities` Package:** Contains the core logic.
    * `Board`: Immutable state container representing the 64 squares.
    * `Piece`: Abstract class with subclasses (`Rook`, `Pawn`, etc.) defining movement vectors.
    * `Move`: Represents a state transition. Subclasses handle special logic (`CastleMove`, `PawnJump`).
* **`gui` Package:** Handles the presentation layer.
    * `GameEngine`: The central controller managing game loop, input handling, and AI triggers.
    * `BoardPanel` & `SquarePanel`: Renders the grid and handles click events.
* **`core.ai` Package:**
    * `MiniMax`: The decision-making algorithm.
    * `StandardBoardEvaluator`: The scoring system for board states.

---

## 🧪 Testing

The project includes a JUnit test suite covering critical game mechanics.

To run tests:
```bash
mvn test

