package testing;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import game.Communication;
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

}
