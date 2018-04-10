package electronicLab.model;

import java.util.HashMap;

public  class CodeTransformer {

	private HashMap<Integer,Integer> rules;
	public CodeTransformer(){
		rules = new HashMap<>();
		rules.put(0, 4); //Original code 0 bit --> 4 bit in new code
		rules.put(1, 5);
		rules.put(2, 6);
		rules.put(3, 7);
		rules.put(4, 9);
		rules.put(5, 10);
		rules.put(6, 13);
		rules.put(7, 14);
	}
	
	public CodeTransformer(HashMap<Integer,Integer> rules){
		this.rules = rules;
	}
	
	public int convert(int val){
		
		if(val > 255 || val < 0) return -1;
		
		int newVal = 0;
		
		if((val&0x01) != 0) newVal |= (0x01<<rules.get(0));
		if((val&0x02) != 0) newVal |= (0x01<<rules.get(1));
		if((val&0x04) != 0) newVal |= (0x01<<rules.get(2));
		if((val&0x08) != 0) newVal |= (0x01<<rules.get(3));
		if((val&0x10) != 0) newVal |= (0x01<<rules.get(4));
		if((val&0x20) != 0) newVal |= (0x01<<rules.get(5));
		if((val&0x40) != 0) newVal |= (0x01<<rules.get(6));
		if((val&0x80) != 0) newVal |= (0x01<<rules.get(7));
		
		return newVal;
	}
	
	public int convert(byte val){
		
		if(val > 128 || val < -128) return -1;
		
		int newVal = 0;
		
		if((val&0x01) != 0) newVal |= (0x01<<rules.get(0));
		if((val&0x02) != 0) newVal |= (0x01<<rules.get(1));
		if((val&0x04) != 0) newVal |= (0x01<<rules.get(2));
		if((val&0x08) != 0) newVal |= (0x01<<rules.get(3));
		if((val&0x10) != 0) newVal |= (0x01<<rules.get(4));
		if((val&0x20) != 0) newVal |= (0x01<<rules.get(5));
		if((val&0x40) != 0) newVal |= (0x01<<rules.get(6));
		if((val&0x80) != 0) newVal |= (0x01<<rules.get(7));
		
		return newVal;
	}
}
