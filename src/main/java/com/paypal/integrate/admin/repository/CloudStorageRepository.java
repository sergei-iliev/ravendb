package com.paypal.integrate.admin.repository;

import java.io.Writer;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class CloudStorageRepository {

	private static final String BUCKET_NAME="sapient-office-232912.appspot.com";
	

	
	public void save(Writer writer,String name){
		Storage storage = StorageOptions.getDefaultInstance().getService();
		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(BUCKET_NAME,name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo,writer.toString().getBytes());		
	}
	
//	public void save( attachment){
//		Storage storage = StorageOptions.getDefaultInstance().getService();
//
//		// Upload a blob to the newly created bucket
//		BlobId blobId = BlobId.of(BUCKET_NAME,attachment.getFileName());
//		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(attachment.getContentType()).build();
//		Blob blob = storage.create(blobInfo,attachment.getBuffer());		
//	}
	
	
	
}
