package net.paypal.integrate.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import net.paypal.integrate.command.Attachment;

@Repository
public class CloudStorageRepository {

	public static final String BUCKET_NAME="sapient-office-232912.appspot.com";
	

	public void saveFile(Attachment attachment){
		Storage storage = StorageOptions.getDefaultInstance().getService();

		Bucket bucket = storage.get(BUCKET_NAME);

		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(BUCKET_NAME,attachment.getFileName());
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(attachment.getContentType()).build();
		Blob blob = storage.create(blobInfo,attachment.getBuffer());		
	}
	
	
	public void saveFile(){
		Storage storage = StorageOptions.getDefaultInstance().getService();

		// Create a bucket

		Bucket bucket = storage.get(BUCKET_NAME);

		// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(BUCKET_NAME, "This is a nice test");
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo, "This should be the content".getBytes());
		System.out.println(blob);
	}
	
}
