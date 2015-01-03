package game;

import it.tejp.codeit.api.Competitor;
import it.tejp.codeit.api.Game;
import network.NewFileFromClientListener;
import network.ServerConnection;
import pong_sample.PongGame;
import pong_sample.PongMove;

import java.io.File;
import java.util.function.BiFunction;

/**
 * Created by tejp on 28/12/14.
 */
public class ServerController implements NewFileFromClientListener {

    private ServerConnection connection;
    private Game game;

    public ServerController() {
        this.connection = new ServerConnection(new File("TempFanceFile"), this);
        connection.startServering();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void start() {
        BiFunction<Competitor<PongGame, PongMove>, Competitor<PongGame, PongMove>, Game> gameFactory = (a, b) -> new PongGame(a, b);

        Model<PongGame, PongMove> model = new Model(100, gameFactory);


//        Model.CompetitorPairIterator pairIterator = model.getCompetitorPairIterator();

//        game = model.createNewGame(pairIterator.next());

        /**
         * wait for new competitors to be added. and make sure every new competitor plays agains every other competitor
         */

        while (true) {
//            if (game.isGameOver()) {
//                if (pairIterator.hasNext()) {
//                    game = model.createNewGame(pairIterator.next());
//                }
//            } else {
//                game.play();
//            }

        }
    }

    @Override
    public void newFileRecieved(String teamName, File f) {
        //Check to se if teamname is already present. If it is. Replace it's file with the new one
        System.out.println("Notified of new file in ServerControll. Fance");
    }

    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.start();
        System.out.println("End of main");
    }
}
