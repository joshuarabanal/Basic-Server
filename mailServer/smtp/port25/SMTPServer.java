package mailServer.smtp.port25;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;

import android.util.Log;
/**
 * this class was made with the help of:
 * <ul>
 * <li>
 * <a href="http://www.rfc-editor.org/rfc/rfc2821.txt">SMTP specification</a>
 * </li>
 * <li>
 * <a href="https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol">WIKIPEDIA</a>
 * </li>
 * </ul>
 * @author Joshu
 *
 */
public class SMTPServer implements Runnable{
	private String domain;
	protected ServerSocket port25;
	private Thread t = new Thread(this);
	
	
	public SMTPServer(String domain) throws IOException {
		this(domain, 25);
	}
	public SMTPServer(String domain, int portNumber) throws IOException{
		this.domain = domain;
		port25 = new ServerSocket(portNumber);
	}
	public void startServer() {
		if(t.isAlive()) return;
		
		t.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Log.i("waiting on port", port25.getLocalPort()+"");
				Socket s = port25.accept();
				Log.i("new packet", s.getInetAddress()+":"+s.getLocalPort());
				handleNewRequest(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	private void handleNewRequest(Socket s) throws IOException {
		
		String from = null, body = null;
		ArrayList<String> to = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		OutputStream out = s.getOutputStream();
		Log.i("outputStream class", out.getClass()+"");
		
		
		String writeLine = "220 "+domain+" ESMTP Postfix\r\n";
		Log.i("writing", writeLine);
		out.write(writeLine.getBytes());
		String line = in.readLine();
		if(line.indexOf("EHLO ") == 0) {
			ehloResponse(out, line);
		}
		else { throw new IndexOutOfBoundsException("unable to handle:"+line); }
		
		while(true) {
			line = in.readLine();
			Log.i("line", line);
			
			if(line.indexOf("MAIL FROM:") == 0) {
				from = line.substring(line.indexOf("<")+1, line.indexOf(">"));
			}
			else if(line.indexOf("RCPT TO:") == 0) {
				to.add(line.substring(line.indexOf("<")+1, line.indexOf(">")));
			}
			else if(line.indexOf("DATA") == 0) {
				out.write("354 End data with <CR><LF>.<CR><LF>\r\n".getBytes());
				StringBuilder sb = new StringBuilder();
				while( !(line = in.readLine()).equals(".")) {
					sb.append(line+"\r\n");
				}
				body = sb.toString();
			}
			else if(line.indexOf("QUIT") == 0) {
				writeEmail(from, to, body);
				out.write("221 Bye\r\n".getBytes());
				out.close();
				return;
			}
			else {
				throw new IndexOutOfBoundsException("unhandled message:"+line);
			}
			out.write("250 Ok\r\n".getBytes());
		}
	}
	
	
	//--------------------------------------------------
	
	private void writeEmail(String from, ArrayList<String> to, String body) {
		Log.i("new email", "from:"+from);
		Log.i("new email", "to:"+to);
		Log.i("new email", "body:"+body);
	}
	private void ehloResponse(OutputStream out, String ehloMessage) throws IOException {
		if(ehloMessage.indexOf("EHLO ") != 0) { throw new IndexOutOfBoundsException(ehloMessage); }
		String clientDomain = ehloMessage.substring("EHLO ".length());
		
		String response = "250-"+domain+" Hello "+clientDomain+"\r\n";
		Log.i("writing", response);
		out.write(response.getBytes());
		
		response = "250-SIZE 14680064\r\n";
		Log.i("writing", response);
		out.write(response.getBytes());
		
		response = "250 HELP\r\n";
		Log.i("writing", response);
		out.write(response.getBytes());
		
	}

}
