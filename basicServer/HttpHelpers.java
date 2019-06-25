package basicServer;

import android.util.Log;
import xml.NameValuePairList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class HttpHelpers {
	
	
//mime types
public static final byte[] MimeHTML = "text/html".getBytes();//"text/html";
public static final byte[] MimeAll = {'*','/','*'};//"*/*";
public static final byte[] MimeCss = "text/css".getBytes();//"text/css";
public static final byte[] MimeJs = "text/javascript".getBytes();//"text/javascript";
public static final byte[] MimeXml = "application/xml".getBytes();// "application/xml";
public static final byte[] MimeTxt = "text/plain".getBytes();//"text/plain";
public static final byte[] MimeGif = "image/gif".getBytes();//"image/gif";
public static final byte[] MimePng = "image/png".getBytes();//"image/png";
public static final byte[] MimeJpg = "image/jpeg".getBytes();//"image/jpeg";
public static final byte[] MimeSvg = "image/svg+xml".getBytes();//image/svg+xml
public static final byte[] MimeJSON = "application/json".getBytes();//application/json


//header fields
private static final byte[] sameOrginHeader = {'X','-','F','r','a','m','e','-','O','p','t','i','o','n','s',':', 'S','A','M','E','O','R','I','G','I','N'};

/* http/1.1 */private static final byte[] statusStart = {'H','T','T','P','/','1','.','1',' '};//http/1.1 
private static final byte[] ContentType = {'C','o','n','t','e','n','t','-','T','y','p','e',':',' '};//"Content-Type: ";
private static final byte[] ContentLength = {'C','o','n','t','e','n','t','-','L','e','n','g','t','h',':',' '};//"Content-Length: ";
private static final byte[] statusSuccess = {'2','0','0',' ','O','K'};//"200 OK"
private static final byte[] tunnelConnectionEstablished = ("200 Connection established").getBytes();
private static final byte[] statusNotChanged = {'3','0','4',' ','N','o','t',' ','M','o','d','i','f','i','e','d'};
private static final byte[] setCookie = {'S','e','t','-','C','o','o','k','i','e',':',' '};//"Set-Cookie: ";
private static final byte[] statusBadRequest = {'4','0','0',' ','B','a','d',' ','R','e','q','u','e','s','t'};//"400 Bad Request";
private static final byte[] status401 = "401 Unauthorized".getBytes();
private static final byte[] statusFileNotFound = "404 Not Found".getBytes();
private static final byte[] ln = ("\r\n").getBytes();//System.lineSeparator();
private static byte[] ContentEncoding_GZIP  = {'C','o','n','t','e','n','t','-','E','n','c','o','d','i','n','g',':',' ','g','z','i','p'};
/**
 * Cache-Control: must-revalidate
 */
private static final byte[] CacheResourceHeader= ("Cache-Control: max-age=120").getBytes();
private static final byte[] CacheEtag=("Etag: ").getBytes();
private static ArrayList<String[]> additionalMimeTypes = new ArrayList<String[]>();

public static void addMimeType(String extension, String mime){
    for(int i = 0; i<additionalMimeTypes.size(); i++){
        if(additionalMimeTypes.get(i)[0].equals(extension)){
            additionalMimeTypes.get(i)[1] = mime;
            return;
        }
    }
    additionalMimeTypes.add(new String[]{extension,mime});
}
	public static String getExtension(byte[] mimeType) throws Exception {
		String s = new String(mimeType);
		switch(s) {
		case "text/html":
			return ".html";
		case "text/css":
			return ".css";
		case "text/javascript":
			return ".js";
		case "application/xml":
			return ".xml";
		case "image/gif":
			return ".gif";
		case "image/png":
			return ".png";
		case "image/jpeg":
			return ".jpg";
		case "image/svg+xml":
			return ".svg";
		case "application/json":
			return ".json";
		}
		for(String[] mime: additionalMimeTypes) {
			if(s.equals(mime[1])) {
				return mime[0];
			}
		}
		Log.e("no extension found for", s);
		Log.i("try adding extensions using", "HttpHelpers.addMimeType(extension, mime);");
		throw new Exception("no extension found error");
		
	}
	public static byte[] getMimeType(String extension){
		 if(extension.equals("html")){ return MimeHTML;}
		else if(extension.equals("css")){ return MimeCss;}
		else if(extension.equals("js")){ return MimeJs;}
		else if(extension.equals("xml")){ return MimeXml;}
		else if(extension.equals("txt")){ return MimeTxt;}
		else if(extension.equals("svg")){ return MimeSvg; }
		else if(extension.equals("gif")){ return MimeGif;}
		else if(extension.equals("png")|| extension.equals("ico")){ return MimePng;}
		else if(extension.equals("jpg")){ return MimeJpg;}
		else if(extension.equals("json")){ return MimeJSON; }
		 for(int i  = 0; i<additionalMimeTypes.size(); i++){
                     if(additionalMimeTypes.get(i)[0].equals(extension)){
                         return additionalMimeTypes.get(i)[1].getBytes();
                     }
                 }
                 Log.i("failed to find mime type", extension);
		return MimeAll;
		
	}
	
	public static File getFileFromUrl(File rootFolder, String url){
            return new File(rootFolder, url);
        }
	
	//private
	private static byte[] parseLength(long length){
		char[] chars = (length+"").toCharArray();
		byte[] retu = new byte[chars.length];
		for(int i = 0; i<retu.length; i++){
			retu[i] = (byte) chars[i];
		}
		return retu;
	}
	/**
	 * 
	 * @param out
	 * @param status
	 * @param mimeType can be null if not used
	 * @param contentLength can be null if not used
	 * @param cookieNameAndValue can be null if no new cookies
	 */
	private static void writeBaseHeader(OutputStream out, byte[] status, byte[] mimeType, long contentLength, ArrayList<Cookie> cookies, boolean autoClose, boolean gzip, String cacheId){
		try {
			out.write(statusStart); out.write(status); out.write(ln);
			out.write(sameOrginHeader); out.write(ln);
			if(mimeType != null){ out.write(ContentType); out.write(mimeType);out.write(ln);}
			if(contentLength >-1){ out.write(ContentLength);  out.write(parseLength(contentLength)); out.write(ln); }
			if(cookies != null){
				for(Cookie c: cookies) {
					out.write(setCookie);
					out.write(c.toString().getBytes());
					out.write(ln);
				}
			} 
			if(gzip){
				out.write(ContentEncoding_GZIP); out.write(ln);
			}
                        writeCacheControl(out, cacheId);
                        
			out.write(ln);
			if(autoClose){ out.close();}
			
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
        private static void writeCacheControl(OutputStream out, String tag) throws IOException{
			out.write(CacheResourceHeader); out.write(ln);
                        if(tag == null) { return; }
            out.write(CacheEtag); 
            out.write('"'); out.write(tag.getBytes()); out.write('"');
            out.write(ln);
        }
	private static BufferedOutputStream httpGetHeader(long length, Request sock, byte[] mimeType)throws Exception{
		BufferedOutputStream out = sock.getOut();
		writeBaseHeader(out, statusSuccess, mimeType, length, null, false, false,null);
		return out;
	 }
	private static BufferedOutputStream httpPostSetCookie(long length, Request sock, byte[] mimeType, ArrayList<Cookie> cookies)throws Exception{
		BufferedOutputStream out = sock.getOut();
		writeBaseHeader(out, statusSuccess, mimeType,length, cookies,false, false,null);
		return out;
	}
	
	//http get
	/**
	 * this function automatically closes the stream
	 * @param sock
	 * @param mimeType
	 * @param text
	 * @throws Exception
	 */
	public static void httpGetResponse(Request sock, byte[] mimeType, String text)throws Exception{
		BufferedOutputStream out  = httpGetHeader(text.length(), sock, mimeType);
                
		
		//out.println(text);
		try {
                        out.write(text.getBytes());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void httpGetResponse(Request sock, File f)throws Exception{
		if(f.isDirectory() && f.exists() && new File(f,"index.html").exists()) {
			f = new File(f,"index.html");
		}
		
		if(sock.getHeaderByName("If-Modified-Since") != null){
			OutputStream out = sock.getOut();
			writeBaseHeader(out, statusNotChanged, null, 0, null, true, false, f.lastModified()+"");
			Log.i("if modified since", ""+f);
		}
		File tempf;
		System.out.println("httpGetResponse:"+f.toString());
		if(f.isDirectory()){ f = new File(f, "index.html"); }
		String head;
		if( (head = sock.getHeaderByName("Accept")) != null && head.contains("webp")){  
			tempf = new File(f.getParent(), f.getName().split("\\.")[0]+".webp"); 
			if(tempf.exists()){ f = tempf;} 
		}
		
                if(sock.getHeaderByName("If-None-Match") != null){
			OutputStream out = sock.getOut();
                        String thisMatch  =("\""+f.lastModified()+"\"");
                        if(
                                thisMatch.equals(
                                sock.getHeaderByName("If-None-Match")
                                )
                        ){
                            Log.i("match", "sending minimum");
                            writeBaseHeader(out, statusNotChanged, null, 0, null, true, false, f.lastModified()+"");
                        }
                        else{
                            Log.i("no match", "file modified:"+f.getName()+", "+thisMatch +"!="+f.lastModified());
                        }
			
		}
		
		
		OutputStream out = null;
		if( (head = sock.getHeaderByName("Accept-Encoding")) !=null &&  head.contains("gzip") && (tempf = new File(f.getParent(), f.getName()+".gz")).exists()){//gzip the file
			out = sock.getOut();
			writeBaseHeader(out, statusSuccess, getMimeType( (f.getName().split("\\."))[1]), tempf.length(), null, false, true,f.lastModified()+"");
			f = tempf;
			//out = new GZIPOutputStream(out);
		}
		else{
			byte[] mimeType = "file/*".getBytes();
			if(f.getName().indexOf('.')>=0) {
				mimeType = getMimeType( (f.getName().split("\\."))[1] );
			}
			out = httpGetHeader(f.length(), sock, mimeType );
		}
		if(!f.exists()){ HttpHelpers.fileNotFound(sock); return; }
		
			FileInputStream fis = new FileInputStream(f);
			int length;
			byte[] buffer = new byte[128];
			while( (length = fis.read(buffer))>0 ){
				out.write(buffer, 0,length);
			}
			fis.close();
			out.close();
		
		
		
	}
	public static void httpGetResponse(Request sock , byte[] mimetype, InputStream in) throws Exception {
		File f = File.createTempFile("http get response", getExtension(mimetype));
		FileOutputStream out = new FileOutputStream(f);
		byte[] b = new byte[1024];
		int howMany;
		while( (howMany = in.read(b))>0) {
			out.write(b, 0, howMany);
		}
		httpGetResponse(sock, f);
		
	}
	/**
	 * for when no appointment unavailable
	 * @param sock
	 * @return status code 401
	 */
	public static void http401Unauthorized(Request sock)throws Exception{
		writeBaseHeader(sock.getOut(), status401, null, -1, null, true, false,null);
		sock.getOut().close();
	}
	
	
	
	//post message responses
	/**
	 * success from  login
	 * @param sock
	 * @param mimeType
	 * @param text
	 * @param CookieName
	 * @param CookieValue
	 */
	public static void httpPostResponse(Request sock, byte[] mimeType, String text, ArrayList<Cookie> cookies)throws Exception{
		System.out.println("httpPostResponse:"+text);
		BufferedOutputStream out;
		if(cookies!= null && cookies.size()>0){
			out = httpPostSetCookie((text == null)? -1: text.length(),sock,mimeType,  cookies);
		}
		else{
			out = httpGetHeader((text == null)? -1:text.length(), sock,mimeType);
		}
			if(text !=null){
				char[] bytes = text.toCharArray();
				for(char b: bytes){ out.write(b); }
			}
			out.close();
		
	}
	/**
	 * convinience function for {@link #httpPostResponse(Request, byte[], String, ArrayList)}
	 * @param sock
	 * @param mimeType
	 * @param text
	 * @param cookiename
	 * @param cookieValue
	 * @throws Exception
	 */
	public static void httpPostResponse(Request sock, byte[] mimeType, String text, Cookie cookie)throws Exception{
		ArrayList<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(cookie);
		httpPostResponse( sock,  mimeType,  text, cookies);
	}
	
	/**
	 * success from appointment 
	 * @param sock
	 * @return status code 200
	 */
	public static void httpSuccessFromPostRequest(Request sock)throws Exception{
		System.out.println("success response");
		BufferedOutputStream out = sock.getOut();
		writeBaseHeader(out, statusSuccess, null, -1, null, true, false,null);
		//out.println(statusStart+statusSuccess);
		//out.println();
		//out.close();
		
	}
       
	/**
	 * for when not loged in or invalid login
	 * @param sock
	 * @return status code 400
	 */
	public static void httpLoginFailed(Request sock)throws Exception{
		System.out.println("login failed"); 
		writeBaseHeader(sock.getOut(), statusBadRequest, null, -1, null, true, false,null);
		//BufferedOutputStream out = sock.getOut();
		//out.println(statusStart+statusBadRequest);
		//out.println();
		//out.close();
	}
	
	public static void fileNotFound(Request sock)throws Exception{
		writeBaseHeader(sock.getOut(), statusFileNotFound, null, -1, null, true, false,null);
		//PrintWriter out = sock.getPrintWriter(); 
		//out.println(statusStart+statusFileNotFound);
		//out.println();
		//out.close();
	}
        /**
         * @deprecated 
         * @param sock
         * @throws Exception 
         */
        public static void proxyServerConnectResponse(Request sock) throws Exception{
            OutputStream out = sock.getOut();
            out.write(statusStart);//HTTP/1.1
            out.write(statusSuccess);//200 Connection established
            out.write(ln);
            //out.write("Proxy-agent: Sun-Java-System-Web-Proxy-Server/4.0 ".getBytes());
            //out.write(ln);
            out.write(ln);
//Date: Mon, 27 Jul 2009 12:28:53 GMT
//Server: Apache/2.2.14 (Win32)
        }
	

}
