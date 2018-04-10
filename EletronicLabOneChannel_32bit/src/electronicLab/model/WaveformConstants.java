package electronicLab.model;

public class WaveformConstants {

	public final static int WAVETYPE_SIN = 1;
	public final static int WAVETYPE_TRIANGLE = 2;
	public final static int WAVETYPE_SQUARE = 3;
	public final static int WAVETYPE_SERRA = 4;
	public final static int WAVETYPE_SERRA_INV = 5;
	public final static int WAVETYPE_USERDEFINED = 6;

	public final static int WAVETYPE_MAX = WAVETYPE_USERDEFINED;
	public final static int WAVETYPE_MIN = WAVETYPE_SIN;
	public final static float FREQ_MAX = 1000000f;
	public final static float FREQ_STEP = 1f;
	public final static float FREQ_MIN = 1f;
	public final static float ALTITUDE_MAX = 10f;
	public final static float ALTITUDE_STEP = 0.1f;
	public final static float ALTITUDE_MIN = 0.1f;
	public final static float PHASE_STEP = 1f;
	public final static float PHASE_MAX = 360f - PHASE_STEP;
	public final static float PHASE_MIN = 0f;
	
	public final static int DUTY_MAX = 99;
	public final static int DUTY_STEP = 1;
	public final static int DUTY_MIN = 1;
	public final static float OFFSET_MAX = 5000f;
	public final static float OFFSET_STEP = 100f;
	public final static float OFFSET_MIN = -5000f;
	
	public final static int CHANNEL_MAX = 0;
	public final static int CHANNEL_MIN = 0;
	
	//Setting relating to MCU
	public final static int BUFFER_LENGTH = 1024; //buffer size in bytes
	public final static double TIMER_OSC_MAX = 48000000.0; //48MHz osc
	public final static int TIMER_CNT_MAX = 65535; //timer counter maximum number
	public final static int SAMPLING_NUM_MIN = 8; // the least number of samples in a frequency
	public final static int DAC_RESOLUTION_MAX = 256;
	

}
