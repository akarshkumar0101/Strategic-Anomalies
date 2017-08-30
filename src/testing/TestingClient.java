package testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import game.Communication;
import game.Game;
import setup.SetupTemplate;

public class TestingClient {

    // GOAL FOR TODAY: FINISH A PROTOTYPE CLIENT AND SERVER PROGRAM COMPLETELY.

    public static final String serverIP = "localhost";

    public static long randomSeed;

    public static void main(String... args) throws UnknownHostException, IOException {
	// Scanner scan = new Scanner(System.in);
	// System.out.println("Enter server ip: ");
	// String ip = scan.nextLine();
	String servip = serverIP;
	if (args != null) {
	    servip = args[0];
	}
	Socket sock = new Socket(servip, TestingServer.PORT);
	System.out.println("connected to " + servip + "!");

	Communication servComm = new Communication(sock);

	if (!TestingServer.INIT_STRING.equals(servComm.recieveObject())) {
	    return;
	}

	TestingClient.randomSeed = (long) servComm.recieveObject();
	// System.out.println(randomSeed);

	boolean first = (boolean) servComm.recieveObject();

	Scanner scan = new Scanner(System.in);
	System.out.println("Type 1 for manual setup of pieces, 2 for file setup");
	int option = scan.nextInt();

	TestingClient.newGame(servComm, first, option == 1);
    }

    public static void newGame(Communication servComm, boolean first, boolean chooseTemplate) {
	SetupTemplate homeSel = null;
	// *display input*:
	if (chooseTemplate) {
	    TestingSetup testingSetup = new TestingSetup();
	    homeSel = testingSetup.getFinalTemplate();
	    testingSetup.dispose();
	}
	// *file input*:
	else {
	    InputStream templateis = TestingClient.class
		    .getResourceAsStream("/template" + (first ? "1" : "2") + ".TAOtmplt");

	    try {
		ObjectInputStream ois = new ObjectInputStream(templateis);
		homeSel = (SetupTemplate) ois.readObject();
		ois.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	// *output*:

	// try {
	// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
	// oos.writeObject(homeSel);
	// oos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }

	servComm.sendObject(homeSel);
	SetupTemplate awaySel = (SetupTemplate) servComm.recieveObject();

	Game game = new Game(servComm, TestingClient.randomSeed, first);
	game.setupBoardWithTemplates(homeSel, awaySel);
	game.startGame();
    }

}
