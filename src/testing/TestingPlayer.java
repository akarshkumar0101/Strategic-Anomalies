package testing;

import game.Communication;
import game.Player;

public class TestingPlayer extends Player {

    private final TestingGame game;
    private TestingFrame testingFrame;

    private Communication gameComm;

    public TestingPlayer(TestingGame game) {
	super(null);
	this.game = game;

	gameComm = game.getCommForPlayer(this).connectLocally();
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
