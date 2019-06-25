package basicServer;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import basicServer.Request;



public class RequestsHandler /*implements Runnable*/{ 
	//private ArrayList<Request> requests; 
        //private Thread processingThread; 
        ProcessRequest processor; 
        
        
       private class HandleSingleRequest implements Runnable{
    	   private Request r;
    	   public HandleSingleRequest(Request r) {
    		  this.r = r;
    	   }
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				processor.processRequest(r);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       }

	public RequestsHandler(ProcessRequest pr){
            setRequestProcessor(pr);
	}
        
        public void setRequestProcessor(ProcessRequest pr){
            processor = pr;
        }

        
	public void addRequest(Socket sock){
            new Thread(
            		new HandleSingleRequest(
            				new Request(sock)
            			)
            		)
            .start();
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
