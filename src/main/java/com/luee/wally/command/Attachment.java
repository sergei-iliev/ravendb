package com.luee.wally.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.IOUtils;
import java.io.*;

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
	
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public void readFromStream(InputStream in)throws IOException{
		try{
		   buffer = IOUtils.toByteArray(in);
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
	
	public void readFromStringWriter(StringWriter sw)throws IOException{
		  try{   
			buffer=sw.toString().getBytes();         
		  }finally{
			sw.close();
		  }
		}
}
