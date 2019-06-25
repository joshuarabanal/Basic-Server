package basicServer.requestsHandler;

import basicServer.ProcessRequest;
import basicServer.Request;

public class HandleSingleRequest implements Runnable{
	   private Request r;
	   private ProcessRequest processor;
	   public HandleSingleRequest(Request r, ProcessRequest rp) {
		  this.r = r;
		  this.processor = rp;
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