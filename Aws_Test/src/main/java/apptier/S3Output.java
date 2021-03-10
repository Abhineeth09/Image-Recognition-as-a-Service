package apptier;
import java.io.File;
import java.io.*;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.samples.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import java.nio.file.Files;


public class S3Output {
	private static final AWSCredentials AWS_CREDENTIALS1;

	static {
        // Your accesskey and secretkey to be entered here
        AWS_CREDENTIALS1 = new BasicAWSCredentials(
               // "AKIAI62IPGPFE42FSEGA",
        		"AKIAWA6WGMZSVSR3CAB4", 
                //"M2prB9Gqz/fmW1jguEQmsnl0CWzbjLD3ym+Q80fU"
        		"ajiA78bOxgmYtclkMUmJ172ITQCE3Odhvdr6ZgJu"
        );
    }
	
	
	public static void addResponseToOutputS3(String output , String Message) 
	{
		String bucket_name = "cloudresponsebucket";
		System.out.format("Uploading to S3 bucket \n",  bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
				.withRegion(Global.REGION).build();
		try {
				s3.putObject(bucket_name, output, Message);
				//Add to the Response Queue
				SqsReadMessage.postMessageToQueue(Message, "ResponseQ");
		} catch (AmazonServiceException e) {
		System.err.println(e.getErrorMessage());
		//System.exit(1);
		}
	}
	
	public static void downloadObjectFromS3(String key_name , String bucket_name)
	{
		key_name = key_name.replaceAll("\\s+","");
		System.out.format("Downloading %sfrom S3 bucket %s...\n", key_name, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
				.withRegion(Global.REGION).build();
		
		
		try {
		   
			S3Object fetchFile = null, objectPortion = null, headerOverrideObject = null;
			fetchFile = s3.getObject(new GetObjectRequest(bucket_name, key_name));
			System.out.println("Downloading an object");
            //fullObject = s3.getObject(new GetObjectRequest(bucket_name, key_name));
			final BufferedInputStream i = new BufferedInputStream(fetchFile.getObjectContent());
			InputStream objectData = fetchFile.getObjectContent();
			Files.copy(objectData, new File("/home/ubuntu/classifier/" + key_name).toPath()); //location to local path
			objectData.close();
		} catch (AmazonServiceException e) {
		    System.err.println(e.getErrorMessage());
		    System.exit(1);
		}
		catch (FileNotFoundException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		} catch (IOException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
	}

}
	public static void getImageFromS3()
	{
		String bucket_name = "cloud-computing-project-input-bucket";
        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
        		.withRegion(Global.REGION).build();
        System.out.println("Hello there\n");
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
        	System.out.println("* " + os.getKey());
        	
        }
       
	}
	public static void downloadImageFromS3(String fileName)
	{
		
	
	}
	
}