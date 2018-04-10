package electronicLab.model;


public class WaveConf {
	
	private int waveform; //1 - Sin, 2 - Triangle, 3 - Square, 4 - Serra, 5 - Serra inverse ,6 - UserDefine
	private float phase; //0-359
	private float altitude; // in V
	private float freq; // in Hz
	private int duty; // 1-99%
	private float offset; // in mV
	private int channel;
	private int timeDiv;
	private int totalNum;

	
	public WaveConf(){
		waveform = 0;
		phase = 0;
		altitude = 0;
		freq = 0;
		duty = 50;
		offset = 0;
		channel = 0;
		timeDiv = 0;
		totalNum = 0;
	}
	
	public int getWaveform(){
		return waveform;
	}
	
	public float getPhase(){
		return phase;
	}
	
	public int getTimeDiv(){
		return timeDiv;
	}
	
	public float getAltitude(){
		return altitude;
	}
	
	public float getFreq(){
		return freq;
	}
	
	public float getDuty(){
		return duty;
	}
	
	public float getOffset(){
		return offset;
	}
	
	public int getChannel(){
		return channel;
	}
	
	public int getTotalNum(){
		return totalNum;
	}
	
	private boolean isValidWaveType(int index){
		return (index >= WaveformConstants.WAVETYPE_MIN && index <=  WaveformConstants.WAVETYPE_MAX);
	}
	
	private boolean isValidPhase(float val){
		return (val >= WaveformConstants.PHASE_MIN && val <= WaveformConstants.PHASE_MAX);
	}
	
	private boolean isValidAltitude(float val){
		return (val >= WaveformConstants.ALTITUDE_MIN && val <= WaveformConstants.ALTITUDE_MAX);
	}
	
	private boolean isValidFreq(float val){
		return (val >= WaveformConstants.FREQ_MIN && val <= WaveformConstants.FREQ_MAX);
	}
	
	private boolean isValidDuty(int val){
		return (val >= WaveformConstants.DUTY_MIN && val <= WaveformConstants.DUTY_MAX);
	}
	
	private boolean isValidOffset(float val){
		return (val >= WaveformConstants.OFFSET_MIN && val <= WaveformConstants.OFFSET_MAX);
	}
	
	private boolean isValidChannel(int val){
		return (val >= WaveformConstants.CHANNEL_MIN && val <= WaveformConstants.CHANNEL_MAX);
	}
	
	private boolean isValidTimeDiv(int val){
		return (val <= 65532 && val >= 5);
	}
	
	private boolean isValidTotalNum(int val){
		return (val <= 1024 && val >= 8);
	}
	
	private int dataChecker(int val, int step, int min){
		int tmp = (val-min)/step;
		return(tmp*step+min);
	}
	
	private float dataChecker(float val, float step, float min){
		float tmp = (val-min)/step;
		return(tmp*step+min);
	}
	
	public void setWaveType(int index) throws DataInvalidException{
		if(isValidWaveType(index)){
			//Valid data
			waveform = index;return;
		}
		//exception here
		throw new DataInvalidException();
	}
	
	
	public void setPhase(float val) throws DataInvalidException{
		if(isValidPhase(val)){
			phase = dataChecker(val, 
					WaveformConstants.PHASE_STEP,
					WaveformConstants.PHASE_MIN);return;
		}
		throw new DataInvalidException();
	}
	
	public void setAltitude(float val) throws DataInvalidException{
		System.out.println(val);
		if(isValidAltitude(val)){
			altitude = dataChecker(val,
					WaveformConstants.ALTITUDE_STEP,
					WaveformConstants.ALTITUDE_MIN);return;
		}
		throw new DataInvalidException();
	}
	
	public void setFreq(float val) throws DataInvalidException{
		if(isValidFreq(val)){
			freq = dataChecker(val,
					WaveformConstants.FREQ_STEP,
					WaveformConstants.FREQ_MIN);return;
		}
		throw new DataInvalidException();
	}

	public void setDuty(int val) throws DataInvalidException{
		if(isValidDuty(val)){
			duty = dataChecker(val,
					WaveformConstants.DUTY_STEP,
					WaveformConstants.DUTY_MIN);return;
		}
		throw new DataInvalidException();
	}
	
	public void setOffset(float val) throws DataInvalidException{
		if(isValidOffset(val)){
			offset = dataChecker(val,
					WaveformConstants.OFFSET_STEP,
					WaveformConstants.OFFSET_MIN);return;
		}
		throw new DataInvalidException();
	}
	
	public void setChannel(int val) throws DataInvalidException {
		if(isValidChannel(val)){
			channel = val;return;
		}
		throw new DataInvalidException();
	}
	
	public void setTimeDiv(int val) throws DataInvalidException{
		if(isValidTimeDiv(val)){
			timeDiv = val;return;
		}
		throw new DataInvalidException();
	}
	
	public void setTotalNum(int val) throws DataInvalidException{
		if(isValidTotalNum(val)){
			totalNum = val;return;
		}
		throw new DataInvalidException();
	}
}
