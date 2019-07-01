package basicServer.custom.multiDomainRequestsHandler;

import java.util.ArrayList;

import android.util.Log;
import basicServer.ProcessRequest;

public class Domain{
	private ArrayList<String> domains = new ArrayList<String>();
	private ProcessRequest pr;
	public Domain(ProcessRequest requestHandler, String... Hosts) {
		for(String host: Hosts) { domains.add(host); }
		pr = requestHandler;
	}
	public boolean canHandle(String host) {
		host = host;
		String log = "";
		for(String domain: domains) {
			if(domain.equals(host)) {
				return true;
			}
			log+=", \n'"+domain +"'='"+host+"'=="+domain.equals(host);
		}
		Log.i("cannot handle"+host, domains.toString());
		Log.i("equality:", log);
		return false;
	}
	public ProcessRequest getProcessRequest() { return pr; }
	public String getDefaultDomain() { return domains.get(0); }
	public String toString() { return "{"+domains+"}"; }
	
}
