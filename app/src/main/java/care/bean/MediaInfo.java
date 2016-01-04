package care.bean;

public class MediaInfo {

	private int id;            //删除id
	
	private String mediaType;  //媒体类型
	
	private String mediaContent;  //媒体内容
	
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	
	public void setMediaType(String mediaType){
		this.mediaType = mediaType;
	}
	public String getMediaType(){
		return this.mediaType;
	}
	
	public void setMediaContent(String mediaContent){
		this.mediaContent = mediaContent;
	}
	public String getMediaContent(){
		return this.mediaContent;
	}
}
