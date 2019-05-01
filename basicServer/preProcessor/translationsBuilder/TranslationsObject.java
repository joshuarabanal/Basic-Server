/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor.translationsBuilder;

import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import xml.NameValuePairList;
import xml.XmlCursor;
import xml.unoptimized.NameValuePair;
import xml.unoptimized.Parser;

/**
 *
 * @author Joshua
 */
public class TranslationsObject implements XmlCursor{
    public static String[] namesAndAttributes = {
        "translations",
        "language",
        "languageCode",
        "translation",
        "id",
        "attribute"
    };
    private ArrayList<Language> translations = new ArrayList<Language>();
    
    public TranslationsObject(File translationsFile) throws Exception{
        try{
        Parser p = new Parser(translationsFile, this, namesAndAttributes);
        p.read();
        }catch(Throwable t){
            Log.i("failed reading file", translationsFile.toString());
            throw t;
        }
    }

    
    public int getNumberOfLanguages(){ return translations.size(); }
    public Language getLanguageAt(int i ){
        return translations.get(i);
    }
    
    
    
    //xml parser functions
    
    @Override
    public void newElement(String name, NameValuePairList attributes, boolean autoClose) throws Exception {
        if(name.equals("language")) {
            String languageCode = attributes.getAttributeValue( "languageCode");
            if(languageCode == null){ throw new NullPointerException("Language element error error in file"); }
            //languageCodes.add(languageCode);
            translations.add(new Language(languageCode));
        }
        else if(name.equals("translation")){
            String id = attributes.getAttributeValue("id");
            String attribute = attributes.getAttributeValue( "attribute");
            String text = attributes.getAttributeValue( "text");
            if(id == null || attribute == null || text == null){
                throw new NullPointerException("null name:"+name +", attrs:"+attributes.toString());
            }
            translations.get(translations.size()-1).add(id,attribute,text);
        }
    }

    @Override
    public void closeElement(String name) throws Exception {
    }

    @Override
    public void textElement(String text) {
        
    }

    
}
