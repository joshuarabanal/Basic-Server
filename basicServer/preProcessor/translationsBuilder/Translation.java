/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor.translationsBuilder;

/**
 *
 * @author Joshua
 */
public class Translation{
            public String id, attribute, text;
            Translation(String id, String attribute, String text){
                this.id = id;
                this.attribute = attribute;
                this.text = text;
            }
        }
