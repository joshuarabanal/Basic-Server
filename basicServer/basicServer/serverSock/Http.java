package basicServer.serverSock;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import basicServer.RequestsHandler;

public class Http implements Runnable{
	protected int portNum = 4244;
	protected ServerSocket serverSock;
	protected RequestsHandler requests;
	
	public Http(RequestsHandler requests){
		this.requests = requests;
		
	}
        public void start() throws IOException{
            start(null);
        }
	public void start(ServerSocket sock) throws IOException{
            
            if(sock!=null){
                serverSock = sock;
            }
		if(serverSock == null){
			serverSock = new ServerSocket(portNum);
			
		}
                Log.i("server start called", serverSock.getLocalPort()+"");
		Thread t = new Thread(this);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
                
	}
	public void stop() throws IOException{
		if(serverSock != null){
                    
                    serverSock.close();
                }
		serverSock = null;
	}
	@Override
	public void run() {
                Log.i("starting server on port", serverSock.getLocalPort()+"");
			Socket sock = null;
		try {
			
			while(serverSock != null){
				sock = serverSock.accept();
				if(sock != null){
					requests.addRequest(sock);
				}
				System.out.println("message added to queue");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
                        if(sock!= null){
                            System.out.print(sock.toString());
                        }
		}//wait for requests
	}

}
