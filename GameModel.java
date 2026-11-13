
import java.util.Random;

/**
 * Represents the game board and rules for 2048.
 * Handles moving tiles, scoring, and checking
 * whether the player has won or lost.
 *
 * @author Rohan
 * @version Nov 13, 2026
 */
public class GameModel {

    private int[][] board;
    private int score;
    private boolean gameOver;
    private boolean won;
    private Random random;
    private TileDeque tileDeque;

    /**
     * Creates a new empty game model.
     */
    public GameModel() {
        board = new int[4][4];
        score = 0;
        gameOver = false;
        won = false;
        random = new Random();
        tileDeque = new TileDeque();
    }

    /**
     * Creates a model with a given board.
     * Mainly used for testing.
     *
     * @param startBoard starting board
     * @param rng random generator
     */
    public GameModel(int[][] startBoard, Random rng) {
        this();

        if (startBoard == null || startBoard.length != 4
            || rng == null) {
            throw new IllegalArgumentException();
        }

        for (int row = 0; row < 4; row++) {
            if (startBoard[row] == null
                || startBoard[row].length != 4) {
                throw new IllegalArgumentException();
            }

            for (int col = 0; col < 4; col++) {
                int value = startBoard[row][col];

                if (value < 0 || value == 1
                    || (value != 0
                    && (value & (value - 1)) != 0)) {
                    throw new IllegalArgumentException();
                }

                board[row][col] = value;
            }
        }

        random = rng;
        won = checkWon();
        gameOver = checkGameOver();
    }

    /**
     * Resets the board and starts a new game.
     * Two random tiles are added.
     */
    public void initializeBoard() {
        board = new int[4][4];
        score = 0;
        gameOver = false;
        won = false;

        tileDeque.clear();

        generateTile();
        generateTile();
    }

    /**
     * Moves tiles in the given direction.
     * Uses TileDeque to process each line.
     *
     * @param direction W, A, S, or D
     * @return true if the board changed
     */
    public boolean moveTiles(String direction) {
        if (direction == null) {
            throw new IllegalArgumentException(
                "Invalid direction");
        }

        String dir = direction.trim().toUpperCase();

        if (!dir.equals("W") && !dir.equals("A")
            && !dir.equals("S") && !dir.equals("D")) {
            throw new IllegalArgumentException(
                "Invalid direction");
        }

        int[][] oldBoard = getBoard();

        // Process each row or column.
        for (int i = 0; i < 4; i++) {
            score += tileDeque.processLine(board, i, dir);
        }

        // Check if any tiles actually moved.
        boolean changed = false;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] != oldBoard[row][col]) {
                    changed = true;
                }
            }
        }

        // Only generate a tile after a valid move.
        if (changed) {
            generateTile();
        }

        won = checkWon();
        gameOver = checkGameOver();

        return changed;
    }

    /**
     * Adds a random tile to an empty space.
     * There is a 90% chance of a 2
     * and a 10% chance of a 4.
     */
    private void generateTile() {
        int emptyCount = 0;

        // Count empty spaces.
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) {
            return;
        }

        // Select one empty space.
        int chosen = random.nextInt(emptyCount);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] == 0) {
                    if (chosen == 0) {
                        if (random.nextInt(10) == 0) {
                            board[row][col] = 4;
                        }
                        else {
                            board[row][col] = 2;
                        }
                        return;
                    }

                    chosen--;
                }
            }
        }
    }

    /**
     * Checks if the player has reached 2048.
     *
     * @return true if the player won
     */
    private boolean checkWon() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] >= 2048) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if no moves are left.
     *
     * @return true if the game is over
     */
    private boolean checkGameOver() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {

                // An empty space means moves remain.
                if (board[row][col] == 0) {
                    return false;
                }

                // Check the tile to the right.
                if (col < 3
                    && board[row][col] == board[row][col + 1]) {
                    return false;
                }

                // Check the tile below.
                if (row < 3
                    && board[row][col] == board[row + 1][col]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns whether the game is over.
     *
     * @return true if no moves remain
     */
    public boolean isGameOver() {
        gameOver = checkGameOver();
        return gameOver;
    }

    /**
     * Returns whether the player has won.
     *
     * @return true if 2048 was reached
     */
    public boolean isWon() {
        won = checkWon();
        return won;
    }

    /**
     * Returns a copy of the board.
     *
     * @return the current board
     */
    public int[][] getBoard() {
        int[][] copy = new int[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                copy[row][col] = board[row][col];
            }
        }

        return copy;
    }

    /**
     * Returns the player's current score.
     *
     * @return score
     */
    public int getScore() {
        return score;
    }
}
