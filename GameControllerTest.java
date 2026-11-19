import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests the GameController class for the 2048 game, using a fake model and
 * a fake view so each test controls exactly what happens.
 *
 * @author Rohan
 * @version Nov 19, 2026
 */
public class GameControllerTest
    extends TestCase
{
    /**
     * fake model the controller talks to
     */
    private FakeModel model;
    /**
     * fake view that records what the controller shows
     */
    private FakeView view;
    /**
     * the controller to test
     */
    private GameController controller;

    // ----------------------------------------------------------
    /**
     * Sets up a controller with a fresh fake model and view before each
     * test.
     */
    public void setUp()
    {
        model = new FakeModel();
        view = new FakeView();
        controller = new GameController(model, view);
    }


    // ----------------------------------------------------------
    /**
     * Asserts that building a controller from the given model and view
     * throws IllegalArgumentException.
     *
     * @param badModel
     *            the model to pass
     * @param badView
     *            the view to pass
     */
    private void assertConstructorThrows(GameModel badModel,
        ConsoleView badView)
    {
        Exception thrown = null;
        try
        {
            new GameController(badModel, badView);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    // ----------------------------------------------------------
    /**
     * Normal: the constructor builds a controller.
     */
    public void testConstructor()
    {
        assertNotNull(controller);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a null model throws IllegalArgumentException.
     */
    public void testNullModel()
    {
        assertConstructorThrows(null, view);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a null view throws IllegalArgumentException.
     */
    public void testNullView()
    {
        assertConstructorThrows(model, null);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a null model and view throw IllegalArgumentException.
     */
    public void testBothNull()
    {
        assertConstructorThrows(null, null);
    }


    // ----------------------------------------------------------
    /**
     * Normal: all four directions are passed to the model.
     */
    public void testValidDirections()
    {
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


    // ----------------------------------------------------------
    /**
     * Normal: lowercase input is passed to the model as uppercase.
     */
    public void testLowercaseInput()
    {
        controller.processMove("w");
        assertEquals("W", model.lastDirection);

        controller.processMove("a");
        assertEquals("A", model.lastDirection);

        controller.processMove("s");
        assertEquals("S", model.lastDirection);

        controller.processMove("d");
        assertEquals("D", model.lastDirection);
    }


    // ----------------------------------------------------------
    /**
     * Normal: spaces around a valid direction are ignored.
     */
    public void testInputWithSpaces()
    {
        controller.processMove("  a  ");
        assertEquals("A", model.lastDirection);
        assertEquals(1, model.moveCount);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an invalid letter shows an error and doesn't move.
     */
    public void testInvalidInput()
    {
        controller.processMove("X");

        assertEquals(0, model.moveCount);
        assertEquals(GameController.INVALID_MESSAGE, view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an empty line shows an error and doesn't move.
     */
    public void testEmptyInput()
    {
        controller.processMove("");

        assertEquals(0, model.moveCount);
        assertEquals(GameController.INVALID_MESSAGE, view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: only spaces shows an error and doesn't move.
     */
    public void testOnlySpaces()
    {
        controller.processMove("   ");

        assertEquals(0, model.moveCount);
        assertEquals(GameController.INVALID_MESSAGE, view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: null input shows an error and doesn't move.
     */
    public void testNullInput()
    {
        controller.processMove(null);

        assertEquals(0, model.moveCount);
        assertEquals("Invalid input.", view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a valid letter plus extra characters is rejected.
     */
    public void testMultipleCharacters()
    {
        controller.processMove("WA");

        assertEquals(0, model.moveCount);
        assertEquals(GameController.INVALID_MESSAGE, view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Normal: a move that changes the board clears the screen and redraws
     * the board and score.
     */
    public void testSuccessfulMove()
    {
        model.moveResult = true;

        controller.handleMove("A");

        assertEquals("A", model.lastDirection);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
    }


    // ----------------------------------------------------------
    /**
     * Normal: a move that changes nothing doesn't redraw and shows a
     * message instead.
     */
    public void testUnsuccessfulMove()
    {
        model.moveResult = false;

        controller.handleMove("A");

        assertEquals(1, model.moveCount);
        assertEquals(0, view.boardCount);
        assertEquals(0, view.scoreCount);
        assertEquals(0, view.clearCount);
        assertEquals("No tiles moved. Try another direction.",
            view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Normal: the score shown after a move comes from the model.
     */
    public void testDisplayAfterMove()
    {
        model.moveResult = true;
        model.score = 16;

        controller.handleMove("D");

        assertEquals(16, view.lastScore);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: when the input stream ends, the game stops cleanly.
     */
    public void testStartGameInputEnds()
    {
        view.nextInput = null;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.boardCount);
        assertEquals(1, view.scoreCount);
        assertEquals(1, view.clearCount);
        assertEquals(0, model.moveCount);
        assertEquals("Game ended.", view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Normal: the game stops and shows the win message when won.
     */
    public void testStartGameWin()
    {
        model.won = true;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.winCount);
        assertEquals(0, view.gameOverCount);
        assertEquals(0, view.inputCount);
    }


    // ----------------------------------------------------------
    /**
     * Normal: the game stops and shows the game over message when lost.
     */
    public void testStartGameLoss()
    {
        model.gameOver = true;

        controller.startGame();

        assertTrue(model.initialized);
        assertEquals(1, view.gameOverCount);
        assertEquals(0, view.winCount);
        assertEquals(0, view.inputCount);
    }


    // ----------------------------------------------------------
    /**
     * Normal: the game loop makes a move, then ends when the fake model
     * says the game is over.
     */
    public void testStartGameWithMove()
    {
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


    // ----------------------------------------------------------
    /**
     * Normal: a win right after a move shows the win message.
     */
    public void testWinAfterMove()
    {
        view.nextInput = "W";
        model.winAfterMove = true;

        controller.startGame();

        assertEquals(1, model.moveCount);
        assertEquals(1, view.winCount);
        assertEquals(0, view.gameOverCount);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: invalid input during the game loop doesn't move, and the
     * loop asks again until the input ends.
     */
    public void testInvalidInputDuringGame()
    {
        view.nextInput = "X";

        controller.startGame();

        assertEquals(0, model.moveCount);
        assertEquals(2, view.inputCount);
        assertEquals("Game ended.", view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Normal: U calls undo() on the model and redraws the board and score,
     * without making a move.
     */
    public void testUndoCommand()
    {
        model.undoResult = true;
        model.score = 12;

        controller.processMove("U");

        assertEquals(1, model.undoCount);
        assertEquals(0, model.moveCount);
        assertEquals(1, view.clearCount);
        assertEquals(1, view.boardCount);
        assertEquals(12, view.lastScore);
    }


    // ----------------------------------------------------------
    /**
     * Normal: lowercase u and spaces around it work too.
     */
    public void testUndoLowercase()
    {
        model.undoResult = true;

        controller.processMove("  u ");

        assertEquals(1, model.undoCount);
        assertEquals(1, view.boardCount);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: U with nothing to undo shows a message and doesn't
     * redraw or crash.
     */
    public void testUndoNothingToUndo()
    {
        model.undoResult = false;

        controller.processMove("U");

        assertEquals(1, model.undoCount);
        assertEquals(0, view.boardCount);
        assertEquals(0, view.clearCount);
        assertEquals(GameController.NOTHING_TO_UNDO_MESSAGE,
            view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: U followed by other text is rejected like any other
     * invalid command and doesn't call undo().
     */
    public void testUndoWithExtraText()
    {
        controller.processMove("undo");

        assertEquals(0, model.undoCount);
        assertEquals(0, model.moveCount);
        assertEquals(GameController.INVALID_MESSAGE, view.lastMessage);
    }


    // ----------------------------------------------------------
    /**
     * Normal: undo works inside the game loop and the game keeps going.
     */
    public void testUndoDuringGame()
    {
        model.undoResult = true;
        view.nextInput = "u";

        controller.startGame();

        assertEquals(1, model.undoCount);
        assertEquals(0, model.moveCount);
        // One draw at the start, one after the undo.
        assertEquals(2, view.boardCount);
        assertEquals("Game ended.", view.lastMessage);
    }


    // -------------------------------------------------------------------------
    /**
     * Fake model used to control test outcomes. It records what the
     * controller asked for instead of running real game logic.
     */
    private static class FakeModel
        extends GameModel
    {
        /**
         * whether initializeBoard() was called
         */
        private boolean initialized;
        /**
         * value returned by isWon()
         */
        private boolean won;
        /**
         * value returned by isGameOver()
         */
        private boolean gameOver;
        /**
         * value returned by moveTiles()
         */
        private boolean moveResult = true;
        /**
         * whether the game ends after the next move
         */
        private boolean endAfterMove;
        /**
         * whether the game is won after the next move
         */
        private boolean winAfterMove;
        /**
         * number of times moveTiles() was called
         */
        private int moveCount;
        /**
         * value returned by getScore()
         */
        private int score;
        /**
         * last direction passed to moveTiles()
         */
        private String lastDirection;
        /**
         * value returned by undo()
         */
        private boolean undoResult;
        /**
         * number of times undo() was called
         */
        private int undoCount;

        // ----------------------------------------------------------
        /**
         * Records that the board was initialized.
         */
        @Override
        public void initializeBoard()
        {
            initialized = true;
        }


        // ----------------------------------------------------------
        /**
         * Records the undo and returns undoResult.
         *
         * @return undoResult
         */
        @Override
        public boolean undo()
        {
            undoCount++;
            return undoResult;
        }


        // ----------------------------------------------------------
        /**
         * Records the move and returns moveResult.
         *
         * @param direction
         *            the direction the controller passed
         * @return moveResult
         */
        @Override
        public boolean moveTiles(String direction)
        {
            lastDirection = direction;
            moveCount++;

            if (endAfterMove)
            {
                gameOver = true;
            }

            if (winAfterMove)
            {
                won = true;
            }

            return moveResult;
        }


        // ----------------------------------------------------------
        /**
         * Returns the gameOver flag.
         *
         * @return gameOver
         */
        @Override
        public boolean isGameOver()
        {
            return gameOver;
        }


        // ----------------------------------------------------------
        /**
         * Returns the won flag.
         *
         * @return won
         */
        @Override
        public boolean isWon()
        {
            return won;
        }


        // ----------------------------------------------------------
        /**
         * Returns an empty board.
         *
         * @return a new empty 4x4 board
         */
        @Override
        public int[][] getBoard()
        {
            return new int[4][4];
        }


        // ----------------------------------------------------------
        /**
         * Returns the score field.
         *
         * @return score
         */
        @Override
        public int getScore()
        {
            return score;
        }
    }


    // -------------------------------------------------------------------------
    /**
     * Fake view that records what the controller shows instead of
     * printing it.
     */
    private static class FakeView
        extends ConsoleView
    {
        /**
         * last message passed to displayMessage()
         */
        private String lastMessage;
        /**
         * value returned by the next getInput() call; null after that
         */
        private String nextInput;
        /**
         * number of times displayBoard() was called
         */
        private int boardCount;
        /**
         * number of times displayScore() was called
         */
        private int scoreCount;
        /**
         * number of times clearScreen() was called
         */
        private int clearCount;
        /**
         * number of times displayWin() was called
         */
        private int winCount;
        /**
         * number of times displayGameOver() was called
         */
        private int gameOverCount;
        /**
         * number of times getInput() was called
         */
        private int inputCount;
        /**
         * last score passed to displayScore()
         */
        private int lastScore;

        // ----------------------------------------------------------
        /**
         * Counts the board display.
         *
         * @param board
         *            the board the controller passed
         */
        @Override
        public void displayBoard(int[][] board)
        {
            boardCount++;
        }


        // ----------------------------------------------------------
        /**
         * Counts the score display and records the score.
         *
         * @param score
         *            the score the controller passed
         */
        @Override
        public void displayScore(int score)
        {
            scoreCount++;
            lastScore = score;
        }


        // ----------------------------------------------------------
        /**
         * Counts the screen clear.
         */
        @Override
        public void clearScreen()
        {
            clearCount++;
        }


        // ----------------------------------------------------------
        /**
         * Records the message.
         *
         * @param message
         *            the message the controller passed
         */
        @Override
        public void displayMessage(String message)
        {
            lastMessage = message;
        }


        // ----------------------------------------------------------
        /**
         * Counts the win message.
         */
        @Override
        public void displayWin()
        {
            winCount++;
        }


        // ----------------------------------------------------------
        /**
         * Counts the game over message.
         */
        @Override
        public void displayGameOver()
        {
            gameOverCount++;
        }


        // ----------------------------------------------------------
        /**
         * Returns nextInput once, then null so the game loop ends.
         *
         * @return the fake input
         */
        @Override
        public String getInput()
        {
            inputCount++;

            String result = nextInput;
            nextInput = null;
            return result;
        }
    }
}
