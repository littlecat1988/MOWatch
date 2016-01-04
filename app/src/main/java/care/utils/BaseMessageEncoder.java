package care.utils;

import com.mina.BaseMessage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import care.bean.FileBean;

public class BaseMessageEncoder implements MessageEncoder<BaseMessage> {

	public void encode(IoSession session, BaseMessage message,ProtocolEncoderOutput outPut) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(1024*1024*50); 
		buffer.putInt(message.getDataType());
		FileBean bean = (FileBean) message.getData();
		byte[] byteStrFileName = getBankStr(bean.getFileName()).getBytes(BeanUtil.charset);
		byte[] byteStrFlieExName = getBankStr(bean.getFlieExName()).getBytes(BeanUtil.charset);
		byte[] byteStrMessageExInfo = getBankStr(message.getMessageExInfo()).getBytes(BeanUtil.charset);
		buffer.putInt(byteStrFileName.length);
		buffer.putInt(byteStrFlieExName.length);
		buffer.putInt(byteStrMessageExInfo.length);
		buffer.putInt(bean.getFileSize());
		buffer.put(byteStrFileName);
		buffer.put(byteStrFlieExName);
		buffer.put(byteStrMessageExInfo);
		if(bean.getFileContent() != null){
		    buffer.put(bean.getFileContent());
		}
		
		buffer.flip();
		outPut.write(buffer);
	}
	
	private String getBankStr(String srcStr){
	    if(srcStr == null){
	        return "";
	    }else{
	        return srcStr;
	    }
	}

}
