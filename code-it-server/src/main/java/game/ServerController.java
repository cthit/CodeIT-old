package game;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import it.tejp.codeit.api.GameMechanic;
import network.NetworkEventListener;
import network.ServerConnection;
import pong_sample.PongGame;
import pong_sample.PongMove;
import utils.JavaSourceFromString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

/**
 * Created by tejp on 28/12/14.
 */
public class ServerController implements NetworkEventListener {

    private ServerConnection connection;
    private Model model;
    private Game game;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public ServerController() {
        this.connection = new ServerConnection(new File("source_server.jar"), this);
// TODO        executor.shutdown(); // executorn skapar ej fler trådar
//        executor.shutdownNow();   // executorn dödar allat hejvilt
//        if (Thread.interrupted()) {
//            throw new RuntimeException("interrupted");
//        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void start() {
        connection.startServering();

        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);
        model = new Model(gameFactory);

    }

    @Override
    public void newFileRecieved(String teamName, File f) {
        String code = null;
        try {
            code = new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        code = code.replaceFirst("package\\s+.+?;", "package pong_sample;");
        code = code.replaceFirst("public\\s+class\\s+.+?\\s", "public class " + teamName + " ");
        code = code.replace("System.out", "//System.out");

        try {
            GameMechanic<PongGame,PongMove> pongPaddle = (GameMechanic<PongGame,PongMove>) JavaSourceFromString.compile(code, teamName + ".java", "pong_sample");
            pongPaddle.createTestGame();
            executor.submit(() -> {
                model.handleContributionFromCompetitor(teamName, pongPaddle);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Double> requestRatings() {
        return model.getRating();
    }

    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.start();
        System.out.println("End of main");
    }
}
