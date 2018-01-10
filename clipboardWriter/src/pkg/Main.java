   /*
    * Class: ClipboardWriter
    * 
    * @Author: Dong Xia
    * @File Created at 06/Jan/2018
    * Change Records:
    *      >>
    *      >>
    */
   package pkg;

   import java.awt.AWTException;
   import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
   import org.jnativehook.GlobalScreen;
   import org.jnativehook.NativeHookException;
   import org.jnativehook.keyboard.NativeKeyEvent;
   import org.jnativehook.keyboard.NativeKeyListener;
   import java.io.*;
   import static java.awt.event.KeyEvent.*;
    


    public class Main extends Robot implements Runnable{

       public int templateCode;
       private BufferedReader fin;
       String txt;
       static Robot robot;

       public Main() throws AWTException, FileNotFoundException
       {
           super();
           templateCode = 0;
           fin = new BufferedReader(new FileReader("conf"));
           robot = new Robot();
           String strLine;
   		try {
   			while ((strLine = fin.readLine()) != null) {
   				txt += (strLine+"\n");
   			}
   		} catch (IOException e1) {
   			// TODO Auto-generated catch block
   			e1.printStackTrace();
   		}
           
       }


       //A thread to listen to the keyboard
       public void run() {
           try{
           	GlobalScreen.registerNativeHook();
           } catch (NativeHookException e) {
           	
           }
           GlobalScreen.addNativeKeyListener(new KeyTracker());
       }

       //class to implements NativeKeyListener
       class KeyTracker implements NativeKeyListener{

           ArrayList<String> keyPressed=new ArrayList<>();
           //When the key is presssed.
           public void nativeKeyPressed(NativeKeyEvent e){

               int keyCode = e.getKeyCode();
               //when pressed a key
               switch(keyPressed.size()){
                   case 0: 
                       if(keyCode == NativeKeyEvent.VC_ALT){
                           keyPressed.add("ALT");
                       }
                       else if(keyCode == NativeKeyEvent.VC_CONTROL){
                           keyPressed.add("CTL");
                       }
                       break;
                   case 1:
                       if(keyCode == NativeKeyEvent.VC_ALT){
                           if(keyPressed.get(0).equals("CTL")){
                               keyPressed.add("ALT");
                           }
                       }
                       else if(keyCode == NativeKeyEvent.VC_CONTROL){
                           if(keyPressed.get(0).equals("ALT")){
                               keyPressed.add("CLT");
                           }                        
                       } else {
                           keyPressed.clear();
                       }
                       break;
                   case 2:
                       switch(keyCode){
                           case NativeKeyEvent.VC_C:
                               keyPressed.add("C");break;
                           case NativeKeyEvent.VC_H:
                               keyPressed.add("H");break;
                           case NativeKeyEvent.VC_J:
                               keyPressed.add("Java");break;
                           case NativeKeyEvent.VC_P:
                               keyPressed.add("Python");break;
                           default: keyPressed.clear(); break;
                       }
                       break;
                   case 3:
                       switch(keyCode){
                           case NativeKeyEvent.VC_H:
                               keyPressed.add("head");break;
                           case NativeKeyEvent.VC_F:
                               keyPressed.add("function");break;
                           default: break;
                       }
                       ////react with the key
                       String partText = "";
                       if(keyPressed.get(2).equals("C")) {
                       	if(keyPressed.get(3).equals("head")) {
                       		partText = txt.substring(txt.indexOf("[C-head]")+9);
                       		partText = partText.substring(0,partText.indexOf("[end]"));
                       	}else if(keyPressed.get(3).equals("function")) {
                           		partText = txt.substring(txt.indexOf("[C-func]")+9);
                           		partText = partText.substring(0,partText.indexOf("[end]"));
                           }
                       }else if(keyPressed.get(2).equals("H")) {
                       	if(keyPressed.get(3).equals("head")) {
                       		partText = txt.substring(txt.indexOf("[H-head]")+9);
                       		partText = partText.substring(0,partText.indexOf("[end]"));
                       	}else if(keyPressed.get(3).equals("function")) {
                           		partText = txt.substring(txt.indexOf("[H-func]")+9);
                           		partText = partText.substring(0,partText.indexOf("[end]"));
                           }
                       }else if(keyPressed.get(2).equals("Java")) {
                       	if(keyPressed.get(3).equals("head")) {
                       		partText = txt.substring(txt.indexOf("[J-head]")+9);
                       		partText = partText.substring(0,partText.indexOf("[end]"));
                       	}else if(keyPressed.get(3).equals("function")) {
                           		partText = txt.substring(txt.indexOf("[J-func]")+9);
                           		partText = partText.substring(0,partText.indexOf("[end]"));
                           }
                       }else if(keyPressed.get(2).equals("Python")) {
                       	if(keyPressed.get(3).equals("head")) {
                       		partText = txt.substring(txt.indexOf("[P-head]")+9);
                       		partText = partText.substring(0,partText.indexOf("[end]"));
                       	}else if(keyPressed.get(3).equals("function")) {
                           		partText = txt.substring(txt.indexOf("[P-func]")+9);
                           		partText = partText.substring(0,partText.indexOf("[end]"));
                           }
                       }
                       StringSelection selec= new StringSelection(partText);
                       Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                       clipboard.setContents(selec, selec);
                       
                       //type(partText);
                       keyPressed.clear();
                       break; 
                       default: keyPressed.clear(); break;
               }
               
               
               
           }
   		@Override
   		public void nativeKeyReleased(NativeKeyEvent arg0) {
   			// TODO Auto-generated method stub
   			
   		}
   		@Override
   		public void nativeKeyTyped(NativeKeyEvent arg0) {
   			// TODO Auto-generated method stub
   			
   		}
       }

       //main function
       public static void main(String[] args) throws FileNotFoundException{
           try{
           	Main key = new Main();
           	key.run();
           }catch(AWTException e){
           	
           }
       } 
    }