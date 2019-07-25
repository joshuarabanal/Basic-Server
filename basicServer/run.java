package basicServer;

import java.io.File;

import basicServer.proxy.ProxyServer;

public class run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		File path = new File(System.getProperty("user.dir"));
		File srcFolder = new File(path,"src"); srcFolder.mkdirs();
		File cache = new File(path,"cache"); cache.mkdirs();
		
		
				ServerSock serv = new ServerSock(new ProxyServer(), srcFolder, cache);
		
	}

}
