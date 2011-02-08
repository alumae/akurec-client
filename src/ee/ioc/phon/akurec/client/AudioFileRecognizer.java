package ee.ioc.phon.akurec.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFileRecognizer {
	
	private static Logger logger = Logger.getLogger("AudioFileRecognizer");
	private AudioInputStream audioInputStream;
    private MessageIO messageIO;
    
    private long step = 1024*16; // number of frames to process at one step 

    /**
     * Converts collection to delimeted string
     * @param <T>
     * @param objs
     * @param delimiter
     * @return
     */
	public static <T> String join(final Collection<T> objs,
			final String delimiter) {
		if (objs == null || objs.isEmpty())
			return "";
		Iterator<T> iter = objs.iterator();
		StringBuffer buffer = new StringBuffer(iter.next().toString());
		while (iter.hasNext())
			buffer.append(delimiter).append(iter.next().toString());
		return buffer.toString();
	}
    
    public AudioFileRecognizer(MessageIO messageIO, File file) throws UnsupportedAudioFileException, IOException {
    	audioInputStream = AudioSystem.getAudioInputStream(file);
    	this.messageIO = messageIO;
    }
	
	void recognize() throws IOException, InterruptedException {
		
		Runnable resultsWatcher = new Runnable() {
			public void run() {
				try {
					// this contains recognized strings
					List<String> rec = new ArrayList<String>();
					// this contains strings that can still change
					List<String> hyp = new ArrayList<String>();
					
					Message message = null;
					do {
						hyp.clear();
						message = messageIO.readMessage();
						if (message.getType() == Message.M_RECOG) {
							String recResult = new String(message.getBytes(), "ISO-8859-1");
							logger.fine("REC: " + recResult);
							parseResult(rec, hyp, recResult);
							
							System.out.println("REC: " + join(rec, " ") + " " + join(hyp, " "));							
						}
					} while (message.getType() != Message.M_RECOG_END);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Failed to read message from the recognizer process" , e);
				}
			}
			
			/**
			 * Parses M_RECOG string in the form of "part * 242 -1 <w> 243 -1 halo 280 -1 nen 382"
			 * @param rec
			 * @param hyp
			 * @param recResult     the input string
			 */
			private void parseResult(List<String> rec, List<String> hyp,
					String recResult) {
				String[] ss = recResult.split(" ");
				hyp.clear();
				if (ss[0].equals("all")) {
					// final full result
					rec.clear();
					int pos = 1;
					while (pos < ss.length - 1) {
						rec.add(ss[pos+2]);
						pos += 3;
					}
					
				} else {
					// partial result
					int pos = 1;
					boolean asteriskSeen = false;
					while (pos < ss.length - 1) {
						if (ss[pos].equals("*")) {
							rec.addAll(hyp);
							hyp.clear();
							pos += 1;
							asteriskSeen = true;
							continue;
						} else {
							hyp.add(ss[pos+2]);
							pos += 3;
						}
					}
					if (!asteriskSeen) {
						// if there was no * in the result, it was actually a recognition result, not hypothesis
						rec.addAll(hyp);
					}
				}
			}
		};
		Thread resultsThread = new Thread(resultsWatcher);
		resultsThread.start();
		
		// we read step * 2 bytes since we assume 16-bit encoding with mono channel in WAV file
		long totalLength = audioInputStream.getFrameLength();
		long leftFrames = totalLength;
		byte[] inBuffer = new byte[(int)step * 2];
		while (leftFrames > 0) {
			int readLength = (int) Math.min(step, leftFrames);
			audioInputStream.read(inBuffer, 0, readLength * 2);
			leftFrames -= readLength;
			Message message = new Message(Message.M_AUDIO, false, inBuffer, readLength * 2);
			messageIO.writeMessage(message);
		}
		
		messageIO.writeMessage(new Message(Message.M_AUDIO_END, false, inBuffer, 0));
		resultsThread.join();
	}

	public static void main(String[] args) throws IOException, InterruptedException, UnsupportedAudioFileException {
		logger.info("Hello");
		MessageIO messageIO = new MessageIO(args[0]);
		File audioFile = new File(args[1]);
		AudioFileRecognizer rec = new AudioFileRecognizer(messageIO, audioFile);
		logger.info("Starting to recognize");
		rec.recognize();
	}

}


