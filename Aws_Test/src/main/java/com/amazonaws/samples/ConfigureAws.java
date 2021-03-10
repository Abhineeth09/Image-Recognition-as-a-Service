package com.amazonaws.samples;
import com.amazonaws.samples.Global;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class ConfigureAws {
	public static final AWSCredentials AWS_CREDENTIALS;

    static {
        // Your accesskey and secretkey to be entered here
        AWS_CREDENTIALS = new BasicAWSCredentials(
               // "AKIAI62IPGPFE42FSEGA",
        		Global.ACCESSKEY, 
                //"M2prB9Gqz/fmW1jguEQmsnl0CWzbjLD3ym+Q80fU"
        		Global.SECRETKEY
        );
    }
    public static AmazonEC2 configureEc2() { 
    	AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Global.REGION)
                .build();
    	return ec2Client; 
    }
    
    public static AmazonS3 configureS3()
    {
    	final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
        		.withRegion(Global.REGION).build();
    	return s3; 
    }

    public static AmazonSQS configureSqs()
    {
    	AmazonSQS sqs = AmazonSQSClientBuilder.standard()
    			  .withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
    			  .withRegion(Global.REGION)
    			  .build();
      	return sqs;
    }
   

}
