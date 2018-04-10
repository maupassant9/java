package electronicLab.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import javafx.scene.control.Alert;
import purejavacomm.*;

public class Serial {
	private CommPortIdentifier com = null;
    private SerialPort serialCom = null;
    public StringBuffer buf;
    
	
	//Listener class for serial
    class MyPortListener implements SerialPortEventListener
    {  
    	@Override
    	public void serialEvent(SerialPortEvent evt)   
    	{  
       		switch (evt.getEventType()) {  
    			case SerialPortEvent.CTS : 
    				System.out.println("CTS event occured."); break;  
    			case SerialPortEvent.CD :  
    				System.out.println("CD event occured.");break;  
    			case SerialPortEvent.BI :  
    				System.out.println("BI event occured.");break;  
    			case SerialPortEvent.DSR :
    				System.out.println("DSR event occured.");break;  
			    case SerialPortEvent.FE :  
			    	System.out.println("FE event occured.");break;  
			    case SerialPortEvent.OE :  
			    	System.out.println("OE event occured.");break;  
			    case SerialPortEvent.PE :  
			    	System.out.println("PE event occured.");break;  
			    case SerialPortEvent.RI :break;  
			    case SerialPortEvent.OUTPUT_BUFFER_EMPTY :  
			    	System.out.println("OUTPUT_BUFFER_EMPTY event occured.");break;  
      			case SerialPortEvent.DATA_AVAILABLE :  
      				System.out.println("DATA_AVAILABLE event occured.");  
      				int ch;  
      				buf = new StringBuffer();  
      				InputStream input;
      				try {
      					input = serialCom.getInputStream();
      					buf = new StringBuffer(); 
  							for(int i = 0; i < 2; i++){ 
  								if( (ch=input.read()) > 0) buf.append((char)ch);
  							}
  							//System.out.printf("....->%x",buf);
      				} catch (IOException e1) {
      					// TODO Auto-generated catch block
      					serialCom.close();
					
      					Alert alert = new Alert(Alert.AlertType.ERROR);
      					alert.setTitle("Device connection error");
      					alert.setHeaderText("Please check the state of the device");
      					alert.setContentText("Connect to device fails!");
      					alert.showAndWait();
      					e1.printStackTrace();
				}
      			break;  
       		}
      
    	}
    }
    	
    	
	public Serial(String serialName) throws NoSuchPortException, PortInUseException, 
	IOException, UnsupportedCommOperationException
	{
		com = CommPortIdentifier.getPortIdentifier(serialName);
		serialCom = (SerialPort) com.open("ComListener", 3000000);
		//2.3.设置两个端口的参数
        serialCom.setSerialPortParams(
        			3000000, //波特率
                    SerialPort.DATABITS_8,//数据位数
                    SerialPort.STOPBITS_1,//停止位
                    SerialPort.PARITY_NONE//奇偶位
            );
	}
	
	public void startReadListener(){
		try {
			serialCom.addEventListener(new MyPortListener());
			serialCom.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			serialCom.close();
			//e.printStackTrace();
		}
	}
	
	public void removeListener(){
			serialCom.removeEventListener();
	}
	
	public byte read(long delay) throws IOException{
		InputStream inputStream = serialCom.getInputStream();
       
        byte[] cache = new byte[1024];
        int availableBytes = 0;

        while(delay != 0){
            availableBytes = inputStream.available();
            if(availableBytes > 0)
            {
            	int ack = inputStream.read();
            	System.out.println("ACK-->"+ack);
            	inputStream.close();
            	return (byte)ack;
            }
            delay--;
        }
        inputStream.close();
        return 0;      
	}
	
	public void write(byte[] cmd) throws IOException{
		OutputStream wr = serialCom.getOutputStream();
		wr.write(cmd);
		wr.close();
	}
	
	public void write(byte cmd) throws IOException{
		OutputStream wr = serialCom.getOutputStream();
		wr.write(cmd);
		wr.close();
	}
	
	
	
	public void close()
	{
		if(serialCom != null) serialCom.close();
	}
}
