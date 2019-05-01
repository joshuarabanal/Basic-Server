package basicServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;

public class GoogleDDNS implements Runnable{
	private String publicIpAddress = null;
	private String base64EncodedAuthorization;
	private URL url;
	public GoogleDDNS(String username, String password, String hostName) throws Exception{
		base64EncodedAuthorization = "Basic "+Base64.getEncoder().encodeToString((username+":"+password) .getBytes());
		url = new URL("https://domains.google.com/nic/update?hostname="+hostName);
		publicIpAddress = getIpAddress();
		System.out.println("Set Ip:"+updateIp());
		
	}
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
				if(!publicIpAddress.equals(getIpAddress())){
					System.out.println("update Ip:"+updateIp());
				}
				Thread.sleep(300000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getIpAddress() throws Exception{
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		String ip = in.readLine(); //you get the IP as a String
		in.close();
		return ip;
		
	}
	private String updateIp() throws IOException{
		HttpsURLConnection conn= (HttpsURLConnection) this.url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setUseCaches( false );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty("Authorization",  this.base64EncodedAuthorization);
		conn.setRequestProperty(Request.headerUserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.30");
		conn.setRequestProperty( "Content-Length", 0+"");
		OutputStream out = conn.getOutputStream();
		out.close();
		int code = conn.getResponseCode();
		   InputStream in = conn.getInputStream();
		   int howmany = -1;
		   byte[] bytes = new byte[100];
		   howmany = in.read(bytes);
		   in.close();
		   return new String(bytes,0,howmany)+", response code:"+code;
		
	}
	/**
	 reading header:GET /?hostname=subdomain.yourdomain.com&myip=1.2.3.4 HTTP/1.1
reading header:Host: 192.168.1.4:4244
reading header:Connection: keep-alive
reading header:Cache-Control: max-age=0
reading header:Upgrade-Insecure-Requests: 1
reading header:User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36
reading header:Accept-Encoding: gzip, deflate
reading header:Accept-Language: en-US,en;q=0.8,es;q=0.6
	 */

}
