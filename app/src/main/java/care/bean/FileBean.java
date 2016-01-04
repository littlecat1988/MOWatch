package care.bean;

import java.io.Serializable;


public class FileBean implements Serializable{
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private int fileSize;
    private String fileName;
    private byte[] fileContent;
    private String flieExName ;

 
 
public int getFileSize() {
	return fileSize;
}
public void setFileSize(int fileSize) {
	this.fileSize = fileSize;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public byte[] getFileContent() {
	return fileContent;
}
public void setFileContent(byte[] fileContent) {
	this.fileContent = fileContent;
}
public String getFlieExName() {
	return flieExName;
}
public void setFlieExName(String flieExName) {
	this.flieExName = flieExName;
}
}
