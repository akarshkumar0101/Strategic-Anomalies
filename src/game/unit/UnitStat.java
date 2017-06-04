package game.unit;

public class UnitStat {
    public final int defaultHealth;
    public final int defaultArmor;
    public final int defaultMoveRange;
    public final int defaultAttackRange;
    public final int defaultPower;
    public final int maxWaitTime;

    public final double defaultSideBlock;
    public final double defaultFrontBlock;

    public UnitStat(int defaultHealth, int defaultArmor, int defaultPower, int defaultAttackRange,
	    double defaultSideBlock, double defaultFrontBlock, int defaultMoveRange, int maxWaitTime) {
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