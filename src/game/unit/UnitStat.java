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

    public UnitStat(int defaultHealth, int defaultArmor, int defaultMoveRange, int defaultAttackRange, int defaultPower,
	    int maxWaitTime, double defaultSideBlock, double defaultFrontBlock) {
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