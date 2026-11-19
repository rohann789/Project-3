# Project 3 — 2048 (Group 51)

A console version of 2048 built with MVC. The model (`GameModel` + `TileDeque`) handles the board and rules, the view (`ConsoleView`) prints the board and reads input, and the controller (`GameController`, started by `GameLauncher`) connects them. Each row or column is processed through a deque so the tiles closest to the direction of movement are handled first.

**Controls:** W, A, S, D move the tiles (any capitalization). **U undoes your last move.** Press Enter after each command.

## How to compile and run

All files are in the repo root. The tests need `student.jar` from CS2-Support; the game itself doesn't.

### Play the game

In Eclipse: right-click `GameLauncher.java` → **Run As → Java Application**.

From a terminal, in the repo folder:

```bash
javac GameLauncher.java GameController.java GameModel.java TileDeque.java ConsoleView.java
```

```bash
java GameLauncher
```

> The screen clears after each move in macOS Terminal, Linux and Windows Terminal. The Eclipse console ignores the clear code, so there each new board prints below the old one. Use a real terminal for the demo.

### Run the tests

In Eclipse: right-click any `*Test.java` file → **Run As → JUnit Test**.

From a terminal (replace `path/to/student.jar` with yours):

```bash
javac -cp path/to/student.jar *.java
```

```bash
java -cp .:path/to/student.jar org.junit.runner.JUnitCore TileDequeTest GameModelTest GameControllerTest ConsoleViewTest InputHandlerTest
```

| Test class | Tests | What it covers |
|---|---|---|
| `TileDequeTest` | 39 | Every deque method (normal + bad input), merging from both ends, interleaved adds and removes, and the row/column helper in all four directions |
| `GameModelTest` | 30 | Every Test Plan case, plus 8 undo tests |
| `GameControllerTest` | 26 | Input validation, moves, the game loop, and 5 undo tests, using a fake model and fake view |
| `ConsoleViewTest` | 10 | Board printing and messages, using hard-coded boards |
| `InputHandlerTest` | 7 | `InputHandler` input checking |

`TileDequeTest` and `GameModelTest` test the model only: they never create a view, controller, or Scanner. All 112 tests pass.

### Run the view on its own

`ConsoleViewDemo` prints hard-coded boards and every message without the model or controller: right-click it → **Run As → Java Application**, or `java ConsoleViewDemo` after compiling.

## Reflection

### The change feature: undo

Our D3 Next Steps named **undo** as the non-trivial feature beyond the MVP: pressing U puts the board and score back to how they were before the last move. It's in, and you can undo several moves in a row.

**How it works.** Before each move, `GameModel.moveTiles` already copied the board to check whether anything moved. Now, when the move does change the board, that copy and the old score are pushed onto two stacks (`boardHistory` and `scoreHistory`). `undo()` pops them back. Moves that change nothing, and invalid directions, are never saved, so U never "undoes" a move that didn't happen. Starting a new game clears the history.

### Classes the feature touched, compared with our D3 prediction

| Class | D3 prediction | What actually happened |
|---|---|---|
| `GameModel` | Add `undo()` and a history; save state only when a move changes the board | **Matched.** Added `undo()`, `canUndo()`, and two history stacks. `moveTiles` saves the board copy it was already making, and `initializeBoard` clears the history. |
| `GameController` | Accept U in `processMove` and call `model.undo()` | **Matched.** Added a U case and `handleUndo()`, which redraws on success or says "Nothing to undo yet." |
| `InputHandler` / `ConsoleView` | Allow U and update the prompt | **Partly matched.** Only the prompt text in `ConsoleView.getInput()` changed. `InputHandler` didn't need to change, because the controller reads input through `ConsoleView` (see below). |
| `TileDeque` | Should not change | **Matched.** No changes. Undo doesn't touch how a move is processed. |
| `ConsoleView.displayBoard` | Should not change | **Matched.** It still just prints whatever board it's given. |
| `GameControllerTest` | Not predicted | **Surprise.** The invalid-input message now mentions U, and the tests compared against the old text word for word. We moved both messages into constants (`GameController.INVALID_MESSAGE`, `NOTHING_TO_UNDO_MESSAGE`) so the tests use the same text as the controller and won't break the next time it changes. |

