package testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import game.Game;
import io.Communication;
import main.Main;
import setup.SetupTemplate;
import testing.ui.LobbyWindow;
import testing.ui.SetupWindow;

public class TestingClient {

    // GOAL FOR CURRENT: finish basic challenge stuff

    public Communication lobbyServComm;

    public long randomSeed;

    public String serverIP;
    public String name;
    public String password;

    public LobbyWindow lobbyWindow;

    public TestingClient(String serverIP, String name, String password) {
	this.serverIP = serverIP;
	this.name = name;
	this.password = password;
    }

    public static void main(String... args) {
	String serverIP = null;
	String name = null;
	String password = null;
	try {
	    serverIP = args[0];
	} catch (NullPointerException | IndexOutOfBoundsException e) {
	    System.out.println("Type in serverIP: ");
	    serverIP = Main.getStringInput();
	}
	try {
	    name = args[1];
	} catch (NullPointerException | IndexOutOfBoundsException e) {
	    System.out.println("Type in your username: ");
	    name = Main.getStringInput();
	}
	try {
	    password = args[2];
	} catch (NullPointerException | IndexOutOfBoundsException e) {
	    System.out.println("Type in your password: ");
	    password = Main.getStringInput();
	}
	TestingClient client = new TestingClient(serverIP, name, password);
	client.start();
    }

    public void start() {
	try {
	    Socket sock = new Socket(serverIP, LobbyServer.LOBBY_PORT);
	    System.out.println("Client connected to " + serverIP + ":" + LobbyServer.LOBBY_PORT + "!");

	    lobbyServComm = new Communication(sock);

	    if (!LobbyServer.INIT_LOBBY_STRING.equals(lobbyServComm.recieveObject())) {
		System.err.println("Could not verify server");
		return;
	    }
	    lobbyServComm.sendObject(name);
	    lobbyServComm.sendObject(password);

	    boolean success = (boolean) lobbyServComm.recieveObject();
	    if (!success) {
		System.err.println("Could not connect to server - invalid login");
		return;
	    }

	    lobbyWindow = new LobbyWindow(this);

	    while (true) {
		Object obj = lobbyServComm.recieveObject();
		if (obj.getClass() == String[].class) {
		    String[] lobbyNames = (String[]) obj;

		    lobbyWindow.set(lobbyNames);

		    System.out.println("In lobby server: ");
		    for (String s : lobbyNames) {
			System.out.println("\t" + s);
		    }
		    System.out.println("\n");
		} else {
		    String oppName = (String) obj;
		    System.out.println("Matched with " + oppName);
		    break;
		}
	    }

	    int gamePort = (int) lobbyServComm.recieveObject();

	    newGame(serverIP, gamePort, name);

	} catch (Exception e) {
	    throw new RuntimeException("Something went wrong with client", e);
	}
    }

    public void clickedName(String name) {
	lobbyServComm.sendObject(name);
    }

    public void newGame(String gameServerIP, int gameServerPort, String name) {
	lobbyWindow.getGUI().setVisible(false);
	try {
	    Socket gameSock = new Socket(gameServerIP, gameServerPort);
	    Communication gameServComm = new Communication(gameSock);

	    gameServComm.sendObject(name);

	    if (!GameServer.INIT_GAME_STRING.equals(gameServComm.recieveObject())) {
		System.err.println("Could not verify game server");
		return;
	    }

	    randomSeed = (long) gameServComm.recieveObject();

	    String firstPlayer = (String) gameServComm.recieveObject();
	    boolean first = name.equals(firstPlayer);

	    System.out.println("Type 1 for manual setup of pieces, 2 for file setup");
	    int option = 2;// Main.getIntInput();

	    SetupTemplate homeSel = null;
	    // *display input*:
	    if (option == 1) {
		SetupWindow setupWindow = new SetupWindow();
		homeSel = setupWindow.getFinalTemplate();
		setupWindow.dispose();
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

	    gameServComm.sendObject(homeSel);
	    SetupTemplate awaySel = (SetupTemplate) gameServComm.recieveObject();

	    gameServComm.sendObject(name);
	    String oppname = (String) gameServComm.recieveObject();

	    Game game = new Game(gameServComm, randomSeed, first, name, oppname);
	    game.setupBoardWithTemplates(homeSel, awaySel);
	    game.startGame();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
