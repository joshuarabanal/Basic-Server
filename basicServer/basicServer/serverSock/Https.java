package basicServer.serverSock;

import android.util.Log;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import basicServer.RequestsHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;



public class Https extends Http{
    protected String trustStoreDir;
    protected String SSLPassword;
	
	public Https(RequestsHandler requests, String trustStoreDirectory,String sslPassword) throws IOException{
		super(requests);
               this.trustStoreDir = trustStoreDirectory;
               this.SSLPassword = sslPassword;
		this.portNum = 4245;
	}


	public void start() throws IOException{
		try {
                    Log.i("starting https","server");
                    File jks = new File(trustStoreDir);
                    KeyStore ks = KeyStore.getInstance("PKCS12");
        	        Log.i("loading keystore", "password:"+SSLPassword+", file:"+jks);
        	        ks.load(new FileInputStream(jks), SSLPassword.toCharArray());
        	        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        	        kmf.init(ks, SSLPassword.toCharArray());

        	        SSLContext sc = SSLContext.getInstance("TLS");
        	        sc.init(kmf.getKeyManagers(), null, null);

        	        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        	        SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(portNum);
        	        
        	        java.security.cert.Certificate c = ks.getCertificate("simple-cert");
        	        Log.i("public key", c.getPublicKey().toString());
        	        Log.i("whole cert", c.toString());
        	        java.security.cert.Certificate[] cs = ks.getCertificateChain("simple-cert");
        	        
        	        Log.i("number of certs", ""+cs.length);
        	        for(int i = 0; cs != null && i<cs.length; i++) {
        		        Log.i("public key["+i+"]", c.getPublicKey().toString());
        		        Log.i("whole cert["+i+"]", c.toString());
        		        Log.i("type["+i+"]", c.getType());
        	        }

        	        serverSock = s;
        			start(s);
		}
		catch(Exception e) {
			IOException io = new IOException("failed to load https server");
			io.addSuppressed(e);
			throw io;
		}

	}
}
