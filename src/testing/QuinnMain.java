package testing;

public class QuinnMain {

    public static void main(String[] args) {
	Thread servThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingServer.main("");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};

	// Inserting the servThread.starting() - QT
	// Another comment to test
	servThread.start();
	try {
	    Thread.sleep(100);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	TestingClient.main("localhost");
    }

}
