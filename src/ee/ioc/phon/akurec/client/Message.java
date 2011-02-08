package ee.ioc.phon.akurec.client;

import java.io.InputStream;
import java.io.OutputStream;

public class Message {

	// Message types, from online-demo/msg.hh
	// 
	public static int M_AUDIO = 0;            // gui -> rec
	public static int M_AUDIO_END  = 1;        // gui -> rec
	public static int M_RESET = 2;            // gui -> rec
	
	public static int M_PROBS = 3;            // rec <- ac, rec -> dec
	public static int M_PROBS_END = 4;        // rec -> dec
	
	public static int M_READY = 5;            // gui <- rec, rec <- dec, rec <- ac
	public static int M_RECOG = 6;            // gui <- rec, rec <- dec
	public static int M_RECOG_END = 7;        // gui <- rec, rec <- dec
	
	    // beam float
	    // lm_stalce float
	public static int M_DECODER_SETTING = 8;  // gui -> rec -> dec
	public static int M_DECODER_PAUSE = 9;    // gui -> rec -> dec
	public static int M_DECODER_UNPAUSE = 10;  // gui -> rec -> dec
	
	public static int M_ADAPT_ON = 11;         // gui -> rec -> dec
	public static int M_ADAPT_OFF = 12;        // gui -> rec -> dec
	public static int M_ADAPT_RESET = 13;      // gui -> rec -> dec
	public static int M_STATE_HISTORY = 14;    // rec <- dec
	
	public static int M_DEBUG = 15;            // gui -> rec
	public static int M_MESSAGE = 16;          // gui <- rec <- dec

	

	private int type;
	private boolean urgent;
	private byte[] bytes;
	private int length;
	
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Message(int type, boolean urgent, byte[] bytes, int length) {
		super();
		this.type = type;
		this.urgent = urgent;
		this.bytes = bytes;
		this.length = length;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isUrgent() {
		return urgent;
	}
	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	
	
	

	
}
