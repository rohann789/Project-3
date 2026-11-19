import student.TestCase;

// -------------------------------------------------------------------------
/**
 * Tests for InputHandler. Input is faked with setSystemIn(), so no one has
 * to type.
 *
 * @author Matthew
 * @version Nov 19, 2026
 */
public class InputHandlerTest
    extends TestCase
{
    // ----------------------------------------------------------
    /**
     * Fakes the given input, then returns what a new InputHandler reads.
     *
     * @param input
     *            the text the player "types"
     * @return the value getInput() returns
     */
    private String readFrom(String input)
    {
        setSystemIn(input);
        InputHandler handler = new InputHandler();
        return handler.getInput();
    }


    // ----------------------------------------------------------
    /**
     * Normal: entering 'w' returns "w".
     */
    public void testGetInputW()
    {
        assertEquals("w", readFrom("w\n"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: entering 'a' returns "a".
     */
    public void testGetInputA()
    {
        assertEquals("a", readFrom("a\n"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: entering 's' returns "s".
     */
    public void testGetInputS()
    {
        assertEquals("s", readFrom("s\n"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: entering 'd' returns "d".
     */
    public void testGetInputD()
    {
        assertEquals("d", readFrom("d\n"));
    }


    // ----------------------------------------------------------
    /**
     * Normal: uppercase input is converted to lowercase.
     */
    public void testGetInputUppercase()
    {
        assertEquals("w", readFrom("W\n"));
    }


    // ----------------------------------------------------------
    /**
     * Bad input: invalid input is rejected until a valid move is entered.
     */
    public void testGetInputInvalidThenValid()
    {
        assertEquals("w", readFrom("x\n1\nhello\nw\n"));
    }


    // ----------------------------------------------------------
    /**
     * Bad input: an invalid uppercase letter is rejected, then a valid
     * uppercase letter is accepted and lowercased.
     */
    public void testGetInputMixedCase()
    {
        assertEquals("a", readFrom("X\nA\n"));
    }
}
