package game.unit;

import java.util.HashMap;
import java.util.Scanner;

public class UnitDefaults {

    private static final HashMap<Class<? extends Unit>, UnitStat> unitDefaultStats;

    static {
	unitDefaultStats = new HashMap<>();

	Scanner scan = new Scanner(UnitDefaults.class.getResourceAsStream("/stats.txt"));
	try {
	    while (scan.hasNextLine()) {
		String[] data = scan.nextLine().split("\t");
		String unitName = data[0].replaceAll(" ", "");

		Class<? extends Unit> unitClass = null;
		for (Class<? extends Unit> clazz : Unit.UNITCLASSES) {
		    if (clazz.getSimpleName().equals(unitName)) {
			unitClass = clazz;
			break;
		    }
		}
		if (unitClass == null) {
		    continue;
		}
		UnitStat stat = new UnitStat();
		// TODO add info to stat;
		stat.defaultHealth = (int) parseNumber(data[1]);
		stat.defaultArmor = (int) parseNumber(data[2]);
		stat.defaultPower = (int) parseNumber(data[3]);
		stat.defaultAttackRange = (int) parseNumber(data[4]);
		stat.defaultSideBlock = parseNumber(data[5]);
		stat.defaultFrontBlock = parseNumber(data[6]);
		stat.canDefaultMove = data[7].equals("TRUE");
		stat.defaultMoveRange = (int) parseNumber(data[8]);
		stat.defaultWaitTime = (int) parseNumber(data[9]);

		unitDefaultStats.put(unitClass, stat);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Trouble reading in unit statistics");
	}
	scan.close();
    }

    private static double parseNumber(String str) {
	if (str.equals("Special")) {
	    return -1;
	}
	if (str.contains("%")) {
	    str = str.substring(0, str.indexOf('%'));
	    return Double.parseDouble(str) / 100;
	} else {
	    return Double.parseDouble(str);
	}
    }

    public static UnitStat getStat(Class<? extends Unit> clazz) {
	return unitDefaultStats.get(clazz);
    }
}
