package care.utils;

import com.mina.BaseMessage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import care.bean.FileBean;

public class BaseMessageDecoder  implements MessageDecoder {
	private AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
	/**
	 * 是否可以解码
	 * */
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		Context context = (Context) session.getAttribute(CONTEXT); 
		try{
			if(context == null || !context.init ){ 
				context = new Context();
				context.dataType = in.getInt(); 
				if(context.dataType == BeanUtil.UPLOAD_FILE){
					context.strFileNameLength = in.getInt();
					context.strFileExNameLength = in.getInt();
					context.strMessageExInfoLength = in.getInt();
					context.byteStrFileName = new byte[context.strFileNameLength];
					context.byteStrFileExName = new byte[context.strFileExNameLength];
					context.byteStrMessageExInfo = new byte[context.strMessageExInfoLength];
					context.fileSize = in.getInt();
					context.byteFile = new byte[context.fileSize];
					session.setAttribute(CONTEXT, context);
					return MessageDecoderResult.OK;
				}else{
					return MessageDecoderResult.NOT_OK;
				}
			}else{
				if(context.dataType == BeanUtil.UPLOAD_FILE){
					return MessageDecoderResult.OK;
				}else{
					return MessageDecoderResult.NOT_OK;
				}
			}
		}catch(Exception e){
			return MessageDecoderResult.NOT_OK;
		}

	}
	/**
	 * 解码
	 * */
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput outPut) throws Exception {
		Context context = (Context) session.getAttribute(CONTEXT);
		if(!context.init){
			context.init = true;
			in.getInt();
			in.getInt();
			in.getInt();
			in.getInt();
			in.getInt();
		}
		byte[] byteFile = context.byteFile;
		int count = context.count;
		int index = 0;
		while(in.hasRemaining()){
			byte b = in.get();
			index++;
			if(context.readtype.equals("ReadFileName")){
			    if(context.strFileNameLength > 0){
			        context.byteStrFileName[count] = b;
			    }else{
                    context.readtype = "ReadFileExName";
			    }

				if(count == context.strFileNameLength-1){
					context.fileName = new String(context.byteStrFileName, BeanUtil.charset);
					System.out.println(context.fileName);
					count = -1;
					context.readtype = "ReadFileExName";
				}
			}

			if(context.readtype.equals("ReadFileExName") && count != -1){
			    if(context.strFileExNameLength > 0){
			        context.byteStrFileExName[count] = b;
			    }else{
                    context.readtype = "ReadMessageExInfo";
                }

				if(count == context.strFileExNameLength-1){
					context.fileExName = new String(context.byteStrFileExName, BeanUtil.charset);
					count = -1;
					context.readtype = "ReadMessageExInfo";
				}
			}

			if(context.readtype.equals("ReadMessageExInfo") && count != -1){
			    if(context.strMessageExInfoLength > 0 && count < context.strMessageExInfoLength){
			        context.byteStrMessageExInfo[count] = b;
			    }else{
                    context.readtype = "ReadFileData";
                }

                if(count == context.strMessageExInfoLength-1){
                    context.messageExInfo = new String(context.byteStrMessageExInfo, BeanUtil.charset);
                    count = -1;
                    context.readtype = "ReadFileData"; 
                } 
            }
			if(count == 7120){
				System.out.print("sdfsd");
			}
			if(context.readtype.equals("ReadFileData") && count == context.fileSize){ 
				BaseMessage message = new BaseMessage();
				message.setDataType(context.dataType);
				message.setMessageExInfo(context.messageExInfo);
				FileBean bean = new FileBean();
				bean.setFileName(context.fileName);
				bean.setFileSize(context.fileSize);
				bean.setFileContent(context.byteFile);
				bean.setFlieExName(context.fileExName);
				message.setData(bean);
				outPut.write(message);
				context.reset();
				session.setAttribute(CONTEXT, context);
				count = -1;
				index--;
				in.position(index);
				if(MessageDecoderResult.OK.equals(decodable(session,in))){
					context = (Context) session.getAttribute(CONTEXT);
					byteFile = context.byteFile;
					count++;
					continue;
				}else {
					return MessageDecoderResult.OK;
				}
			}
			
			if(context.readtype.equals("ReadFileData") && count != -1 && context.fileSize>0){
				byteFile[count] = b;
			}
			count++;
		}
		context.count = count;
		session.setAttribute(CONTEXT, context);
		if(context.count == context.fileSize || context.fileSize == 0){ 
			BaseMessage message = new BaseMessage();
			message.setDataType(context.dataType); 
			message.setMessageExInfo(context.messageExInfo);
			FileBean bean = new FileBean();
			bean.setFileName(context.fileName);  
			bean.setFileSize(context.fileSize);
			bean.setFileContent(context.byteFile);
			bean.setFlieExName(context.fileExName);
			message.setData(bean);
			outPut.write(message);
			context.reset();
			session.setAttribute(CONTEXT, context);
		}
		return MessageDecoderResult.OK;
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput outPut)
			throws Exception {
	}
	
	private class Context{
		public int dataType;
		public byte[] byteFile;
		public int count;
		public int strFileNameLength;
		public int strFileExNameLength;
		public int strMessageExInfoLength;
		public int fileSize;
		public byte[] byteStrFileName;
		public byte[] byteStrFileExName;
		public byte[] byteStrMessageExInfo;
		public String fileName;
		public String fileExName;
		public String messageExInfo;
		public String readtype="ReadFileName";
		public boolean init = false;
		
		public void reset(){
			dataType = 1;
			byteFile = null;
			count = 0;
			strFileNameLength = 0;
			strFileExNameLength = 0;
			strMessageExInfoLength=0;
			fileSize = 0;
			byteStrFileName = null;
			fileName = null;
			fileExName = null;
			readtype="ReadFileName";
			init = false;
		}
	}
}
