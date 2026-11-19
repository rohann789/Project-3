import java.util.Arrays;
import java.util.Random;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for GameModel, following the GameModel section of the Test Plan,
 * plus tests for undo. Every test builds its board with
 * GameModel(int[][] startBoard, Random rng) and a seeded Random, so the
 * results are always the same. No view, controller, or Scanner is created.
 *
 * @author arrushshah
 * @version Nov 19, 2026
 */
public class GameModelTest
    extends TestCase
{
    /**
     * seed for the Random, so new tiles always land in the same place
     */
    private static final long SEED = 2048L;

    // ----------------------------------------------------------
    /**
     * Builds a model from a board using a seeded Random.
     *
     * @param board
     *            the starting board
     * @return the new model
     */
    private GameModel model(int[][] board)
    {
        return new GameModel(board, new Random(SEED));
    }


    // ----------------------------------------------------------
    /**
     * Returns an empty 4x4 board.
     *
     * @return a new empty board
     */
    private int[][] empty()
    {
        return new int[4][4];
    }


    // ----------------------------------------------------------
    /**
     * Returns a full board where no two neighbors match.
     *
     * @return a new stuck board
     */
    private int[][] stuckBoard()
    {
        return new int[][] {
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 },
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 } };
    }


    // ----------------------------------------------------------
    /**
     * Counts the non-empty cells on a board.
     *
     * @param board
     *            the board to count
     * @return the number of tiles
     */
    private int countTiles(int[][] board)
    {
        int count = 0;
        for (int[] row : board)
        {
            for (int value : row)
            {
                if (value != 0)
                {
                    count++;
                }
            }
        }
        return count;
    }


    // ----------------------------------------------------------
    /**
     * Asserts that two boards hold the same values.
     *
     * @param expected
     *            the expected board
     * @param actual
     *            the actual board
     */
    private void assertBoard(int[][] expected, int[][] actual)
    {
        assertEquals(Arrays.deepToString(expected),
            Arrays.deepToString(actual));
    }


    // ----------------------------------------------------------
    /**
     * Asserts that building a model from the board throws
     * IllegalArgumentException.
     *
     * @param board
     *            the invalid starting board
     */
    private void assertConstructorThrows(int[][] board)
    {
        Exception thrown = null;
        try
        {
            model(board);
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
     * Asserts that moveTiles(direction) throws IllegalArgumentException.
     *
     * @param game
     *            the model to move
     * @param direction
     *            the invalid direction
     */
    private void assertMoveThrows(GameModel game, String direction)
    {
        Exception thrown = null;
        try
        {
            game.moveTiles(direction);
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
     * Normal: a valid 4x4 board is returned by getBoard() and the score is
     * 0.
     */
    public void testConstructor()
    {
        int[][] start = empty();
        start[0][0] = 2;
        start[3][2] = 1024;
        GameModel game = model(start);
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: the constructor copies the board, so changing the original
     * array afterward doesn't change the model.
     */
    public void testConstructorCopiesBoard()
    {
        int[][] start = empty();
        start[1][1] = 4;
        GameModel game = model(start);
        start[1][1] = 512;
        assertEquals(4, game.getBoard()[1][1]);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a 3x4 board throws IllegalArgumentException.
     */
    public void testConstructorWrongSize()
    {
        assertConstructorThrows(new int[3][4]);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a tile value of 3 throws IllegalArgumentException.
     */
    public void testConstructorBadTile()
    {
        int[][] start = empty();
        start[2][2] = 3;
        assertConstructorThrows(start);
    }


    // ----------------------------------------------------------
    /**
     * Normal: initializeBoard() leaves exactly 2 tiles, each a 2 or 4, and
     * the score is 0.
     */
    public void testInitializeBoard()
    {
        GameModel game = model(empty());
        game.initializeBoard();
        int[][] board = game.getBoard();
        assertEquals(2, countTiles(board));
        for (int[] row : board)
        {
            for (int value : row)
            {
                assertTrue(value == 0 || value == 2 || value == 4);
            }
        }
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: called in the middle of a game with a score of 120, the
     * board resets to 2 tiles and the score resets to 0.
     */
    public void testInitializeBoardMidGame()
    {
        int[][] start = {
            { 32, 32, 16, 16 },
            { 8, 8, 0, 0 },
            { 4, 4, 0, 0 },
            { 0, 0, 0, 0 } };
        GameModel game = model(start);
        assertTrue(game.moveTiles("A"));
        assertEquals(120, game.getScore());

        game.initializeBoard();
        assertEquals(2, countTiles(game.getBoard()));
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: row [2,2,2,_] moved A becomes [4,2,_,_], the score goes up by
     * 4, it returns true, and one new tile appears in an empty spot.
     */
    public void testMoveTilesLeft()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 2, 0 };
        GameModel game = model(start);

        assertTrue(game.moveTiles("A"));
        int[][] board = game.getBoard();
        assertEquals(4, board[0][0]);
        assertEquals(2, board[0][1]);
        assertEquals(4, game.getScore());
        // 2 tiles from the merge, plus 1 new tile
        assertEquals(3, countTiles(board));
    }


    // ----------------------------------------------------------
    /**
     * Normal: lowercase directions work the same as uppercase.
     */
    public void testMoveTilesLowercase()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 0, 0 };
        GameModel game = model(start);
        assertTrue(game.moveTiles("a"));
        assertEquals(4, game.getBoard()[0][0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: each direction moves tiles toward its own edge.
     */
    public void testMoveTilesEachDirection()
    {
        int[][] start = empty();
        start[1][1] = 8;

        GameModel game = model(start);
        assertTrue(game.moveTiles("D"));
        assertEquals(8, game.getBoard()[1][3]);

        game = model(start);
        assertTrue(game.moveTiles("W"));
        assertEquals(8, game.getBoard()[0][1]);

        game = model(start);
        assertTrue(game.moveTiles("S"));
        assertEquals(8, game.getBoard()[3][1]);

        game = model(start);
        assertTrue(game.moveTiles("A"));
        assertEquals(8, game.getBoard()[1][0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: [2,2,2,_] moved D becomes [_,_,2,4], because the tiles
     * nearest the right edge merge first.
     */
    public void testMoveTilesRightMergeOrder()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 2, 0 };
        GameModel game = model(start);
        assertTrue(game.moveTiles("D"));
        int[][] board = game.getBoard();
        assertEquals(2, board[0][2]);
        assertEquals(4, board[0][3]);
        assertEquals(4, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: 'P' throws IllegalArgumentException, and the board and
     * score don't change.
     */
    public void testMoveTilesInvalidDirection()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 0, 0 };
        GameModel game = model(start);
        assertMoveThrows(game, "P");
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Extra case: row [2,2,4,_] moved left becomes [4,4,_,_], not
     * [8,_,_,_] (no double merge).
     */
    public void testMoveTilesNoDoubleMerge()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 4, 0 };
        GameModel game = model(start);
        assertTrue(game.moveTiles("A"));
        int[][] board = game.getBoard();
        assertEquals(4, board[0][0]);
        assertEquals(4, board[0][1]);
        assertEquals(4, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Extra case: row [2,_,_,_] moved left with nothing else on the board
     * returns false and no new tile spawns.
     */
    public void testMoveTilesNoChange()
    {
        int[][] start = empty();
        start[0][0] = 2;
        GameModel game = model(start);
        assertFalse(game.moveTiles("A"));
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: a full board with no matching neighbors is game over.
     */
    public void testIsGameOver()
    {
        assertTrue(model(stuckBoard()).isGameOver());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a full board with one matching pair next to each other is
     * not game over, because a full board alone isn't a loss.
     */
    public void testIsGameOverWithMatch()
    {
        int[][] start = stuckBoard();
        // now matches the 4 above it
        start[3][3] = 4;
        assertFalse(model(start).isGameOver());
    }


    // ----------------------------------------------------------
    /**
     * Normal: a board with an empty cell is not game over.
     */
    public void testIsGameOverNotFull()
    {
        int[][] start = stuckBoard();
        start[0][0] = 0;
        assertFalse(model(start).isGameOver());
    }


    // ----------------------------------------------------------
    /**
     * Normal: a board containing a 2048 tile is won.
     */
    public void testIsWon()
    {
        int[][] start = empty();
        start[2][1] = 2048;
        assertTrue(model(start).isWon());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a board with two 1024s that haven't merged is not won,
     * but merging them is.
     */
    public void testIsWonTwo1024s()
    {
        int[][] start = empty();
        start[0][0] = 1024;
        start[0][1] = 1024;
        GameModel game = model(start);
        assertFalse(game.isWon());

        game.moveTiles("A");
        assertTrue(game.isWon());
    }


    // ----------------------------------------------------------
    /**
     * Normal: getBoard() returns a 4x4 array matching the current board.
     */
    public void testGetBoard()
    {
        int[][] start = stuckBoard();
        int[][] board = model(start).getBoard();
        assertEquals(4, board.length);
        for (int[] row : board)
        {
            assertEquals(4, row.length);
        }
        assertBoard(start, board);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: editing the array from getBoard() doesn't change the
     * model, because it returns a copy.
     */
    public void testGetBoardReturnsCopy()
    {
        GameModel game = model(stuckBoard());
        int[][] board = game.getBoard();
        board[0][0] = 2048;
        board[3] = new int[4];
        assertBoard(stuckBoard(), game.getBoard());
    }


    // ----------------------------------------------------------
    /**
     * Normal: after merging two 4s, the score goes up by 8.
     */
    public void testGetScore()
    {
        int[][] start = empty();
        start[0][1] = 4;
        start[3][1] = 4;
        GameModel game = model(start);
        assertTrue(game.moveTiles("W"));
        assertEquals(8, game.getScore());
        assertEquals(8, game.getBoard()[0][1]);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: before any move the score is 0, never negative, and a
     * move that changes nothing doesn't change it.
     */
    public void testGetScoreBeforeMove()
    {
        GameModel game = model(stuckBoard());
        assertEquals(0, game.getScore());
        assertFalse(game.moveTiles("A"));
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: undo() puts back the exact board and score from before the
     * move, including removing the tile that spawned.
     */
    public void testUndo()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 4, 0 };
        GameModel game = model(start);
        assertTrue(game.moveTiles("A"));
        assertEquals(4, game.getScore());

        assertTrue(game.undo());
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: several undos step back one move at a time, newest first.
     */
    public void testUndoSeveralMoves()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 0, 0 };
        GameModel game = model(start);

        assertTrue(game.moveTiles("A"));
        int[][] afterFirst = game.getBoard();
        int scoreAfterFirst = game.getScore();
        assertTrue(game.moveTiles("S"));

        assertTrue(game.undo());
        assertBoard(afterFirst, game.getBoard());
        assertEquals(scoreAfterFirst, game.getScore());

        assertTrue(game.undo());
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());

        assertFalse(game.undo());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: undo() before any move returns false and changes nothing.
     */
    public void testUndoNothingToUndo()
    {
        int[][] start = empty();
        start[1][1] = 8;
        GameModel game = model(start);
        assertFalse(game.canUndo());
        assertFalse(game.undo());
        assertBoard(start, game.getBoard());
        assertEquals(0, game.getScore());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a move that changes nothing isn't recorded, so undo()
     * after it still has nothing to undo.
     */
    public void testUndoIgnoresNoChangeMove()
    {
        int[][] start = empty();
        start[0][0] = 2;
        GameModel game = model(start);
        assertFalse(game.moveTiles("A"));
        assertFalse(game.canUndo());
        assertFalse(game.undo());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an invalid direction isn't recorded either.
     */
    public void testUndoIgnoresInvalidDirection()
    {
        GameModel game = model(empty());
        assertMoveThrows(game, "P");
        assertFalse(game.canUndo());
    }


    // ----------------------------------------------------------
    /**
     * Normal: starting a new game clears the undo history.
     */
    public void testInitializeBoardClearsUndo()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 0, 0 };
        GameModel game = model(start);
        assertTrue(game.moveTiles("A"));
        assertTrue(game.canUndo());

        game.initializeBoard();
        assertFalse(game.canUndo());
        assertFalse(game.undo());
    }


    // ----------------------------------------------------------
    /**
     * Normal: undoing the winning merge also undoes the win, and the
     * player can make a different move afterward.
     */
    public void testUndoWinThenMoveAgain()
    {
        int[][] start = empty();
        start[0][0] = 1024;
        start[0][1] = 1024;
        GameModel game = model(start);
        assertTrue(game.moveTiles("A"));
        assertTrue(game.isWon());

        assertTrue(game.undo());
        assertFalse(game.isWon());
        assertBoard(start, game.getBoard());

        assertTrue(game.moveTiles("S"));
        assertEquals(1024, game.getBoard()[3][0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: on a nearly full board, undo() restores the exact board and
     * the game is not over afterward.
     */
    public void testUndoNearlyFullBoard()
    {
        int[][] start = stuckBoard();
        // row 3 is now [4, 2, 2, 0]
        start[3][3] = 0;
        start[3][2] = 2;
        GameModel game = model(start);
        assertFalse(game.isGameOver());

        assertTrue(game.moveTiles("D"));
        assertTrue(game.undo());
        assertBoard(start, game.getBoard());
        assertFalse(game.isGameOver());
    }
}
