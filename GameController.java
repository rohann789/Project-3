
/**
 * Controls the 2048 game by connecting the
 * GameModel and ConsoleView classes.
 *
 * @author Rohan
 * @version Nov 13, 2026
 */
public class GameController {

    private GameModel model;
    private ConsoleView view;

    /**
     * Creates the game controller.
     *
     * @param model the game model
     * @param view the console view
     */
    public GameController(GameModel model, ConsoleView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException(
                "Model and view cannot be null");
        }

        this.model = model;
        this.view = view;
    }

    /**
     * Starts the game and keeps running
     * until the player wins or loses.
     */
    public void startGame() {
        model.initializeBoard();

        view.displayMessage("Welcome to 2048!");
        view.displayMessage("Use W, A, S, D to move tiles.");

        displayGame();

        while (!model.isGameOver() && !model.isWon()) {
            String input = view.getInput();

            // Stop if input has ended.
            if (input == null) {
                view.displayMessage("Game ended.");
                return;
            }

            processMove(input);
        }

        if (model.isWon()) {
            view.displayWin();
        }
        else if (model.isGameOver()) {
            view.displayGameOver();
        }
    }

    /**
     * Checks the player's input before
     * passing it to the model.
     *
     * @param input the player's command
     */
    public void processMove(String input) {
        if (input == null) {
            view.displayMessage("Invalid input.");
            return;
        }

        String direction = input.trim().toUpperCase();

        if (direction.equals("W") || direction.equals("A")
            || direction.equals("S") || direction.equals("D")) {
            handleMove(direction);
        }
        else {
            view.displayMessage(
                "Invalid move. Please enter W, A, S, or D.");
        }
    }

    /**
     * Moves the tiles and updates the screen
     * if the board changed.
     *
     * @param direction the movement direction
     */
    public void handleMove(String direction) {
        boolean moved = model.moveTiles(direction);

        if (moved) {
            displayGame();
        }
        else {
            view.displayMessage(
                "No tiles moved. Try another direction.");
        }
    }

    /**
     * Displays the current board and score.
     */
    private void displayGame() {
        view.clearScreen();
        view.displayBoard(model.getBoard());
        view.displayScore(model.getScore());
    }
}
