package DNS.server;

import java.util.ArrayList;

import android.util.Log;

public class MessageParser {
	int identification = 0;
	int control;
	RR[] questions;
	RR[] responses;
	RR[] authorities;
	RR[] additionals;

	
	public MessageParser(byte[] all) {
		readHeader(all);
		int offset = 12;
		
		//questions
		ArrayList<RR> temp = new ArrayList<RR>();
		for(int i = 0;i<questions.length; i++) {
			offset=RR.incompleteRR(all, offset, temp);
			questions[i] = temp.remove(0);
		}
		
		//responses
		for(int i = 0;i<responses.length; i++) {
			offset=RR.completeRR(all, offset, temp);
			responses[i] = temp.remove(0);
		}
		
		//authorities
		for(int i = 0;i<authorities.length; i++) {
			offset=RR.completeRR(all, offset, temp);
			authorities[i] = temp.remove(0);
		}
		
		//additionals
		for(int i = 0;i<additionals.length; i++) {
			offset=RR.completeRR(all, offset, temp);
			additionals[i] = temp.remove(0);
		}
	}
	
	
	
	/**
	 * byte of length[12 bytes]
	 * @param header
	 */
	public void readHeader(byte[] header) {
		
		//identification field
		
		identification= ((header[0] & 0xff) <<8)+ (header[1]&0xff);//2 bytes
		control = ((header[2]&0xff)<<8) + (header[3]& 0xff);//2 bytes
		
		int questionCount =  ((header[4]<<8)&0xff) + (header[5]&0xff);//2 bytes
			questions = new RR[questionCount];
			
		int answerCount =  ((header[6]<<8)&0xff) + (header[7]&0xff);//2 bytes
			responses = new RR[answerCount];
			
		int authorityCount =  ((header[8]<<8)&0xff) + (header[9]&0xff);//2 bytes
			authorities = new RR[authorityCount];
			
		int additionalCount =  ((header[10]<<8)&0xff) + (header[11]&0xff);//2 bytes
			additionals = new RR[additionalCount];	
	} 

	public String toString() {
		StringBuilder retu =  new StringBuilder(
				"ID:"+identification+
				", QR:"+isQuestion()+
				", OPCode:"+getOpCode()+
				", AA:"+isAuthoritativeAnswer()+
				", TC:"+isTruncated()+
				", RD:"+isRecursionDesired()+
				", RA:"+isRecursionAvailable()+
				", Z:"+isZero()+
				", Rcode:"+getRCode()
			);
		retu.append(",\n questions:[");
		for(RR r : questions) { retu.append(", "+r.toString()+"\n"); }
		
		retu.append("],\n responses:[");
		for(RR r : responses) { retu.append(", "+r.toString()+"\n"); }
		
		retu.append("],\n authorities:[");
		for(RR r : authorities) { retu.append(", "+r.toString()+"\n"); }
		
		retu.append("],\n additionals:[");
		for(RR r : additionals) { retu.append(", "+r.toString()+"\n"); }
		retu.append("]");
		
		return retu.toString();
				
	}
	public byte[] toByteArray() {
		ArrayList<Integer> b = new ArrayList<Integer>();
		
		//identification
			b.add(	(identification>>8) & 0xff   ); 
			b.add( (identification&0xff)  );
		//control
				b.add(	(control>>8) & 0xff   ); 
				b.add( (control&0xff)  );
		//question count
				b.add((questions.length>>8)&0xff);
				b.add(questions.length&0xff);
		//responses count
				b.add((responses.length>>8)&0xff);
				b.add(responses.length&0xff);
		//authorities count
				b.add((authorities.length>>8)&0xff);
				b.add(authorities.length&0xff);
		//additionals count
				b.add((additionals.length>>8)&0xff);
				b.add(additionals.length&0xff);
				
		//starting at byte 12


				for(RR q : questions) { q.append(b); }
				for(RR q : responses) { q.append(b); }
				for(RR q : authorities) { q.append(b); }
				for(RR q : additionals) { q.append(b); }

				
				
		byte[] retu = new  byte[b.size()];
		for(int i = 0; i<retu.length; i++) { retu[i] = (byte) (b.get(i) & 0xff); }
		return retu;
		
	}
	/** QR 1 bit, request (0) or response (1)**/
	public boolean isQuestion() {  return (control>>15) == 1; }
	/**
	 * 0 = query
	 * 1 = iquery
	 * 2 = status
	 * 3 = reserved
	 * 4 = notify
	 * 5 = update
	 * @return
	 */
	public int getOpCode() { return (control & 0x7800)>>11; }
	/** AA Authoritative Answer : 1 bit, reply from authoritative (1) or from cache (0) **/
	public boolean isAuthoritativeAnswer() {  return (control & 0x0400) != 0; }
	/** TC Truncated : 1 bit, response too large for UDP (1). **/
	public boolean isTruncated() {  return (control & 0x200) != 0; }
	/** RD Recursion Desired: 1bit, ask for recursive (1) or iterative (0) response **/
	public boolean isRecursionDesired() {  return (control & 0x100) != 0; }
	/** RA Recursion Available : 1bit, server manages recursive (1) or not (0) **/
	public boolean isRecursionAvailable() {  return (control & 0x80) != 0; }
	/** 3 bit Zeros, reserved for extensions **/
	public int isZero() {  return (control & 0x70); }
	/**
	 * response code:
	 * 0 = no error
	 * 1 = format error
	 * 2 = server failure
	 * 3 = name error
	 * 4 = not implented
	 * 5 = refused
	 * 6 = yx domain
	 * 7 = yx RR set 
	 * 8 = nx RR set
	 * 9 not auth
	 * 10 not zone
	 * @return
	 */
	public int getRCode() { return control & 0x0F; }
	


	
	
	
	//_________________________________________________________________
	//helper classes

