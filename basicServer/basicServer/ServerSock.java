package basicServer;


import android.util.Log;
import basicServer.ProcessRequest;
import basicServer.RequestsHandler;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;

import basicServer.serverSock.Http;
import basicServer.serverSock.Https;
import javax.swing.JFileChooser;



public class ServerSock extends WindowAdapter implements ActionListener{
        private JFileChooser fileChoose = new JFileChooser();
        private JFrame frame;
    
    
	public static final int portNum = 4244;
	private static final String startButton = "Start";
	private static final String endButton = "Pause";
	private static final String reStartButton = "Restart";
        private static final String folderSelectButton = "folder";
        private static final String compileButton = "compile root";
	
	private RequestsHandler requests;
        
        private String ssltrustStore;
        private String sslPassword;
	private Http http;
	private Https https;

	/**
	 * 
	 * @param pr
	 * @param rootDirectory root directory of server
	 */
	public  ServerSock( ProcessRequest pr, File rootDirectory) {
		requests = new RequestsHandler(pr);
		
	}
        public void setSSL( String sslTrustStoreDir, String SSLPassword){
		this.ssltrustStore = sslTrustStoreDir;
                this.sslPassword = SSLPassword;
            
        }
        
	public void startServer(){ createGUI();}
	private void startButton() throws IOException{
            Log.i("start button","called");
            
		if(http == null){
			http = new Http(requests);
                        Log.i("initialized http", "initialized http");
		}
		http.start();
		if(https == null && ssltrustStore != null){
                Log.i("https initialize","called");
			https = new Https(requests, ssltrustStore, sslPassword);
		}
		if(https!=null){
                    Log.i("https start","called");
                    https.start();
                }
                
                
		
	}
	private void compileButton(){
		
            fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChoose.setCurrentDirectory(new java.io.File("."));
                fileChoose.setDialogTitle("select root directory");
                int returnVal = fileChoose.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    new Thread( new Runnable(){
				    public void run(){
				  	requests.preProcess(fileChoose.getSelectedFile());
				    }
			    }).run();
                    } 
                    else{
                        Log.i("no file choosen", "choose a root directory to continue");
                        return;
                    }
	}
	private void stopButton() throws IOException{
		System.out.println("stop button");
		if(http != null){
			http.stop();
			http = null;
		}
		if(https != null){
			https.stop();
			https = null;
		}
	}
	private void createGUI(){
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
             
                
		JButton button;
                
		
		button = new JButton(folderSelectButton);
		button.addActionListener(this);
		//frame.getContentPane().add(BorderLayout.NORTH, button);
                
		button = new JButton(startButton);
		button.addActionListener(this);
		frame.getContentPane().add(BorderLayout.EAST, button);
		
		button = new JButton(endButton);
		button.addActionListener(this);
		frame.getContentPane().add(BorderLayout.WEST, button);
		
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		System.out.println(event.getActionCommand());
		System.out.println(event.toString());
		System.out.println();
		try{
		switch(event.getActionCommand()){
		case startButton:
			startButton();
			break;
		case endButton:
			stopButton();
			break;
		default:
			System.out.print("unknown command:"+event.getActionCommand());
		}
		}
		catch(Exception e){
			e.printStackTrace();
                        Log.i("extecption thorw button click", e.getLocalizedMessage());
		}
		
	}
	

	 public void windowClosing(WindowEvent e){	
		 	try {
				stopButton();
                                
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
                        System.exit(0);
		}
	       
	    

}
