package basicServer.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import basicServer.HttpHelpers;
import basicServer.ProcessRequest;
import basicServer.Request;

public class AccountsManager {
	public static final String LastIdFile = "/lastId.txt";
	public static final String cookieSeparator = "-";
	public static final String employeesFolder = "/employees";
	public static final String customersFolder = "/customers";
	public static final String orderHistoryFolder = "/orderHistory";
	public static final String loginInfoFileName = "loginInfo.txt";
	
	private static byte[] defaultLastMadeId = new byte[]{65};
	private String privateDir;
	private byte[] lastMadeId;
	private LoginKeys keys;
	private NewOrderHandler newOrderHandler;
	
	//instantators
	public AccountsManager(String currentDirectory, NewOrderHandler noh){
		privateDir = currentDirectory+ProcessRequest.privateFileFolder;
		try{
			lastMadeId = openLastMadeId(privateDir+LastIdFile);
		}
		catch(Exception e){
			
			lastMadeId = defaultLastMadeId;
		}
		keys = new LoginKeys(currentDirectory);
		newOrderHandler = noh;
	}
	
	
	//static functions
	public static File getCustomerFolder(String privateDir, String cookie){
		return new File(privateDir, cookie.replaceAll(cookieSeparator, customersFolder+"/"));
	}
	public static File getEmployeeFolder(String privateDir, String cookie){
		return new File(privateDir, cookie.replaceAll(cookieSeparator, employeesFolder+"/"));
	}
	/**
	 * 
	 * @param AppointmentFile : file given by scheduler.addAppointment
	 * @return
	 */
	public static String getEmployeeEmail(File AppointmentFile){
		AppointmentFile = new File(AppointmentFile.getParentFile(), loginInfoFileName);
		return getLineFromloginInfoFile(AppointmentFile, 1);
	}
	public static String getCustomerAddress(String privateDir, String cookie){
		File f = new File(getCustomerFolder(privateDir,cookie),loginInfoFileName);
		return getLineFromloginInfoFile(f,4);
	}
	public static String getCustomerEmail(String privateDir, String cookie){
		File f = new File(getCustomerFolder(privateDir,cookie),loginInfoFileName);
		return getLineFromloginInfoFile(f,1);
	}
	private static String getLineFromloginInfoFile(File f, int lineNumber){
		String retu = "";
		if(f != null && f.exists()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				for(int i = 0; i<lineNumber; i++){ retu = br.readLine(); }
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retu;
	}
	
	
	
	public String getScheduleAvailability(String cookie){
		return newOrderHandler.getScheduleAvailability(cookie);
	}
	public void createNewAccount(Request sock ,String email,String password, String address, String contractorId, String businessId)throws Exception{
		if(businessId == null){//customer account
			createCustomerToContractor(sock, email, password,address, contractorId);
		}
		else{//contractor account
			createSubContractor(sock, email, password, businessId);
		}
		//login(sock,email, password);
	}
	private void createSubContractor(Request sock, String email, String password, String businessId)throws Exception{
		String rawFold = privateDir+"/"+businessId+employeesFolder;
		byte[] id = openLastMadeId(rawFold);
		id= incrementLastMadeId(id);
		File newEmployeeFolder = new File(rawFold, new String(id) );
		makeLoginInfoFile(newEmployeeFolder, email, password, businessId);
		saveLastMadeId(id, rawFold);
		HttpHelpers.httpSuccessFromPostRequest(sock);
		return;
	}
	public void createContractor(Request sock)throws Exception{
		byte[] id = openLastMadeId(privateDir);
		id = incrementLastMadeId(id);
		String cookie = new String(id);
		File contractorFolder = new File(privateDir,cookie);
		contractorFolder.mkdirs();
		new File(contractorFolder,customersFolder).mkdirs();
		new File(contractorFolder,employeesFolder).mkdirs();
		HttpHelpers.httpPostResponse(sock, HttpHelpers.MimeTxt, cookie, "cookie", cookie);
		saveLastMadeId(id, privateDir);
	}

	private void createCustomerToContractor(Request sock, String email, String password, String address, String contractorId)throws Exception{
		System.out.println("new customer :"+contractorId+" email:"+email+" password:"+password);
		File f = new File(privateDir+"/"+contractorId+"/customers");
		if(f.exists()){//create new customer
			
			byte[] id = incrementLastMadeId( openLastMadeId(privateDir+"/"+contractorId) );
			
			
			if( !keys.addNewLoginKeys(email, password, contractorId+cookieSeparator+new String(id)) ){
				HttpHelpers.httpAppointmentFailed(sock);
				return;
			}
			else{
				HttpHelpers.httpPostResponse(sock, null, null, "cookie", contractorId+cookieSeparator+new String(id));
				saveLastMadeId(id, privateDir+"/"+contractorId);
			}
			f = new File(f, new String(id));
			f.mkdirs();
			makeLoginInfoFile(f, email, password, contractorId, address);
			
			
			File baby = new File(f, orderHistoryFolder); baby.mkdirs();//order history folder
			
		}
		else{//invalid contractor id
			HttpHelpers.httpLoginFailed(sock);
		}
	}
	public void login(Request sock, String uName, String Password)throws Exception{
		String cookie = keys.getContractorId(uName,Password);
		if(cookie == null){
			HttpHelpers.httpLoginFailed(sock);
		}
		else{
			HttpHelpers.httpPostResponse(sock, null, null, "cookie", cookie);
		}
		
	}
	private void makeLoginInfoFile(File f, String... info){
		if(!f.exists()){ f.mkdirs(); }		
		try {
			f = new File(f,loginInfoFileName);
			PrintWriter fos = new PrintWriter(f);
			for(String i :info ){
				fos.println(i);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void newOrder(Request sock, String data, String cookie)throws Exception{
		
		System.out.println("Coolie va:"+cookie);
		File f = new File(privateDir, cookie.replaceAll(cookieSeparator, "/customers/")+"/orderHistory");
		System.out.println(f.toString()+":"+f.exists()+" file name:"+System.currentTimeMillis());
		if(f.exists()){
			f = new File(f, System.currentTimeMillis()+".txt");
			
				PrintWriter fos = new PrintWriter(f);
				fos.print(data);
				fos.close();
				
				if(newOrderHandler.newOrder(cookie, data)){
					HttpHelpers.httpSuccessFromPostRequest(sock);
				}
				else{
					HttpHelpers.httpAppointmentFailed(sock);
				}
				
			
			
		}
		else{
			HttpHelpers.httpLoginFailed(sock);
		}
	}
	
	
	//cookie id handlers
	private byte[] incrementLastMadeId(byte[] id){ //number ordinated in little endian
		byte lastByte;
		System.out.println("last made id:"+new String(id));
		for(int i = 0; i>=0; i++){
			lastByte = (byte) (id[i]+1);
			if(lastByte<91){//last byte to be edited
				id[i] = lastByte;
				
				break;
			}
			else{
				lastByte = 65;
				id[i] = lastByte;
				if(i+1>=id.length){
					
					byte[] b = new byte[i+2];
					for(int index = 0; index<=id.length; index++){
						b[i] = id[i];
					}
					b[i+1] = 65;
					id = b;
				}
			}
		}
		
		return id;
	}
	private byte[] openLastMadeId(String rootFolder) throws Exception{
		File f = new File(rootFolder,LastIdFile);
		if(f.exists()){
				FileInputStream fis  = new FileInputStream(f);
				ArrayList<Byte> bytes = new ArrayList<Byte>();
				byte b;
				while( (b = (byte) fis.read()) >=0){
					bytes.add(b);
				}
				byte[] retu = new byte[bytes.size()];
				for(int i = 0; i<bytes.size(); i++){ retu[i] = bytes.get(i);}
				bytes.clear();
				fis.close();
				return retu;
			
			
		}
		else{
			return defaultLastMadeId;
		}
	}
	private void saveLastMadeId(byte[] value, String rootFolder){
		File f = new File(rootFolder, LastIdFile);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			fos.write(value);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
