/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicServer.Preprocessor;

import android.util.Log;
import basicServer.ProcessRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Joshua
 */
public class GZipFIles {
    
	
/**
 * when you gzip a folder often you want to copy the directory to a new one in order to prevent duplicates
 */
    private static byte[] b;
public static File moveFolder(File oldFolder, File newFolder) throws FileNotFoundException, IOException {
	newFolder.mkdirs();
	for(File f: oldFolder.listFiles()) {
		MoveFile(f,newFolder);
	}
	return newFolder;
}
public static File MoveFile(File oldFile, File newDirectory) throws FileNotFoundException, IOException{
    if(newDirectory == null){
        newDirectory = File.createTempFile("server rootDir", "temporary");
        deleteFolder(newDirectory);
        newDirectory.mkdir();
        File[] childs = oldFile.listFiles();
        for(int i = 0; i<childs.length; i++){
            MoveFile(childs[i], newDirectory);
        }
        return newDirectory;
    }
    if(oldFile.isFile()){
        FileInputStream fis = new FileInputStream(oldFile);
        try{
            File f = new File(newDirectory, URLEncoder.encode(oldFile.getName()));
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);

            int howMany;
            if(b == null){ b = new byte[8192];}
            while((howMany = fis.read(b)) >0){
                fos.write(b,0,howMany);
            }
            fos.close();
            fis.close();
        }catch(Exception e){ Log.i("file", URLEncoder.encode(oldFile.getName())); throw e; }
    }
    else{
        newDirectory = new File(newDirectory, oldFile.getName());
        newDirectory.mkdir();
        File[] childs = oldFile.listFiles();
        for(int i = 0; i<childs.length; i++){
            MoveFile(childs[i], newDirectory);
        }
    }
    return newDirectory;
}

public static void deleteFolder(File f){
    
    if(f.isDirectory()){
        File[] files = f.listFiles();
        for(int i = 0; i<files.length; i++){
            deleteFolder(files[i]);
        }
    }
        f.delete();
}    
	/**
	 * attempts to gzip all the files in this directory, if the output gzip is bigger than the original(as in jpeg files) then the gzip attempt is abandoned for that specific file
	 * @param publicFilesDirectory
	 */
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
