# Project 3 — 2048 (Group 51)

A console version of 2048 built with MVC. The model (`GameModel` + `TileDeque`) handles the board and rules, the view (`ConsoleView`) prints the board and reads input, and the controller (`GameController`, started by `GameLauncher`) connects them. Each row or column is processed through a deque so the tiles closest to the direction of movement are handled first.

## How to run

All files are in the repo root. The tests use `student.jar` from CS2-Support.

### Run the model tests (no view needed)

In Eclipse: right-click `TileDequeTest.java` or `GameModelTest.java` → **Run As → JUnit Test**.

From a terminal (replace the path to `student.jar` with yours):

```bash
javac -cp path/to/student.jar *.java
java -cp .:path/to/student.jar org.junit.runner.JUnitCore TileDequeTest GameModelTest
```

The tests only use `GameModel` and `TileDeque`. They never create a view, controller, or Scanner.

### Run the thin view

`ConsoleViewDemo` prints hard-coded boards, a score, an invalid-input message, the win and game-over messages, and then reads one command and echoes it back. It doesn't need the model or controller.

In Eclipse: right-click `ConsoleViewDemo.java` → **Run As → Java Application**.

From a terminal:

```bash
javac ConsoleView.java ConsoleViewDemo.java
java ConsoleViewDemo
```

The view tests run the same way as the model tests: `ConsoleViewTest`.

### Run the game

Run `GameLauncher` (Eclipse: **Run As → Java Application**, or `java GameLauncher` from a terminal). Enter W, A, S, or D and press Enter to move.

> The screen clears after each move in macOS Terminal, Linux and Windows Terminal. The Eclipse console ignores the clear code, so there each new board prints below the old one.

<!-- TODO before submitting: check that GameLauncher runs once GameModel and GameController are in. -->

## Reflection

### What changed from the spec

