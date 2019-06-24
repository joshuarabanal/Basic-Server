package basicServer;
import android.util.Log;
import xml.NameValuePairList;
import xml.unoptimized.Attribute;
import xml.unoptimized.NameValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;


public class Request{
	
	public final static String headerCookie = "Cookie";
	public final static String  headerUserAgent = "User-Agent";
	public final static int 
                METHOD_GET = 1, METHOD_POST = 2, 
                METHOD_PUT = 3, METHOD_PATCH = 4,
                METHOD_COPY = 5, METHOD_HEAD = 6,
                METHOD_OPTIONS = 7, METHOD_LINK = 8,
                METHOD_UNLINK = 9, METHOD_PURGE = 10,
                METHOD_CONNECT = 11;
	
	
	
	
	private ArrayList<String> headers;
	private String URL = null,
					query = null,
			 		method = null,
			 		data = null;  
	private Socket sock;
        private BufferedReader in;
        public boolean ssl = false;
	
		protected Request(Socket s){
			if(s!= null) {
				Log.i("new socket recieved", s.toString());
			}
			if(s != null && s instanceof SSLSocket) {
				this.ssl = true;
			}
			sock = s;
			headers = new ArrayList<String>();
			
		}
		
		
//public getters
	public void close() {
            Log.i("socket closed", "socket closed");
            try{ 
                sock.close();
            }
            catch(Exception e){ e.printStackTrace();}
			
	}
	private BufferedOutputStream out = null;
	public BufferedOutputStream getOut() throws Exception{
		if(out == null) {
			out=  new BufferedOutputStream(sock.getOutputStream());
		}
			return out;
		
	}
	public void writeFile(File f) throws Exception{
			OutputStream out = getOut();
			FileInputStream fis = new FileInputStream(f);
			int length;
			byte[] buffer = new byte[128];
			while( (length = fis.read(buffer))>0 ){
				out.write(buffer, 0, length);
			}
			out.flush();
			fis.close();
	}
	public PrintWriter getPrintWriter() throws Exception{
			return new PrintWriter(getOut());
	}
	public String getFirstHeader() throws IOException {
		if(headers.size() == 0){ readHeaders(); } 
		if(headers.size()==0) { return null; }
		return headers.get(0);
		
	}
	public String getData(){
		return data;
	}
	public NameValuePairList getFormData() throws IOException {
		String contentType = getHeaderByName("Content-Type");
		if(contentType == null) {
			throw new IOException("no content type");
		}
		if(contentType.contains("application/x-www-form-urlencoded")) {
			String[] items = getData().split("&");
			NameValuePairList nvpl = new NameValuePairList();
			for(String s : items) {
				//Log.i("string item", s);
				int index = s.indexOf("=");
				nvpl.add(
						URLDecoder.decode(s.substring(0, index)),
						URLDecoder.decode(s.substring(index+1))
					);
			}
			return nvpl;
		}
		else {
			Log.i("content type", contentType);
			throw new IOException("unsupported content type");
		}
	}
	public int getMethod() throws Exception{
		if(headers.size() == 0){ readHeaders(); } 
		if(method == null) { parseFirstHeader(); }
		if(method == null ) { return -1; }
		switch(method) {
			case "GET":
				return METHOD_GET;
			case "POST":
				return METHOD_POST;
			case "PUT":
				return METHOD_PUT;
			case "PATCH":
				return METHOD_PATCH;
			case "COPY":
				return METHOD_COPY;
			case "HEAD":
				return METHOD_HEAD;
			case "OPTIONS":
				return METHOD_OPTIONS;
			case "LINK":
				return METHOD_LINK;
			case "UNLINK":
				return METHOD_UNLINK;
			case "PURGE":
				return METHOD_PURGE;
			case "CONNECT":
				return METHOD_CONNECT;
			default: return -1;
		}
		
	}
	public String getURL() throws Exception{
		if(headers.size() == 0){ readHeaders(); } 
		if(URL != null){	return URL;	}
		parseFirstHeader();
		return URL;
	}
	public NameValuePairList getQuery() throws Exception {

		if(headers.size() == 0){ readHeaders(); } 
		if(URL == null){	parseFirstHeader();	}
		NameValuePairList retu = new NameValuePairList();
		if(query == null) {
			return retu;
		}
		
		String[] querys = query.split("&");
		for(String s : querys) {
			if(s.contains("=")) {
				String[] nvp = s.split("=");
				retu.add(nvp[0],nvp[1]);
			}
			else {
				retu.add(s,null);
			}
		}
		Log.i("created query list", retu.toString());
		return retu;
	}
	public ArrayList<String> getAllHeadersWithName(String name) throws IOException {
		if(headers.size() == 0){ readHeaders(); } 
		ArrayList<String> retu = new ArrayList<String>();
		for(String header : headers){
			if(header.contains(name) && header.indexOf(name) < header.indexOf(':')){
				retu.add( header.substring(header.indexOf(':')+1).trim() );//header.split(":")[1].trim();
			}
		}
		return retu;
	}
	public String getHeaderByName(String name) throws IOException{
		if(headers.size() == 0){ readHeaders(); } 
		
		for(String header : headers){
			if(header.contains(name) && header.indexOf(name) < header.indexOf(':')){
				return header.substring(header.indexOf(':')+1);//header.split(":")[1].trim();
			}
		}
		
		return null;
	}
	
