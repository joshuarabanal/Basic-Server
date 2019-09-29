package mailServer.smtp.port25;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import android.util.Log;

/**
 * smtp uses port 465 for SSL
 * @author Joshu
 *
 */
public class SMTP_SSL_Server extends SMTPServer {
	

	public SMTP_SSL_Server(String domain, File jksFile, String SSLPassword) throws IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		super(domain,465+1);
		port25.close();
		setServerSocket(jksFile, SSLPassword);
	}
	private void setServerSocket(File jks, String SSLPassword) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
		Log.i("starting https","server");
        KeyStore ks = KeyStore.getInstance("PKCS12");
        Log.i("loading keystore", "password:"+SSLPassword+", file:"+jks);
        ks.load(new FileInputStream(jks), SSLPassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, SSLPassword.toCharArray());

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(465);
        
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

        port25 = s;
	}

}
