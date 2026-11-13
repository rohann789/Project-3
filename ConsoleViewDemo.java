// -------------------------------------------------------------------------
/**
 * A tiny driver that shows the thin view working on its own, using
 * hard-coded boards. It doesn't need GameModel or GameController.
 *
 * Run it with: java ConsoleViewDemo
 *
 * @author Wyatt
 * @version Nov 13, 2026
 */
public class ConsoleViewDemo
{
    // ----------------------------------------------------------
    /**
     * Prints a few hard-coded boards and every kind of message, then reads
     * one command and echoes it back.
     *
     * @param args
     *            ignored
     */
    public static void main(String[] args)
    {
        ConsoleView view = new ConsoleView();

        view.displayMessage("Starting board:");
        view.displayBoard(new int[][] {
            { 0, 0, 2, 0 },
            { 0, 0, 0, 0 },
            { 0, 4, 0, 0 },
            { 0, 0, 0, 0 } });
        view.displayScore(0);

        view.displayMessage("\nMid-game board:");
        view.displayBoard(new int[][] {
            { 2, 4, 8, 16 },
            { 32, 64, 128, 256 },
            { 512, 1024, 0, 0 },
            { 0, 0, 0, 2 } });
        view.displayScore(5124);
        view.displayMessage("Invalid input. Please enter W, A, S, or D.");

        view.displayMessage("\nWinning board:");
        view.displayBoard(new int[][] {
            { 2048, 4, 2, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 2 } });
        view.displayWin();

        view.displayMessage("\nLosing board:");
        view.displayBoard(new int[][] {
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 },
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 } });
        view.displayGameOver();

        String command = view.getInput();
        view.displayMessage("You typed: " + command);
    }
}
