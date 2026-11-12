public class GameLauncher {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        ConsoleView view = new ConsoleView();
        GameController controller = new GameController(model , view, inputHandler);
        controller.startGame();
    }
}
