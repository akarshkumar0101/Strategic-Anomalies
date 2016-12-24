package main;

import javax.swing.JFrame;

import game.Game;
import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.interaction.effect.Affectable;
import game.interaction.effect.Effect;
import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentReporter;
import game.unit.Knight;
import game.unit.Unit;
import game.util.Direction;
import game.util.PathFinder;
import testingframe.TestingFrame;

public class Main {

    public static Unit movableUnit;

    public static boolean test() {
	return false;
    }

    public static void main(String[] args) {
	if (test()) {
	    return;
	}

	Team team1 = new Team(1), team2 = new Team(2);
	Player player1 = team1.getPlayers()[0], player2 = team2.getPlayers()[0];

	Game game = new Game(team1, team2);
	Board board = game.getBoard();

	Unit unit1 = new Knight(game, player1, Direction.LEFT, new Coordinate(1, 1));
	Unit unit2 = new Knight(game, player2, Direction.RIGHT, new Coordinate(5, 6));
	movableUnit = unit1;

	board.linkBoardToUnit(unit1);
	board.linkBoardToUnit(unit2);

	for (byte y = -1; y < 12; y++) {
	    for (byte x = -1; x < 12; x++) {
		Coordinate coor = new Coordinate(x, y);
		if (!board.isInBoard(coor)) {
		    System.out.print("X ");
		} else if (board.getUnitAt(coor) == unit1) {
		    System.out.print("1 ");
		} else if (board.getUnitAt(coor) == unit2) {
		    System.out.print("2 ");
		} else if (PathFinder.getPath(unit1, coor) != null) {
		    System.out.print("- ");
		} else {
		    System.out.print("* ");
		}
	    }
	    System.out.println();
	}

	TestingFrame testingFrame = new TestingFrame(board, player1, player2);

	testingFrame.setSize(1400, 1000);
	testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	testingFrame.setVisible(true);

	unit2.getHealthProp().setPropertyValue(20);

	IncidentReporter randomReporter = new IncidentReporter();
	Effect moveEffect = new Effect(EffectType.OTHER, unit2, null) {
	    @Override
	    public void performEffect(Affectable affectbleObject, Object... args) {
		Unit unit = (Unit) affectbleObject;
		Coordinate toCoor = null;
		int i = 0;
		do {
		    if (i > 0) {
			int ordin = (unit.getPosProp().getDirFacingProp().getCurrentPropertyValue().ordinal() + 1)
				% Direction.values().length;
			unit.getPosProp().getDirFacingProp().setPropertyValue(Direction.values()[ordin]);
		    }
		    toCoor = Coordinate.shiftCoor(unit.getPosProp().getCurrentPropertyValue(),
			    unit.getPosProp().getDirFacingProp().getCurrentPropertyValue());
		} while (i++ < -1 || !board.isInBoard(toCoor) || board.getUnitAt(toCoor) != null);

		unit.getPosProp().setPropertyValue(toCoor);
	    }
	};
	unit1.addEffect(moveEffect, randomReporter);
	unit2.addEffect(moveEffect, randomReporter);

	while (true) {
	    try {
		Thread.sleep(300);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    randomReporter.reportIncident();

	    testingFrame.updateInformation();
	    testingFrame.repaint();
	}

    }
}
