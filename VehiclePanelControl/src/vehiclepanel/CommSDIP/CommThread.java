package vehiclepanel.CommSDIP;
//for communication service:

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

//1. Receive the data sent by SDIP
//2. Validate the data
//3. Send commands and calibration table

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import vehiclepanel.Controller;


public class CommThread implements Runnable {

    public SerialPort serialPort;
    private Object lockMain; // used for external control
    private Object lock; // used for internal lock
    private Controller ctr;

    //Two buffers for other threads send and receive datas
    public static volatile ConcurrentLinkedQueue<ArrayList<Byte>> bufferTx;
    public static volatile ConcurrentLinkedQueue<ArrayList<Byte>> bufferRx;

    //raw data from serial port
    private ConcurrentLinkedQueue<Byte> rxRawDatas; 

    private static CommThread myown = null;
    //This is a lock for some command that need response.
    public static Object commLock;
    
    private CommThread(Object lock, Controller ctr) {
        this.lockMain = lock;
        this.lock = new Object();
        this.ctr = ctr;

        //buffer Rx&Tx for other thread 
        bufferRx = new ConcurrentLinkedQueue<>();
        bufferTx = new ConcurrentLinkedQueue<>();
        
        //raw data for serial port
        rxRawDatas = new ConcurrentLinkedQueue<>();
        myown = this;
        commLock = new Object();
    }


    public static CommThread getCommThread(Object lock, Controller ctr)
    {
        if(myown == null){
            return new CommThread(lock, ctr);
        } else {
            return myown;
        }
    }

    public static CommThread getCommThread()
    {
        return myown;
    }

    @Override
    public void run() {
        // wait for sync: this sync should be
        // sent in controller inicialization
        synchronized (lockMain) {
            try {
                lockMain.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // start to configuration
        SerialPort[] sers = SerialPort.getCommPorts();
        

        // show the dialog in the platform
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ChoiceDialog<SerialPort> diag = new ChoiceDialog<>();
                        diag.setContentText("Select SDIP Serial Port:");
                        diag.setHeaderText("Configuração da porta serial");
                        diag.setTitle("Serial Port Configuration");
                        diag.getItems().addAll(sers);
                        Optional<SerialPort> selSerial = diag.showAndWait();
                        serialPort = selSerial.get();
                        serialPort.setBaudRate(115200);
                        serialPort.setNumDataBits(8);
                        serialPort.setParity(SerialPort.NO_PARITY);
                        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                                                
                        synchronized (lock) {
                            lock.notify();
                        }
                        break;
                    } catch (Exception e) {
                        if(serialPort != null){
                            serialPort.closePort();
                        }
                        
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Serial port error!! \n Do you want to choose another serial port?", ButtonType.YES,
                                ButtonType.NO);
                        Optional<ButtonType> clickedBut = alert.showAndWait();
                        if (clickedBut.get() == ButtonType.NO) {
                            // should close the program
                            alert = new Alert(Alert.AlertType.ERROR,
                            "Program will be closed!!!",
                            ButtonType.OK);
                            alert.showAndWait();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Window win = ctr.vbox.getScene().getWindow();
                                    win.fireEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSE_REQUEST));
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });

        // wait until serial port is correctly configured
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //open serial port
        serialPort.openPort();
        serialPort.addDataListener(new SerialPortDataListener(){
        
            @Override
            public void serialEvent(SerialPortEvent evt) {
                byte[] newDatas = evt.getReceivedData();
                for(byte data: newDatas){
                    rxRawDatas.add(data);
                    //ystem.out.println(data+" rsved.");
                }
            }
        
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }
        });

        //valid data get from raw data, temperorily saved place
        ArrayList<Byte> rxValidData = new ArrayList<>(); 

        //monitor the serial port here.
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //monitoring write buffer
            while(!bufferTx.isEmpty()){
                //send the data
                ArrayList<Byte> dataToSent = bufferTx.poll();
                Byte[] datasInBytes = new Byte[dataToSent.size()];
                datasInBytes = dataToSent.toArray(datasInBytes);
                serialPort.writeBytes(tobyte(datasInBytes), datasInBytes.length);
            }

            //System.out.println("buffer size = "+bufferRx.size());
            //check read buffer here
            while(!rxRawDatas.isEmpty()){
                if(isValidDataFrame(rxValidData))
                {
                    bufferRx.add(rxValidData);
                    //create a new instance.
                    rxValidData = new ArrayList<>();
                }
            }
        }
    }

    
   
    //convert Byte[] to byte
    private byte[] tobyte(Byte[] array)
    {
        byte[] byteArray = new byte[array.length];
        int idx = 0;
        for(Byte no: array){
            byteArray[idx++] = (byte)no;
        }
        return byteArray;
    }

    //get a valid data frame from some serialized data
    private boolean isValidDataFrame(ArrayList<Byte> list){

        int pos = list.size();
        byte val = rxRawDatas.poll().byteValue();
        if(isValidValue(val, pos)){
            list.add(val);
            if(flagEndOfFrame){
                flagEndOfFrame = false;
                return true;
            }
        }else{
            list.clear();
        }
        return false;
    }

    private final int MAX_LENGTH_FRAME = 200;
    private int dataCnter = 0;
    private final int FRAME_HEAD_LENGTH = 4; //
    private UByteLikeC crc = new UByteLikeC(0);
    private boolean flagEndOfFrame = false;

    //check is it is a valid value for a frame
    private Boolean isValidValue(byte val, int pos){
        boolean isValid = false;
        //if it is in head area
        if(pos < FRAME_HEAD_LENGTH){
            switch(pos)
            {
                case 0: //it is in frame head area
                    if((int)val == (byte)0xaa){
                        isValid = true;
                        crc.set(0);
                        crc.add(val);
                        flagEndOfFrame = false;
                    }
                    break;
                case 1: // route byte
                    if((int)val == (byte)0x10){
                        isValid = true;
                        crc.add(val);
                    }
                    break;
                case 2: //Type
                    isValid = true;
                    crc.add(val);
                    break;
                case 3: //size
                    isValid = true;
                    dataCnter = (int)val;
                    crc.add(val);
                    break;
            }
            
        } else if( pos < MAX_LENGTH_FRAME){
            //Data bytes
            if(dataCnter != 0){  //if it is some data
                dataCnter--;
                crc.add(val);
                isValid = true;
            } else { 
                //no data or data transmitted, check crc here
                if((byte)crc.value == (byte)val) 
                {
                    flagEndOfFrame = true;
                    isValid = true;
                }
            }
        } 
        return isValid;
    }


    public ArrayList<Byte> generateFrame(byte[]datas, byte type)
    {
        UByteLikeC crc = new UByteLikeC(0);
        
        ArrayList<Byte> frame = new ArrayList<>();
        frame.add((byte)0xaa);//start byte
        crc.add(0xaa);
        frame.add((byte)0x01);//route byte
        crc.add(0x01);
        frame.add((byte)type); //type
        crc.add(type);
        frame.add((byte)datas.length);
        for(byte data:datas){
            frame.add(data);
            crc.add(data);
        }
        frame.add((byte)crc.value);
        return frame;
    }


}

