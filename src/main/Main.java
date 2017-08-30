package main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.UIManager;

import game.Communication;
import testing.TestingClient;
import testing.TestingServer;

// magic witch select 4

// can select unit
//can attack friendly unit
//if it kills iteself, dont crash.
//dont use template

public class Main {

    static {
	try {
	    // UIManager.LookAndFeelInfo[] looks =
	    // UIManager.getInstalledLookAndFeels();
	    // javax.swing.plaf.metal.MetalLookAndFeel
	    // javax.swing.plaf.nimbus.NimbusLookAndFeel
	    // com.sun.java.swing.plaf.motif.MotifLookAndFeel
	    // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
	    // com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void test() throws Exception {
	Thread servThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingServer.main(null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread client1Thead = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingClient.main(null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread client2Thead = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingClient.main(null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};

	servThread.start();
	Thread.sleep(100);
	client1Thead.start();
	client2Thead.start();
    }

    public static boolean servtest() throws Exception {
	ServerSocket servsock = new ServerSocket(8839);
	Socket sock = servsock.accept();
	Communication comm = new Communication(sock);
	System.out.println("serv running");
	System.out.println(comm.recieveObject());
	comm.sendObject("this is serv message");
	servsock.close();
	return true;
    }

    public static boolean clienttest() throws Exception {
	Socket sock = new Socket("localhost", 8839);
	Communication comm = new Communication(sock);
	System.out.println("client running");
	System.out.println(comm.recieveObject());
	comm.sendObject("this is client message");
	return true;
    }

    public static void main(String[] args) throws Exception {
	boolean test = false;
	try {
	    if (test) {
		Main.test();
		return;
	    }
	} catch (Exception e) {
	    return;
	}
	System.out.println("Server ip: ");
	Scanner scan = new Scanner(System.in);
	String ip = scan.nextLine();
	scan.close();
	TestingClient.main(ip);

    }
}
// Game game = new Game(new Account[] { new Account() }, new Account[] { new
// Account() });
//
// Team team1 = game.getTeam1(), team2 = game.getTeam2();
// Player player1 = team1.getPlayers()[0], player2 = team2.getPlayers()[0];
//
// Board board = game.getBoard();
//
// Unit unit1 = new Warrior(game, player1, Direction.LEFT, new Coordinate(1,
// 1));
// Unit unit2 = new Warrior(game, player2, Direction.RIGHT, new Coordinate(5,
// 6));
// movableUnit = unit1;
//
// board.linkBoardToUnit(unit1);
// board.linkBoardToUnit(unit2);
//
// for (byte y = -1; y < 12; y++) {
// for (byte x = -1; x < 12; x++) {
// Coordinate coor = new Coordinate(x, y);
// if (!board.isInBoard(coor)) {
// System.out.print("X ");
// } else if (board.getUnitAt(coor) == unit1) {
// System.out.print("1 ");
// } else if (board.getUnitAt(coor) == unit2) {
// System.out.print("2 ");
// } else if (PathFinder.getPath(unit1, coor) != null) {
// System.out.print("- ");
// } else {
// System.out.print("* ");
// }
// }
// System.out.println();
// }
//
// TestingFrame testingFrame = new TestingFrame(game);
//
// testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// testingFrame.setVisible(true);
//
// unit2.getHealthProp().setPropertyValue(20);
//
// IncidentReporter randomReporter = new IncidentReporter();
// Effect moveEffect = new Effect(EffectType.OTHER, game,
// Condition.trueCondition) {
// @Override
// public void performEffect(Affectable affectbleObject, Object... args) {
// Unit unit = (Unit) affectbleObject;
// Coordinate toCoor = null;
// int i = 0;
// do {
// if (i > 0) {
// int ordin =
// (unit.getPosProp().getDirFacingProp().getCurrentPropertyValue().ordinal() +
// 1)
// % Direction.values().length;
// unit.getPosProp().getDirFacingProp().setPropertyValue(Direction.values()[ordin]);
// }
// toCoor = Coordinate.shiftCoor(unit.getPosProp().getCurrentPropertyValue(),
// unit.getPosProp().getDirFacingProp().getCurrentPropertyValue());
// } while (i++ < -1 || !board.isInBoard(toCoor) || board.getUnitAt(toCoor) !=
// null);
//
// unit.getPosProp().setPropertyValue(toCoor);
// }
// };
// unit1.addEffect(moveEffect, randomReporter);
// unit2.addEffect(moveEffect, randomReporter);
//
// while (true) {
// try {
// Thread.sleep(1000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// randomReporter.reportIncident();
//
// testingFrame.updateInformation();
// testingFrame.repaint();
// }
