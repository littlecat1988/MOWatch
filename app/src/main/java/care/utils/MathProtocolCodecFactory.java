package care.utils;

import com.mina.BaseMessage;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

public class MathProtocolCodecFactory extends DemuxingProtocolCodecFactory{
	
	public MathProtocolCodecFactory(boolean server){
			super.addMessageDecoder(BaseMessageDecoder.class);
			super.addMessageEncoder(BaseMessage.class, BaseMessageEncoder.class);
	}
}
