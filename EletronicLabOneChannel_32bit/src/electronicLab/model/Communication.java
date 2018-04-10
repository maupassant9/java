package electronicLab.model;

import java.io.IOException;
import java.io.OutputStream;

import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;

public class Communication {

	private Serial com;
	public boolean isReady = false;
	
	public Communication(String str) throws NoSuchPortException,
		PortInUseException, IOException, UnsupportedCommOperationException {
		com = new Serial(str);	
		com.startReadListener();
	}
	
	public byte send(byte[] cmd) throws IOException{
		//send the command using serial port
		com.write(cmd);
		//wait for the acknowledgement from board
		long delay = 0x7fffffffl;
		while(delay != 0){
			if (com.buf != null && com.buf.length() > 0)
			{
				byte ack = (byte) com.buf.charAt(0);
				return ack;
			}
			delay--;
		}
		return 0;
	}
	
	
	public byte getResponse(){
		
		long delay = 0x7fffffffl;
		while(delay-- != 0)
		{
			//check if there is any response
			if (com.buf != null && com.buf.length() > 0)
			{
				byte ack = (byte) com.buf.charAt(0);
				return ack;
			}
		}
		return 0;		
	}
	
	
	public byte send(byte cmd) throws IOException{
		//send the command using serial port
		com.write(cmd);
		//wait for the acknowledgement from board
		//byte ack = com.read(10000);
		long delay = 0x7fffffffl;
		while(delay != 0){
			if (com.buf != null && com.buf.length() > 0)
			{
				byte ack = (byte) com.buf.charAt(0);
				return ack;
			}
			delay--;
		}
		return 0;
		
	}
	
	public void sendData(char[] data){
		
	}
	
	public void delay()
	{
		for(long j = 0; j < 10000000; j++);
	}
	
	public void close()
	{
		isReady = false;
		com.removeListener();
		com.close();
	}
}
