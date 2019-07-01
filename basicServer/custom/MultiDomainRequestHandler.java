package basicServer.custom;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import basicServer.ProcessRequest;
import basicServer.Request;
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
		for(int i = 0; i<requestHandlers.size(); i++) {
			if(requestHandlers.get(i).canHandle(Host)) {
				return requestHandlers.get(i).getProcessRequest().processRequest(r);
			}
		}
		String possible  = "";
		for(Domain d: requestHandlers) { possible+=","+d; }
		Log.e("available domains", possible);
		throw new IndexOutOfBoundsException("invalid domain:"+Host);
	}

	@Override
	public void preProcess(File folder) {
		// TODO Auto-generated method stub
		for(int i  = 0; i<requestHandlers.size(); i++) {
			requestHandlers.get(i).getProcessRequest().preProcess(new File(folder, requestHandlers.get(i).getDefaultDomain()));
		}
		
	}

	@Override
	public void saveState(File file) {
		// TODO Auto-generated method stub
		String fileName = "/"+file.getName();
		file = file.getParentFile();
		for(int i  = 0; i<requestHandlers.size(); i++) {
			requestHandlers.get(i).getProcessRequest().saveState(
					new File(file, requestHandlers.get(i).getDefaultDomain()+file.getName())
			);
		}
	}

	@Override
	public void openCache(File file) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String fileName = file.getName();
				file = file.getParentFile();
				for(int i  = 0; i<requestHandlers.size(); i++) {
					requestHandlers.get(i).getProcessRequest().openCache(new File(file, requestHandlers.get(i).getDefaultDomain()+"/"+file.getName()));
				}
	}

}

