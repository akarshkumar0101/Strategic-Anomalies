package testing;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = -7853582653929790549L;

    private String username;
    private String password;
    private String inGameName;

    public Account(String username, String password, String inGameName) {
	this.username = username;
	this.password = password;
	this.inGameName = inGameName == null ? username : inGameName;
    }

    public String getUsername() {
	return username;
    }

    public String getPassword() {
	return password;
    }

    public String getInGameName() {
	return inGameName;
    }
}
