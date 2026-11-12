import java.util.ArrayDeque;
import java.util.NoSuchElementException;

// -------------------------------------------------------------------------
/**
 * Temporarily holds the tile values of one row or column while a move is
 * being processed. GameModel loads a line into the deque, merges it, and
 * writes it back to the board. The deque is cleared and reused for every
 * row or column, and it never stores the full board.
 *
 * Lines are always loaded in the same order: rows left to right, columns
 * top to bottom. Moves toward the front of that order (A and W) are
 * processed from the front; moves toward the back (D and S) are processed
 * from the back, so the tiles closest to the direction of movement are
 * always handled first.
 *
 * @author arrushshah
 * @version Nov 12, 2026
 */
public class TileDeque
{
    private final ArrayDeque<Integer> tiles;
    private int mergeScore;


    // ----------------------------------------------------------
    /**
     * Creates an empty deque.
     */
    public TileDeque()
    {
        tiles = new ArrayDeque<Integer>();
        mergeScore = 0;
    }


    // ----------------------------------------------------------
    /**
     * Adds a tile value to the back of the deque.
     *
     * @param value
     *            the tile value, which must be a power of 2 that is at
     *            least 2
     * @throws IllegalArgumentException
     *             if value is 0, negative, 1, or not a power of 2
     */
    public void addTile(int value)
    {
        if (!isValidTile(value))
        {
            throw new IllegalArgumentException(
                "Invalid tile value: " + value);
        }
        tiles.addLast(value);
    }


    // ----------------------------------------------------------
    /**
     * Removes and returns the tile at the front of the deque.
     *
     * @return the front tile
     * @throws NoSuchElementException
     *             if the deque is empty
     */
    public Integer removeTile()
    {
        if (tiles.isEmpty())
        {
            throw new NoSuchElementException("The deque is empty");
        }
        return tiles.removeFirst();
    }


    // ----------------------------------------------------------
    /**
     * Removes and returns the tile at the back of the deque. Used for
     * right (D) and down (S) moves.
     *
     * @return the back tile
     * @throws NoSuchElementException
     *             if the deque is empty
     */
    public Integer removeLastTile()
    {
        if (tiles.isEmpty())
        {
            throw new NoSuchElementException("The deque is empty");
        }
        return tiles.removeLast();
    }


    // ----------------------------------------------------------
    /**
     * Combines matching neighboring tiles in movement order. Each tile can
     * merge at most once, so [2, 2, 4] becomes [4, 4], not [8]. The deque
     * keeps its front-to-back order afterward. An empty deque stays empty.
     *
     * The points earned by this merge can be read with getMergeScore().
     *
     * @param fromFront
     *            true to process from the front (left and up moves), false
     *            to process from the back (right and down moves)
     */
    public void mergeTiles(boolean fromFront)
    {
        mergeScore = 0;
        ArrayDeque<Integer> merged = new ArrayDeque<Integer>();
        while (!tiles.isEmpty())
        {
            int current = fromFront ? tiles.removeFirst() : tiles.removeLast();
            Integer next = fromFront ? tiles.peekFirst() : tiles.peekLast();
            if (next != null && next == current)
            {
                // Consume the partner so it can't merge again this move.
                if (fromFront)
                {
                    tiles.removeFirst();
                }
                else
                {
                    tiles.removeLast();
                }
                current *= 2;
                mergeScore += current;
            }
            if (fromFront)
            {
                merged.addLast(current);
            }
            else
            {
                merged.addFirst(current);
            }
        }
        tiles.addAll(merged);
    }


    // ----------------------------------------------------------
    /**
     * Returns the points earned by the most recent call to mergeTiles(),
     * which is the sum of every new tile created by that merge.
     *
     * @return the score from the last merge, or 0 if nothing merged
     */
    public int getMergeScore()
    {
        return mergeScore;
    }


    // ----------------------------------------------------------
    /**
     * Returns a copy of the tiles, front to back, so callers can't change
     * this deque.
     *
     * @return a copy of the tiles
     */
    public ArrayDeque<Integer> getTiles()
    {
        return new ArrayDeque<Integer>(tiles);
    }


    // ----------------------------------------------------------
    /**
     * Removes all tiles and resets the merge score. Does nothing if the
     * deque is already empty.
     */
    public void clear()
    {
        tiles.clear();
        mergeScore = 0;
    }


    // ----------------------------------------------------------
    /**
     * Returns the front tile without removing it.
     *
     * @return the front tile, or null if the deque is empty
     */
    public Integer peekTile()
    {
        return tiles.peekFirst();
    }


