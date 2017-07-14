package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
import testing.TestingClient;
import testing.TestingGame;
import testing.TestingServer;

public class Main {

    public static boolean test() throws Exception {
	Thread servThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingServer.main(null);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread.sleep(500);
	servThread.start();
	Thread client1Thead = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingClient.main(null);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	};
	client1Thead.start();
	Thread client2Thead = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingClient.main(null);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	};
	client2Thead.start();
	return true;
    }

    public static boolean servtest() throws Exception {
	ServerSocket servsock = new ServerSocket(8839);
	Socket sock = servsock.accept();
	Communication comm = new Communication(sock);
	System.out.println("serv running");
	System.out.println(comm.recieveObject());
	comm.sendObject("this is serv message");
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

    public static void main(String[] args) {
	try {
	    if (test()) {
		return;
	    }
	} catch (Exception e) {
	    return;
	}

	TestingGame tgame = new TestingGame();

	Unit warrior = new Warrior(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(3, 3));
	Unit guardian = new Guardian(tgame, tgame.getPlayer2(), Direction.LEFT, new Coordinate(4, 3));

	Unit pyromancer = new Pyromancer(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(3, 4));
	Unit aquamancer = new Aquamancer(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(4, 4));
	Unit lightningmancer = new Lightningmancer(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(5, 4));

	Unit scout = new Scout(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(3, 5));
	Unit archer = new Archer(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(4, 5));
	Unit hunter = new Hunter(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(5, 5));

	Unit darkmagicwitch = new DarkMagicWitch(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(3, 6));
	Unit lightmagicwitch = new LightMagicWitch(tgame, tgame.getPlayer1(), Direction.LEFT, new Coordinate(4, 6));

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

	tgame.startGame();

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
