package com.luee.wally.admin.repository;

import java.io.Writer;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.luee.wally.command.Attachment;
import com.luee.wally.utils.Utilities;

public class CloudStorageRepository {
	

	
	public void save(Writer writer,String name){
		Storage storage = StorageOptions.getDefaultInstance().getService();
		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(Utilities.getBucketName(),name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo,writer.toString().getBytes());		
	}
	
	public void saveFile(Attachment attachment){
		Storage storage = StorageOptions.getDefaultInstance().getService();
		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(Utilities.getBucketName(),attachment.getFileName());
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(attachment.getContentType()).build();
		Blob blob = storage.create(blobInfo,attachment.getBuffer());		
	}
	
	
	
}
