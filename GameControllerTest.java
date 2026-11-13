public 
import student.TestCase;

/**
 * Tests the GameController class for the 2048 game.
 *
 * @author Rohan
 * @version Nov 13, 2026
 */
public class GameControllerTest extends TestCase {

    private FakeModel model;
    private FakeView view;
    private GameController controller;

    /**
     * Sets up the controller before each test.
     */
    public void setUp() {
        model = new FakeModel();
        view = new FakeView();
        controller = new GameController(model, view);
    }

    /**
     * Tests a normal constructor.
     */
    public void testConstructor() {
        assertNotNull(controller);
    }

    /**
     * Tests a null model.
     */
    public void testNullModel() {
        try {
            new GameController(null, view);
            fail("Expected exception");
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * Tests a null view.
     */
    public void testNullView() {
        try {
            new GameController(model, null);
            fail("Expected exception");
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * Tests both arguments being null.
     */
    public void testBothNull() {
        try {
            new GameController(null, null);
            fail("Expected exception");
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * Tests all four valid directions.
     */
    public void testValidDirections() {
        controller.processMove("W");
        assertEquals("W", model.lastDirection);

        controller.processMove("A");
        assertEquals("A", model.lastDirection);

        controller.processMove("S");
        assertEquals("S", model.lastDirection);

        controller.processMove("D");
        assertEquals("D", model.lastDirection);

        assertEquals(4, model.moveCount);
    }

    /**
     * Tests lowercase input.
     */
    public void testLowercaseInput() {
        controller.processMove("w");
        assertEquals("W", model.lastDirection);

        controller.processMove("a");
        assertEquals("A", model.lastDirection);

        controller.processMove("s");
        assertEquals("S", model.lastDirection);

        controller.processMove("d");
        assertEquals("D", model.lastDirection);
    }

    /**
     * Tests spaces around a valid direction.
     */
    public void testInputWithSpaces() {
        controller.processMove("  a  ");
        assertEquals("A", model.lastDirection);
        assertEquals(1, model.moveCount);
    }

    /**
     * Tests invalid letters.
     */
    public void testInvalidInput() {
        controller.processMove("X");

        assertEquals(0, model.moveCount);
        assertEquals(
            "Invalid move. Please enter W, A, S, or D.",
            view.lastMessage);
    }

    /**
     * Tests an empty input.
     */
    public void testEmptyInput() {
        controller.processMove("");

        assertEquals(0, model.moveCount);
        assertEquals(
            "Invalid move. Please enter W, A, S, or D.",
            view.lastMessage);
    }

    /**
     * Tests spaces without a direction.
     */
    public void testOnlySpaces() {
        controller.processMove("   ");

        assertEquals(0, model.moveCount);
        assertEquals(
            "Invalid move. Please enter W, A, S, or D.",
            view.lastMessage);
    }

    /**
     * Tests null input.
     */
    public void testNullInput() {
        controller.processMove(null);

        assertEquals(0, model.moveCount);
        assertEquals("Invalid input.", view.lastMessage);
    }

    /**
     * Tests multiple characters.
     */
    public void testMultipleCharacters() {
        controller.processMove("WA");

        assertEquals(0, model.moveCount);
        assertEquals(
            "Invalid move. Please enter W, A, S, or D.",
            view.lastMessage);
    }

    /**
     * Tests a successful move.
     */
    public void testSuccessfulMove() {
        model.moveResult = true;

        controller.handleMove("A");

        assertEquals("A", model.lastDirection);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
    }

    /**
     * Tests a move that does not change the board.
     */
    public void testUnsuccessfulMove() {
        model.moveResult = false;

        controller.handleMove("A");

        assertEquals(1, model.moveCount);
        assertEquals(0, view.boardCount);
        assertEquals(0, view.scoreCount);
        assertEquals(0, view.clearCount);
        assertEquals(
            "No tiles moved. Try another direction.",
            view.lastMessage);
    }

    /**
     * Tests that the board and score are displayed.
     */
    public void testDisplayAfterMove() {
        model.moveResult = true;
        model.score = 16;

        controller.handleMove("D");

        assertEquals(16, view.lastScore);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
    }

    /**
     * Tests startup when the input stream ends.
     */
    public void testStartGameInputEnds() {
        view.nextInput = null;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
        assertEquals(0, model.moveCount);
        assertEquals("Game ended.", view.lastMessage);
    }

    /**
     * Tests that the game stops when won.
     */
    public void testStartGameWin() {
        model.won = true;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.winCount);
        assertEquals(0, view.gameOverCount);
        assertEquals(0, view.inputCount);
    }

    /**
     * Tests that the game stops when lost.
     */
    public void testStartGameLoss() {
        model.gameOver = true;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.gameOverCount);
        assertEquals(0, view.winCount);
        assertEquals(0, view.inputCount);
    }

    /**
     * Tests the game loop with a successful move.
     * The fake model ends the game after one move.
     */
    public void testStartGameWithMove() {
        view.nextInput = "a";
        model.endAfterMove = true;
        model.moveResult = true;

        controller.startGame();

        assertEquals("A", model.lastDirection);
        assertEquals(1, model.moveCount);
        assertEquals(2, view.boardCount);
        assertEquals(2, view.scoreCount);
        assertEquals(1, view.gameOverCount);
    }

    /**
     * Tests a win that occurs after a move.
     */
    public void testWinAfterMove() {
        view.nextInput = "W";
        model.winAfterMove = true;

        controller.startGame();

        assertEquals(1, model.moveCount);
        assertEquals(1, view.winCount);
        assertEquals(0, view.gameOverCount);
    }

    /**
     * Tests invalid input during the game loop.
     * Input ends on the following read.
     */
    public void testInvalidInputDuringGame() {
        view.nextInput = "X";

        controller.startGame();

        assertEquals(0, model.moveCount);
        assertEquals(2, view.inputCount);
        assertEquals("Game ended.", view.lastMessage);
    }

    /**
     * Fake model used to control test outcomes.
     */
    private static class FakeModel extends GameModel {

        private boolean initialized;
        private boolean won;
        private boolean gameOver;
        private boolean moveResult = true;
        private boolean endAfterMove;
        private boolean winAfterMove;
        private int moveCount;
        private int score;
        private String lastDirection;

        @Override
        public void initializeBoard() {
            initialized = true;
        }

        @Override
        public boolean moveTiles(String direction) {
            lastDirection = direction;
            moveCount++;

            if (endAfterMove) {
                gameOver = true;
            }

            if (winAfterMove) {
                won = true;
            }

            return moveResult;
        }

        @Override
        public boolean isGameOver() {
            return gameOver;
        }

        @Override
        public boolean isWon() {
            return won;
        }

        @Override
        public int[][] getBoard() {
            return new int[4][4];
        }

        @Override
        public int getScore() {
            return score;
        }
    }

    /**
     * Fake view that records controller output.
     */
    private static class FakeView extends ConsoleView {

        private String lastMessage;
        private String nextInput;
        private int boardCount;
        private int scoreCount;
        private int clearCount;
        private int winCount;
        private int gameOverCount;
        private int inputCount;
        private int lastScore;

        @Override
        public void displayBoard(int[][] board) {
            boardCount++;
        }

        @Override
        public void displayScore(int score) {
            scoreCount++;
            lastScore = score;
        }

        @Override
        public void clearScreen() {
            clearCount++;
        }

        @Override
        public void displayMessage(String message) {
            lastMessage = message;
        }

        @Override
        public void displayWin() {
            winCount++;
        }

        @Override
        public void displayGameOver() {
            gameOverCount++;
        }

        @Override
        public String getInput() {
            inputCount++;

            String result = nextInput;
            nextInput = null;
            return result;
        }
    }
}
 {
    
}
