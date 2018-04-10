package electronicLab.model;

import java.util.ArrayList;

public class CommandGenerator {
//MSB--->LSB
	private final static long HEAD_PACKAGE = 0xa5a5a5;
	private final static long TAIL_PACKAGE = 0x555555;
	private final static byte HEAD_CMD_FREQ = (byte)0x91;
	private final static byte HEAD_CMD_OFFSET = (byte)0x93;
	private final static byte HEAD_CMD_DUTY = (byte)0x92;
	private final static byte HEAD_CMD_QUERY = (byte)0x97;
	private final static byte HEAD_CMD_TIMER_DIV = (byte)0x98;

	private final static byte HEAD_CMD_ALTITUDE = (byte)0x95;
	private final static byte HEAD_CMD_WAVETYPE = (byte)0x94;
	private final static byte HEAD_CMD_CHANNEL = (byte)0x96;
	private final static byte TAIL_CMD_END = (byte)0xff;
	private final static long HEAD_DATA = 0x434343;
	private final static long TAIL_DATA = 0x555555;
	
	public final static byte[] CMD_CONNECT = {(byte) 0xa5,(byte) 0xa5,(byte) 0xa5,0x33,0x55,0x55,0x55};
	public final static byte[] CMD_QUERY = {(byte) 0xa5,(byte) 0xa5,(byte)0xa5,(byte)0x97,0x02,0x55,0x55,0x55};
	public final static byte[] CMD_CLOSE = {(byte) 0xa5,(byte) 0xa5,(byte) 0xa5,(byte)0xe4,0x55,0x55,0x55};
	public final static byte CMD_QUERY_DISCONNECTED = 0x01;
	public final static byte CMD_QUERY_CONNECTED = 0x02;
	public final static byte CMD_QUERY_CONFIGURED = 0x03;
	public final static byte CMD_QUERY_RUNNING = 0x04;
	
	
	public final static byte CMD_RESPONDE_OK = (byte)0xa5;
	
	
	
	//parameters for mcu, data for mcu
	private int [] data; 
	private int dataSz;
	private int timerDiv;
	private int freqNum;
	
	private byte[] sndBuffer;
	
	private ArrayList<Byte> cmd;
	private WaveConf wConf;
	
	public CommandGenerator(WaveConf wConf) throws DataInvalidException{
		this.wConf = wConf;
		cmd = new ArrayList<>();
		data = new int[WaveformConstants.BUFFER_LENGTH];
		calcParameters(); // calculate the value for data_sz,timerDiv and freqNum
	}
	
	public byte[] generatePackage(){
		
		cmd.clear();
		addCmdPackageHead();
		addFreq(wConf.getFreq());
		addAltitude(wConf.getAltitude());
		addWavetype(wConf.getWaveform());
		addChannel(wConf.getChannel());	
		addDuty((int)wConf.getDuty());
		addOffset((int)wConf.getOffset());
		addTimeDiv(wConf.getTimeDiv());
		addCmdPackageTail();
		
		sndBuffer = new byte[cmd.size()];
		for(int i = 0; i < cmd.size(); i++){
			sndBuffer[i] = cmd.get(i);
		}
		return sndBuffer;
	}
	
	public byte[] generatePackage(byte[] data){
		cmd.clear();
		addDataPackageHead();
		int len = data.length/2;
		cmd.add((byte)0x00);
		cmd.add((byte)(len&0xff));
		cmd.add((byte)((len&0xff00)>>8));
			
		
		for(int i = 0; i < data.length; i++){
			cmd.add(data[i]);
		}		
		addDataPackageTail();
		
		sndBuffer = new byte[cmd.size()];
		for(int i = 0; i < cmd.size(); i++){
			sndBuffer[i] = cmd.get(i);
		}
		return sndBuffer;
	}
	
	
	private void addDataPackageHead(){
		cmd.add((byte)((HEAD_DATA&0xff0000)>>16));
		cmd.add((byte)((HEAD_DATA&0xff00)>>8));
		cmd.add((byte)((HEAD_DATA&0xff)));
	}
	
