/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor;

import android.util.Log;
import basicServer.Preprocessor.translationsBuilder.Language;
import basicServer.Preprocessor.translationsBuilder.Translation;
import java.io.File;
import java.util.ArrayList;
import basicServer.Preprocessor.translationsBuilder.TranslationsObject;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import xml.NameValuePairList;
import xml.XmlCursor;
import xml.unoptimized.Attribute;
import xml.unoptimized.NameValuePair;
import xml.unoptimized.Parser;

/**
 *this file takes a .translations file and passes it through all of the .html files
 * for each .html file create a file with the same name and the extension ".html.translation" in the same directory.<br>
 * then use {@Link crawlFolderForTranslations(File)} to auto generate the translated files
 * the translati
 * @author Joshua
 */
public class TranslationsBuilder  implements XmlCursor{
    private File translationsFile;
    private File htmlFile;
    private TranslationsObject translations;
    public static final String translationFileExtension = ".translations";
    
    
    public static void crawlFolderForTranslations(File folder) throws Exception{
        
        if(folder.isDirectory()){
            File[] files = folder.listFiles();
            for(int i =0; i<files.length; i++){
                crawlFolderForTranslations(files[i]);
            }
        }
        else if(folder.getName().contains(".html")){
            File translation = new File(folder.getParent(), folder.getName()+translationFileExtension);
            if(translation.exists()){
                Log.i("starting translation", folder.toString());
                new TranslationsBuilder(translation, folder).run();
            }
        }
    }
    
    public TranslationsBuilder(File translationsXML, File defaultHTMLFile){
        translationsFile = translationsXML;
        htmlFile = defaultHTMLFile;
    }
    
    
    public void run() throws Exception{
        translations = new TranslationsObject(translationsFile);
        for(int i =0; i<translations.getNumberOfLanguages(); i++){
            writeFile(translations.getLanguageAt(i));
        }
        
        
    }
    
    
    
    
    private PrintWriter out;
    private Language lan;
    private void writeFile(Language l) throws Exception{
        lan = l;
        out = new PrintWriter(new FileOutputStream(new File(htmlFile.getParentFile(), htmlFile.getName()+"."+l.languageCode)));
        Parser p = new Parser(htmlFile, this, null);
        p.read();
        Log.i("file translated", htmlFile.toString()+"."+l.languageCode);
    }

    private int stack = 0;
    private int replaceInnerHtmlStack = -1;
    private String innerHtml;
    @Override
    public void newElement(String name, NameValuePairList attributes, boolean autoClose) throws Exception {
        if(!autoClose){
            stack++;
        }
        if(replaceInnerHtmlStack >-1){
            replaceInnerHtmlStack++;
        }
        String id = null;
        if(attributes !=null){
            id= attributes.getAttributeValue("id");//Parser.getAttributeValue(attributes, "id");
        }
        if(id != null){
            ArrayList<Translation> t= lan.get(id);
            for(int tr =0; tr<t.size(); tr++){//for each matching translation
                Translation trans = t.get(tr);
                for(int i = 0; i<attributes.size(); i++){
                    if(attributes.get(i).getName().equals(trans.attribute)){//if this is the right attribute
                        ((Attribute)attributes.get(i)).setValue(trans.text);//replace the value
                    }
                }
                if(trans.attribute.equals("innerHTML")){
                    replaceInnerHtmlStack = 0;
                    innerHtml = trans.text;
                }
            }
        }
        
        
        //print out generated object
        out.print("<"+name);
        for(int i = 0; (attributes != null && i<attributes.size()); i++){
            out.print(attributes.get(i).toString());
        }
        if(autoClose){ out.println("/>"); }
        else{ out.println(">"); }
    }
   

    @Override
    public void closeElement(String name) throws Exception {
        stack--;
        out.println("</"+name+">");
        if(replaceInnerHtmlStack >-1){
            replaceInnerHtmlStack--;
            if(replaceInnerHtmlStack == -1){
                innerHtml = null;
            }
        }
        if(stack == 0){
            out.close();
        }
    }

    @Override
    public void textElement(String text) {
        Log.i("text element", text);
        if(replaceInnerHtmlStack == -1){
            out.print(text);
        }
    }
    
    
}