    // ----------------------------------------------------------
    /**
     * Returns whether the deque has no tiles.
     *
     * @return true if the deque is empty
     */
    public boolean isEmpty()
    {
        return tiles.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Returns the number of tiles in the deque.
     *
     * @return the number of tiles
     */
    public int size()
    {
        return tiles.size();
    }


    // ----------------------------------------------------------
    /**
     * Clears the deque and loads one row or column of the board into it,
     * skipping empty (0) cells. Rows are read left to right and columns top
     * to bottom, whatever the direction, so the same code handles all four
     * moves.
     *
     * @param board
     *            the square game board
     * @param index
     *            the row number for A/D, or the column number for W/S
     * @param direction
     *            the move: W, A, S, or D (any capitalization)
     * @throws IllegalArgumentException
     *             if the direction is invalid, the board is null or not
     *             square, or index is out of range
     */
    public void loadLine(int[][] board, int index, String direction)
    {
        boolean isRow = isRowMove(direction);
        checkLine(board, index);
        clear();
        for (int pos = 0; pos < board.length; pos++)
        {
            int value = isRow ? board[index][pos] : board[pos][index];
            if (value != 0)
            {
                addTile(value);
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Writes the deque back into one row or column of the board, emptying
     * the deque. Tiles are packed against the side the move goes toward:
     * left for A, top for W, right for D, bottom for S. Remaining cells are
     * set to 0.
     *
     * @param board
     *            the square game board to write into
     * @param index
     *            the row number for A/D, or the column number for W/S
     * @param direction
     *            the move: W, A, S, or D (any capitalization)
     * @throws IllegalArgumentException
     *             if the direction is invalid, the board is null or not
     *             square, index is out of range, or the deque holds more
     *             tiles than fit in the line
     */
    public void writeLine(int[][] board, int index, String direction)
    {
        boolean isRow = isRowMove(direction);
        boolean fromFront = isFrontMove(direction);
        checkLine(board, index);
        int size = board.length;
        if (tiles.size() > size)
        {
            throw new IllegalArgumentException(
                "Too many tiles for a line of length " + size);
        }
        for (int step = 0; step < size; step++)
        {
            // A/W fill from position 0 upward; D/S fill from the end down.
            int pos = fromFront ? step : size - 1 - step;
            int value = 0;
            if (!tiles.isEmpty())
            {
                value = fromFront ? removeTile() : removeLastTile();
            }
            if (isRow)
            {
                board[index][pos] = value;
            }
            else
            {
                board[pos][index] = value;
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Loads one row or column, merges it in the direction of the move, and
     * writes it back to the board.
     *
     * @param board
     *            the square game board to update
     * @param index
     *            the row number for A/D, or the column number for W/S
     * @param direction
     *            the move: W, A, S, or D (any capitalization)
     * @return the points earned by merges in this line
     * @throws IllegalArgumentException
     *             under the same conditions as loadLine()
     */
    public int processLine(int[][] board, int index, String direction)
    {
        loadLine(board, index, direction);
        mergeTiles(isFrontMove(direction));
        int points = mergeScore;
        writeLine(board, index, direction);
        return points;
    }


    // ----------------------------------------------------------
    /**
     * Returns true for A and D (row moves) and false for W and S (column
     * moves).
     */
    private static boolean isRowMove(String direction)
    {
        String dir = normalize(direction);
        return dir.equals("A") || dir.equals("D");
    }


    /**
     * Returns true for A and W (moves toward index 0) and false for D and
     * S.
     */
    private static boolean isFrontMove(String direction)
    {
        String dir = normalize(direction);
        return dir.equals("A") || dir.equals("W");
    }


    /**
     * Uppercases a direction and checks that it is W, A, S, or D.
     */
    private static String normalize(String direction)
    {
        if (direction == null)
        {
            throw new IllegalArgumentException("Direction is null");
        }
        String dir = direction.trim().toUpperCase();
        if (!(dir.equals("W") || dir.equals("A") || dir.equals("S")
            || dir.equals("D")))
        {
            throw new IllegalArgumentException(
                "Invalid direction: " + direction);
        }
        return dir;
    }


    /**
     * Checks that the board is square and the index is a valid line.
     */
    private static void checkLine(int[][] board, int index)
    {
        if (board == null || board.length == 0)
        {
            throw new IllegalArgumentException("Board is null or empty");
        }
        for (int[] row : board)
        {
            if (row == null || row.length != board.length)
            {
                throw new IllegalArgumentException("Board must be square");
            }
        }
        if (index < 0 || index >= board.length)
        {
            throw new IllegalArgumentException(
                "Line index out of range: " + index);
        }
    }


    /**
     * Returns true if value is a power of 2 that is at least 2.
     */
    private static boolean isValidTile(int value)
    {
        return value >= 2 && (value & (value - 1)) == 0;
    }
}
