package testing;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import game.Communication;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;
import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.DarkMagicWitch;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.LightMagicWitch;
import game.unit.listofunits.Lightningmancer;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.Scout;
import game.unit.listofunits.Warrior;
import setup.SetupTemplate;

public class TestingClient {

    // GOAL FOR TODAY: FINISH A PROTOTYPE CLIENT AND SERVER PROGRAM COMPLETELY.

    public static final String serverIP = "localhost";

    public static long randomSeed;

    public static void main(String[] args) throws UnknownHostException, IOException {
	Socket sock = new Socket(TestingClient.serverIP, TestingServer.PORT);

	Communication servComm = new Communication(sock);

	if (!TestingServer.INIT_STRING.equals(servComm.recieveObject())) {
	    return;
	}

	TestingClient.randomSeed = (long) servComm.recieveObject();
	// System.out.println(randomSeed);

	boolean first = (boolean) servComm.recieveObject();

	TestingClient.newGame(servComm, first);
    }

    public static void oldGame(Communication servComm, boolean first) {
	TestingGame tgame = TestingClient.establishGame(servComm, TestingClient.randomSeed, first);
	try {
	    tgame.startGame();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    public static void newGame(Communication servComm, boolean first) {
	TestingSetup testingSetup = new TestingSetup();
	SetupTemplate homeSel = testingSetup.getFinalTemplate();
	testingSetup.dispose();
	servComm.sendObject(homeSel);
	SetupTemplate awaySel = (SetupTemplate) servComm.recieveObject();

	TestingGame tgame = new TestingGame(servComm, TestingClient.randomSeed, first);
	TestingPlayer player1 = (TestingPlayer) tgame.getPlayer1(), player2 = (TestingPlayer) tgame.getPlayer2();
	tgame.getBoard().setupBoard(tgame, player1, player2, homeSel, awaySel);
	tgame.startGame();
    }

    public static TestingGame establishGame(Communication servComm, long randomSeed, boolean first) {
	TestingGame tgame = new TestingGame(servComm, randomSeed, first);

	TestingPlayer player1 = (TestingPlayer) tgame.getPlayer1(), player2 = (TestingPlayer) tgame.getPlayer2();

	Unit warrior = new Warrior(tgame, player1, Direction.LEFT, new Coordinate(3, 3));
	Unit guardian = new Guardian(tgame, player2, Direction.LEFT, new Coordinate(4, 3));

	Unit pyromancer = new Pyromancer(tgame, player1, Direction.LEFT, new Coordinate(3, 4));
	Unit aquamancer = new Aquamancer(tgame, player1, Direction.LEFT, new Coordinate(4, 4));
	Unit lightningmancer = new Lightningmancer(tgame, player1, Direction.LEFT, new Coordinate(5, 4));

	Unit scout = new Scout(tgame, player1, Direction.LEFT, new Coordinate(3, 5));
	Unit archer = new Archer(tgame, player1, Direction.LEFT, new Coordinate(4, 5));
	Unit hunter = new Hunter(tgame, player1, Direction.LEFT, new Coordinate(5, 5));

	Unit darkmagicwitch = new DarkMagicWitch(tgame, player1, Direction.LEFT, new Coordinate(3, 6));
	Unit lightmagicwitch = new LightMagicWitch(tgame, player1, Direction.LEFT, new Coordinate(4, 6));

	tgame.getBoard().linkBoardToUnit(warrior);
	tgame.getBoard().linkBoardToUnit(guardian);

	tgame.getBoard().linkBoardToUnit(pyromancer);
	tgame.getBoard().linkBoardToUnit(aquamancer);
	tgame.getBoard().linkBoardToUnit(lightningmancer);

	tgame.getBoard().linkBoardToUnit(scout);
	tgame.getBoard().linkBoardToUnit(archer);
	tgame.getBoard().linkBoardToUnit(hunter);

	tgame.getBoard().linkBoardToUnit(darkmagicwitch);
	tgame.getBoard().linkBoardToUnit(lightmagicwitch);

	return tgame;
    }

}
