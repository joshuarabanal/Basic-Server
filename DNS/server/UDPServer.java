package DNS.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class UDPServer implements Runnable{
	private DatagramSocket dnsUDP;//65,535
	private boolean running = false;
	private Thread t = new Thread(this);
	
	public UDPServer(int port53) throws SocketException {
		dnsUDP = new DatagramSocket(port53);
	}
	public void start() {
		if(!running) { t.start(); }
	}
	public void stop() {
		running = false;
	}
	
	public static void logString(byte[] b, int start, int length) {
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i<length; i++) {
			if(i>0) { sb.append(", "); }
			sb.append(""+b[i+start]);
		}
		Log.i("logging bytes",  sb+"]");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		running = true;
		while(running) {
			byte[] buf = new byte[65507];
			DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);
			Log.i("waiting on UDP port", dnsUDP.getLocalPort()+"");
			try {
				dnsUDP.receive(incomingPacket);
			
				MessageParser mp = new MessageParser(incomingPacket.getData());
				
				byte[] fabricated = mp.toByteArray();
				
				/**
				
				**/
				
				Log.i("attempting", "to forward request");
				byte[] googleRecord = forwardForResponse(fabricated);
				byte[] responseArray = googleRecord;//googleRecord.toByteArray();
				dnsUDP.send(
						new DatagramPacket(
						responseArray, responseArray.length,incomingPacket.getAddress(), incomingPacket.getPort()
						)
				);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private static DatagramSocket other;
	public static byte[] forwardForResponse(byte[] recieved) throws IOException {
		
		DatagramSocket other = new DatagramSocket(); 
		DatagramPacket dp = new DatagramPacket(recieved, recieved.length,InetAddress.getByName("8.8.8.8"), 53);
		other.send(dp);
		byte[] googleResponseBuf = new byte[65507];
        dp = new DatagramPacket(googleResponseBuf, googleResponseBuf.length);
        other.receive(dp);
        MessageParser retu = new MessageParser(dp.getData());
        
        byte[] fabricated = retu.toByteArray();
        /**
      //check for errors
		for(int i = 0; i<fabricated.length; i++) { 
			if(fabricated[i] != googleResponseBuf[i]) {
				Log.i("index", ""+i);
				logString(fabricated, i, 24);
				logString(googleResponseBuf, i, 24);
				throw new IndexOutOfBoundsException("fabricated array incorrect");
			}
		}
		**/
        
		return googleResponseBuf;
	}

}
