package basicServer;

import java.io.File;


public interface ProcessRequest {
	public static final String privateFileFolder = "/PrivateFilesDirectory";
	public static final String publicFileFolder = "/PublicFilesDirectory";

	public int processRequest(Request r) throws Exception;

    public void preProcess(File folder);
}
