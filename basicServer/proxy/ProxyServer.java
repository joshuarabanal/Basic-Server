/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.proxy;

import android.util.Log;
import basicServer.HttpHelpers;
import basicServer.ProcessRequest;
import basicServer.Request;
import basicServer.RequestsHandler;
import basicServer.serverSock.Http;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
public class ProxyServer extends RequestsHandler implements ProcessRequest{
    private static final String
            header_HOST = "Host:",
            header_Proxy_Connection = "Proxy-Connection:";
    
    private ServerSocket sock;
    private Http http;
    
    public ProxyServer(){
        super(null);
        this.setRequestProcessor(this);
    }
    public void setHTTP(ServerSocket s){
        sock =s;
        http = new Http(this);
    }
    public void start() throws IOException{
        http.start(sock);
    }
    public void preProcess(){
        
    }

    private ArrayList<String> headers = new ArrayList<String>();
    private ArrayList<String> proxyHeaders = new ArrayList<String>();
    private String method;
    private String url;
    @Override
    public int processRequest(Request r) throws Exception {
        
        try{
            setHeaders(r);

            int method = r.getMethod();
            if(method == Request.METHOD_GET){ passOnRequest(r); }
            else if(method == Request.METHOD_CONNECT){ ConnectRequest(r); }
            else{
                    Log.i("unknown method", r.getMethodAsString());
                    r.close();
            }
            

                    Log.i("finished request", r+"");
                    Log.i("method",r.getMethodAsString()+" == "+method);
                    Log.i("headers",headers.toString());
                    Log.i("proxy headers", proxyHeaders.toString());
                    Log.i("url",url);
                    Log.i("request data", r.getData());
                    headers.clear(); r.getHeaders(headers);
                    Log.i("raw headers", headers.toString());
                    System.out.println("_");
                    System.out.println("_");
                    System.out.println("___________________________________");
                    System.out.println("_");
                    System.out.println("_");
                    
            return 0;
        }
        catch(Exception e){
            Log.i("exception",e.getMessage());
            Log.i("method",method);
            Log.i("headers",headers.toString());
            Log.i("proxy headers", proxyHeaders.toString());
            Log.i("url",url);
            e.printStackTrace();
            throw e;
        }
    }
    public void ConnectRequest(Request r) throws Exception{
        String url = this.url.substring(0, this.url.indexOf(":"));
        int port = Integer.parseInt(this.url.substring(this.url.indexOf(":")+1));
        Log.i("url", url);
        Log.i("port",port+"");
        Log.i("body", r.getData());
            TunnelConnection tc = new TunnelConnection(url,port, r); 
          tc.run();
        
        
    }
    private void setHeaders(Request r) throws Exception{
        headers.clear(); proxyHeaders.clear();
        //r.getMethod();
        r.getHeaders(headers);
        for(int i = 0; i<headers.size(); i++){
            String header = headers.get(i);
           if(
                   header.contains(header_Proxy_Connection) ||
                   header.contains(header_HOST)
            ){
               proxyHeaders.add(headers.remove(i));
               i--;
           }
        }
        
        
        
        String method = headers.remove(0);
       
        
        this.method = r.getMethodAsString();
        this.url = r.getURL();
        for(int i = 0; i<headers.size(); i++){
            if(headers.get(i).indexOf(this.method) ==0){
                headers.remove(i);
                break;
            }
        }
        
    }
    
    private byte[] buffer = new byte[1024];
    
    private void passOnRequest(Request r) throws Exception{
        URL url = new URL(r.getURL());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
       for(int i = 0; i<headers.size(); i++){
           String header = headers.get(i);
            connection.addRequestProperty(
                    header.substring(0,header.indexOf(":")), 
                    header.substring(header.indexOf(":")).trim()
                    );
       }
        
        
        connection.connect();
     
            InputStream response;
            try{ 
                response = connection.getInputStream();
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
                response = connection.getErrorStream();
            }
            BufferedOutputStream out = r.getOut();
            int howmany;
            StringBuilder debug = new StringBuilder();
            while((howmany = response.read(buffer))>0){
                out.write(buffer,0,howmany);
                debug.append(new String(buffer,0,howmany));
            }

            out.close();
            response.close();
            r.close();
            connection.disconnect();
        
    }
	@Override
	public void saveState(File file) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void openCache(File file) {
		// TODO Auto-generated method stub
		
	}
    
}
