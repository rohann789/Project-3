import java.util.Scanner;
public class InputHandler {
    public InputHandler() {
    }
    public String getInput() {
        Scanner scan = new Scanner(System.in);
        String input = "";
        while(!(input.equals("w") || input.equals("a") || input.equals("s") || input.equals("d"))) {
            System.out.print("Enter a move (w/a/s/d): ");
            input = scan.nextLine();
            input = input.toLowerCase();
        }
        return input;
    }
}
