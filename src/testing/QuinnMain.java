package testing;

public class QuinnMain {

    public static void main(String[] args) throws Exception {
	Thread servThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    TestingServer.main(null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	TestingClient.main(null);
    }

}