	private void addDataPackageTail(){
		cmd.add((byte)((TAIL_DATA&0xff0000)>>16));
		cmd.add((byte)((TAIL_DATA&0xff00)>>8));
		cmd.add((byte)((TAIL_DATA&0xff)));
	}
	
	private void addFreq(float freq){
		long tmp = (long)freq;

		cmd.add(HEAD_CMD_FREQ);
		cmd.add((byte)((tmp&0xff0000)>>16));
		cmd.add((byte)((tmp&0xff00)>>8));
		cmd.add((byte)(tmp&0xff));
		cmd.add(TAIL_CMD_END);
	}

	private void addTimeDiv(int div){

		cmd.add(HEAD_CMD_TIMER_DIV);
		cmd.add((byte)((div&0xff00)>>8));
		cmd.add((byte)(div&0xff));
		cmd.add(TAIL_CMD_END);
	}
	
	private void addAltitude(float altitude){
		int tmp = (int)(altitude*1000); //in mV

		cmd.add(HEAD_CMD_ALTITUDE);
		cmd.add((byte)((tmp&0xff00)>>8));
		cmd.add((byte)(tmp&0xff));
		cmd.add(TAIL_CMD_END);

	}
	
	private void addWavetype(int type){
		cmd.add(HEAD_CMD_WAVETYPE);
		cmd.add((byte)type);
		cmd.add(TAIL_CMD_END);
	}
	
	private void addDuty(int duty){
		cmd.add(HEAD_CMD_DUTY);
		cmd.add((byte)duty);
		cmd.add(TAIL_CMD_END);
	}
	
	
	private void addOffset(int offset){

		cmd.add(HEAD_CMD_OFFSET);
		cmd.add((byte)((offset&0xff00)>>8));
		cmd.add((byte)(offset&0xff));
		cmd.add(TAIL_CMD_END);
	}
	
	private void addChannel(int channel){
		cmd.add(HEAD_CMD_CHANNEL);
		cmd.add((byte)channel);
		cmd.add(TAIL_CMD_END);
	}
		
	private void addCmdPackageHead(){
		cmd.add((byte)((HEAD_PACKAGE&0xff0000)>>16));
		cmd.add((byte)((HEAD_PACKAGE&0xff00)>>8));
		cmd.add((byte)(HEAD_PACKAGE&0xff));
	}
	
	private void addCmdPackageTail(){
		cmd.add((byte)((TAIL_PACKAGE&0xff0000)>>16));
		cmd.add((byte)((TAIL_PACKAGE&0xff00)>>8));
		cmd.add((byte)((TAIL_PACKAGE&0xff)));
	}
	
	
	private void calcParameters() throws DataInvalidException{
		switch(wConf.getWaveform()){
		case WaveformConstants.WAVETYPE_SIN: calcSinParameters();break;
		case WaveformConstants.WAVETYPE_TRIANGLE:calcTriParameters();break;
		case WaveformConstants.WAVETYPE_SERRA: calcSerraParameters();break;
		case WaveformConstants.WAVETYPE_SERRA_INV: calcSerraInvParameters();break;
		default:break;
		}
	}
	
	private void calcTriParameters() throws DataInvalidException{
		double sigFreq = (double)wConf.getFreq();
		dataSz = 2*WaveformConstants.DAC_RESOLUTION_MAX;
		double samplingFreq = sigFreq*(dataSz);
		while(samplingFreq > (WaveformConstants.TIMER_OSC_MAX/6)){
			dataSz /= 2;
			samplingFreq = sigFreq*dataSz;
		}
		
		wConf.setTotalNum(dataSz);
		timerDiv = (int)(WaveformConstants.TIMER_OSC_MAX/samplingFreq);
		wConf.setTimeDiv(timerDiv-1);
		
		System.out.println("*************************");
		System.out.println("total number of point: "+dataSz);
		System.out.println("timer divider number: "+(timerDiv));
		System.out.println("*************************");
	}

