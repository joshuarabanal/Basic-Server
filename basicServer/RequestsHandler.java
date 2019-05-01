package basicServer;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import requetsHandler.HandleSingleRequest;



public class RequestsHandler /*implements Runnable*/{ 
	//private ArrayList<Request> requests; 
        //private Thread processingThread; 
        ProcessRequest processor; 

	public RequestsHandler(ProcessRequest pr){
            setRequestProcessor(pr);
	}
        
        public void setRequestProcessor(ProcessRequest pr){
            processor = pr;
        }

        
	public void addRequest(Socket sock){
            new Thread(new HandleSingleRequest(new Request(sock),processor)).start();
	}
        
        /** 
         * 
         * @param folder the directory to obtain the servers contents
         */
        public void preProcess(File folder){
            processor.preProcess(folder);
        }

	private void processRequest(Request r){
		try{
			processor.processRequest(r);
		}
		catch(Exception e){
			e.printStackTrace();
			r.close();
		}
		//r.close();
		
	}
     
}