	public void resetForNextRequest() { 
		headers.clear();
		data= null;
		data = method = URL = null;
	}
	public Socket getUnderlyingSocket() { return sock; }
    /**
     * this method should not be used,
     * reading from this input stream will prevent the request object from reading the headers.
     * any data you read will be your own responsibility <br/>
     * TIP: you can use {@link #resetForNextRequest()} to read annother packet of http information
     * @return 
     */
    public BufferedReader getInputStream() throws IOException{
        if(in == null){
        	/**
        	InputStream fin = sock.getInputStream();
        	byte[] b = new byte[2048];
        	int howMany = 0;
        	while( (howMany = fin.read(b)) > 0) {
        		String s = new String(b,0,howMany)
        				.replaceAll("\n", "[n]")
        				.replaceAll("\r", "[r]")
        				.replaceAll("\t", "[t]");
        		
        		Log.i("reading", s);
        	}
        	**/
            in =  new BufferedReader( new InputStreamReader(sock.getInputStream(),"UTF-8") );
        }
        return in;
    }
	private void readHeaders() throws IOException{
			BufferedReader br = getInputStream();
			String line;
			int length = -1;
			//System.out.println("waiting for request headers:");
			while( (line = br.readLine()) != null && line.length() >0 ){
				//System.out.println(line+"; "+line.length());
				if(line.contains("Content-Length")){
					length = Integer.parseInt(line.split(":")[1].replace(" ",""));
				}
				headers.add(line);
			}
			
			//System.out.println();//log what was read
			//for(String lin : headers){ System.out.println("reading header:"+lin); }
			System.out.println();
			
			if(length>0){
				//System.out.println("reading data");
				char[] c = new char[length];
				br.read(c);
				data = new String(c);
			}
                        
			//System.out.println("finished reading");
		
	}


    public String getMethodAsString() throws Exception{
    		if(headers.size() == 0){ readHeaders(); } 
    		if(method == null) { parseFirstHeader(); }
    		return method;
        }

	private void parseFirstHeader()throws Exception{
		if(headers.size() <= 0){ return; }
		String[] headPieces = headers.get(0).split(" ");
		if(method == null){ method = headPieces[0]; }
		if(headPieces.length<=0) {
			Log.i("strange header", headers.get(0));
		}
		if(headPieces[1] != null) {
			URL = headPieces[1].trim();
			if(URL.contains("?")) {
				int queryIndex = URL.indexOf("?");
				query = URL.substring(queryIndex);
				URL = URL.substring(0,  queryIndex);
			}
		}
		 
	}
        public void getHeaders(ArrayList<String> retu) throws IOException{
            if(headers.size() == 0){
                readHeaders();
            }
            for(int i = 0; i<headers.size(); i++){
                retu.add(headers.get(i));
            }
        }
       
    public NameValuePairList getCookies() throws IOException {
    	String cookieString = getHeaderByName(headerCookie);
    	if(cookieString == null || cookieString.length() == 0) {
    		return null;
    	}
    	NameValuePairList retu = new NameValuePairList();
    	String[] cookieArray = cookieString.split(";");
    	for(String cookie : cookieArray) {
    		if(cookie.length() == 0) { continue; }
    		String[] nvp = cookie.split("=");
    		if(nvp.length ==1) {
    			retu.add(new Attribute(cookie, null));
    		}
    		else if(nvp.length == 2) {
    			retu.add(new Attribute(nvp[0], nvp[1]));
    		}
    		else {
    			Log.e("cookie", cookie);
    			throw new IndexOutOfBoundsException("incoorect cookie");
    		}
    	}
		return retu;
    }
     public String toString(){
        	if(headers.size() == 0){
                try {
					readHeaders();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return "{Request, url:"+URL+", method:"+method+"headers:"+headers+"}\n data:"+data;
        }
        public void logValues() {
        	if(headers.size() == 0){
                try {
					readHeaders();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        	Log.i("logging request", "logging request");
        	Log.i("url", this.URL);
        	for(int i = 0; i<this.headers.size(); i++) {
        		Log.i("header", this.headers.get(i));
        	}
        	Log.i("content body", this.data);
        	Log.i("end of request logs", "end of request logs");
        }
        public File toFile() throws IOException {
        	if(headers.size() == 0){
					readHeaders();
            }
        	byte[] newLine = "\r\n".getBytes();
        	
        	File retu = File.createTempFile("request", ".txt");
        	FileOutputStream fout = new FileOutputStream(retu);
        	for(int i = 0; i<headers.size(); i++) {
        		fout.write(headers.get(i).getBytes());
        		fout.write(newLine);
        	}
        	if(data!=null) {
        		fout.write(newLine);
        		fout.write(data.getBytes());
        	}
    		fout.write(newLine);
    		fout.write(newLine);
        	fout.close();
        	
        	return retu;
        }
}
