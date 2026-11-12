import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for TileDeque, following the TileDeque section of the Test Plan,
 * plus tests for the row/column helper (loadLine, writeLine, processLine).
 *
 * @author arrushshah
 * @version Nov 12, 2026
 */
public class TileDequeTest
    extends TestCase
{
    private TileDeque deque;


    // ----------------------------------------------------------
    /**
     * Creates a fresh, empty deque before each test.
     */
    public void setUp()
    {
        deque = new TileDeque();
    }


    /**
     * Adds each value to the deque in order.
     */
    private void addAll(int... values)
    {
        for (int value : values)
        {
            deque.addTile(value);
        }
    }


    /**
     * Returns the deque's tiles as an array, front to back.
     */
    private int[] contents()
    {
        ArrayDeque<Integer> copy = deque.getTiles();
        int[] result = new int[copy.size()];
        int i = 0;
        for (int value : copy)
        {
            result[i++] = value;
        }
        return result;
    }


    /**
     * Asserts that two int arrays have the same values.
     */
    private void assertArray(int[] expected, int[] actual)
    {
        assertEquals(java.util.Arrays.toString(expected),
            java.util.Arrays.toString(actual));
    }


    // ----------------------------------------------------------
    // addTile(int value)
    // ----------------------------------------------------------

    /**
     * Normal: add 2 then 4, and peekTile() returns 2.
     */
    public void testAddTile()
    {
        addAll(2, 4);
        assertEquals(2, (int)deque.peekTile());
    }


    /**
     * Bad input: adding 0 (an empty space) throws
     * IllegalArgumentException.
     */
    public void testAddTileZero()
    {
        try
        {
            deque.addTile(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(deque.isEmpty());
        }
    }


    /**
     * Bad input: negative numbers, 1, and non powers of 2 throw
     * IllegalArgumentException.
     */
    public void testAddTileOtherInvalid()
    {
        int[] badValues = { -2, 1, 3, 6, 100 };
        for (int value : badValues)
        {
            try
            {
                deque.addTile(value);
                fail("Expected IllegalArgumentException for " + value);
            }
            catch (IllegalArgumentException e)
            {
                // expected
            }
        }
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    // removeTile()
    // ----------------------------------------------------------

    /**
     * Normal: after 2 and 4 are added, it returns 2, then 4.
     */
    public void testRemoveTile()
    {
        addAll(2, 4);
        assertEquals(2, (int)deque.removeTile());
        assertEquals(4, (int)deque.removeTile());
    }


    /**
     * Bad input: on an empty deque it throws NoSuchElementException.
     */
    public void testRemoveTileEmpty()
    {
        try
        {
            deque.removeTile();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e)
        {
            // expected
        }
    }


    // ----------------------------------------------------------
    // removeLastTile()
    // ----------------------------------------------------------

    /**
     * Normal: after 2 and 4 are added, it returns 4, then 2.
     */
    public void testRemoveLastTile()
    {
        addAll(2, 4);
        assertEquals(4, (int)deque.removeLastTile());
        assertEquals(2, (int)deque.removeLastTile());
    }


    /**
     * Bad input: on an empty deque it throws NoSuchElementException.
     */
    public void testRemoveLastTileEmpty()
    {
        try
        {
            deque.removeLastTile();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e)
        {
            // expected
        }
    }


    // ----------------------------------------------------------
    // peekTile()
    // ----------------------------------------------------------

    /**
     * Normal: returns the front value without removing it.
     */
    public void testPeekTile()
    {
        addAll(8, 2);
        assertEquals(8, (int)deque.peekTile());
        assertEquals(8, (int)deque.peekTile());
        assertArray(new int[] { 8, 2 }, contents());
    }


    /**
     * Bad input: on an empty deque it returns null and doesn't crash.
     */
    public void testPeekTileEmpty()
    {
        assertNull(deque.peekTile());
    }


    // ----------------------------------------------------------
    // isEmpty()
    // ----------------------------------------------------------

    /**
     * Normal: true on a new deque, false after one add.
     */
    public void testIsEmpty()
    {
        assertTrue(deque.isEmpty());
        deque.addTile(2);
        assertFalse(deque.isEmpty());
    }


    /**
     * Bad input: true again after everything has been removed.
     */
    public void testIsEmptyAfterRemoving()
    {
        addAll(2, 4);
        deque.removeTile();
        deque.removeLastTile();
        assertTrue(deque.isEmpty());
    }


    // ----------------------------------------------------------
    // mergeTiles(boolean fromFront)
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


    /**
     * Normal: [4,2,2] from the back becomes [4,4], not [8].
     */
    public void testMergeTilesFromBackNoDoubleMerge()
    {
        addAll(4, 2, 2);
        deque.mergeTiles(false);
        assertArray(new int[] { 4, 4 }, contents());
    }


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


    /**
     * Bad input: on an empty deque it stays empty and doesn't throw.
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
    // getTiles()
    // ----------------------------------------------------------

    /**
     * Normal: returns the tiles in order after a merge.
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


    /**
     * Bad input: editing the returned deque doesn't change the original.
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
    // clear()
    // ----------------------------------------------------------

    /**
     * Normal: after adding 3 tiles, isEmpty() returns true.
     */
    public void testClear()
    {
        addAll(2, 4, 8);
        deque.clear();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }


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
    // Row/column helper: loadLine, writeLine, processLine
    // ----------------------------------------------------------

    /**
     * Returns a 4x4 board for the helper tests.
     */
    private int[][] sampleBoard()
    {
        return new int[][] {
            { 2, 0, 2, 4 },
            { 0, 4, 0, 4 },
            { 8, 0, 0, 0 },
            { 8, 2, 0, 2 } };
    }


    /**
     * Normal: a row loads left to right, skipping empty cells.
     */
    public void testLoadLineRow()
    {
        deque.loadLine(sampleBoard(), 0, "A");
        assertArray(new int[] { 2, 2, 4 }, contents());
        deque.loadLine(sampleBoard(), 0, "D");
        assertArray(new int[] { 2, 2, 4 }, contents());
    }


    /**
     * Normal: a column loads top to bottom, skipping empty cells.
     */
    public void testLoadLineColumn()
    {
        deque.loadLine(sampleBoard(), 0, "W");
        assertArray(new int[] { 2, 8, 8 }, contents());
        deque.loadLine(sampleBoard(), 1, "s");
        assertArray(new int[] { 4, 2 }, contents());
    }


    /**
     * Normal: loading replaces whatever was in the deque before.
     */
    public void testLoadLineClearsFirst()
    {
        addAll(1024, 1024);
        deque.loadLine(sampleBoard(), 2, "A");
        assertArray(new int[] { 8 }, contents());
    }


    /**
     * Normal: loading doesn't change the board.
     */
    public void testLoadLineLeavesBoard()
    {
        int[][] board = sampleBoard();
        deque.loadLine(board, 3, "D");
        assertEquals(java.util.Arrays.deepToString(sampleBoard()),
            java.util.Arrays.deepToString(board));
    }


    /**
     * Normal: writing a row for A packs tiles to the left and zero-fills
     * the rest.
     */
    public void testWriteLineLeft()
    {
        int[][] board = sampleBoard();
        addAll(4, 4);
        deque.writeLine(board, 0, "A");
        assertArray(new int[] { 4, 4, 0, 0 }, board[0]);
        assertTrue(deque.isEmpty());
    }


    /**
     * Normal: writing a row for D packs tiles to the right in the same
     * order.
     */
    public void testWriteLineRight()
    {
        int[][] board = sampleBoard();
        addAll(2, 4);
        deque.writeLine(board, 0, "D");
        assertArray(new int[] { 0, 0, 2, 4 }, board[0]);
    }


    /**
     * Normal: writing a column for W packs tiles to the top, and for S to
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


    /**
     * Normal: writing only touches the chosen line.
     */
    public void testWriteLineOnlyChangesOneLine()
    {
        int[][] board = sampleBoard();
        addAll(4);
        deque.writeLine(board, 2, "A");
        int[][] expected = sampleBoard();
        expected[2] = new int[] { 4, 0, 0, 0 };
        assertEquals(java.util.Arrays.deepToString(expected),
            java.util.Arrays.deepToString(board));
    }


    /**
     * Normal: processLine works for all four directions on the same
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


    /**
     * Normal: an empty line stays empty and scores 0.
     */
    public void testProcessLineEmpty()
    {
        int[][] board = new int[4][4];
        assertEquals(0, deque.processLine(board, 2, "W"));
        assertEquals(java.util.Arrays.deepToString(new int[4][4]),
            java.util.Arrays.deepToString(board));
    }


    /**
     * Bad input: an invalid or null direction throws
     * IllegalArgumentException and leaves the board alone.
     */
    public void testHelperInvalidDirection()
    {
        String[] badDirections = { "P", "", "WA", null };
        for (String dir : badDirections)
        {
            int[][] board = sampleBoard();
            try
            {
                deque.processLine(board, 0, dir);
                fail("Expected IllegalArgumentException for " + dir);
            }
            catch (IllegalArgumentException e)
            {
                assertEquals(java.util.Arrays.deepToString(sampleBoard()),
                    java.util.Arrays.deepToString(board));
            }
        }
    }


    /**
     * Bad input: an out-of-range index, a null board, or a non-square
     * board throws IllegalArgumentException.
     */
    public void testHelperBadBoardOrIndex()
    {
        Object[][] cases = {
            { sampleBoard(), 4 },
            { sampleBoard(), -1 },
            { null, 0 },
            { new int[3][4], 0 } };
        for (Object[] c : cases)
        {
            try
            {
                deque.loadLine((int[][])c[0], (Integer)c[1], "A");
                fail("Expected IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                // expected
            }
        }
    }


    /**
     * Bad input: writing more tiles than the line holds throws
     * IllegalArgumentException.
     */
    public void testWriteLineTooManyTiles()
    {
        addAll(2, 4, 8, 16, 32);
        try
        {
            deque.writeLine(sampleBoard(), 0, "A");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(5, deque.size());
        }
    }
}
