package testing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import game.unit.Unit;
import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.Warrior;

public class TestingSetup {
    @SuppressWarnings("unchecked")
    public static final Class<? extends Unit>[] unitClasses;
    static {
	unitClasses = new Class[3];
	TestingSetup.unitClasses[0] = Warrior.class;
	TestingSetup.unitClasses[1] = Aquamancer.class;
	TestingSetup.unitClasses[2] = Pyromancer.class;
    }

    public static void startTestingSetup() {
	new TestingSetupGUI(null);
    }
}

class TestingSetupGUI {

    private final TestingSetup testingSetup;

    private final JFrame frame;
    private final GridBagLayout gblayout;
    private final GridBagConstraints gbc;

    private final PickPiecePanel pickPanel;
    private final PlacePiecePanel placePanel;

    public TestingSetupGUI(TestingSetup testingSetup) {
	this.testingSetup = testingSetup;

	frame = new JFrame();
	gblayout = new GridBagLayout();
	gbc = new GridBagConstraints();

	pickPanel = new PickPiecePanel();
	placePanel = new PlacePiecePanel();

	organizeComponents();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);
    }

    private void organizeComponents() {
	frame.getContentPane().setLayout(gblayout);
	int gridx = 0, gridy = 0;

	gbc.gridx = gridx;
	gbc.gridy = gridy;
	gbc.weightx = 1;
	gbc.weighty = 0;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.NORTH;
	gbc.insets = new Insets(0, 0, 0, 0);
	// gbc.setFont(normalFont);
	frame.getContentPane().add(pickPanel, gbc);

	gbc.gridx = gridx;
	gbc.gridy = ++gridy;
	gbc.weightx = 1;
	gbc.weighty = 0;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.NORTH;
	gbc.insets = new Insets(0, 0, 0, 0);
	// gbc.setFont(normalFont);
	frame.getContentPane().add(placePanel, gbc);

    }

    class PickPiecePanel extends JPanel {
	private static final long serialVersionUID = -2216791025981947840L;

	private HashMap<Class<? extends Unit>, JToggleButton> toggleButtons;
	private GridLayout gridLayout;

	public PickPiecePanel() {
	    toggleButtons = new HashMap<>();
	    gridLayout = new GridLayout(2, 5);

	    for (Class<? extends Unit> clazz : TestingSetup.unitClasses) {
		toggleButtons.put(clazz, new JToggleButton());
	    }

	    organizeComponents();
	}

	private void organizeComponents() {
	    setLayout(gridLayout);

	    for (JToggleButton jtb : toggleButtons.values()) {
		add(jtb);
	    }
	}

    }

    class PlacePiecePanel extends JPanel {
	private static final long serialVersionUID = 8040952489704113667L;

    }
}
