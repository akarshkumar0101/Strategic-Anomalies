package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.board.Coordinate;
import game.board.Direction;
import game.board.NormalBoard;
import game.unit.Unit;
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
import setup.SetupTemplate;

@SuppressWarnings("unchecked")
public class TestingSetup {

    public static final Class<? extends Unit>[] unitClasses;
    static {
	unitClasses = new Class[11];
	TestingSetup.unitClasses[0] = Aquamancer.class;
	TestingSetup.unitClasses[1] = Archer.class;
	TestingSetup.unitClasses[2] = Cleric.class;
	TestingSetup.unitClasses[3] = DarkMagicWitch.class;
	TestingSetup.unitClasses[4] = Guardian.class;
	TestingSetup.unitClasses[5] = Hunter.class;
	TestingSetup.unitClasses[6] = LightMagicWitch.class;
	TestingSetup.unitClasses[7] = Lightningmancer.class;
	TestingSetup.unitClasses[8] = Pyromancer.class;
	TestingSetup.unitClasses[9] = Scout.class;
	TestingSetup.unitClasses[10] = Warrior.class;
    }

    private final TestingSetupGUI gui;

    private SetupTemplate template;

    public TestingSetup() {
	template = new SetupTemplate(NormalBoard.class);
	gui = new TestingSetupGUI(this);
    }

    public SetupTemplate getTemplateInUse() {
	return template;
    }

    /**
     * this method will block until user pushes done.
     */
    public SetupTemplate getFinalTemplate() {
	synchronized (gui.doneButton) {
	    while (!gui.donePressed) {
		try {
		    gui.doneButton.wait();
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		}
	    }
	}
	return template;
    }

    public void dispose() {
	gui.dispose();
    }

    public boolean isInSelectionBoard(Coordinate coor) {
	if (coor == null) {
	    return false;
	}
	return NormalBoard.isInNormalBoard(coor);
    }

    private Class<? extends Unit> selectedUnitClass = null;

    public boolean canCurrentlyClick(Coordinate coor) {
	if (selectedUnitClass != null) {
	    return true;
	}
	if (template.pieceExistsAt(coor)) {
	    return true;
	}
	return false;
    }

    public void unitClassClicked(Class<? extends Unit> unitClass) {
	if (selectedUnitClass == unitClass) {
	    gui.pickPanel.setSelected(unitClass, false);
	    selectedUnitClass = null;
	} else {
	    if (selectedUnitClass != null) {
		gui.pickPanel.setSelected(selectedUnitClass, false);
	    }

	    selectedUnitClass = unitClass;
	    gui.pickPanel.setSelected(unitClass, true);
	}

	gui.repaint();
    }

    public void coordinateClicked(Coordinate coor) {
	if (selectedUnitClass == null && template.pieceExistsAt(coor)) {
	    Class<? extends Unit> unitClass = template.getUnitClassAt(coor);
	    Direction dir = template.getDirFacing(coor).next();
	    gui.placePanel.set(coor, unitClass, dir);
	    template.put(unitClass, coor, dir);
	} else {
	    gui.placePanel.set(coor, selectedUnitClass, Direction.UP);
	    template.put(selectedUnitClass, coor, Direction.UP);

	    gui.pickPanel.setSelected(selectedUnitClass, false);
	    selectedUnitClass = null;
	}

	gui.repaint();
    }
}

class TestingSetupGUI extends JFrame {
    private static final long serialVersionUID = -6412445808406230039L;

    private final TestingSetup testingSetup;

    private final GridBagLayout gblayout;
    private final GridBagConstraints gbc;

    public final PickPiecePanel pickPanel;
    public final PlacePiecePanel placePanel;
    public final JButton doneButton;

    public boolean donePressed = false;

    public TestingSetupGUI(TestingSetup testingSetup) {
	super("Picking Pieces for Game");
	this.testingSetup = testingSetup;

	gblayout = new GridBagLayout();
	gbc = new GridBagConstraints();

	pickPanel = new PickPiecePanel();
	placePanel = new PlacePiecePanel();
	doneButton = new JButton("Done");
	doneButton.addActionListener(e -> {
	    synchronized (doneButton) {
		donePressed = true;
		doneButton.notifyAll();
	    }
	});

	organizeComponents();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	pack();
	setVisible(true);
    }

