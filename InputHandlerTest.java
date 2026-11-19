
import java.io.ByteArrayInputStream;
import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for InputHandler.
 *
 * @author
 * @version Nov 19, 2026
 */
public class InputHandlerTest extends TestCase
{

    /**
     * Sets up simulated user input.
     *
     * @param input
     *            the input to simulate
     */
    private void setInput(String input)
    {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }


    // ----------------------------------------------------------
    // getInput()
    // ----------------------------------------------------------

    /**
     * Normal: entering 'w' returns 'w'.
     */
    public void testGetInputW()
    {
        setInput("w\n");
        InputHandler handler = new InputHandler();

        assertEquals("w", handler.getInput());
    }


    /**
     * Normal: entering 'a' returns 'a'.
     */
    public void testGetInputA()
    {
        setInput("a\n");
        InputHandler handler = new InputHandler();

        assertEquals("a", handler.getInput());
    }


    /**
     * Normal: entering 's' returns 's'.
     */
    public void testGetInputS()
    {
        setInput("s\n");
        InputHandler handler = new InputHandler();

        assertEquals("s", handler.getInput());
    }


    /**
     * Normal: entering 'd' returns 'd'.
     */
    public void testGetInputD()
    {
        setInput("d\n");
        InputHandler handler = new InputHandler();

        assertEquals("d", handler.getInput());
    }


    /**
     * Normal: uppercase input is converted to lowercase.
     */
    public void testGetInputUppercase()
    {
        setInput("W\n");
        InputHandler handler = new InputHandler();

        assertEquals("w", handler.getInput());
    }


    /**
     * Bad input: invalid input is rejected until a valid move is entered.
     */
    public void testGetInputInvalidThenValid()
    {
        setInput("x\n1\nhello\nw\n");
        InputHandler handler = new InputHandler();

        assertEquals("w", handler.getInput());
    }


    /**
     * Extra case: mixed uppercase and lowercase input.
     */
    public void testGetInputMixedCase()
    {
        setInput("X\nA\n");
        InputHandler handler = new InputHandler();

        assertEquals("a", handler.getInput());
    }
}