### Did the change cross the model/view line?

Barely, and only through the controller. The controller learned one new model method, `undo()`. The view learned nothing about the model: `ConsoleView` still keeps no copy of the board or score and only prints what the controller hands it. The one view change was the prompt text. The ripple stayed inside the controller's tests, as described above.

### What changed from the D3 plan, and why

- **Undo history uses two stacks, not our `TileDeque`.** The Revised Scope said undo "reuses our deque." `TileDeque` only holds tile values (powers of 2) for one line, so it can't hold whole boards or scores. A plain `ArrayDeque` used as a stack was simpler and kept `TileDeque` unchanged.
- **Input goes through `ConsoleView.getInput()`, not `InputHandler`.** The D3 spec had both. The controller ended up validating input itself (`processMove`), which made it easy to add U in one place. `InputHandler` and its tests still exist but aren't used by the game. See Next Steps.
- **Fixed `GameControllerTest`.** It had a stray `public` on the first line and an extra `{ }` at the end, so the project didn't compile. All 26 of its tests pass now.
- **Added interleaved deque tests.** `TileDequeTest` now mixes adds with removes from both ends and checks the order after every step, including after a merge.
- **More stretch goals than planned.** We have more time than expected, so endless mode and quit/new game moved from "if time allows" to committed.

### What doesn't work yet

- **Screen clearing in Eclipse.** It works in a real terminal; the Eclipse console ignores the clear code.
- **No way to keep playing after 2048** (endless mode) or to quit/restart mid-game. Both are planned below.
- **Undo can't be used after the game ends**, because the game loop stops on a win or loss. Endless mode and quit/new game will change the end-of-game flow, so we'll revisit this then.
- **`InputHandler` is unused**, and its `getInput()` would crash if the input ended, because it doesn't check for the end of the input.

### A design change that would make the next feature cheaper

Undo needed two parallel stacks because the board and score live in separate fields. If `GameModel` kept its state in one small `GameState` object (board + score), undo would be a single stack of states. Endless mode and quit/new game, our next two features, would also get cheaper: "new game" becomes "replace the state," and saving or restoring a game becomes one object instead of several fields. Second, the controller's `if`/`else` chain in `processMove` would be easier to grow as a small map from command letter to action. Quit (Q) and new game (N) would each be one new entry instead of another branch.

## Next Steps

### What's left for the final build, in order

| # | Task | Owner |
|---|---|---|
| 1 | **Endless mode.** On a win, ask "Keep playing? (Y/N)" instead of ending. Touches `GameController.startGame` (the loop condition) and adds a `ConsoleView` prompt. `GameModel` doesn't change. | Matthew |
| 2 | **Quit / new game.** Q ends the game; N calls `model.initializeBoard()` and redraws. Touches `GameController.processMove` and the prompt text. | Rohan |
| 3 | **Allow undo after a loss.** Once the game-over flow from #1 and #2 is in, offer "Undo, new game, or quit?" when the board is stuck. | Arrush |
| 4 | **Clean up input.** Remove `InputHandler` and `InputHandlerTest`, since the controller does all validation, or wire it in. Decide as a group. | Matthew |
| 5 | **Score display polish.** Show the points gained on each move (e.g. "Score: 120 (+8)"). | Wyatt |
| 6 | **Tests for the new features.** Controller tests with the fake model for Y/N after a win, Q, and N; model test that `moveTiles` keeps working after `isWon()`. Each owner writes tests for their own feature. | Each owner |
| 7 | **Final run-through.** Play a full game in a real terminal, check the README, and rehearse the demo. | Everyone |

### Stretch goals

