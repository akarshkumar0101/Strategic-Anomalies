package game;

import testing.TestingFrame;

/**
 * Player that is in a team and a game, will be tied to a data stream that gives
 * inputs, either by live program input, or input over Internet (multiplayer
 * online)
 * 
 * @author Akarsh
 *
 */
public class Player {

    private final Team team;

    private final String name;

    private TestingFrame testingFrame;

    private Communication gameComm;

    public Player(String name, Communication gameComm) {
	team = new Team(this);
	this.name = name;

	this.gameComm = gameComm;
    }

    public Team getTeam() {
	return team;
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
