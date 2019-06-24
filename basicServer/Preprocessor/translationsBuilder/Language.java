/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor.translationsBuilder;

import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
    public class Language{
        public final String languageCode;
        ArrayList<Translation> translations= new ArrayList<Translation>();
        Language(String languageCode){
            this.languageCode = languageCode;
        }
        void add(String id, String attribute, String text){
            if(id == null || attribute == null || text == null){
                throw new NullPointerException("id:"+id+", attr:"+attribute+", text:"+text);
            }
            translations.add(new Translation(id,attribute,text));
        }
        public ArrayList<Translation> get(String id){
            ArrayList<Translation> retu = new ArrayList<Translation>();
            for(int i = 0; i<translations.size(); i++){
                if(translations.get(i).id.equals(id)){
                     retu.add(translations.get(i));
                }
            }
            return retu;
        }
        
    }
