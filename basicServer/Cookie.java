package basicServer;

import java.util.ArrayList;

import xml.unoptimized.NameValuePair;

public class Cookie {
	public String
		name, value;
	private ArrayList<String> flags = new ArrayList<String>();
	
	public Cookie(String name, String value, NameValuePair... flags) {
		this.name = name;
		this.value = value;
	}
	
	public static Cookie fromSetCookieString(String header) {
		if(!header.contains(";")) {
			String[] nvp = header.split("=");
			return new Cookie(nvp[0], nvp[1]);
		}
		String[] nvp = header.substring(0, header.indexOf(";")).split("=");
		
		Cookie retu = new Cookie(nvp[0], nvp[1]);
		header = header.substring(header.indexOf(";")+2);
		while(header.contains(";")) {
			String head = header.substring(0,header.indexOf(";"));
			header = header.substring(header.indexOf(";")+2);
			retu.parseFlag(head);
		}
		retu.parseFlag(header);
		return retu;
	}
	
	public void parseFlag(String flag) {
		flags.add(flag);
	}
	public String toString() {
		return name+"="+value+"; "+flags;
	}

}