    private void organizeComponents() {
	setContentPane(new JPanel() {
	    private static final long serialVersionUID = -5960100728713917480L;

	    @Override
	    public Dimension getPreferredSize() {
		return new Dimension(900, 900);
	    }
	});

	getContentPane().setLayout(gblayout);
	getContentPane().setBackground(Color.black);
	int gridx = 0, gridy = 0;

	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.weightx = 1;
	gbc.weighty = .5;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.insets = new Insets(0, 0, 0, 0);
	// gbc.setFont(normalFont);
	getContentPane().add(pickPanel, gbc);

	gbc.gridx = gridx;
	gbc.gridy = ++gridy;
	gbc.weightx = 1;
	gbc.weighty = 1;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.insets = new Insets(0, 0, 0, 0);
	// gbc.setFont(normalFont);
	getContentPane().add(placePanel, gbc);

	gbc.gridx = ++gridx;
	gbc.gridy = --gridy;
	gbc.weightx = .1;
	gbc.weighty = .1;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.NONE;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.insets = new Insets(0, 0, 0, 0);
	// gbc.setFont(normalFont);
	getContentPane().add(doneButton, gbc);

    }

    class PickPiecePanel extends JPanel {
	private static final long serialVersionUID = -2216791025981947840L;

	private HashMap<Class<? extends Unit>, Button> pickButtons;
	private GridLayout gridLayout;

	public PickPiecePanel() {
	    pickButtons = new HashMap<>();
	    gridLayout = new GridLayout(2, 6);

	    for (Class<? extends Unit> clazz : TestingSetup.unitClasses) {
		Button button = new Button(clazz);
		pickButtons.put(clazz, button);
	    }

	    organizeComponents();
	}

	private void organizeComponents() {
	    setLayout(gridLayout);

	    for (Class<? extends Unit> clazz : TestingSetup.unitClasses) {
		Button button = pickButtons.get(clazz);
		button.setToolTipText(clazz.getSimpleName());
		add(button);
	    }
	}

	public void setSelected(Class<? extends Unit> unitClass, boolean selected) {
	    pickButtons.get(unitClass).setSelected(selected);
	}

	public Dimension dim = new Dimension(TestingSetupGUI.unitImageDimension.width * 6,
		TestingSetupGUI.unitImageDimension.height * 2);

	@Override
	public Dimension getPreferredSize() {
	    return dim;
	}
    }

    class PlacePiecePanel extends JPanel {

	private static final long serialVersionUID = 8040952489704113667L;

	private Button[][] buttonGrid;
	private GridLayout gridLayout;

	public PlacePiecePanel() {
	    gridLayout = new GridLayout(NormalBoard.HEIGHT / 2, NormalBoard.WIDTH);
	    buttonGrid = new Button[NormalBoard.WIDTH][NormalBoard.HEIGHT / 2];

	    for (int x = 0; x < NormalBoard.WIDTH; x++) {
		for (int y = 0; y < NormalBoard.HEIGHT / 2; y++) {
		    buttonGrid[x][y] = new Button(new Coordinate(x, y));
		}
	    }

	    organizeComponents();
	}

	public void set(Coordinate coor, Class<? extends Unit> unitClass, Direction dir) {
	    buttonGrid[coor.x()][coor.y()].setUnitClass(unitClass);
	    buttonGrid[coor.x()][coor.y()].setDirection(dir);
	}

	private void organizeComponents() {
	    setLayout(gridLayout);

	    for (int y = NormalBoard.HEIGHT / 2 - 1; y >= 0; y--) {
		for (int x = 0; x < NormalBoard.WIDTH; x++) {
		    add(buttonGrid[x][y]);
		}
	    }
	}

	public Dimension dim = new Dimension(TestingSetupGUI.unitImageDimension.width * NormalBoard.WIDTH,
		TestingSetupGUI.unitImageDimension.height * (NormalBoard.HEIGHT / 2));

	@Override
	public Dimension getPreferredSize() {
	    return dim;
	}
    }

    public static final Dimension unitImageDimension = new Dimension(90, 90);

    class Button extends JComponent implements MouseListener {
	private static final long serialVersionUID = -4968219156079484631L;

	private final boolean boardButton;

	private Class<? extends Unit> unitClass;
	private Image imageToDraw;

	private Coordinate coor;
	private Direction dir;

	private boolean isSelected;

	private boolean mouseIn;
	private boolean mousePressing;

	public Button(Class<? extends Unit> unitClazz) {
	    boardButton = false;
	    setUnitClass(unitClazz);

	    coor = null;
	    dir = null;

	    isSelected = false;

	    addMouseListener(this);
	}

	public Button(Coordinate coor) {
	    boardButton = true;

	    setUnitClass(null);

	    this.coor = coor;
	    dir = null;

	    isSelected = false;

	    addMouseListener(this);
	}

