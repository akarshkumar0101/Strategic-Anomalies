package testing;

import game.Communication;
import game.Player;

public class TestingPlayer extends Player {

    private final String name;

    private final TestingGame game;
    private TestingFrame testingFrame;

    private Communication gameComm;

    public TestingPlayer(String name, TestingGame game) {
	super(null);
	this.name = name;
	this.game = game;

	gameComm = game.getCommForPlayer(this).connectLocally();
    }

    public String getName() {
	return name;
    }

    public TestingGame getGame() {
	return game;
    }

    public void setTestingFrame(TestingFrame testingFrame) {
	this.testingFrame = testingFrame;
    }

    public TestingFrame getTestingFrame() {
	return testingFrame;
    }

    public void setGameComm(Communication gameComm) {
	this.gameComm = gameComm;
    }

    public Communication getGameComm() {
	return gameComm;
    }

}
