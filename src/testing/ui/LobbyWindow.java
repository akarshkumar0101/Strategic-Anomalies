package testing.ui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import testing.TestingClient;

public class LobbyWindow extends JFrame {

    private static final long serialVersionUID = 3759719847857051056L;

    private JPanel panel;
    private TestingClient client;

    public LobbyWindow(TestingClient client) {
	super("Lobby Window for TAO - logged in as " + client.name);
	this.client = client;

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
		client.clickedName(name);
	    });
	}

	setVisible(true);
    }

}
