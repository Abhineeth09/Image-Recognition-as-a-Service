package com.amazonaws.samples;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;




public class EC2Create
{
    private static final AWSCredentials AWS_CREDENTIALS;
    
    static {
        // Your accesskey and secretkey
        AWS_CREDENTIALS = new BasicAWSCredentials(
                "",
                ""
        );
    }
    public static String createEC2() {
        
        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();
         
        // Launch EC2 Instance -- standard linux -- ami-00ddb0e5626798373
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId("ami-0ee8cf7b8a34448a6")
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("test2")
                .withSecurityGroupIds("");
 
        RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstancesRequest);
 
        Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
        //System.out.println("EC2 Instance Id: " + instanceId);
 
        // Set Tags
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance.getInstanceId())
                .withTags(new Tag("Name", "Testing1"));
        ec2Client.createTags(createTagsRequest);
        
        // Start Instance
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);
 
        ec2Client.startInstances(startInstancesRequest);
        return "EC2 Instance Id: " + instanceId;

    }
    public static void main(String[] args) {
    	//create an EC2 instance
    	//String instanceName = createEC2();
    	
    	//Read the files in the s3 bucket
    	String bucket_name = "cse546-test-images";
    	System.out.format("Objects in S3 bucket %s:\n", bucket_name);
    	final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
    	ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
    	List<S3ObjectSummary> objects = result.getObjectSummaries();
    	List filesInS3 = new ArrayList();
    	for (S3ObjectSummary os : objects) {
    		String filename = os.getKey();
    	    //System.out.println("* " + filename);
    	    filesInS3.add(filename);
    	}
    	System.out.println(filesInS3);
    	
    	//List SQS Queues
    	AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    	ListQueuesResult lq_result = sqs.listQueues();
    	System.out.println("Your SQS Queue URLs:");
    	for (String url : lq_result.getQueueUrls()) {
    	    System.out.println(url);
    	}
    	
    	//Add an elements to the SQS Queue from the s3 bucket
    	String requestQueueURL = "https://sqs.us-east-1.amazonaws.com/798924831596/requestQueue";
    	/*for(int i=0;i<filesInS3.size();i++) {
    		SendMessageRequest send_msg_request = new SendMessageRequest()
        	        .withQueueUrl(requestQueueURL)
        	        .withMessageBody(""+filesInS3.get(i))
        	        .withDelaySeconds(5);
    	}*/
    	List<String> attributeNames = new ArrayList();
    	attributeNames.add("ApproximateNumberOfMessagesVisible");
    	GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(requestQueueURL,attributeNames);
    	System.out.println("Attributes are: "+getQueueAttributesRequest.toString());
    	
    	String instanceName = createEC2();
    	/*SendMessageRequest send_msg_request = new SendMessageRequest()
    	        .withQueueUrl(requestQueueURL)
    	        .withMessageBody("hello world")
    	        .withDelaySeconds(5);*/
    	//sqs.sendMessage(send_msg_request);


    	//System.out.println(instanceName);
    }
}
