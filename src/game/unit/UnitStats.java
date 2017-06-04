package game.unit;

import java.util.HashMap;
import java.util.Scanner;

import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.Cleric;
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

    @SuppressWarnings("unchecked")
    public static final Class<? extends Unit>[] UNITCLASSES = (Class<? extends Unit>[]) new Class<?>[] {
	    Aquamancer.class, Archer.class, Cleric.class, DarkMagicWitch.class, Guardian.class, Hunter.class,
	    LightMagicWitch.class, Lightningmancer.class, Pyromancer.class, Scout.class, Warrior.class };

    static {
	unitStats = new HashMap<>();

	Scanner scan = new Scanner(UnitStats.class.getResourceAsStream("/stats.txt"));
	try {
	    while (scan.hasNextLine()) {
		String[] data = scan.nextLine().split("\t");
		String unitName = data[0].replaceAll(" ", "");

		Class<? extends Unit> unitClass = null;
		for (Class<? extends Unit> clazz : UNITCLASSES) {
		    if (clazz.getSimpleName().equals(unitName)) {
			unitClass = clazz;
			break;
		    }
		}
		if (unitClass == null) {
		    continue;
		}
		UnitStat stat = new UnitStat((int) parseNumber(data[1]), (int) parseNumber(data[2]),
			(int) parseNumber(data[3]), (int) parseNumber(data[4]), parseNumber(data[5]),
			parseNumber(data[6]), (int) parseNumber(data[7]), (int) parseNumber(data[8]));
		unitStats.put(unitClass, stat);
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
}
