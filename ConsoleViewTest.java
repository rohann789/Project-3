import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for ConsoleView using hard-coded boards. Output is captured with
 * systemOut() and input is faked with setSystemIn(), so no one has to type.
 *
 * @author Wyatt
 * @version Nov 13, 2026
 */
public class ConsoleViewTest
    extends TestCase
{
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


    /**
     * Returns everything printed since the last clear.
     */
    private String printed()
    {
        return systemOut().getHistory();
    }


    // ----------------------------------------------------------
    // displayBoard(int[][] board)
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


    /**
     * Bad input: a null board throws IllegalArgumentException.
     */
    public void testDisplayBoardNull()
    {
        try
        {
            view.displayBoard(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("", printed());
        }
    }


    /**
     * Bad input: a 3x4 board or a board with a short row throws
     * IllegalArgumentException.
     */
    public void testDisplayBoardWrongSize()
    {
        int[][] shortRow = new int[4][4];
        shortRow[2] = new int[3];
        int[][][] badBoards = { new int[3][4], shortRow };
        for (int[][] board : badBoards)
        {
            try
            {
                view.displayBoard(board);
                fail("Expected IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                // expected
            }
        }
        assertEquals("", printed());
    }


    // ----------------------------------------------------------
    // Messages
    // ----------------------------------------------------------

    /**
     * Normal: displayMessage prints the message on its own line.
     */
    public void testDisplayMessage()
    {
        view.displayMessage("Invalid input");
        assertFuzzyEquals("Invalid input\n", printed());
    }


    /**
     * Bad input: a null message throws IllegalArgumentException.
     */
    public void testDisplayMessageNull()
    {
        try
        {
            view.displayMessage(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("", printed());
        }
    }


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


    /**
     * Normal: clearScreen prints the ANSI clear code.
     */
    public void testClearScreen()
    {
        view.clearScreen();
        assertTrue(printed().contains("\033[2J"));
    }


    // ----------------------------------------------------------
    // getInput()
    // ----------------------------------------------------------

    /**
     * Normal: returns each line the player types, with a prompt.
     */
    public void testGetInput()
    {
        setSystemIn("w\nD\n");
        view = new ConsoleView();
        assertEquals("w", view.getInput());
        assertEquals("D", view.getInput());
        assertTrue(printed().contains("W/A/S/D"));
    }


    /**
     * Bad input: when the input stream ends, it returns null instead of
     * crashing.
     */
    public void testGetInputEndOfStream()
    {
        setSystemIn("");
        view = new ConsoleView();
        assertNull(view.getInput());
    }
}
