package testing.ui;

import java.awt.Color;

public class GUIUtil {
    public static Color lighterColor(Color col, int amount) {
	return new Color(GUIUtil.makeRGBRange(col.getRed() + amount), GUIUtil.makeRGBRange(col.getGreen() + amount),
		GUIUtil.makeRGBRange(col.getBlue() + amount));
    }

    public static Color mixColors(Color col1, Color col2) {
	return new Color(GUIUtil.makeRGBRange((col1.getRed() + col2.getRed()) / 2),
		GUIUtil.makeRGBRange((col1.getGreen() + col2.getGreen()) / 2),
		GUIUtil.makeRGBRange((col1.getBlue() + col2.getBlue()) / 2));
    }

    public static Color addColors(Color col1, Color col2) {
	return new Color(GUIUtil.makeRGBRange(col1.getRed() + col2.getRed()),
		GUIUtil.makeRGBRange(col1.getGreen() + col2.getGreen()),
		GUIUtil.makeRGBRange(col1.getBlue() + col2.getBlue()));
    }

    private static int makeRGBRange(int a) {
	if (a > 255) {
	    return 255;
	} else if (a < 0) {
	    return 0;
	} else {
	    return a;
	}
    }

}
