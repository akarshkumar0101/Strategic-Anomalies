package game;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

public class Communication {

    private ObjectInputStream objin;
    private ObjectOutputStream objout;

    public Communication() {
	objin = null;
	objout = null;
    }

    public Communication(InputStream in, OutputStream out) {
	try {
	    objout = new ObjectOutputStream(out);
	    objout.flush();
	    objin = new ObjectInputStream(in);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public Communication(Socket sock) throws IOException {
	this(sock.getInputStream(), sock.getOutputStream());
    }

    public Communication connectLocally() {
	try {
	    PipedOutputStream thisout = new PipedOutputStream();
	    PipedInputStream thisin = new PipedInputStream();

	    PipedOutputStream otherout = new PipedOutputStream();
	    PipedInputStream otherin = new PipedInputStream();

	    otherout.connect(thisin);
	    thisout.connect(otherin);

	    objout = new ObjectOutputStream(thisout);
	    flush();
	    Communication othercomm = new Communication(otherin, otherout);
	    othercomm.flush();

	    objin = new ObjectInputStream(thisin);

	    return othercomm;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

    }

    public void sendObject(Object obj) {
	sendObject(obj, true);
    }

    public void sendObject(Object obj, boolean flush) {
	try {
	    objout.writeObject(obj);
	    if (flush) {
		objout.flush();
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public Object recieveObject() {
	try {
	    return objin.readObject();
	} catch (ClassNotFoundException | IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public void flush() {
	try {
	    objout.flush();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
