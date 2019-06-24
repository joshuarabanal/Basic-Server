/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Joshua
 */
public class GZipFIles {
    
    
	public static void GZipFilesInFolder(File publicFilesDirectory){
		File[] files = publicFilesDirectory.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				GZipFilesInFolder(file);
			}
			else if(file.isFile()){
				GzipFile(file);
			}
		}
	}
	private static void GzipFile(File f){
		if(f.getName().contains(".gz")){ return; }
		File out = new File(f.getParentFile(), f.getName()+".gz");
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(out));
			FileInputStream in = new FileInputStream(f);
			byte[] buffer = new byte[1024];
			int readCount;
			while( (readCount = in.read(buffer)) >0){
				gzip.write(buffer, 0, readCount);
			}
			gzip.close();
			in.close();
                        if(out.length() > f.length()){
                            Log.i("failed to gzip", out.toString());
                            out.delete();
                        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
