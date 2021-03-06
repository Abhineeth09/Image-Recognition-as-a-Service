package com.amazonaws.samples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceNetworkInterfaceSpecification;
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
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;




public class createAwsInstance
{
    private static final AWSCredentials AWS_CREDENTIALS;

    static {
        // Your accesskey and secretkey to be entered here
        AWS_CREDENTIALS = new BasicAWSCredentials(
                "AKIAI62IPGPFE42FSEGA",
                "M2prB9Gqz/fmW1jguEQmsnl0CWzbjLD3ym+Q80fU"
        );
    }

    static void StartEc2Instance(String imageId, String instanceName)
    {
        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();

        // Launch EC2 Instance -- standard linux -- ami-00ddb0e5626798373 Mostly for web tier
        // App Tier = ami-0ee8cf7b8a34448a6
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(imageId)
                .withInstanceType("t2.micro") // This just tells  how many vCpus would be used .
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("my_key_pair")
                .withSecurityGroupIds("sg-795a3174");
        RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstancesRequest);

        Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
        System.out.println("EC2 Instance Id: " + instanceId);

        // Set Tags
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance.getInstanceId())
                .withTags(new Tag("Name", instanceName));
        ec2Client.createTags(createTagsRequest);

        // Start Instance
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);

        ec2Client.startInstances(startInstancesRequest);
    }
    static void getImageFromS3()
    {
        String bucket_name = "inputcloud";
        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
        		.withRegion(Regions.US_EAST_1).build();
        System.out.println("Hello there\n");
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
        	System.out.println("* " + os.getKey());
        	postMessageToQueue(os.getKey(), "RequestQueue");
        }
    }
    public static AmazonSQS configureSQS() 
    {
    	AmazonSQS sqs = AmazonSQSClientBuilder.standard()
  			  .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
  			  .withRegion(Regions.US_EAST_1)
  			  .build();
    	return sqs;
    	
    }
    static void createSQS(final String QUEUE_NAME) 
    { 
    	AmazonSQS sqs = configureSQS();
    	CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
     // Get the URL for a queue
        String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	
    }
    
    // This will post the Message to the  Request Queue in the Format : 0_cat.png : Which is the name of the file 
    static void postMessageToQueue(String image_name, final String QUEUE_NAME)
    {
    	AmazonSQS sqs = configureSQS();
    	String standardQueueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
    	.withQueueUrl(standardQueueUrl)
    	.withMessageBody(image_name)
    	.withDelaySeconds(30);
    	
    	
    	sqs.sendMessage(sendMessageStandardQueue);
    }
    static List<Message> getMessageFromQueue(final String QUEUE_NAME)
    {
    	System.out.println("\nReceive messages from SQS ");

    	AmazonSQS sqs = configureSQS();
    	String standardQueueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(standardQueueUrl)
    			  .withWaitTimeSeconds(10)
    			  .withMaxNumberOfMessages(10);

    			List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    			
    			return sqsMessages;
    	
    	
    }
    static void runDeepLearningModel(String msg) throws IOException
    {
    	// Get the Link of the S3 bucket and then the message from the Queue 
    	System.out.println("Hello from run DeepLearningModel \n");
    	// Fill The Response Queue Here.
    	Runtime rt = Runtime.getRuntime();
    	Process pr = rt.exec("python image_classification.py  " + msg);
    	
    	postMessageToQueue(msg +  " Cat ", "ResponseQueue");
    	
    }
    
    public static void main(String[] args) {
        // Start the Web Tier
        //Launch EC2 Instance -- standard linux -- ami-00ddb0e5626798373 Mostly for web tier
        // App Tier = ami-0ee8cf7b8a34448a6

        //StartEc2Instance("ami-00ddb0e5626798373", "App-Tier"); // Starting the Web tier

        // Starting the Web Tier
        //StartEc2Instance("ami-0ee8cf7b8a34448a6", "Web-Tier")i; // Starting the App Tier
    	//createSQS("RequestQueue");
    	//createSQS("ResponseQueue");
    	//getImageFromS3();
    	List<Message> sqsMessage = getMessageFromQueue("RequestQueue");
    	System.out.println("Before Printitng the Message");
    	System.out.println(sqsMessage);
    	for (Message msg : sqsMessage) {
    		System.out.println(msg.getBody());
    	// call the python Deep Learning model here 
    		try {
                runDeepLearningModel(msg.getBody());
            } catch (IOException e) {
                 {
                	 System.err.println("Caught IOException: " +  e.getMessage());
                    
                }
            }
    	
    	List<Message> sqsMessage1 = getMessageFromQueue("ResponseQueue");
    	System.out.println("Before Printitng the  Response Message");
    	System.out.println(sqsMessage1);
    	for (Message msg1 : sqsMessage1) 
    		System.out.println(msg1.getBody());
    	
    	System.out.println("Done for Response Queue ! ");

       // }

    }
}
}



