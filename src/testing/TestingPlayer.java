package testing;

import game.Communication;
import game.Player;
import game.Team;
import testing.gameframe.TestingFrame;

public class TestingPlayer extends Player {

    private final String name;

    private TestingFrame testingFrame;

    private Communication gameComm;

    public TestingPlayer(String name, Communication gameComm) {
	super(new Team(null));
	this.name = name;

	this.gameComm = gameComm;
    }

    public String getName() {
	return name;
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
