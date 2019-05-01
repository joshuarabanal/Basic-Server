/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor;

import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Joshua
 */

public class SiteMapBuilder {
    private File rootSearchFolder;
    private ArrayList<String> exludedFileExtensions = new ArrayList<String>();
    private PrintStream out;
    
    
    /**
     * 
     * @param rootSearchFolder the folder
     * @throws FileNotFoundException 
     */
    public SiteMapBuilder(File rootSearchFolder) throws FileNotFoundException{
        //Log.i("sitemap builder","initiALIZING");
        this.rootSearchFolder = rootSearchFolder;
        FileOutputStream fos = new FileOutputStream(new File(rootSearchFolder,"sitemap.xml"));
        this.out = new PrintStream(fos);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        addExcludedFileExtensions("css", "js","png", "svg", "jpeg","gz","json");
    }


    
    public void addExcludedFileExtensions(String... extensions){
        for(int i = 0; i<extensions.length; i++){
            this.exludedFileExtensions.add(extensions[i]);
        }
    }
    
    /**
     * 
     * @param domainURL http://example.com
     * @param dynamicURLS list of urls that wont be found
     */
    public void build(String domainURL, ArrayList<String> dynamicURLS){
        File[] files = rootSearchFolder.listFiles();
        for(int i = 0; i<files.length; i++){
            scanFolder(files[i], domainURL);
        }
        
        if(dynamicURLS !=null){
            //Log.i("dynamic urls", dynamicURLS.toString());
          for(int i = 0; i<dynamicURLS.size(); i++){
              addURLToMap( dynamicURLS.get(i));
          }
        }
        out.println("</urlset>");
        out.close();
    }
    private void scanFolder(File searchFile, String currentDirectory){
        //Log.i("scanning file", searchFile.getName());
        if(searchFile.isFile()){
            String[] extensions = searchFile.getName().split("\\.");
            String extension = extensions[extensions.length-1];
            for(int i = 0; i<this.exludedFileExtensions.size(); i++){
                if(extension.equals(this.exludedFileExtensions.get(i))){
                     return;
                }
            }
            addFileToMap(searchFile, currentDirectory+"/"+searchFile.getName());
        }
        else{
            File[] children = searchFile.listFiles();
            for(int i = 0; i<children.length; i++){
                scanFolder(children[i], currentDirectory+"/"+searchFile.getName());
            }
        }
    }
    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy:MM:dd");
        return format.format(date).replaceAll(":","-");
    }
    private void addURLToMap(String url){
        //Log.i("adding url", url);
        out.println("<url>");
        out.println("<loc>"+url+"</loc>");
        out.println("<changefreq>monthly</changefreq>");
        out.println("</url>");
    }
    private void addFileToMap(File file, String fileDirectory){
        out.println("<url>");
        out.println("<loc>"+fileDirectory+"</loc>");
        out.println("<lastmod>"+convertTime(file.lastModified())+"</lastmod>");
        out.println("<changefreq>monthly</changefreq>");
        out.println("</url>");
    }

}
