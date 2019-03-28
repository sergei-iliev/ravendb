package net.paypal.integrate.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;


public class Attachment {

	private String fileName;
	private String contentType;
	private byte[] buffer;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getBuffer() {
		return buffer;
	}
	
	public void readFromStream(InputStream in)throws IOException{
		try{
		   buffer = IOUtils.toByteArray(in);
           //Files.copy(in, Paths.get("D:\\u" + fileName),StandardCopyOption.REPLACE_EXISTING);           
		}finally{
			in.close();
		}
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
