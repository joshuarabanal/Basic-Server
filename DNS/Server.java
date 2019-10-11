package DNS;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import DNS.server.TCPServer;
import DNS.server.UDPServer;
/**
 * this class was made using: http://www-inf.int-evry.fr/~hennequi/CoursDNS/NOTES-COURS_eng/msg.html
 * <br/>
 * http://www.keyboardbanger.com/dns-message-format-name-compression/
 * <br/>
 * http://tcpipguide.com/free/t_DNSMessageHeaderandQuestionSectionFormat.htm
 * @author Joshu
 *
 */
public class Server {
	private TCPServer tcp;
	private UDPServer udp;
	
	public Server() throws IOException {
		tcp = new TCPServer(4246);
		udp = new UDPServer(4247);
	}
	
	public void start() {
		tcp.start();
		udp.start();
	}
	public void stop() {
		tcp.stop();
		udp.stop();
	}
}
