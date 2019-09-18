package basicServer.custom;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import basicServer.ProcessRequest;
import basicServer.Request;
import basicServer.Preprocessor.GZipFIles;
import basicServer.custom.multiDomainRequestsHandler.Domain;

public class MultiDomainRequestHandler implements ProcessRequest {
	private ArrayList<Domain> requestHandlers = new ArrayList<Domain>();
	
	
	
	public void add(Domain domain) {
		requestHandlers.add(domain);
	}

	@Override
	public int processRequest(Request r) throws Exception {
		// TODO Auto-generated method stub
		String Host = r.getHeaderByName("Host");
		if(Host == null || Host.length() == 0) {
			Log.i("unable to find request domain", r.toString());
			throw new NullPointerException("null host in request");
		}
		for(int i = 0; i<requestHandlers.size(); i++) {
			if(requestHandlers.get(i).canHandle(Host)) {
				return requestHandlers.get(i).getProcessRequest().processRequest(r);
			}
		}
		String possible  = "";
		for(Domain d: requestHandlers) { possible+=","+d; }
		Log.e("available domains", possible);
		Log.i("looking for domain", Host);
		throw new IndexOutOfBoundsException("invalid domain:"+Host);
	}

	@Override
	public void preProcess(File folder) {
		// TODO Auto-generated method stub
		Log.i("preprocess", ""+folder);
		for(int i  = 0; i<requestHandlers.size(); i++) {
			File f = new File(folder, requestHandlers.get(i).getDefaultDomain());
			f.mkdirs();
			requestHandlers.get(i).getProcessRequest().preProcess(f);
		}
		
	}

	@Override
	public void saveState(File file) {
		// TODO Auto-generated method stub
		String fileName = "/"+file.getName();
		file = file.getParentFile();
		for(int i  = 0; i<requestHandlers.size(); i++) {
			File f = new File(file, requestHandlers.get(i).getDefaultDomain()+file.getName());
			f.mkdirs();
			requestHandlers.get(i).getProcessRequest().saveState(f);
		}
	}

	@Override
	public void openCache(File file) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String fileName = file.getName();
				file = file.getParentFile();
				for(int i  = 0; i<requestHandlers.size(); i++) {
					File f = new File(file,"cache/"+ requestHandlers.get(i).getDefaultDomain()+"/"+file.getName());
					f.mkdirs();
					requestHandlers.get(i).getProcessRequest().openCache( f );
				}
	}

	@Override
	public void setRoot(File root) {
		// TODO Auto-generated method stub
		Log.i("set root", ""+root);
		root = new File(root, "publicFilesDirectory");
		for(int i  = 0; i<requestHandlers.size(); i++) {
			File f = new File(root, requestHandlers.get(i).getDefaultDomain());
			f.mkdirs();
			requestHandlers.get(i).getProcessRequest().setRoot( f );
		}
	}

}

