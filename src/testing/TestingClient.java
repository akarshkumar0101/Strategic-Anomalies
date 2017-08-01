package testing;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import game.Communication;
import setup.SetupTemplate;

public class TestingClient {

    // GOAL FOR TODAY: FINISH A PROTOTYPE CLIENT AND SERVER PROGRAM COMPLETELY.

    public static final String serverIP = "localhost";

    public static long randomSeed;

    public static void main(String[] args) throws UnknownHostException, IOException {
	Scanner scan = new Scanner(System.in);
	System.out.println("Enter server ip: ");
	String ip = scan.nextLine();
	Socket sock = new Socket(ip, TestingServer.PORT);
	System.out.println("connected");

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
	tgame.setupBoardWithTemplates(homeSel, awaySel);
	tgame.startGame();
    }

}
