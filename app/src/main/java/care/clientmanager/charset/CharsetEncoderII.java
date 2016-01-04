package care.clientmanager.charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 * @ClassName CharsetEncoderII
 * @Description character encode
 *
 */
public class CharsetEncoderII implements ProtocolEncoder {
	
	// code format
	private Charset mCharset;
	// line break character
	private String mDelimiter; 

	/**
	 * fun name: CharsetEncoderII
	 * 
	 * @description construction method 
	 * @param  charset: coded format
	 * 		   delimiter: line break character
	 * @return
	 */
	public CharsetEncoderII(Charset charset, String delimiter) {
		this.mCharset = charset;
		this.mDelimiter = delimiter;
	}

	/**
	 * fun name: encode
	 * 
	 * @description construction method 
	 * @return
	 */
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		//if don't specify line break character,use default delimiter "\r\n"
		if (mDelimiter == null || "".equals(mDelimiter)) { 
			mDelimiter = "\r\n";
		}
		if (mCharset == null) {
			mCharset = Charset.forName("utf-8");
		}

		String value = message.toString();
		IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
		// real data
		buf.putString(value, mCharset.newEncoder()); 
		// line break character 
		buf.putString(mDelimiter, mCharset.newEncoder()); 
		buf.flip();
		out.write(buf);
	}

	public void dispose(IoSession session) throws Exception {
	}

}
