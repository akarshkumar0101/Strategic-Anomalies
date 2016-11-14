package main;

import javax.swing.JFrame;

import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.board.NormalBoard;
import game.unit.Knight;
import game.unit.Unit;
import game.util.Direction;
import game.util.PathFinder;
import testingframe.TestingFrame;

public class Main {

	public static void main(String[] args) {
		Player player1 = new Player(), player2 = new Player();
		Team team1 = new Team(player1), team2 = new Team(player2);

		Board board = new NormalBoard();

		Unit unit1 = new Knight(player1, team1, board, Direction.LEFT, null);
		board.getSquare(new Coordinate(5, 5)).setUnitOnTop(unit1);

		Unit unit2 = new Knight(player2, team2, board, Direction.LEFT, null);
		board.getSquare(new Coordinate(5, 6)).setUnitOnTop(unit2);

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

		testingFrame.setSize(900, 900);
		testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testingFrame.setVisible(true);
	}

}