	private void calcSinParameters() throws DataInvalidException{
		double freq = (double)wConf.getFreq();
		double timerFreq = WaveformConstants.TIMER_OSC_MAX; //-->Input timer freq
		int mRecord = 0,freqNumRecord = 0;
		double min_t = 100.0;int divRecord = 1;
		
		for(int div = 6; div <= WaveformConstants.TIMER_CNT_MAX; div++){
		//for(int div = WaveformConstants.TIMER_CNT_MAX; div >= 6; div--){
			double sampling = timerFreq/div;
			if(sampling < freq*WaveformConstants.SAMPLING_NUM_MIN) continue;
			int freqNumLimit = (int)(WaveformConstants.BUFFER_LENGTH*freq/sampling);
			for(int freqNum = 1; freqNum <= freqNumLimit; freqNum++)
			{
				int m = (int)Math.round((freqNum*sampling/freq));
				if(m > WaveformConstants.BUFFER_LENGTH) break;
				double delta_t = Math.abs((m/sampling - freqNum/freq));
				if(delta_t < min_t){
					min_t = delta_t;
					mRecord = m;
					freqNumRecord = freqNum;	
					divRecord = div;
				}
				if(min_t == 0.0) break;
			}
			
			//if freq > 200kHz, using div = 6
			if((freq > 200000) && timerDiv == 6) break;
			
		}
		dataSz = mRecord; // total number of points
		wConf.setTotalNum(mRecord);
		freqNum = freqNumRecord; //the number of frequencies in the waveform
		timerDiv = divRecord-1; //timer divider for MCU!
		wConf.setTimeDiv(divRecord-1);
		
		System.out.println("*************************");
		System.out.println("total number of point: "+mRecord);
		System.out.println("frequency number: "+freqNumRecord);
		System.out.println("timer divider number: "+divRecord);
		System.out.println("Real Freq: "+(timerFreq/divRecord*freqNumRecord/mRecord));
		System.out.println("*************************");
		
	}
	
	private void calcSerraParameters() throws DataInvalidException{
		double sigFreq = (double)wConf.getFreq();
		dataSz = WaveformConstants.DAC_RESOLUTION_MAX+1;
		double samplingFreq = sigFreq*(dataSz);
		while(samplingFreq > (WaveformConstants.TIMER_OSC_MAX/6)){
			dataSz /= 2;
			samplingFreq = sigFreq*dataSz;
		}
		
		wConf.setTotalNum(dataSz);
		timerDiv = (int)(WaveformConstants.TIMER_OSC_MAX/samplingFreq);
		wConf.setTimeDiv(timerDiv-1);
		
		System.out.println("*************************");
		System.out.println("total number of point: "+dataSz);
		System.out.println("timer divider number: "+(timerDiv));
		System.out.println("*************************");
	}
	
	private void calcSerraInvParameters() throws DataInvalidException{
		double sigFreq = (double)wConf.getFreq();
		dataSz = WaveformConstants.DAC_RESOLUTION_MAX+1;
		double samplingFreq = sigFreq*(dataSz);
		while(samplingFreq > (WaveformConstants.TIMER_OSC_MAX/6)){
			dataSz /= 2;
			samplingFreq = sigFreq*dataSz;
		}
		
		wConf.setTotalNum(dataSz);
		timerDiv = (int)(WaveformConstants.TIMER_OSC_MAX/samplingFreq);
		wConf.setTimeDiv(timerDiv-1);
		
		System.out.println("*************************");
		System.out.println("total number of point: "+dataSz);
		System.out.println("timer divider number: "+(timerDiv));
		System.out.println("*************************");
	}
	
	private void calcSquareParameters(){
		
	}
	
	private void calcUserDefinedParameters(){
		
	}
	
	public byte[] genWaveform() throws DataInvalidException{
		//generate waveform using parameters 
		switch(wConf.getWaveform()){
		case WaveformConstants.WAVETYPE_SIN: return genSinWave();
		case WaveformConstants.WAVETYPE_TRIANGLE:return genTriWave();
		case WaveformConstants.WAVETYPE_SERRA:return genSerraWave();
		case WaveformConstants.WAVETYPE_SERRA_INV: return genSerraInvWave();
//		case WaveformConstants.WAVETYPE_SQUARE: return genSquareWave();
//		case WaveformConstants.WAVETYPE_USERDEFINED: return genUserDefined();
		default:return null;
		}
	}
		
