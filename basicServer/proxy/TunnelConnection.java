/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.proxy;

import android.util.Log;
import basicServer.HttpHelpers;
import basicServer.Request;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Joshua
 */
public class TunnelConnection {
    private ServerSocket sock;
    private String url;
    private int port;
    private Request r;
    private char[] buffer = new char[1024];
    public TunnelConnection(String url,int port,Request r) throws Exception{
        this.url = url;
        //sock = new ServerSocket(port);
        this.url = ""+this.url;
         r.getOut().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
         //r.getOut().close();
         this.r = r;
         this.port = port;
    }
    public void Connect() throws IOException{
        Socket s = new Socket(url, port);
    }
   
    public void run() throws Exception{
        if(true){ return; }
        Log.i("tunnel connection", "reading:"+r);
        r.resetForNextRequest();
        BufferedReader in = null;
        try{
            in = r.getInputStream();
            int howMany;
            StringBuilder sb = new StringBuilder();
            while((howMany = in.read(buffer)) >0){
                Log.i("data", new String(buffer,0, howMany));
                sb.append(new String(buffer,0, howMany));
                
            }
            Log.i("finished reading"," "+sb.toString());
        }
        catch(Exception e){
            Log.i("tunnel connection error", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        System.exit(1);
    }
    /**
    public void runz(){
        try{
        URL url = new URL("https://"+this.url+":"+443);
        
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.connect();
            OutputStream out = r.getOut();
            InputStream in = connection.getInputStream();
            int howMany;
            while((howMany = in.read(buffer))>0){
                out.write(buffer,0,howMany);
                Log.i("tunnel", new String(buffer, 0,howMany));
            }
            out.close();
            in.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
            //System.exit(1);
        }
    }
    public void runs(){
        try{
        Socket s = sock.accept();
        InputStream in = s.getInputStream();
        int howMany;
        while((howMany = in.read(buffer))>0){
            Log.i("reading", new String(buffer,0,howMany));
        }
        s.close();
        
        System.exit(1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    **/
}
