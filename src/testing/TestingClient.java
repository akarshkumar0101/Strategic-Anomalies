package testing;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import game.Communication;
import game.Game;
import main.Main;
import setup.SetupTemplate;

public class TestingClient {

    // GOAL FOR TODAY: FINISH A PROTOTYPE CLIENT AND SERVER PROGRAM COMPLETELY.

    public static long randomSeed;

    public static void main(String... args) {
	try {

	    String serverIP = null;
	    if (args == null || args.length == 0) {
		System.out.println("Type in serverIP: ");
		serverIP = Main.getStringInput();
	    } else {
		serverIP = args[0];
	    }
	    Socket sock;
	    sock = new Socket(serverIP, TestingServer.PORT);
	    System.out.println("Client connected to " + serverIP + "!");

	    Communication servComm;
	    servComm = new Communication(sock);

	    if (!TestingServer.INIT_STRING.equals(servComm.recieveObject())) {
		return;
	    }

	    TestingClient.randomSeed = (long) servComm.recieveObject();
	    // System.out.println(randomSeed);

	    boolean first = (boolean) servComm.recieveObject();

	    System.out.println("Type 1 for manual setup of pieces, 2 for file setup");
	    int option = Main.getIntInput();

	    TestingClient.newGame(servComm, first, option == 1);

	} catch (Exception e) {
	    throw new RuntimeException("Something went wrong with client", e);
	}
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
