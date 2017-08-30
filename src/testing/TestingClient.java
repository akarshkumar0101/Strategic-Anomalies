package testing;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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

	TestingClient.newGame(servComm, first);
    }

    public static void newGame(Communication servComm, boolean first) {
	File file = new File("C:\\Users\\akars\\Documents\\GitHub\\Strategic-Anomalies\\resources\\template"
		+ (first ? "1" : "2") + ".TAOtmplt");

	// *display input*:

	TestingSetup testingSetup = new TestingSetup();
	SetupTemplate homeSel = testingSetup.getFinalTemplate();
	testingSetup.dispose();

	// *input*:
	// SetupTemplate homeSel = null;
	// try {
	// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	// homeSel = (SetupTemplate) ois.readObject();
	// ois.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }

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
