package game;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class GameComm {

    private static final RuntimeException notConnectedException = new RuntimeException("Not connected");

    private boolean connected;

    private ObjectInputStream objin;
    private ObjectOutputStream objout;

    public GameComm() {
	connected = false;

	objin = null;
	objout = null;
    }

    public GameComm(InputStream in, OutputStream out) {
	try {
	    objin = new ObjectInputStream(in);
	    objout = new ObjectOutputStream(out);
	    connected = true;
	} catch (IOException e) {
	    throw new RuntimeException("Something went wrong connecting the object streams to socket streams");
	}

    }

    public GameComm connectLocally() {
	if (connected) {
	    throw new RuntimeException("Already connected");
	}

	try {
	    PipedInputStream thisin = new PipedInputStream();
	    PipedOutputStream thisout = new PipedOutputStream();

	    PipedInputStream otherin = new PipedInputStream();
	    PipedOutputStream otherout = new PipedOutputStream();

	    otherout.connect(thisin);
	    thisout.connect(otherin);

	    objin = new ObjectInputStream(thisin);
	    objout = new ObjectOutputStream(thisout);

	    GameComm gc = new GameComm(otherin, otherout);

	    connected = true;

	    return gc;
	} catch (IOException e) {
	    throw new RuntimeException("Something went wrong connecting game communications");
	}

    }

    public boolean isConnected() {
	return connected;
    }

    private void checkConnection() {
	if (!connected) {
	    throw notConnectedException;
	}
    }

    public void writeObject(Object obj) {
	checkConnection();
	try {
	    objout.writeObject(obj);
	} catch (IOException e) {
	    throw new RuntimeException("Couldn't write object");
	}
    }

    public Object readObject() {
	checkConnection();
	try {
	    return objin.readObject();
	} catch (Exception e) {
	    throw new RuntimeException("Couldn't read object");
	}
    }

}
