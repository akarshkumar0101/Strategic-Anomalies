package testing.gameframe;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

import game.unit.Unit;
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

public class Images {
    public static final HashMap<Class<? extends Unit>, Image> classImages = new HashMap<>();

    public static Image warriorImage;
    public static Image guardianImage;

    public static Image pyromancerImage;
    public static Image aquamancerImage;
    public static Image lightningmancerImage;

    public static Image scoutImage;
    public static Image archerImage;
    public static Image hunterImage;

    public static Image darkmagicwitchImage;
    public static Image lightmagicwitchImage;

    public static Image upArrowImage;
    public static Image rightArrowImage;
    public static Image downArrowImage;
    public static Image leftArrowImage;

    public static Image stunnedImage;
    public static Image waitingImage;

    public static Image goldenFrameImage;

    public static Image blockedImage;

    static {
	try {
	    Images.classImages.put(Warrior.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/warrior.png")));
	    Images.classImages.put(Guardian.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/guardian.png")));

	    Images.classImages.put(Pyromancer.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/pyromancer.png")));
	    Images.classImages.put(Aquamancer.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/aquamancer.png")));
	    Images.classImages.put(Lightningmancer.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/lightningmancer.png")));

	    Images.classImages.put(Scout.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/scout.png")));
	    Images.classImages.put(Archer.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/archer.png")));
	    Images.classImages.put(Hunter.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/hunter.png")));

	    Images.classImages.put(DarkMagicWitch.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/darkmagicwitch.png")));
	    Images.classImages.put(LightMagicWitch.class,
		    ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/lightmagicwitch.png")));

	    Images.upArrowImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/redarrow.png"));
	    Images.rightArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 90);
	    Images.downArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 180);
	    Images.leftArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 270);

	    Images.stunnedImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/dizzy.png"));

	    Images.waitingImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/waiting.png"));

	    Images.goldenFrameImage = ImageIO
		    .read(TestingFrame.class.getResourceAsStream("/temp_pics/goldenframe.png"));
	    Images.blockedImage = ImageIO.read(TestingFrame.class.getResourceAsStream("/temp_pics/blocked.png"));

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static Image getImage(Class<? extends Unit> unitClass) {
	return Images.classImages.get(unitClass);
    }

    // the following is not my code
    private static BufferedImage rotate(BufferedImage image, int _thetaInDegrees) {
	double _theta = Math.toRadians(_thetaInDegrees);

	AffineTransform xform = new AffineTransform();

	if (image.getWidth() > image.getHeight()) {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getWidth());
	    xform.rotate(_theta);

	    int diff = image.getWidth() - image.getHeight();

	    switch (_thetaInDegrees) {
	    case 90:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
		break;
	    case 180:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
		break;
	    default:
		xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth());
		break;
	    }
	} else if (image.getHeight() > image.getWidth()) {
	    xform.setToTranslation(0.5 * image.getHeight(), 0.5 * image.getHeight());
	    xform.rotate(_theta);

	    int diff = image.getHeight() - image.getWidth();

	    switch (_thetaInDegrees) {
	    case 180:
		xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
		break;
	    case 270:
		xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
		break;
	    default:
		xform.translate(-0.5 * image.getHeight(), -0.5 * image.getHeight());
		break;
	    }
	} else {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getHeight());
	    xform.rotate(_theta);
	    xform.translate(-0.5 * image.getHeight(), -0.5 * image.getWidth());
	}

	AffineTransformOp op = new AffineTransformOp(xform, AffineTransformOp.TYPE_BILINEAR);

	return op.filter(image, null);
    }
}