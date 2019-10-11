package DNS;


import DNS.Server;

public class run_DNSSERVER {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Server s = new Server();
			s.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
