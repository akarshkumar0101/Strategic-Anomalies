package testing;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import game.unit.Unit;

public class Images {
    public static final HashMap<Class<? extends Unit>, Image> classImages = new HashMap<>();

    public static Image stunnedImage;
    public static Image waitingImage;

    public static Image goldenFrameImage;

    public static Image blockedImage;

    public static Image greenDotImage;
    public static Image redDotImage;

    public static Image upArrowImage;
    public static Image rightArrowImage;
    public static Image downArrowImage;
    public static Image leftArrowImage;

    static {
	try {
	    for (Class<? extends Unit> clazz : Unit.UNITCLASSES) {
		String filename = clazz.getSimpleName().toLowerCase() + ".png";
		InputStream is = Images.class.getResourceAsStream("/temp_pics/" + filename);

		if (is != null) {
		    Image img = ImageIO.read(is);

		    Images.classImages.put(clazz, img);
		}
	    }

	    Images.stunnedImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/dizzy.png"));

	    Images.waitingImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/waiting.png"));

	    Images.goldenFrameImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/goldenframe.png"));
	    Images.blockedImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/blocked.png"));

	    Images.greenDotImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/greendot.png"));
	    Images.redDotImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/reddot.png"));

	    Images.upArrowImage = ImageIO.read(Images.class.getResourceAsStream("/temp_pics/redarrow.png"));

	    Images.rightArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 90);
	    Images.downArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 180);
	    Images.leftArrowImage = Images.rotate((BufferedImage) Images.upArrowImage, 270);

	} catch (IOException e) {
	    throw new RuntimeException("Could not load images", e);
	}

    }

    public static Image getImage(Class<? extends Unit> unitClass) {
	return Images.classImages.get(unitClass);
    }

    public static Image getScaledImage(Image image, int width, int height) {
	Image img = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	return img;
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