package com.amazonaws.samples;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Implemntation {
	
	static void getImageFromS3()
    {
        String bucket_name = "inputcloud";
        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
        		.withRegion(Global.REGION).build();
        System.out.println("Hello there\n");
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
        	System.out.println("* " + os.getKey());
        	SqsImplementation.postMessageToQueue(os.getKey(), "RequestQueue");
        	// Check for the duplicate entries 
        	
        }
    }

}
