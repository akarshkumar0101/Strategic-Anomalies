package game.unit;

import java.util.HashMap;

public class UnitStats {

    public static final HashMap<Class<? extends Unit>, PieceStats> unitStats;

    static {
	unitStats = new HashMap<>();

	int defaultHealth = 50;
	int defaultArmor = 8;
	int defaultPower = 25;
	int defaultAttackRange = 1;
	double defaultSideBlock = .35;
	double defaultFrontBlock = .8;
	int defaultMoveRange = 3;
	int maxWaitTime = 1;
	PieceStats stats = new PieceStats(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange,
		defaultPower, maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Warrior.class, stats);

	defaultHealth = 50;
	defaultArmor = 25;
	defaultPower = 22;
	defaultAttackRange = 1;
	defaultSideBlock = .2;
	defaultFrontBlock = .9;
	defaultMoveRange = 3;
	maxWaitTime = 1;
	stats = new PieceStats(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Guardian.class, stats);

	defaultHealth = 50;
	defaultArmor = 25;
	defaultPower = 22;
	defaultAttackRange = 1;
	defaultSideBlock = .2;
	defaultFrontBlock = .9;
	defaultMoveRange = 3;
	maxWaitTime = 1;
	stats = new PieceStats(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Guardian.class, stats);

    }
}

class PieceStats {

    public final int defaultHealth;
    public final int defaultArmor;
    public final int defaultMoveRange;
    public final int defaultAttackRange;
    public final int defaultPower;
    public final int maxWaitTime;

    public final double defaultSideBlock;
    public final double defaultFrontBlock;

    public PieceStats(int defaultHealth, int defaultArmor, int defaultMoveRange, int defaultAttackRange,
	    int defaultPower, int maxWaitTime, double defaultSideBlock, double defaultFrontBlock) {
	this.defaultHealth = defaultHealth;
	this.defaultArmor = defaultArmor;
	this.defaultMoveRange = defaultMoveRange;
	this.defaultAttackRange = defaultAttackRange;
	this.defaultPower = defaultPower;
	this.maxWaitTime = maxWaitTime;

	this.defaultSideBlock = defaultSideBlock;
	this.defaultFrontBlock = defaultFrontBlock;
    }
}
