/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Joshua
 */
public class Dir_JSOn_builder {
    
    public static void buildDir_JSONinDirs(File folder) throws FileNotFoundException, IOException{
        if(folder.isDirectory()){
            File[] children = folder.listFiles();
            FileOutputStream fout = new FileOutputStream(new File(folder,"dir.json"));
            fout.write("[".getBytes() );
            for(int i = 0; i<children.length; i++){
                if(i>0){ fout.write(",".getBytes());}
                fout.write( 
                        ("\""+children[i].getName()+"\"").getBytes()
                );
            }
            fout.write("]".getBytes());
            fout.close();
            for(int i = 0; i<children.length; i++){ buildDir_JSONinDirs(children[i]); }
        }
    }
    
}
