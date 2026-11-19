import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.NoSuchElementException;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for TileDeque, following the TileDeque section of the Test Plan,
 * plus tests for the row/column helper (loadLine, writeLine, processLine).
 *
 * @author arrushshah
 * @version Nov 19, 2026
 */
public class TileDequeTest
    extends TestCase
{
    /**
     * the deque to test with
     */
    private TileDeque deque;

    // ----------------------------------------------------------
    /**
     * Creates a fresh, empty deque before each test.
     */
    public void setUp()
    {
        deque = new TileDeque();
    }


    // ----------------------------------------------------------
    /**
     * Adds each value to the back of the deque, in order.
     *
     * @param values
     *            the tile values to add
     */
    private void addAll(int... values)
    {
        for (int value : values)
        {
            deque.addTile(value);
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns the deque's tiles as an array, front to back.
     *
     * @return the tiles in order
     */
    private int[] contents()
    {
        ArrayDeque<Integer> copy = deque.getTiles();
        int[] result = new int[copy.size()];
        int i = 0;
        for (int value : copy)
        {
            result[i] = value;
            i++;
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Asserts that two int arrays hold the same values.
     *
     * @param expected
     *            the expected values
     * @param actual
     *            the actual values
     */
    private void assertArray(int[] expected, int[] actual)
    {
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }


    // ----------------------------------------------------------
    /**
     * Returns a 4x4 board for the helper tests.
     *
     * @return a new sample board
     */
    private int[][] sampleBoard()
    {
        return new int[][] {
            { 2, 0, 2, 4 },
            { 0, 4, 0, 4 },
            { 8, 0, 0, 0 },
            { 8, 2, 0, 2 } };
    }


    // ----------------------------------------------------------
    /**
     * Asserts that addTile(value) throws IllegalArgumentException and
     * leaves the deque unchanged.
     *
     * @param value
     *            the invalid tile value
     */
    private void assertAddThrows(int value)
    {
        int sizeBefore = deque.size();
        Exception thrown = null;
        try
        {
            deque.addTile(value);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(sizeBefore, deque.size());
    }


    // ----------------------------------------------------------
    /**
     * Asserts that removeTile() throws NoSuchElementException.
     */
    private void assertRemoveThrows()
    {
        Exception thrown = null;
        try
        {
            deque.removeTile();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof NoSuchElementException);
    }


    // ----------------------------------------------------------
    /**
     * Asserts that removeLastTile() throws NoSuchElementException.
     */
    private void assertRemoveLastThrows()
    {
        Exception thrown = null;
        try
        {
            deque.removeLastTile();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof NoSuchElementException);
    }


    // ----------------------------------------------------------
    /**
     * Asserts that processLine() throws IllegalArgumentException and
     * leaves the board unchanged.
     *
     * @param board
     *            the board to process
     * @param index
     *            the row or column number
     * @param direction
     *            the move direction
     */
    private void assertProcessThrows(int[][] board, int index,
        String direction)
    {
        String before = Arrays.deepToString(board);
        Exception thrown = null;
        try
        {
            deque.processLine(board, index, direction);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(before, Arrays.deepToString(board));
    }


    // ----------------------------------------------------------
    /**
     * Asserts that loadLine() throws IllegalArgumentException.
     *
     * @param board
     *            the board to load from
     * @param index
     *            the row or column number
     */
    private void assertLoadThrows(int[][] board, int index)
    {
        Exception thrown = null;
        try
        {
            deque.loadLine(board, index, "A");
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
     * Normal: add 2 then 4, and peekTile() returns 2.
     */
    public void testAddTile()
    {
        addAll(2, 4);
        assertEquals(2, (int)deque.peekTile());
        assertEquals(2, deque.size());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: adding 0 (an empty space) throws
     * IllegalArgumentException.
     */
    public void testAddTileZero()
    {
        assertAddThrows(0);
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: negative numbers, 1, and non powers of 2 throw
     * IllegalArgumentException.
     */
    public void testAddTileOtherInvalid()
    {
        assertAddThrows(-2);
        assertAddThrows(1);
        assertAddThrows(3);
        assertAddThrows(6);
        assertAddThrows(100);
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Normal: after 2 and 4 are added, removeTile() returns 2, then 4.
     */
    public void testRemoveTile()
    {
        addAll(2, 4);
        assertEquals(2, (int)deque.removeTile());
        assertEquals(4, (int)deque.removeTile());
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: removeTile() on an empty deque throws
     * NoSuchElementException.
     */
    public void testRemoveTileEmpty()
    {
        assertRemoveThrows();
    }


    // ----------------------------------------------------------
    /**
     * Normal: after 2 and 4 are added, removeLastTile() returns 4, then 2.
     */
    public void testRemoveLastTile()
    {
        addAll(2, 4);
        assertEquals(4, (int)deque.removeLastTile());
        assertEquals(2, (int)deque.removeLastTile());
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: removeLastTile() on an empty deque throws
     * NoSuchElementException.
     */
    public void testRemoveLastTileEmpty()
    {
        assertRemoveLastThrows();
    }


    // ----------------------------------------------------------
    /**
     * Normal: mixing adds with removes from both ends keeps the order
     * correct at every step.
     */
    public void testInterleavedAddsAndRemoves()
    {
        addAll(2, 4);
        // [2, 4]
        assertEquals(2, (int)deque.removeTile());
        // [4]
        addAll(8);
        // [4, 8]
        assertEquals(4, (int)deque.peekTile());
        addAll(16);
        // [4, 8, 16]
        assertEquals(16, (int)deque.removeLastTile());
        // [4, 8]
        addAll(32, 64);
        // [4, 8, 32, 64]
        assertEquals(4, (int)deque.removeTile());
        // [8, 32, 64]
        assertEquals(64, (int)deque.removeLastTile());
        // [8, 32]
        assertArray(new int[] { 8, 32 }, contents());
        assertEquals(8, (int)deque.removeTile());
        // [32]
        assertEquals(32, (int)deque.removeLastTile());
        // []
        assertTrue(deque.isEmpty());

        // The deque can be reused after being emptied.
        addAll(2);
        assertEquals(2, (int)deque.peekTile());
    }


    // ----------------------------------------------------------
    /**
     * Normal: removes after a merge come out in the merged order, and new
     * tiles go behind them.
     */
    public void testInterleavedAfterMerge()
    {
        addAll(2, 2, 4);
        deque.mergeTiles(true);
        // [4, 4]
        assertEquals(4, (int)deque.removeTile());
        // [4]
        addAll(8);
        // [4, 8]
        assertEquals(8, (int)deque.removeLastTile());
        // [4]
        assertEquals(4, (int)deque.removeTile());
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: removing past empty in the middle of a sequence throws,
     * and the deque still works afterward.
     */
    public void testInterleavedRemovePastEmpty()
    {
        addAll(2);
        deque.removeLastTile();
        assertRemoveThrows();
        addAll(4);
        assertEquals(4, (int)deque.removeTile());
    }


    // ----------------------------------------------------------
    /**
     * Normal: peekTile() returns the front value without removing it.
     */
    public void testPeekTile()
    {
        addAll(8, 2);
        assertEquals(8, (int)deque.peekTile());
        assertEquals(8, (int)deque.peekTile());
        assertArray(new int[] { 8, 2 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: peekTile() on an empty deque returns null and doesn't
     * crash.
     */
    public void testPeekTileEmpty()
    {
        assertNull(deque.peekTile());
    }


    // ----------------------------------------------------------
    /**
     * Normal: isEmpty() is true on a new deque and false after one add.
     */
    public void testIsEmpty()
    {
        assertTrue(deque.isEmpty());
        deque.addTile(2);
        assertFalse(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: isEmpty() is true again after everything has been
     * removed.
     */
    public void testIsEmptyAfterRemoving()
    {
        addAll(2, 4);
        deque.removeTile();
        deque.removeLastTile();
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Normal: [2,2,2] becomes [4,2] when processed from the front.
     */
    public void testMergeTilesThreeSame()
    {
        addAll(2, 2, 2);
        deque.mergeTiles(true);
        assertArray(new int[] { 4, 2 }, contents());
        assertEquals(4, deque.getMergeScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: [2,2,4] becomes [4,4], not [8] (no double merge).
     */
    public void testMergeTilesNoDoubleMerge()
    {
        addAll(2, 2, 4);
        deque.mergeTiles(true);
        assertArray(new int[] { 4, 4 }, contents());
        assertEquals(4, deque.getMergeScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: processed from the back (right and down moves), [2,2,2]
     * becomes [2,4], because the back tiles merge first.
     */
    public void testMergeTilesFromBack()
    {
        addAll(2, 2, 2);
        deque.mergeTiles(false);
        assertArray(new int[] { 2, 4 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: [4,2,2] from the back becomes [4,4], not [8].
     */
    public void testMergeTilesFromBackNoDoubleMerge()
    {
        addAll(4, 2, 2);
        deque.mergeTiles(false);
        assertArray(new int[] { 4, 4 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: [2,2,2,2] makes two merges and scores 8.
     */
    public void testMergeTilesTwoPairs()
    {
        addAll(2, 2, 2, 2);
        deque.mergeTiles(true);
        assertArray(new int[] { 4, 4 }, contents());
        assertEquals(8, deque.getMergeScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: tiles with no matching neighbor are unchanged.
     */
    public void testMergeTilesNoMatches()
    {
        addAll(2, 4, 2, 4);
        deque.mergeTiles(true);
        assertArray(new int[] { 2, 4, 2, 4 }, contents());
        assertEquals(0, deque.getMergeScore());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: merging an empty deque leaves it empty and doesn't throw.
     */
    public void testMergeTilesEmpty()
    {
        deque.mergeTiles(true);
        assertTrue(deque.isEmpty());
        deque.mergeTiles(false);
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.getMergeScore());
    }


    // ----------------------------------------------------------
    /**
     * Normal: getTiles() returns the tiles in order after a merge.
     */
    public void testGetTiles()
    {
        addAll(4, 4, 8, 2);
        deque.mergeTiles(true);
        ArrayDeque<Integer> result = deque.getTiles();
        assertEquals(3, result.size());
        assertEquals(8, (int)result.removeFirst());
        assertEquals(8, (int)result.removeFirst());
        assertEquals(2, (int)result.removeFirst());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: editing the deque returned by getTiles() doesn't change
     * the original.
     */
    public void testGetTilesReturnsCopy()
    {
        addAll(2, 4);
        ArrayDeque<Integer> copy = deque.getTiles();
        copy.clear();
        copy.add(1024);
        assertArray(new int[] { 2, 4 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: after adding 3 tiles, clear() empties the deque.
     */
    public void testClear()
    {
        addAll(2, 4, 8);
        deque.clear();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: clearing an already-empty deque does nothing and doesn't
     * throw.
     */
    public void testClearEmpty()
    {
        deque.clear();
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Normal: loadLine() loads a row left to right, skipping empty cells,
     * for both row directions.
     */
    public void testLoadLineRow()
    {
        deque.loadLine(sampleBoard(), 0, "A");
        assertArray(new int[] { 2, 2, 4 }, contents());
        deque.loadLine(sampleBoard(), 0, "D");
        assertArray(new int[] { 2, 2, 4 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: loadLine() loads a column top to bottom, skipping empty
     * cells.
     */
    public void testLoadLineColumn()
    {
        deque.loadLine(sampleBoard(), 0, "W");
        assertArray(new int[] { 2, 8, 8 }, contents());
        deque.loadLine(sampleBoard(), 1, "s");
        assertArray(new int[] { 4, 2 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: loadLine() replaces whatever was in the deque before.
     */
    public void testLoadLineClearsFirst()
    {
        addAll(1024, 1024);
        deque.loadLine(sampleBoard(), 2, "A");
        assertArray(new int[] { 8 }, contents());
    }


    // ----------------------------------------------------------
    /**
     * Normal: loadLine() doesn't change the board.
     */
    public void testLoadLineLeavesBoard()
    {
        int[][] board = sampleBoard();
        deque.loadLine(board, 3, "D");
        assertEquals(Arrays.deepToString(sampleBoard()),
            Arrays.deepToString(board));
    }


    // ----------------------------------------------------------
    /**
     * Normal: writeLine() for A packs tiles to the left and fills the
     * rest with 0.
     */
    public void testWriteLineLeft()
    {
        int[][] board = sampleBoard();
        addAll(4, 4);
        deque.writeLine(board, 0, "A");
        assertArray(new int[] { 4, 4, 0, 0 }, board[0]);
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    /**
     * Normal: writeLine() for D packs tiles to the right in the same
     * order.
     */
    public void testWriteLineRight()
    {
        int[][] board = sampleBoard();
        addAll(2, 4);
        deque.writeLine(board, 0, "D");
        assertArray(new int[] { 0, 0, 2, 4 }, board[0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: writeLine() for W packs a column to the top, and for S to
     * the bottom.
     */
    public void testWriteLineColumns()
    {
        int[][] board = sampleBoard();
        addAll(16, 2);
        deque.writeLine(board, 0, "W");
        assertEquals(16, board[0][0]);
        assertEquals(2, board[1][0]);
        assertEquals(0, board[2][0]);
        assertEquals(0, board[3][0]);

        addAll(16, 2);
        deque.writeLine(board, 3, "S");
        assertEquals(0, board[0][3]);
        assertEquals(0, board[1][3]);
        assertEquals(16, board[2][3]);
        assertEquals(2, board[3][3]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: writeLine() only changes the chosen line.
     */
    public void testWriteLineOnlyChangesOneLine()
    {
        int[][] board = sampleBoard();
        addAll(4);
        deque.writeLine(board, 2, "A");
        int[][] expected = sampleBoard();
        expected[2] = new int[] { 4, 0, 0, 0 };
        assertEquals(Arrays.deepToString(expected),
            Arrays.deepToString(board));
    }


    // ----------------------------------------------------------
    /**
     * Bad input: writing more tiles than the line holds throws
     * IllegalArgumentException and keeps the tiles.
     */
    public void testWriteLineTooManyTiles()
    {
        addAll(2, 4, 8, 16, 32);
        Exception thrown = null;
        try
        {
            deque.writeLine(sampleBoard(), 0, "A");
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(5, deque.size());
    }


    // ----------------------------------------------------------
    /**
     * Normal: processLine() works for all four directions on the same
     * board, using the one helper.
     */
    public void testProcessLineAllDirections()
    {
        int[][] board = sampleBoard();
        assertEquals(4, deque.processLine(board, 0, "A"));
        assertArray(new int[] { 4, 4, 0, 0 }, board[0]);

        board = sampleBoard();
        assertEquals(8, deque.processLine(board, 1, "D"));
        assertArray(new int[] { 0, 0, 0, 8 }, board[1]);

        board = sampleBoard();
        assertEquals(16, deque.processLine(board, 0, "W"));
        assertEquals(2, board[0][0]);
        assertEquals(16, board[1][0]);
        assertEquals(0, board[2][0]);
        assertEquals(0, board[3][0]);

        board = sampleBoard();
        assertEquals(16, deque.processLine(board, 0, "S"));
        assertEquals(0, board[0][0]);
        assertEquals(0, board[1][0]);
        assertEquals(2, board[2][0]);
        assertEquals(16, board[3][0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: [2,2,2,_] moved right becomes [_,_,2,4], because the tiles
     * nearest the right edge merge first.
     */
    public void testProcessLineRightMergeOrder()
    {
        int[][] board = new int[4][4];
        board[0] = new int[] { 2, 2, 2, 0 };
        deque.processLine(board, 0, "D");
        assertArray(new int[] { 0, 0, 2, 4 }, board[0]);
    }


    // ----------------------------------------------------------
    /**
     * Normal: processing an empty line leaves it empty and scores 0.
     */
    public void testProcessLineEmpty()
    {
        int[][] board = new int[4][4];
        assertEquals(0, deque.processLine(board, 2, "W"));
        assertEquals(Arrays.deepToString(new int[4][4]),
            Arrays.deepToString(board));
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an invalid, empty, or null direction throws
     * IllegalArgumentException and leaves the board alone.
     */
    public void testHelperInvalidDirection()
    {
        assertProcessThrows(sampleBoard(), 0, "P");
        assertProcessThrows(sampleBoard(), 0, "");
        assertProcessThrows(sampleBoard(), 0, "WA");
        assertProcessThrows(sampleBoard(), 0, null);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an out-of-range index, a null board, or a non-square
     * board throws IllegalArgumentException.
     */
    public void testHelperBadBoardOrIndex()
    {
        assertLoadThrows(sampleBoard(), 4);
        assertLoadThrows(sampleBoard(), -1);
        assertLoadThrows(null, 0);
        assertLoadThrows(new int[3][4], 0);
    }
}
