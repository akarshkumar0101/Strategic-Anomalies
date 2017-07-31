package testing.gameframe;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import testing.Images;

//TODO MAKE SURE YOU USE JAVAFX IN FINAL VERSION

public class Test extends JFrame {

    private static final long serialVersionUID = 2570119871946595519L;

    private final GridLayout gLayout;

    private final JButton upDirButton, leftDirButton, rightDirButton, downDirButton;

    public static void main(String[] args) {
	Test frame = new Test();
	frame.setVisible(true);
    }

    public Test() {
	super("Test");

	gLayout = new GridLayout(1, 4);
	upDirButton = new JButton("up");
	leftDirButton = new JButton("left");
	rightDirButton = new JButton("right");
	downDirButton = new JButton("down");

	organizeComponents();

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// pack();
	setSize(500, 500);
	setResizable(false);
    }

    public void organizeComponents() {

	getContentPane().setLayout(gLayout);

	getContentPane().add(leftDirButton);
	getContentPane().add(downDirButton);
	getContentPane().add(upDirButton);
	getContentPane().add(rightDirButton);

	int arrowLength = 40;
	new Thread() {
	    @Override
	    public void run() {
		long start = System.currentTimeMillis();
		Image up = Images.upArrowImage;
		Image down = Images.downArrowImage;
		Image left = Images.leftArrowImage;
		Image right = Images.rightArrowImage;

		System.out.println("cashed " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		ImageIcon upicon = new ImageIcon(up);
		ImageIcon lefticon = new ImageIcon(left);
		ImageIcon righticon = new ImageIcon(right);
		ImageIcon downicon = new ImageIcon(down);

		System.out.println("icon time " + (System.currentTimeMillis() - start));

		upDirButton.setIcon(upicon);
		leftDirButton.setIcon(lefticon);
		rightDirButton.setIcon(righticon);
		downDirButton.setIcon(downicon);
		repaint();
	    }
	}.start();

    }

}