package basicServer.serverSock;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import basicServer.RequestsHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.UnknownHostException;
import static java.security.AccessController.getContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Https extends Http{
    private String trustStoreDir;
    private String SSLPasword;
	
	public Https(RequestsHandler requests, String trustStoreDirectory,String sslPassword) throws IOException{
		super(requests);
               this.trustStoreDir = trustStoreDirectory;
               this.SSLPasword = sslPassword;
		this.portNum = 4245;
	}

   

	public void start() throws IOException{
                    Log.i("starting https","server");
		if(this.serverSock == null){
                    System.setProperty("javax.net.ssl.keyStore",trustStoreDir);
                    System.setProperty("javax.net.ssl.keyStorePassword", SSLPasword);
                   /* SSLServerSocket sslserver =  (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(portNum);
                    String[] suites = sslserver.getEnabledCipherSuites();
                    for(String suite:suites){
                        Log.i("cipher suite",suite);
                    }
                    SSLParameters ssl = new SSLParameters();
                    ssl.setCipherSuites(suites);
                  */
                    
                    //this.serverSock = delete.createSSLContext(trustStoreDir,SSLPasword,portNum);//sslserver;//
		}
		start(serverSock);
                
                //testServerSocket();
	}
}
