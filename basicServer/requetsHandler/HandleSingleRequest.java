/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package requetsHandler;

import basicServer.ProcessRequest;
import basicServer.Request;

/**
 *
 * @author Joshua
 */
public class HandleSingleRequest implements Runnable{
private Request request;
private ProcessRequest processRequest;
    public HandleSingleRequest(Request r, ProcessRequest pr) {
        request = r;
        processRequest = pr;
    }

    @Override
    public void run() {
        try{
			processRequest.processRequest(request);
		}
		catch(Exception e){
			e.printStackTrace();
			request.close();
		} 
		//request.close();
    }
    
    
}