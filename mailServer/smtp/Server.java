package mailServer.smtp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import android.util.Log;
import mailServer.smtp.port25.SMTPServer;
import mailServer.smtp.port25.SMTP_SSL_Server;



public class Server {
	private String domain;
	private ServerSocket port587;
	private Thread t587;
	private SMTPServer smtp;
	private SMTP_SSL_Server sslSmtp;
	
	public static void main(String[] args) {
		try {
			Server s = new Server("joshuarabanal.info");
			
			s.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Server(String domain) throws IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		this.domain = domain;
		//port 25
		smtp = new SMTPServer(domain, 4246);
		
		//ssl on port 465
		sslSmtp = new SMTP_SSL_Server(
				domain, 
				new File("C:\\Users\\Joshu\\OneDrive\\Documents\\GitHub\\serverDirectories\\joshuarabanal.info.jks"), 
				"test12345"
			);
		
		port587 = new ServerSocket(587);
	}
	
	public void start() {
		smtp.startServer();

		sslSmtp.startServer();
		
		t587 = new Thread(new run(port587));
		t587.start(); 
	}
	
	private class run implements Runnable{
		private ServerSocket socket;

		run(ServerSocket thisSocket){ this.socket = thisSocket; }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
					try {
						Log.i("waiting on port", socket.getLocalPort()+"");
						Socket s = socket.accept();
						byte[] b = new byte[1024];
						int howmany;
						InputStream in = s.getInputStream();
						Log.i("new packet", s.getInetAddress()+"");
						while( (howmany = in.read(b))>0) {
							Log.i("read:"+socket.getLocalPort(), new String(b, 0, howmany));
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
		}
		
	}

}
