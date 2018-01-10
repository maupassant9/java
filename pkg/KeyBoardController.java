package pkg;
/*
 * Class: KeyBoardController
 * 
 * @Author: Dong Xia
 * @File Created at 06/Jan/2018
 * Change Records:
 *      >>
 *      >>
 */
import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.io.*;
import static java.awt.event.KeyEvent.*;
 


 public class KeyBoardController extends Robot implements Runnable{

    public int templateCode;
    private BufferedReader fin;
    String txt;
    static Robot robot;

    public KeyBoardController() throws AWTException, FileNotFoundException
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

    //Type just one keycode
    public void type(char i) {
        switch (i) {
        case 'a': doType(VK_A); break;
        case 'b': doType(VK_B); break;
        case 'c': doType(VK_C); break;
        case 'd': doType(VK_D); break;
        case 'e': doType(VK_E); break;
        case 'f': doType(VK_F); break;
        case 'g': doType(VK_G); break;
        case 'h': doType(VK_H); break;
        case 'i': doType(VK_I); break;
        case 'j': doType(VK_J); break;
        case 'k': doType(VK_K); break;
        case 'l': doType(VK_L); break;
        case 'm': doType(VK_M); break;
        case 'n': doType(VK_N); break;
        case 'o': doType(VK_O); break;
        case 'p': doType(VK_P); break;
        case 'q': doType(VK_Q); break;
        case 'r': doType(VK_R); break;
        case 's': doType(VK_S); break;
        case 't': doType(VK_T); break;
        case 'u': doType(VK_U); break;
        case 'v': doType(VK_V); break;
        case 'w': doType(VK_W); break;
        case 'x': doType(VK_X); break;
        case 'y': doType(VK_Y); break;
        case 'z': doType(VK_Z); break;
        case 'A': doType(VK_SHIFT, VK_A); break;
        case 'B': doType(VK_SHIFT, VK_B); break;
        case 'C': doType(VK_SHIFT, VK_C); break;
        case 'D': doType(VK_SHIFT, VK_D); break;
        case 'E': doType(VK_SHIFT, VK_E); break;
        case 'F': doType(VK_SHIFT, VK_F); break;
        case 'G': doType(VK_SHIFT, VK_G); break;
        case 'H': doType(VK_SHIFT, VK_H); break;
        case 'I': doType(VK_SHIFT, VK_I); break;
        case 'J': doType(VK_SHIFT, VK_J); break;
        case 'K': doType(VK_SHIFT, VK_K); break;
        case 'L': doType(VK_SHIFT, VK_L); break;
        case 'M': doType(VK_SHIFT, VK_M); break;
        case 'N': doType(VK_SHIFT, VK_N); break;
        case 'O': doType(VK_SHIFT, VK_O); break;
        case 'P': doType(VK_SHIFT, VK_P); break;
        case 'Q': doType(VK_SHIFT, VK_Q); break;
        case 'R': doType(VK_SHIFT, VK_R); break;
        case 'S': doType(VK_SHIFT, VK_S); break;
        case 'T': doType(VK_SHIFT, VK_T); break;
        case 'U': doType(VK_SHIFT, VK_U); break;
        case 'V': doType(VK_SHIFT, VK_V); break;
        case 'W': doType(VK_SHIFT, VK_W); break;
        case 'X': doType(VK_SHIFT, VK_X); break;
        case 'Y': doType(VK_SHIFT, VK_Y); break;
        case 'Z': doType(VK_SHIFT, VK_Z); break;
        case '`': doType(192); break;
        case '0': doType(VK_0); break;
        case '1': doType(VK_1); break;
        case '2': doType(VK_2); break;
        case '3': doType(VK_3); break;
        case '4': doType(VK_4); break;
        case '5': doType(VK_5); break;
        case '6': doType(VK_6); break;
        case '7': doType(VK_7); break;
        case '8': doType(VK_8); break;
        case '9': doType(VK_9); break;
        case '-': doType(45); break;
        case '=': doType(61); break;
        case '~': doType(16, 192); break;
        case '!': doType(16,VK_1); break;
        case '@': doType(16,VK_2); break;
        case '#': doType(16,VK_3); break;
        case '$': doType(16,VK_4); break;
        case '%': doType(16, VK_5); break;
        case '^': doType(16,54); break;
        case '&': doType(16,55); break;
        case '*': doType(16,56); break;
        case '(': doType(16,57); break;
        case ')': doType(16,48); break;
        case '_': doType(16,45); break;
        case '+': doType(16,61); break;
        case '\t': doType(VK_TAB); break;
        case '\n': doType(10); break;
        case '[': doType(91); break;
        case ']': doType(93); break;
        case '\\': doType(92); break;
        case '{': doType(16, 91); break;
        case '}': doType(16, 93); break;
        case '|': doType(16, 92); break;
        case ';': doType(59); break;
        case ':': doType(16,59); break;
        case '\'': doType(222); break;
        case '"': doType(16,222); break;
        case ',': doType(44); break;
        case '<': doType(16, 44); break;
        case '.': doType(46); break;
        case '>': doType(16, 46); break;
        case '/': doType(47); break;
        case '?': doType(16, 47); break;
        case ' ': doType(32); break;
        default: break;
        }
    }

    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        delay(10);
        robot.keyRelease(keyCodes[offset]);
    }
    //Type all the text 
    public void type(String text){
        for(int i = 0; i < text.length(); i++){
            type(text.charAt(i)); 
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
                    type(partText);
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
        	KeyBoardController key = new KeyBoardController();
        	key.run();
        }catch(AWTException e){
        	
        }
    } 
 }