| Goal | Status | Why |
|---|---|---|
| Score display | **Done** | The model tracks the score and the view shows it after every move. |
| Undo | **Done** | Built this deliverable. |
| Endless mode | **Committed** | Small: a controller change only. |
| Quit / new game | **Committed** | Small: `initializeBoard()` already resets everything, including the undo history. |
| High score file (`ScoreFileHandler`) | **Dropped** | Needs file I/O and error handling for little gameplay value. |
| Replay | **Dropped** | Needs every move saved plus a separate playback screen. Undo covers the useful part. |

### The 3-minute demo

Run `GameLauncher` in a real terminal (not Eclipse) so the screen clears.

| Time | What we show |
|---|---|
| 0:00–0:30 | Start the game. Explain the controls and the MVC split in one sentence each. |
| 0:30–1:00 | Bad input: `p`, an empty line, and `wa` each show an error and don't change the board. A move that changes nothing doesn't add a tile. |
| 1:00–1:45 | Normal play: a few moves with merges, with the score going up. |
| 1:45–2:25 | **Undo (new feature):** U before any move says "Nothing to undo yet." Then make a merge, press U, and the board, score and new tile all go back. Press U twice to step back two moves. |
| 2:25–3:00 | Run the JUnit tests (112 passing) and show the undo tests, including the one that undoes a winning merge, since reaching 2048 live takes too long. |

## Work Attribution

| File | Team member(s) | Contribution (how) | GenAI note |
|---|---|---|---|
| `TileDeque.java` | Arrush | Wrote it, including the row/column helper (`loadLine`, `writeLine`, `processLine`) | Written with Claude (Claude Code) from our spec, then reviewed and tested |
| `TileDequeTest.java` | Arrush | Wrote the Test Plan tests, the helper tests, and (D4) the interleaved add/remove tests | Written with Claude (Claude Code) from our Test Plan |
| `GameModel.java` | Rohan; Arrush (undo) | Rohan wrote the class from the test plan. Arrush added `undo()`, `canUndo()` and the undo history for D4. | Rohan: got help from ChatGPT making the class from our spec. Arrush: undo written with Claude (Claude Code). |
| `GameModelTest.java` | Arrush | Wrote the Test Plan tests and (D4) the undo tests | Written with Claude (Claude Code); first checked against a temporary GameModel written to the spec, now run against Rohan's GameModel |
| `GameController.java` | Rohan; Arrush (undo) | Rohan wrote the class from the test plan. Arrush added the U command, `handleUndo()`, and the message constants for D4. | Rohan: got help from ChatGPT making the class from our spec. Arrush: undo written with Claude (Claude Code). |
| `GameControllerTest.java` | Rohan; Arrush (undo, fix) | Rohan wrote the tests with a fake model and fake view. Arrush fixed the compile errors and added the undo tests. | Rohan: <!-- TODO (Rohan): was GenAI used? --> Arrush: written with Claude (Claude Code). |
| `ConsoleView.java` | Arrush, for Wyatt's part | Written for Wyatt's part of the plan; Arrush updated the prompt for undo | Written with Claude (Claude Code) from our spec. <!-- TODO (Wyatt): add if you reviewed or revised it --> |
| `ConsoleViewTest.java` | Arrush, for Wyatt's part | Written for Wyatt's part of the plan | Written with Claude (Claude Code). <!-- TODO (Wyatt) --> |
| `ConsoleViewDemo.java` | Arrush, for Wyatt's part | Written for Wyatt's part of the plan | Written with Claude (Claude Code). <!-- TODO (Wyatt) --> |
| `GameLauncher.java` | Matthew | Wrote the GameLauncher class from the D2 spec | No GenAI used |
| `InputHandler.java` | Matthew | Wrote the InputHandler class | No GenAI used |
| `InputHandlerTest.java` | Matthew | Wrote the InputHandlerTest class with ChatGPT | Written by ChatGPT |
| `README.md` | Arrush; <!-- TODO: name of GitHub user asu127 --> | Arrush drafted it and rewrote the D4 Reflection and Next Steps around the undo feature. <!-- TODO: name --> wrote the first D4 Reflection, Next Steps, demo plan and feature owners, which this version builds on. | Arrush's parts drafted with Claude (Claude Code), then edited |
