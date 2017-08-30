package testing;

import java.util.Scanner;

public class AkarshMain {

    public static void main(String[] args) throws Exception {
	System.out.println("Server ip: ");
	Scanner scan = new Scanner(System.in);
	String ip = scan.nextLine();
	scan.close();
	TestingClient.main(ip);

    }

}
