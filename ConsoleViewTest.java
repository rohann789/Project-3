import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for ConsoleView using hard-coded boards. Output is captured with
 * systemOut() and input is faked with setSystemIn(), so no one has to type.
 *
 * @author Wyatt
 * @version Nov 19, 2026
 */
public class ConsoleViewTest
    extends TestCase
{
    /**
     * the view to test with
     */
    private ConsoleView view;

    // ----------------------------------------------------------
    /**
     * Creates a fresh view and clears captured output before each test.
     */
    public void setUp()
    {
        view = new ConsoleView();
        systemOut().clearHistory();
    }


    // ----------------------------------------------------------
    /**
     * Returns everything printed since the last clear.
     *
     * @return the captured output
     */
    private String printed()
    {
        return systemOut().getHistory();
    }


    // ----------------------------------------------------------
    /**
     * Asserts that displayBoard(board) throws IllegalArgumentException and
     * prints nothing.
     *
     * @param board
     *            the invalid board
     */
    private void assertDisplayBoardThrows(int[][] board)
    {
        Exception thrown = null;
        try
        {
            view.displayBoard(board);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals("", printed());
    }


    // ----------------------------------------------------------
    /**
     * Normal: every tile is printed and empty cells show as dots.
     */
    public void testDisplayBoard()
    {
        view.displayBoard(new int[][] {
            { 2, 0, 0, 2048 },
            { 0, 0, 0, 0 },
            { 0, 16, 0, 0 },
            { 0, 0, 0, 4 } });
        String out = printed();
        assertTrue(out.contains("2048"));
        assertTrue(out.contains("16"));
        assertTrue(out.contains("."));
        // 4 rows of cells plus 5 borders
        assertEquals(9, out.split("\n").length);
    }


    // ----------------------------------------------------------
    /**
     * Normal: every printed line is the same width, so the grid lines up
     * even with 4-digit tiles.
     */
    public void testDisplayBoardAligned()
    {
        view.displayBoard(new int[][] {
            { 2, 2048, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 1024, 0 },
            { 0, 0, 0, 4 } });
        String[] lines = printed().split("\n");
        for (String line : lines)
        {
            assertEquals(lines[0].length(), line.length());
        }
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a null board throws IllegalArgumentException.
     */
    public void testDisplayBoardNull()
    {
        assertDisplayBoardThrows(null);
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a 3x4 board or a board with a short row throws
     * IllegalArgumentException.
     */
    public void testDisplayBoardWrongSize()
    {
        assertDisplayBoardThrows(new int[3][4]);

        int[][] shortRow = new int[4][4];
        shortRow[2] = new int[3];
        assertDisplayBoardThrows(shortRow);
    }


    // ----------------------------------------------------------
    /**
     * Normal: displayMessage() prints the message on its own line.
     */
    public void testDisplayMessage()
    {
        view.displayMessage("Invalid input");
        assertFuzzyEquals("Invalid input\n", printed());
    }


    // ----------------------------------------------------------
    /**
     * Bad input: a null message throws IllegalArgumentException and prints
     * nothing.
     */
    public void testDisplayMessageNull()
    {
        Exception thrown = null;
        try
        {
            view.displayMessage(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals("", printed());
    }


    // ----------------------------------------------------------
    /**
     * Normal: the win, game over, and score messages print.
     */
    public void testWinGameOverScore()
    {
        view.displayWin();
        assertTrue(printed().contains("2048"));
        systemOut().clearHistory();

        view.displayGameOver();
        assertTrue(printed().toLowerCase().contains("game over"));
        systemOut().clearHistory();

        view.displayScore(120);
        assertTrue(printed().contains("120"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: clearScreen() prints the ANSI clear code.
     */
    public void testClearScreen()
    {
        view.clearScreen();
        assertTrue(printed().contains("\033[2J"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: getInput() returns each line the player types, after a
     * prompt that lists the commands.
     */
    public void testGetInput()
    {
        setSystemIn("w\nD\n");
        view = new ConsoleView();
        assertEquals("w", view.getInput());
        assertEquals("D", view.getInput());
        assertTrue(printed().contains("W/A/S/D"));
        assertTrue(printed().contains("U to undo"));
    }


    // ----------------------------------------------------------
    /**
     * Bad input: when the input stream ends, getInput() returns null
     * instead of crashing.
     */
    public void testGetInputEndOfStream()
    {
        setSystemIn("");
        view = new ConsoleView();
        assertNull(view.getInput());
    }
}
