package care.clientmanager.charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * @ClassName CharsetCodecFactory
 * 
 * @description  character Encode/Decode/EncodingFilter
 *               
 */
public class CharsetCodecFactory implements ProtocolCodecFactory {
	
	// coded format
	private Charset mCharset; 
	// text separate
	private String mDelimiter; 

	public CharsetCodecFactory(Charset charset, String delimiter) {
		this.mCharset = charset;
		this.mDelimiter = delimiter;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new CharsetDecoderII(mCharset, mDelimiter);
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new CharsetEncoderII(mCharset, mDelimiter);
	}

}
