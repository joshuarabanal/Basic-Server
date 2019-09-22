package basicServer.custom;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import basicServer.HttpHelpers;
import basicServer.ProcessRequest;
import basicServer.Request;


public class MyProcessor implements ProcessRequest {
	private static final String dataSectionSplit = ";";
	private static final String dataNameValueSplit = "=";
	private String PublicFilesDirectory;
	private String PrivateFilesDirectory;
	private AccountsManager accountsManager;
	
	MyProcessor(NewOrderHandler noh, String path){
		PublicFilesDirectory = path+publicFileFolder;
		PrivateFilesDirectory = path+privateFileFolder;
		accountsManager = new AccountsManager(path, noh);
	}
	
	public int processRequest(Request r) throws Exception {
		// TODO Auto-generated method stub
		
			respond(r);
		return 0;
	}

	
	private void respond(Request sock) throws Exception{
		
		switch(sock.getMethod()){
		case Request.METHOD_GET:
			openFile(sock);
			break;
		case Request.METHOD_POST : 
			PostRequest(sock);
			break;
		case -1:
			System.out.println("blank headers error");
			HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "unknown method");
			
			break;
			default : 
				HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "unknown method");
				break;
		}
		
		
		
		
		
		System.out.println("finished message");
		
	}
	private void PostRequest(Request sock)throws Exception{
		System.out.println("post reqest started");
		String data = sock.getData();
		System.out.println("data:"+data);
		String[] lines = data.split(dataSectionSplit);
		
		if(lines[0].equals("Login")){//Login;uname=mama;pswd=mame
			login(sock,lines);
		}
		else if(lines[0].equals("newOrder")){//newOrder;data=blah
			newOrder(sock,lines);
		}
		else if(lines[0].equals("newAccount")){//newAccount;email=exampl@example.com;contid=sample;numOemp=16;pswd =password
			newAccount(sock, lines);
		}
		else if(lines[0].equals("scheduleAvailibility")){
			String availability = accountsManager.getScheduleAvailability("B-b");//default contractor set for single contractor instance
			
			HttpHelpers.httpGetResponse(sock, HttpHelpers.MimeTxt, availability);
		}
		else if(lines[0].equals("NewContractor")){//makes new folder for company to hold subcontractors
			accountsManager.createContractor(sock);
		}
		else{
			HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "could not find response");
		}
		
		
		
		
	}
	private void newOrder(Request sock , String[] lines)throws Exception{
		
		String[] line;
		for(int i = 0; i<lines.length; i++){
			line = lines[i].split(dataNameValueSplit);
			if(line[0].equals("data")){
				String cookie = sock.getHeaderByName(Request.headerCookie);
				if(cookie == null){ HttpHelpers.httpLoginFailed(sock);  return; }
				accountsManager.newOrder(
						sock, 
						line[1],
						cookie.split(dataNameValueSplit)[1]);
				break;
			}
		}
	}
	private void newAccount(Request sock , String[] lines)throws Exception{
		String email = null;
		String password = null;
		String contractorId = null;
		String address = null;
		String businessId = null;
		String[] line;
		for(int i = 0; i<lines.length; i++){
			line = lines[i].split(dataNameValueSplit);
			if(line[0].equals("email")){ email = line[1]; }
			else if(line[0].equals("pswd")){ password = line[1]; }
			else if(line[0].equals("contid")){  contractorId = line[1]; }
			else if(line[0].equals("businessId")){ businessId = line[1]; }
			else if(line[0].equals("address")){ address = line[1]; }
		}
		if( email != null && password != null ){
			
			accountsManager.createNewAccount(sock,email,password, address, contractorId, businessId);
		}
		else{
			System.out.println("could not find headers");
			HttpHelpers.httpLoginFailed(sock);
		}
		
	}
	private void login(Request sock , String[] lines)throws Exception{
		String uname = null;
		String password = null;
		String[] line;
		for(int i = 0; i<lines.length; i++){
			line = lines[i].split(dataNameValueSplit);
			if(line[0].equals("uname")){ uname = line[1]; }
			else if(line[0].equals("pswd")){  password = line[1]; }
		}
		if(uname != null && password != null){
			accountsManager.login(sock, uname, password);
		}
		else{
			HttpHelpers.httpLoginFailed(sock);
		}
	}
	
	
	private void openFile(Request sock)throws Exception{
		File f = new File(PublicFilesDirectory+sock.getURL());
		if (f.exists()){ HttpHelpers.httpGetResponse(sock, f); }
		else{	HttpHelpers.fileNotFound(sock);
				System.out.println("file not found on server");
		}
	}

    @Override
    public void preProcess(File root) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public void saveState() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setRoot(File root) {
		// TODO Auto-generated method stub
		
	}
	



 


}
