package DNS.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class TCPServer  implements Runnable{
	private ServerSocket dnssocket;
	private boolean running = false;
	private Thread t = new Thread(this);
	
	public TCPServer(int port53) throws IOException {
		dnssocket = new ServerSocket(port53);
	}
	
	public void start() {
		if(!running) { t.start(); }
	}
	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		running = true;
		while(running) {
			try {
				Log.i("waiting on port", dnssocket.getLocalPort()+"");
				Socket s = dnssocket.accept();
				byte[] b = new byte[1024];
				int howmany;
				InputStream in = s.getInputStream();
				Log.i("new packet(TCP)", s.getInetAddress()+"");
				while( (howmany = in.read(b))>0) {
					Log.i("read:"+dnssocket.getLocalPort(), new String(b, 0, howmany));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