	public static class RR{
		String name = ""; //variable length quantity
		int type;//16 bit = 2byte
		int clas;//2 byte
		int timeToLive = -1;//4 byte
		//int resourceDataLength;//2 byte
		byte[] resourceData;
		
		public String toString() {
			String retu =  "{\nname:"+name+",\n type:"+type+",class:"+clas+", timeToLive:"+timeToLive;
			if(resourceData!= null) {
				retu += ", resourceData_length:"+resourceData.length;
				retu+=",\n data:"+new String(resourceData);
				
				retu+=", \n data_array:[";
				for(int i = 0; i<resourceData.length; i++) { 
					if(i>0) { retu+=","; }
					retu += (resourceData[i]&0xff);
				}
				retu+="]";
				
			}
			
			return retu+"\n}";
		}
		static int completeRR(byte[] data, int offset, ArrayList<RR> returnArray) {
			RR retu = new RR();
			int oldOffset = offset;
			offset = readName(data, offset, retu); 
			
			retu.type = ( (data[offset]&0xff) <<8) + (data[offset+1]);
			offset+=2;
			
			retu.clas = ((data[offset]&0xff)<<8) + (data[offset+1]);
			offset+=2;
			
			retu.timeToLive =
					(data[offset]<<24) + 
					(data[offset+1]<<16)+
					(data[offset+2]<<8)+
					(data[offset+3]);
			offset+=4;
			
			int resourceDataLength = ((data[offset]& 0xff)<<8) + (data[offset+1]&0xff);
			offset+=2;
			retu.resourceData = new byte[resourceDataLength];
			for(int i = 0; i<retu.resourceData.length; i++) {
				retu.resourceData[i]  = data[offset+i];
			}
			
			returnArray.add(retu);
			return offset+retu.resourceData.length;
		}
		static int incompleteRR(byte[] data, int offset, ArrayList<RR> returnArray) {
			RR retu = new RR();
			offset = readName(data, offset, retu);
			
			retu.type = (data[offset]<<8) + (data[offset+1]);
			offset+=2;
			
			retu.clas = (data[offset]<<8) + (data[offset+1]);
			offset+=2;

			returnArray.add(retu);
			return offset;
		}
		public int getBufferLength() {
			if(resourceData == null) {
				return name.length()+2+2+2;
			}
			else return name.length()+2+2+2+4+2+resourceData.length;
		}
		
		private static int readName(byte[] b, int offset, RR retu) {
			int length = b[offset]; offset++;
			while(length!=0) {
				if( (length & 0xc0) == 0xc0) {//referenced section
					
					length = (length& 0x3f )>>8;
					length += b[offset] & 0xff;  
					offset++;
					
					readName(b, length,retu);
					return offset;
				}
				else {	
					if(retu.name.length()>0) { retu.name+="."; }
					retu.name+=new String(b,offset, length); offset+=length;
				}
				
				length = b[offset]; 
				offset++;
			}
			if(retu.name.length() == 0) {
				throw new IndexOutOfBoundsException("name should at least have length of 2");
			}
			return offset;
		}
		public void append(ArrayList<Integer> bytearray) {
			
			//name section
			String[] names = name.split("\\.");
			for(String section: names) {
				bytearray.add(section.length());
				for(int i = 0; i<section.length(); i++) { bytearray.add((int) section.charAt(i)); }
			}
			bytearray.add(0);
			
			//type
			bytearray.add( type>>8); bytearray.add(type & 0xff);
			
			//class
			bytearray.add(type>>8); bytearray.add(type&0xff);
			
			if(resourceData != null) {
				//timeToLive
				bytearray.add(timeToLive>>8); bytearray.add(timeToLive&0xff);
				
				//resource lkength
				bytearray.add(resourceData.length>>8); bytearray.add(resourceData.length&0xff);
				
				for(byte b : resourceData) { bytearray.add((int) b); }
			}
			
			
			
		}
	}
}
