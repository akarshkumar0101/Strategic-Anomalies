package testing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import game.Communication;
import game.board.Coordinate;
import game.board.NormalBoard;

public class TestingServer {
    public static final String INIT_STRING = "init99";

    public static final int NUM_PLAYERS = 2;
    public static final int PORT = 37852;

    public static void main(String[] args) throws IOException {
	long randomSeed = (long) ((Math.random() * 2 - 1) * Long.MAX_VALUE);
	ServerSocket serverSock = new ServerSocket(PORT);
	Communication[] clientComms = new Communication[NUM_PLAYERS];
	for (int i = 0; i < NUM_PLAYERS; i++) {
	    Socket sock = serverSock.accept();
	    clientComms[i] = new Communication(sock);
	    final Communication comm = clientComms[i];
	    comm.flush();
	    Thread thread = new Thread() {
		@Override
		public void run() {
		    try {
			Object prevData = null;
			while (true) {
			    Object data = comm.recieveObject();
			    if (data != null && data.getClass() == Coordinate.class) {
				data = NormalBoard.transformCoordinateForOtherPlayerNormalBoard((Coordinate) data);
			    }
			    // System.out.println(data);

			    // if (Message.HOVER.equals(data) ||
			    // Message.HOVER.equals(prevData)) {
			    //
			    // } else {
			    // System.out.println(data);
			    // }
			    // prevData = data;
			    // System.out.println(data);
			    for (Communication c : clientComms) {
				if (c == comm) {
				    continue;
				}
				c.sendObject(data);
			    }
			    // if (comm == clientComms[0]) {
			    // System.out.println("Client 1 to 2");
			    // } else {
			    // System.out.println("Client 2 to 1");
			    // }
			    // System.out.println(data);
			}
		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		    }
		}
	    };
	    thread.start();
	}
	boolean first = true;
	for (Communication comm : clientComms) {
	    comm.sendObject(INIT_STRING);
	    comm.sendObject(randomSeed);
	    comm.sendObject(first);
	    first = false;
	}
    }

}
