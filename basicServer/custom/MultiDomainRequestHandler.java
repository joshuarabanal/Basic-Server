package basicServer.custom;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import basicServer.HttpHelpers;
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
		Log.i("invalid domain",Host);
		//HttpHelpers.httpBadRequest(r);
		return-1;
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
	public void saveState() {
		// TODO Auto-generated method stub
		for(int i  = 0; i<requestHandlers.size(); i++) {
			requestHandlers.get(i).getProcessRequest().saveState();
		}
	}
/**
	@Override
	public void openCache(File file) {
		file = new File(file, "cache");
				for(int i  = 0; i<requestHandlers.size(); i++) {
<<<<<<< HEAD
					File f = new File(file, requestHandlers.get(i).getDefaultDomain());
=======
					File f = new File(file,"cache/"+ requestHandlers.get(i).getDefaultDomain()+"/"+file.getName());
>>>>>>> bfcfbd314c1b8957de229564182d54881811d0dc
					f.mkdirs();
					requestHandlers.get(i).getProcessRequest().openCache( f );
				}
	}

**/
	@Override
	public void setRoot(File root) {
		// TODO Auto-generated method stub
		root = new File(root, "Domains");
		
		for(int i  = 0; i<requestHandlers.size(); i++) {
			File f = new File(root, requestHandlers.get(i).getDefaultDomain());
			f.mkdirs();
			requestHandlers.get(i).getProcessRequest().setRoot( f );
		}
		
		
	}

}

