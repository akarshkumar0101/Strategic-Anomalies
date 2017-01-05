package game;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Communication {

    private static final RuntimeException notConnectedException = new RuntimeException("Not connected");

    private boolean connected;

    private ObjectInputStream objin;
    private ObjectOutputStream objout;

    public Communication() {
	connected = false;

	objin = null;
	objout = null;
    }

    public Communication(InputStream in, OutputStream out) {
	try {
	    objin = new ObjectInputStream(in);
	    objout = new ObjectOutputStream(out);
	    objout.flush();
	    connected = true;
	} catch (IOException e) {
	    throw new RuntimeException("Something went wrong connecting the object streams to socket streams");
	}

    }

    public Communication connectLocally() {
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

	    objout = new ObjectOutputStream(thisout);
	    objout.flush();

	    Communication comm = new Communication(otherin, otherout);

	    objin = new ObjectInputStream(thisin);

	    connected = true;

	    return comm;
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

    public void sendObject(Object obj) {
	checkConnection();
	try {
	    objout.writeObject(obj);
	} catch (IOException e) {
	    throw new RuntimeException("Couldn't write object");
	}
    }

    public Object recieveObject() {
	checkConnection();
	try {
	    return objin.readObject();
	} catch (Exception e) {
	    throw new RuntimeException("Couldn't read object");
	}
    }

}
