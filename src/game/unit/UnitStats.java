package game.unit;

import java.util.HashMap;

import game.unit.listofunits.Guardian;
import game.unit.listofunits.Warrior;

public class UnitStats {

    public static final HashMap<Class<? extends Unit>, UnitStat> unitStats;

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
	UnitStat stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Warrior.class, stat);

	defaultHealth = 50;
	defaultArmor = 25;
	defaultPower = 22;
	defaultAttackRange = 1;
	defaultSideBlock = .2;
	defaultFrontBlock = .9;
	defaultMoveRange = 3;
	maxWaitTime = 1;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Guardian.class, stat);

	defaultHealth = 50;
	defaultArmor = 25;
	defaultPower = 22;
	defaultAttackRange = 1;
	defaultSideBlock = .2;
	defaultFrontBlock = .9;
	defaultMoveRange = 3;
	maxWaitTime = 1;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Guardian.class, stat);

    }
}
