package basicServer.custom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import basicServer.ProcessRequest;

public class LoginKeys {
	//public static final String loginKeyFile = "/loginKeyStore.txt";
	public static final String keyFileItemSeparator =  "=";
	
	
	ArrayList<NameValuePair> keys;
	File referenceFile;
	
	public LoginKeys(String currentDirectory){
		referenceFile = new File(currentDirectory,ProcessRequest.privateFileFolder);
		keys = new ArrayList<NameValuePair>();
		
		if(!createKeys( new File(currentDirectory, ProcessRequest.privateFileFolder) )){
			System.out.println("could not generate login FIles");
		}
		
	}
	private boolean createKeys(File PrivateDirectory){
		if(!PrivateDirectory.exists()){ return false; }
		File[] children = PrivateDirectory.listFiles();
		
		File[] accounts;
		String cookie;
		for(File child : children){
			if(child.isDirectory()){
				cookie = child.getName();
				child = new File(child, AccountsManager.customersFolder);
				if(!child.exists()){ continue; }
				accounts = child.listFiles();
				for(File account: accounts){
					if(account.isDirectory()){
						//cookie+="-"+account.getName();
						addNewKey(account, cookie+"-"+account.getName());
					}
				}
				//createKeys(child, cookie+"-"+child.getName());
			}
			
		}
		return true;
	}
	private void addNewKey(File file, String cookie){
		file = new File(file, AccountsManager.loginInfoFileName);
		try {
			BufferedReader br = new BufferedReader( new FileReader(file) );
			keys.add( new NameValuePair( br.readLine().trim(), br.readLine().trim(), cookie) );
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private NameValuePair getAccount(String uId){
		
		for(NameValuePair key : keys){
			if(key.uId.equals(uId)){
				System.out.println("found key:"+key.uId);
				return key;
			}
		}
		return null;
	}
	/**
	 * 
	 * @param userId
	 * @param Password
	 * @return the contractor id or null for invalid login
	 */
	public String getContractorId(String userId, String Password){
		NameValuePair key = getAccount(userId);
		if(key == null ){ return null; }
		if(key.password.equals(Password)){
			return key.cookie;
		}
		return null;
		
	}
	private void addKeyToList(String... data){
		keys.add( 0, new NameValuePair(data)  );
	}
	public boolean addNewLoginKeys(String uId, String password, String cookie){
			for(NameValuePair nvp : keys){
				if(nvp.uId.equals(uId)){
					return false;
				}
			}
		    addKeyToList(uId, password, cookie);
		    return true;
		
	}
	
	
	private class NameValuePair{ 
		NameValuePair(String... stuff){
			uId = stuff[0];
			password = stuff[1];
			cookie = stuff[2];
			System.out.println("new index:"+uId+"/"+password+"/"+cookie);
		}
		String cookie;
		String uId;
		String password;
	}
	
}
