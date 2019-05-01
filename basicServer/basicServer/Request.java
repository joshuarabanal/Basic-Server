package basicServer;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


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
	private String URL = null;
	private int method = -1;
	private String data = null;  
	private Socket sock;
        private BufferedReader in;
	
		Request(Socket s){
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
	public BufferedOutputStream getOut() throws Exception{
			return new BufferedOutputStream(sock.getOutputStream());
		
	}
	public void writeFile(File f) throws Exception{
			OutputStream out = sock.getOutputStream();
			FileInputStream fis = new FileInputStream(f);
			int length;
			byte[] buffer = new byte[128];
			while( (length = fis.read(buffer))>0 ){
				out.write(buffer, 0, length);
			}
			fis.close();
	}
	public PrintWriter getPrintWriter() throws Exception{
			return new PrintWriter(getOut());
	}
	public String getData(){
		return data;
	}
	public int getMethod() throws Exception{
		if(headers.size() == 0){ readHeaders(); } 
		if(method >0){ return method; }
		parseFirstHeader();
		return method;
	}
	public String getURL() throws Exception{
		if(headers.size() == 0){ readHeaders(); } 
		if(URL != null){	return URL;	}
		parseFirstHeader();
		return URL;
	}
	public String getHeaderByName(String name) throws IOException{
		if(headers.size() == 0){ readHeaders(); } 
		
		for(String header : headers){
			if(header.contains(name)){
				return header.substring(header.indexOf(":")+1);//header.split(":")[1].trim();
			}
		}
		
		return null;
	}
	
	
    /**
     * this method should not be used,
     * reading from this input stream will prevent the request object from reading the headers.
     * any data you read will be your own responsibility
     * @return 
     */
    public BufferedReader getInputStream() throws IOException{
        if(in == null){
            in =  new BufferedReader( new InputStreamReader(sock.getInputStream()) );
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
			//System.out.println();
			
			if(length>0){
				//System.out.println("reading data");
				char[] c = new char[length];
				br.read(c);
				data = new String(c);
			}
                        
			//System.out.println("finished reading");
		
	}

	
	
	
	//pieces of header one
	private void getMethod(String head){
		if(method != -1){ return; }
                     if(head.equals("GET")){ method= METHOD_GET;
                        }
                    else if(head.equals("POST")){ method= METHOD_POST;
                        }
                    else if(head.equals("PUT")){ method= METHOD_PUT;
                        }
                    else if(head.equals("PATCH")){ method= METHOD_PATCH;
                        }
                    else if(head.equals("COPY")){ method= METHOD_COPY;
                        }
                    else if(head.equals("HEAD")){ method= METHOD_HEAD;
                        }
                    else if(head.equals("OPTIONS")){ method= METHOD_OPTIONS;
                        }
                    else if(head.equals("LINK")){ method= METHOD_LINK;
                        }
                    else if(head.equals("UNLINK")){ method= METHOD_UNLINK;
                        }
                    else if(head.equals("PURGE")){ method= METHOD_PURGE;
                        }
                    else if(head.equals("CONNECT")){ method= METHOD_CONNECT;
                        }
                //Log.i("method found",  head+" = "+method);
		return;
	}
        public String getMethodAsString() throws Exception{
            switch(getMethod()){
                case METHOD_COPY:
                    return "COPY";
                case METHOD_GET:
                    return "GET";
                case METHOD_HEAD:
                    return "HEAD";
                case METHOD_LINK:
                    return "LINK";
                case METHOD_OPTIONS:
                    return "OPTIONS";
                case METHOD_PATCH:
                    return "PATCH";
                case METHOD_POST:
                    return "POST";
                case METHOD_PURGE:
                    return "PURGE";
                case METHOD_PUT:
                    return "PUT";
                case METHOD_UNLINK:
                    return "UNLINK";
                case METHOD_CONNECT:
                    return "CONNECT";
                default:
                    Log.i("Request headers", headers.toString());
                    throw new UnsupportedOperationException("method:"+method);
                    
            }
        }
	private void getURL(String head){
		if(URL != null){ return; }
		//System.out.println("current url:"+head);
		URL = head.trim();
	}
	private void parseFirstHeader()throws Exception{
		if(headers.size() <= 0){ return; }
		String[] headPieces = headers.get(0).split(" ");
		getMethod(headPieces[0]);
		getURL(headPieces[1]);
		 
	}
        public void getHeaders(ArrayList<String> retu) throws IOException{
            if(headers.size() == 0){
                readHeaders();
            }
            for(int i = 0; i<headers.size(); i++){
                retu.add(headers.get(i));
            }
        }
        public String toString(){
            return "{Request, url:"+URL+", method:"+method+"headers:"+headers+"}\n data:"+data;
        }
}
