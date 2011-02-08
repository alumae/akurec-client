package ee.ioc.phon.akurec.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


public class MessageIO {
	private static Logger logger = Logger.getLogger("MessageIO");
	
	private DataOutputStream out;
	private DataInputStream in;
	private Process process;

    public static int swapInteger(int value) {
        return
            ( ( ( value >> 0 ) & 0xff ) << 24 ) +
            ( ( ( value >> 8 ) & 0xff ) << 16 ) +
            ( ( ( value >> 16 ) & 0xff ) << 8 ) +
            ( ( ( value >> 24 ) & 0xff ) << 0 );
    }

	
	
	/** 
	 * Starts the given process and waits for the M_READY message 
	 **/
	public MessageIO(String... command) throws IOException {
		ProcessBuilder probuilder = new ProcessBuilder(command);
		process = probuilder.start();
		in = new DataInputStream(process.getInputStream());
		out = new DataOutputStream(process.getOutputStream());
		Message msg = readMessage();
		if (msg.getType() == Message.M_READY) {
			return;
		} else {
			throw new IOException("Expected M_READY message (" + Message.M_READY + ") but received " + msg.getType());
		}
	}
	
	public void writeMessage(Message messageIn) throws IOException {
		byte[] messageBytes = messageIn.getBytes();
		
		byte[] message = new byte[6 + messageIn.getLength()];
		int totalLength = message.length;
		// seems that we have to swap endianness
		out.writeInt(swapInteger(totalLength));
		out.writeByte(messageIn.getType());
		out.writeBoolean(messageIn.isUrgent());
		out.write(messageIn.getBytes(), 0, messageIn.getLength());
		out.flush();
	}
	
	public Message readMessage() throws IOException {
		int messageLength = swapInteger(in.readInt());
		int type = in.readByte();
		logger.finest("Got message of type " + type + ", length = " + messageLength);
		boolean urgent = in.readBoolean();
		byte [] messageData = new byte[messageLength - 6];
		in.readFully(messageData);		
		return new Message(type, urgent, messageData, messageData.length);
	}
	
	private void destroy() {
		process.destroy();
	}
	
}
