/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor.InlineCSS;

import Analytics.CrashReporter;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import xml.NameValuePairList;
import xml.XmlCursor;
import xml.unoptimized.Parser;
import xml.unoptimized.Parser2;

/**
 *
 * @author Joshua
 */
public class InlineCSS  implements XmlCursor{
    private File[] files;
    private ArrayList<String> stack = new ArrayList<String>();
    private PrintStream out;
    private File currentReadingFile;
    private File rootFile;
    
    
    
    private static final String[] wellKnownTags = {
        "html", 
        "div","h1","h2", "h3", "link",//5
        "float", "id", "style", "href", "type",//10
        "rel", "stylesheet","id","a", "span","p", "class"
    };
    private static final int TAGINDEX_LINK = 5;
    
    
    
    
    public InlineCSS(File[] files, File root){
        this.files = files;
        rootFile = root;
    }
    public InlineCSS(File rootFolder) throws Exception{
        rootFile = rootFolder;
        ArrayList<File> files = new ArrayList<File>();
        parseFileTree(rootFolder,files);
        this.files = new File[files.size()];
        for(int i= 0; i<files.size(); i++){
            this.files[i] = files.get(i);
        }
        
    }
    private void parseFileTree(File rootFile, ArrayList<File> output) {
       if(rootFile.isDirectory()){
          File[] files = rootFile.listFiles();
          for(int i = 0; i<files.length; i++){
              parseFileTree(files[i],output);
          }
       }
       else{
           String[] extensions = rootFile.getName().split("\\.");
            String extension = extensions[extensions.length-1];
           if(!output.contains(rootFile) && extension.equals("html")){
             
               output.add(rootFile);
           }
       }
    }
    
    public void run()throws Exception {
        try{
        for(int i = 0; i<this.files.length; i++){
            currentReadingFile = files[i];
            File temp = new File(files[i].toString()+".tmp");
            out = new PrintStream(new FileOutputStream(temp));
            stack.clear();
            Parser2 p = new Parser2(currentReadingFile, this, wellKnownTags );
            p.read();
            out.close();
            
            //copy over the new file
            FileOutputStream fout = new FileOutputStream(currentReadingFile);
            FileInputStream fis = new FileInputStream(temp);
            int howMany;
            byte[] b = new byte[1024];
            while((howMany = fis.read(b))>0){
                fout.write(b, 0, howMany);
            }
            fis.close();
            fout.close();
            temp.delete();
            
        }
        }
        catch(Exception e){
            Log.i("ERROR:CSS", "while reading:"+currentReadingFile.toString());
            throw e;
        }
    }

    private byte[] b = new byte[1024];
    @Override
    public void newElement(String name, NameValuePairList attributes, boolean autoClose) throws Exception {
        if(
                name.equals("di")||
                name.equals("l")||
                name.equals("spa")||
                name.equals("htm")||
                name.contains(" ")
        ){
            CrashReporter.log("elem name:"+name);
            throw new Exception("malformed input");
        }
        
        
        if(
                name.equals(wellKnownTags[TAGINDEX_LINK]) &&
                attributes.getAttributeValue( "rel") != null && attributes.getAttributeValue( "rel").equals("stylesheet") &&
                attributes.getAttributeValue( "type") != null && attributes.getAttributeValue( "type").equals("text/css") 
        ){//if this is a css element that we need to include
            String href = attributes.getAttributeValue( "href");
            File hrefFile;
            if(href.indexOf("/") == 0 || href.indexOf("../") == 0){
                hrefFile = new File(rootFile, href);
            }
            else{
                hrefFile = new File(currentReadingFile.getParentFile(), href);
            }
            if(!hrefFile.exists()){
                Log.i("failed to find css file", hrefFile.toString());
                Log.i("href link",href);
                Log.i("current reading file", currentReadingFile.toString());
                throw new FileNotFoundException("inline css failed to include");
            }
            FileInputStream fis = new FileInputStream(hrefFile);
            int howmany;
            out.println("<style>");
            while((howmany = fis.read(b))>0){
                out.write(b,0,howmany);
            }
            fis.close();
            out.println();
            out.println("</style>");
        }
        else{//write out the normal tag
            if(name.trim().length() == 0){
                throw new IndexOutOfBoundsException("the tag is null"); 
            }
            out.print("<"+name+" ");
            for(int i = 0; attributes != null && i<attributes.size(); i++){
                out.print(" "+attributes.get(i).getName()+"=\""+attributes.get(i).getValue()+"\"");
            }
           if(autoClose){
            out.println("/>");
           }
           else{
               out.println(">");
           }
        }
        
        
    }

    @Override
    public void closeElement(String name) throws Exception {
        out.println("</"+name+">");
    }

    @Override
    public void textElement(String text) {
        if(
                text.charAt(0) == '<' && 
                text.charAt(1) == '!' &&
                text.charAt(2) == '-' &&
                text.charAt(3) == '-'
        ){
            if(text.indexOf("-->") != text.length()-3){
                CrashReporter.log("text element:"+text);
                throw new IndexOutOfBoundsException("commentn made");
            }
            return;
        }
        //Log.i("text element", text);
        out.print(text.trim());
    }
}
