import java.util.Scanner;

// -------------------------------------------------------------------------
/**
 * The console view for 2048. Prints the board, the score and messages,
 * and reads the player's next command. It never changes the board or
 * merges tiles; it only shows what the controller gives it.
 *
 * @author Wyatt
 * @version Nov 13, 2026
 */
public class ConsoleView
{
    /** Number of rows and columns on the board. */
    private static final int SIZE = 4;
    /** Width of one cell; fits 2048 and larger tiles. */
    private static final int CELL_WIDTH = 6;
    /** ANSI codes that move the cursor home and clear the screen. */
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private final Scanner input;


    // ----------------------------------------------------------
    /**
     * Creates the view and the Scanner used to read the player's input.
     */
    public ConsoleView()
    {
        input = new Scanner(System.in);
    }


    // ----------------------------------------------------------
    /**
     * Prints the current 4x4 board as an ASCII grid. Empty cells are shown
     * as a dot.
     *
     * @param board
     *            the board to print
     * @throws IllegalArgumentException
     *             if the board is null or not 4x4
     */
    public void displayBoard(int[][] board)
    {
        if (board == null || board.length != SIZE)
        {
            throw new IllegalArgumentException("Board must be 4x4");
        }
        for (int[] row : board)
        {
            if (row == null || row.length != SIZE)
            {
                throw new IllegalArgumentException("Board must be 4x4");
            }
        }
        System.out.print(boardToString(board));
    }


    // ----------------------------------------------------------
    /**
     * Prints the player's current score.
     *
     * @param score
     *            the score to show
     */
    public void displayScore(int score)
    {
        System.out.println("Score: " + score);
    }


    // ----------------------------------------------------------
    /**
     * Tells the player the game is over.
     */
    public void displayGameOver()
    {
        System.out.println("Game over! No more moves are possible.");
    }


    // ----------------------------------------------------------
    /**
     * Tells the player they reached 2048.
     */
    public void displayWin()
    {
        System.out.println("You win! You made the 2048 tile!");
    }


    // ----------------------------------------------------------
    /**
     * Prompts for and reads the player's next command.
     *
     * @return the line the player typed, or null if the input stream has
     *         ended so the controller can exit
     */
    public String getInput()
    {
        System.out.print("Enter a move (W/A/S/D): ");
        if (!input.hasNextLine())
        {
            System.out.println();
            return null;
        }
        return input.nextLine();
    }


    // ----------------------------------------------------------
    /**
     * Prints a message to the player, such as an invalid input warning.
     *
     * @param message
     *            the message to print
     * @throws IllegalArgumentException
     *             if message is null
     */
    public void displayMessage(String message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("Message is null");
        }
        System.out.println(message);
    }


    // ----------------------------------------------------------
    /**
     * Clears the terminal so each new board replaces the old one instead
     * of printing under it. Uses ANSI escape codes, which work in macOS
     * Terminal, Linux and Windows Terminal. The Eclipse console ignores
     * them, so there the board just prints below the old one.
     */
    public void clearScreen()
    {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }


    /**
     * Builds the ASCII grid for a board that is already known to be 4x4.
     */
    private static String boardToString(int[][] board)
    {
        StringBuilder border = new StringBuilder("+");
        for (int c = 0; c < SIZE; c++)
        {
            border.append("-".repeat(CELL_WIDTH)).append("+");
        }
        border.append("\n");

        StringBuilder out = new StringBuilder(border);
        for (int[] row : board)
        {
            out.append("|");
            for (int value : row)
            {
                String text = value == 0 ? "." : String.valueOf(value);
                out.append(center(text)).append("|");
            }
            out.append("\n").append(border);
        }
        return out.toString();
    }


    /**
     * Centers text in a cell of CELL_WIDTH characters.
     */
    private static String center(String text)
    {
        int padding = Math.max(0, CELL_WIDTH - text.length());
        int left = padding / 2;
        return " ".repeat(left) + text + " ".repeat(padding - left);
    }
}