| Area | What the spec said | What we built | Why |
|---|---|---|---|
| TileDeque signatures | `addTile`, `removeTile`, `removeLastTile`, `mergeTiles(boolean)`, `getTiles`, `clear`, `peekTile`, `isEmpty` | All of those, plus `getMergeScore()` and `size()` | `mergeTiles` returns nothing, and merging doesn't change the sum of the tiles, so `GameModel` had no way to know how many points a move earned. `getMergeScore()` returns the points from the last merge. `size()` is used to check a line fits before writing it back. |
| Row/column helper | Revised Scope #3: one helper for rows and columns | `loadLine`, `writeLine`, and `processLine` in `TileDeque` | Rows always load left to right and columns top to bottom. A/W merge and write from the front; D/S merge and write from the back. One method covers all four directions, so `GameModel.moveTiles` is just a loop calling `processLine` for each line. We put it in `TileDeque` instead of `GameModel` so it could be built and tested before `GameModel` existed. |
| No double merge | Revised Scope #4: a `justMerged` flag | The merge loop removes both tiles of a pair as soon as they merge | Same result ([2,2,4] → [4,4]) without a flag, because a merged tile is never looked at again. |
| Invalid directions | Method Signatures: `moveTiles("P")` returns false. Test Plan: it throws `IllegalArgumentException`. | Throws `IllegalArgumentException` | The two sections disagreed. We followed the Test Plan, and the controller only passes valid directions anyway. |
| ConsoleView | `displayBoard`, `displayGameOver`, `displayWin`, `getInput`, `displayMessage` | All of those, plus `clearScreen()` (Revised Scope #1) and `displayScore(int)` | The class design says the view shows the score, but no method in the spec did that. |
| Input validation | Validation section: a separate `InputHandler` reads input. Method Signatures: `ConsoleView.getInput()` reads input. | <!-- TODO (Matthew): which one did the controller end up using? --> | <!-- TODO --> |
| Tests | Test Plan: JUnit tests for `GameModel` and `TileDeque` | Every Test Plan case, plus extra tests for the row/column helper and all four directions, and `ConsoleViewTest` for the view | The helper is shared by all four directions, so it needed its own tests. The view tests use hard-coded boards, as the integration plan says. |

### Did the model really stand alone?

Yes. `TileDeque` and its 36 tests were written and passing before any view or controller code existed. `GameModelTest` only uses `GameModel(int[][] startBoard, Random rng)` with a seeded `Random`, so it never needs input or a screen. `ConsoleView` was also built and tested on its own with hard-coded boards (`ConsoleViewDemo`, `ConsoleViewTest`). Neither side imports the other.

### What happened to the unknowns from the scope

- **How to load a column into the deque.** Figured out: columns load top to bottom like rows load left to right, and the direction only decides which end we merge and write from.
- **The [2,2,4,_] → [8,_,_,_] double-merge bug.** Figured out: each tile merges at most once. Tested in both `TileDequeTest` and `GameModelTest`.
- **Should a move that changes nothing spawn a tile?** Decided: no. `moveTiles` returns false and no tile spawns. Tested in `GameModelTest.testMoveTilesNoChange`.
- **Clearing the screen.** Partly open: ANSI codes work in real terminals but not in the Eclipse console.
- <!-- TODO: any other unknowns from the scope doc? -->

### What doesn't work yet

<!-- TODO: update this right before submitting. -->
- `GameModel` (Rohan) is not finished, so `GameModelTest` and `GameLauncher` don't compile yet.
- `GameController` (Matthew) is still an empty class, so the full game can't be played yet.

## Next Steps

### What's left to finish the MVP, in order

| # | Task | Owner |
|---|---|---|
| 1 | Write `GameModel`: constructors, `initializeBoard`, `moveTiles` (loop over `tileDeque.processLine`), `generateTile`, `isGameOver`, `isWon`, `getBoard`, `getScore`. Get `GameModelTest` passing. | Rohan |
| 2 | Write `GameController`: `startGame` loop, `processMove` (validate W/A/S/D, show an error otherwise), `handleMove` (call `moveTiles`, redraw only if it returned true). Stop on win, game over, or `getInput()` returning null. | Matthew |
| 3 | Decide between `InputHandler` and `ConsoleView.getInput()` and remove the other. | Matthew |
| 4 | Join everything in `GameLauncher` and play one full game together. | Matthew (all four present) |
| 5 | Fix bugs found while playing. Each bug goes to the owner of the class. | Everyone |

### One non-trivial feature beyond the MVP: undo

Pressing U puts the board and score back to how they were before the last move.

- **Classes it will touch:**
  - `GameModel`: add `undo()` and a history of past boards and scores; save the state at the start of `moveTiles` only when the move changes the board.
  - `GameController`: accept U in `processMove` and call `model.undo()`.
  - `InputHandler`/`ConsoleView`: allow U, and update the prompt text.
- **Classes that should not change:** `TileDeque` (moves still work the same way), and `ConsoleView.displayBoard` (it just prints whatever board it gets).

### Stretch goals

| Goal | Status | Why |
|---|---|---|
| Score display | **Committed** | Mostly done already: the model tracks score and the view has `displayScore`. |
| Undo | **Committed** | Reuses the existing state and a small history. Planned above. |
| Endless mode (keep playing after 2048) | **Committed if time allows** | Small: the controller asks whether to continue instead of stopping on a win. |
| Quit / new game | **Committed if time allows** | Moved out of the MVP in the Revised Scope. `initializeBoard()` already resets the game. |
| High score file (`ScoreFileHandler`) | **Dropped** | Needs file I/O and error handling for little gameplay value. |
| Replay | **Dropped** | Cut in the Revised Scope: needs every move saved plus a playback screen. |

### New tests needed

- `GameModelTest`:
  - `undo()` restores the previous board and score.
  - `undo()` with no history does nothing.
  - A move that changes nothing doesn't add to the history.
- `GameModelTest`: `moveTiles` keeps working after `isWon()` is true (endless mode).
- Controller tests with a fake model: invalid input shows an error and doesn't call `moveTiles`; U calls `undo()`.

## Work Attribution

| File | Team member(s) | Contribution (how) | GenAI note |
|---|---|---|---|
| `TileDeque.java` | Arrush | Wrote it, including the row/column helper (`loadLine`, `writeLine`, `processLine`) | Written with Claude (Claude Code) from our spec, then reviewed and tested |
| `TileDequeTest.java` | Arrush | Wrote the tests from the Test Plan plus the helper tests | Written with Claude (Claude Code) from our Test Plan |
| `GameModelTest.java` | Arrush | Wrote the tests from the Test Plan | Written with Claude (Claude Code) from our Test Plan; checked against a temporary GameModel written to the spec |
| `GameModel.java` | Rohan | Wrote the class refrencing the test plan | got assistance from GPT in making the class from our spec. |
| `ConsoleView.java` | Wyatt | <!-- TODO: e.g. reviewed and revised --> | Written with Claude (Claude Code) from our spec |
| `ConsoleViewTest.java` | Wyatt | <!-- TODO --> | Written with Claude (Claude Code) |
| `ConsoleViewDemo.java` | Wyatt | <!-- TODO --> | Written with Claude (Claude Code) |
| `GameController.java` | Rohan | Wrote the class refrencing the test plan | got assistance from GPT in makign the class from our spec |
| `GameLauncher.java` | Matthew | Wrote GameLauncher class based on spec from deliverable 2 | No GenAI used|
| `InputHandler.java` | Matthew | Wrote InputHandler class  | No GenAI used |
| `README.md` | Arrush | Drafted it; teammates fill in their own rows | Drafted with Claude (Claude Code), then edited |
