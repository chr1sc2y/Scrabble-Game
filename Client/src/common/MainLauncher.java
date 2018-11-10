package common;
/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */


import com.Board;
import com.GameWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;


public class MainLauncher extends Application {
    public static Stage window;
    private static Scene loginScene, lobbyScene, gameScene;
    private static Thread sessionThread;
    private static Parent sideRoot;
    private static Parent gameRoot;
    private static Board board;

    public static MainLauncher instance;

    public static MainLauncher getInstance() { return instance; }

    public MainLauncher() { instance = this; }

    public Board getBoard() { return board; }

    /**
     * start the login screen. main
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // for later scene switch
        window = primaryStage;

        // disable resize
        window.setResizable(false);

        // login scene
        Parent loginRoot = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        loginScene = new Scene(loginRoot);

        // lobby scene for later switch
        Parent lobbyRoot = FXMLLoader.load(getClass().getResource("LobbyView.fxml"));
        lobbyScene = new Scene(lobbyRoot);

        // sidepane of game window
        sideRoot = FXMLLoader.load(getClass().getResource("SideView.fxml"));

        // game window
        gameRoot = (BorderPane) FXMLLoader.load(getClass().getResource("MainView.fxml"));
        board = new Board();
        ((Pane) ((BorderPane) gameRoot).getCenter()).getChildren().add(board);
//        ((BorderPane) gameRoot).setCenter(new Board());
        ((Pane) ((BorderPane) gameRoot).getRight()).getChildren().add(sideRoot);
//        ((BorderPane) gameRoot).setRight(sideRoot);
        gameScene = new Scene(gameRoot);
        gameScene.getStylesheets().add("scrabble.css");



        // show login scene by default
//        window.setScene(loginScene);
//        Scene boardScene = new Scene(new Board());

        window.setScene(loginScene);
        window.setTitle("Login");
        window.show();
        
        window.setOnCloseRequest(e -> {
        	System.exit(0);
        });
//        List<String> players = new ArrayList<>();
//        players.add("player1");
//        players.add("player2");
//        switchToGame(players);

        // start client listener
        ClientListener clientListener = new ClientListener();
        Thread x = new Thread(clientListener);
        x.start();
    }

    /* swith to the lobby scene */
    public static void switchToLobby() {
        // set transparent before relocate, for visually comfortability
        window.setOpacity(0.0);
        // set title and scene
        window.setTitle("Lobby: " + LoginController.getInstance().getPlayerName());
        window.setScene(lobbyScene);
        // relocate window and show
        moveToCenter();
        window.setOpacity(1.0);
    }

    /* switch to the login scene */
    public static void switchToLogin() {
        window.setTitle("Login");
        window.setScene(loginScene);
    }

    /* switch to the game scene */
    public static void switchToGame(List<String> playerNames){

        Platform.runLater(() -> {
            // game scene for later switch
            // add player
//            int numPlayers = playerNames.size();
//            Player[] players = new Player[numPlayers];
//            for (int i = 0; i < numPlayers; i++)
//                players[i] = new Player(playerNames.get(i));
//            gameScene = new Scene(new GameWindow(players, sideRoot));
//            gameScene.getStylesheets().add("scrabble.css");
//            // set transparent before relocate, for visually comfortability
//            window.setOpacity(0.0);
//            // set title and scene
//            window.setTitle("Scrabble");
//            window.setScene(gameScene);
//            // relocate window and show
//            moveToCenter();
//            window.setOpacity(1.0);

            // set transparent before relocate, for visually comfortability
            window.setOpacity(0.0);
            // set title and scene
            window.setTitle("Scrabble: " + LoginController.getInstance().getPlayerName());
            for (String player: playerNames)
                SideController.getInstance().addPlayer(player);
            window.setScene(gameScene);
            // relocate window and show
            moveToCenter();
            window.setOpacity(1.0);
        });
    }

    /* move the window to the center of the screen */
    public static void moveToCenter() {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        window.setX((primScreenBounds.getWidth() - window.getWidth()) / 2);
        window.setY((primScreenBounds.getHeight() - window.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