	private byte[] genSinWave(){
		//generate sin wave
		//1 - the sampling freq and signal freq
		double samplingFreq = WaveformConstants.TIMER_OSC_MAX/(timerDiv+1);
		double sigFreq = (double)wConf.getFreq();
		double deltaPhase = (double)(2*Math.PI*sigFreq/samplingFreq);
		double initialPhase = (double)wConf.getPhase();
		for(int i = 0; i < dataSz; i++){
			data[i] = (int) (((double)WaveformConstants.DAC_RESOLUTION_MAX/2) * 
					Math.sin(initialPhase+i*deltaPhase)+(double)WaveformConstants.DAC_RESOLUTION_MAX/2);
		}
		
		CodeTransformer cg = new CodeTransformer();
		byte[] newData = new byte[dataSz*2];
		for(int i = 0;i < dataSz;i++)
		{
			int val = cg.convert(data[i]);
			newData[2*i+1] = (byte)((val&0xff00)>>8);
			newData[2*i] = (byte)((val&0xff));
			System.out.printf("%d,",(int)data[i]);
		}
		System.out.println("");
		return newData;
	}
	
	private byte[] genTriWave() throws DataInvalidException{
		//generate sin wave
		//1 - the sampling freq and signal freq
		double delta = (double)(WaveformConstants.DAC_RESOLUTION_MAX*2.0-
						2.0)/(dataSz-1);
		for(int i = 0; i < dataSz; i++){
			int val = (int)(i * delta);
			if(val > 255) val = (WaveformConstants.DAC_RESOLUTION_MAX*2 - val);
			data[i] = val;
		}
		
		CodeTransformer cg = new CodeTransformer();
		byte[] newData = new byte[dataSz*2];
		for(int i = 0;i < dataSz;i++)
		{
			int val = cg.convert(data[i]);
			newData[2*i+1] = (byte)((val&0xff00)>>8);
			newData[2*i] = (byte)((val&0xff));
			System.out.printf("%d,",(int)data[i]);
		}
		System.out.println("");
		return newData;
	}
	
	private byte[] genSerraWave(){
		//generate sin wave
				//1 - the sampling freq and signal freq
				double delta = (double)(WaveformConstants.DAC_RESOLUTION_MAX/(dataSz-1));
				for(int i = 0; i < dataSz; i++){
					int val = (int)(i * delta);
					data[i] = val;
				}
				
				CodeTransformer cg = new CodeTransformer();
				byte[] newData = new byte[dataSz*2];
				for(int i = 0;i < dataSz;i++)
				{
					int val = cg.convert(data[i]);
					newData[2*i+1] = (byte)((val&0xff00)>>8);
					newData[2*i] = (byte)((val&0xff));
					System.out.printf("%d,",(int)data[i]);
				}
				System.out.println("");
				return newData;
	}

	private byte[] genSerraInvWave(){
		//generate sin wave
		//1 - the sampling freq and signal freq
		double delta = (double)(WaveformConstants.DAC_RESOLUTION_MAX/(dataSz-1));
		for(int i = 0; i < dataSz; i++){
			int val = (int)(255 - (i * delta));
			data[i] = val;
		}
		
		CodeTransformer cg = new CodeTransformer();
		byte[] newData = new byte[dataSz*2];
		for(int i = 0;i < dataSz;i++)
		{
			int val = cg.convert(data[i]);
			newData[2*i+1] = (byte)((val&0xff00)>>8);
			newData[2*i] = (byte)((val&0xff));
			System.out.printf("%d,",(int)data[i]);
		}
		System.out.println("");
		return newData;
	}
	/*
	private byte[] genSquareWave(){
		
	}

	private byte[] genUserDefinedWave(){
	}*/
}