	public void setUnitClass(Class<? extends Unit> unitClass) {
	    this.unitClass = unitClass;
	    if (unitClass == null) {
		imageToDraw = null;
		dir = null;
	    } else {
		imageToDraw = Images.getImage(unitClass);
	    }
	}

	public void setDirection(Direction dir) {
	    this.dir = dir;
	}

	public void setSelected(boolean isSelected) {
	    this.isSelected = isSelected;
	}

	@Override
	public Dimension getPreferredSize() {
	    return TestingSetupGUI.unitImageDimension;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    if (boardButton) {
		if (testingSetup.isInSelectionBoard(coor) && testingSetup.canCurrentlyClick(coor)) {
		    testingSetup.coordinateClicked(coor);
		}
	    } else {
		testingSetup.unitClassClicked(unitClass);
	    }
	}

	@Override
	public void mousePressed(MouseEvent e) {
	    if (!boardButton || testingSetup.canCurrentlyClick(coor)) {
		mousePressing = true;
		repaint();
	    }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    if (mousePressing) {
		mousePressing = false;
		repaint();
	    }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    mouseIn = true;
	    repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
	    mouseIn = false;
	    repaint();
	}

	public Color determineBackgroundColor() {
	    Color col = Color.lightGray;
	    if (boardButton && !testingSetup.isInSelectionBoard(coor)) {
		return Color.black;
	    }

	    if (mousePressing || isSelected) {
		col = TestingFrameGUI.darkerColor(col, 50);
	    } else if (mouseIn) {
		col = TestingFrameGUI.darkerColor(col, 25);
	    }

	    return col;
	}

	@Override
	public void paintComponent(Graphics g) {
	    try {
		paintComponentActual(g);
	    } catch (Exception e) {
		e.printStackTrace();
		// paintComponent(g);
	    }
	}

	public void paintComponentActual(Graphics g) {
	    Color background = determineBackgroundColor();
	    // clear
	    g.setColor(background);
	    g.fillRect(0, 0, getWidth(), getHeight());

	    // if outside of board
	    if (boardButton && !testingSetup.isInSelectionBoard(coor)) {
		return;
	    }
	    // border
	    g.setColor(Color.black);
	    g.drawRect(0, 0, getWidth(), getHeight());

	    // if empty square, end it
	    if (imageToDraw == null) {
		return;
	    }

	    // draw image in the center
	    double aspectratio = (double) getHeight() / getWidth();
	    double imgaspectratio = (double) imageToDraw.getHeight(null) / imageToDraw.getWidth(null);
	    double ratio = 0;
	    if (imgaspectratio > aspectratio) {
		ratio = (double) getHeight() / imageToDraw.getHeight(null);
	    } else {
		ratio = (double) getWidth() / imageToDraw.getWidth(null);
	    }

	    int imgWidth = (int) (ratio * imageToDraw.getWidth(null)),
		    imgHeight = (int) (ratio * imageToDraw.getHeight(null));
	    g.drawImage(imageToDraw, (getWidth() - imgWidth) / 2, (getHeight() - imgHeight) / 2, imgWidth, imgHeight,
		    null);

	    // draw direction facing arrow
	    int arrowWidth = (int) (.5 * getWidth()), arrowHeight = (int) (.2 * getHeight());
	    g.setColor(Color.red);
	    if (dir == Direction.UP) {
		g.drawImage(Images.upArrowImage, (getWidth() - arrowWidth) / 2, 0, arrowWidth, arrowHeight, null);
	    } else if (dir == Direction.DOWN) {
		g.drawImage(Images.downArrowImage, (getWidth() - arrowWidth) / 2, getHeight() - arrowHeight, arrowWidth,
			arrowHeight, null);
	    }
	    arrowHeight = (int) (.2 * getWidth());
	    arrowWidth = (int) (.5 * getHeight());
	    if (dir == Direction.LEFT) {
		g.drawImage(Images.leftArrowImage, 0, (getHeight() - arrowWidth) / 2, arrowHeight, arrowWidth, null);

	    } else if (dir == Direction.RIGHT) {
		g.drawImage(Images.rightArrowImage, getWidth() - arrowHeight, (getHeight() - arrowWidth) / 2,
			arrowHeight, arrowWidth, null);
	    }

	    // golden border if unit is picked
	    if (isSelected) {
		g.drawImage(Images.goldenFrameImage, 1, 1, getWidth() - 2, getHeight() - 2, null);
	    }
	    // border
	    g.setColor(Color.black);
	    g.drawRect(0, 0, getWidth(), getHeight());

	}
    }
}
