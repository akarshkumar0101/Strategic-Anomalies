package testing.ui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import testing.TestingClient;

public class LobbyWindow {
    protected final TestingClient client;

    private final LobbyWindowGUI lobbyWindowGUI;

    public LobbyWindow(TestingClient client) {
	this.client = client;

	lobbyWindowGUI = new LobbyWindowGUI(this);

    }

    public JFrame getGUI() {
	return lobbyWindowGUI;
    }

    public void set(String[] names) {
	lobbyWindowGUI.set(names);
    }
}

class LobbyWindowGUI extends JFrame {

    private static final long serialVersionUID = 5609424981495462156L;

    public final LobbyWindow lobbyWindow;

    private JPanel panel;

    public LobbyWindowGUI(LobbyWindow lobbyWindow) {
	super("Lobby Window for TAO - logged in as " + lobbyWindow.client.name);
	this.lobbyWindow = lobbyWindow;

	setupDisplay();

	setSize(900, 900);
	setVisible(true);
    }

    public void setupDisplay() {
	panel = new JPanel();
	panel.setLayout(new GridLayout(100, 1));
	JScrollPane scrollPane = new JScrollPane(panel);
	getContentPane().add(scrollPane);
    }

    public void set(String[] names) {
	setVisible(false);
	panel.removeAll();
	Font normalFont = new Font("Times New Roman", Font.PLAIN, 20);
	for (String name : names) {
	    JButton button = new JButton(name);
	    button.setFont(normalFont);
	    panel.add(button);
	    button.addActionListener(e -> {
		lobbyWindow.client.clickedName(name);
	    });
	}

	setVisible(true);
    }

}
