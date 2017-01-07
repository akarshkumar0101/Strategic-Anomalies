package game.unit;

import java.util.HashMap;

import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.DarkMagicWitch;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.LightMagicWitch;
import game.unit.listofunits.Lightningmancer;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.Scout;
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

	defaultHealth = 30;
	defaultArmor = 0;
	defaultPower = 15;
	defaultAttackRange = 3;
	defaultSideBlock = .4;
	defaultFrontBlock = .6;
	defaultMoveRange = 3;
	maxWaitTime = 3;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Pyromancer.class, stat);

	defaultHealth = 28;
	defaultArmor = 0;
	defaultPower = 18;
	defaultAttackRange = 2;
	defaultSideBlock = .25;
	defaultFrontBlock = .7;
	defaultMoveRange = 4;
	maxWaitTime = 3;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Aquamancer.class, stat);

	defaultHealth = 35;
	defaultArmor = 0;
	defaultPower = 15;
	defaultAttackRange = 4;
	defaultSideBlock = 1.0;
	defaultFrontBlock = 1.0;
	defaultMoveRange = 3;
	maxWaitTime = 3;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Lightningmancer.class, stat);

	defaultHealth = 40;
	defaultArmor = 8;
	defaultPower = 18;
	defaultAttackRange = 6;
	defaultSideBlock = .3;
	defaultFrontBlock = .6;
	defaultMoveRange = 4;
	maxWaitTime = 2;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Scout.class, stat);

	defaultHealth = 35;
	defaultArmor = 8;
	defaultPower = 22;
	defaultAttackRange = 5;
	defaultSideBlock = .5;
	defaultFrontBlock = .7;
	defaultMoveRange = 3;
	maxWaitTime = 2;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Archer.class, stat);

	defaultHealth = 40;
	defaultArmor = 0;
	defaultPower = 12;
	defaultAttackRange = 8;
	defaultSideBlock = .4;
	defaultFrontBlock = .5;
	defaultMoveRange = 3;
	maxWaitTime = 2;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(Hunter.class, stat);

	defaultHealth = 28;
	defaultArmor = 0;
	defaultPower = 24;
	defaultAttackRange = 4;
	defaultSideBlock = .2;
	defaultFrontBlock = .1;
	defaultMoveRange = 3;
	maxWaitTime = 3;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(DarkMagicWitch.class, stat);

	defaultHealth = 28;
	defaultArmor = 0;
	defaultPower = 26;
	defaultAttackRange = 4;
	defaultSideBlock = .2;
	defaultFrontBlock = .1;
	defaultMoveRange = 3;
	maxWaitTime = 3;
	stat = new UnitStat(defaultHealth, defaultArmor, defaultMoveRange, defaultAttackRange, defaultPower,
		maxWaitTime, defaultSideBlock, defaultFrontBlock);
	unitStats.put(LightMagicWitch.class, stat);
    }
}
