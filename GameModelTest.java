import java.util.Arrays;
import java.util.Random;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for GameModel, following the GameModel section of the Test Plan.
 * Every test builds its board with GameModel(int[][] startBoard, Random rng)
 * and a seeded Random, so the results are always the same. No view,
 * controller, or Scanner is created.
 *
 * @author arrushshah
 * @version Nov 12, 2026
 */
public class GameModelTest
    extends TestCase
{
    private static final long SEED = 2048L;


    /**
     * Builds a model from a board using a seeded Random.
     */
    private GameModel model(int[][] board)
    {
        return new GameModel(board, new Random(SEED));
    }


    /**
     * Returns an empty 4x4 board.
     */
    private int[][] empty()
    {
        return new int[4][4];
    }


    /**
     * Counts the non-empty cells on a board.
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


    /**
     * Asserts that two boards hold the same values.
     */
    private void assertBoard(int[][] expected, int[][] actual)
    {
        assertEquals(Arrays.deepToString(expected),
            Arrays.deepToString(actual));
    }


    /**
     * A full board where no two neighbors match.
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
    // GameModel(int[][] startBoard, Random rng)
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


    /**
     * Bad input: a 3x4 board throws IllegalArgumentException.
     */
    public void testConstructorWrongSize()
    {
        try
        {
            model(new int[3][4]);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Bad input: a tile value of 3 throws IllegalArgumentException.
     */
    public void testConstructorBadTile()
    {
        int[][] start = empty();
        start[2][2] = 3;
        try
        {
            model(start);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    // ----------------------------------------------------------
    // initializeBoard()
    // ----------------------------------------------------------

    /**
     * Normal: the board has exactly 2 tiles, each a 2 or 4, and the score
     * is 0.
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
    // moveTiles(String direction)
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


    /**
     * Bad input: 'P' throws IllegalArgumentException, and the board and
     * score don't change.
     */
    public void testMoveTilesInvalidDirection()
    {
        int[][] start = empty();
        start[0] = new int[] { 2, 2, 0, 0 };
        GameModel game = model(start);
        try
        {
            game.moveTiles("P");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertBoard(start, game.getBoard());
            assertEquals(0, game.getScore());
        }
    }


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
    // isGameOver()
    // ----------------------------------------------------------

    /**
     * Normal: a full board with no matching neighbors is game over.
     */
    public void testIsGameOver()
    {
        assertTrue(model(stuckBoard()).isGameOver());
    }


    /**
     * Bad input: a full board with one matching pair next to each other is
     * not game over, because a full board alone isn't a loss.
     */
    public void testIsGameOverWithMatch()
    {
        int[][] start = stuckBoard();
        start[3][3] = 4; // now matches the 4 above it
        assertFalse(model(start).isGameOver());
    }


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
    // isWon()
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


    /**
     * Bad input: a board with two 1024s that haven't merged is not won.
     */
    public void testIsWonTwo1024s()
    {
        int[][] start = empty();
        start[0][0] = 1024;
        start[0][1] = 1024;
        GameModel game = model(start);
        assertFalse(game.isWon());

        // Merging them reaches 2048.
        game.moveTiles("A");
        assertTrue(game.isWon());
    }


    // ----------------------------------------------------------
    // getBoard()
    // ----------------------------------------------------------

    /**
     * Normal: returns a 4x4 array matching the current board.
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


    /**
     * Bad input: editing the returned array doesn't change the model,
     * because getBoard() returns a copy.
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
    // getScore()
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


    /**
     * Bad input: before any move the score is 0, never negative.
     */
    public void testGetScoreBeforeMove()
    {
        GameModel game = model(stuckBoard());
        assertEquals(0, game.getScore());
        // A move that changes nothing doesn't change the score either.
        assertFalse(game.moveTiles("A"));
        assertEquals(0, game.getScore());
    }
}
