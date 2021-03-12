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

import apptier.RunDeepLearningModel;

import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.samples.Controller;
import com.amazonaws.samples.*; 


//  This file would contain the Main of the application
// I will receivce the request from the Web Server. 
// Storing the Image in S3 bucket is done by Justin. 
// After getting the request from the web server ,   I would store the image name in the SQS.
// The Controller script would be running in  background , checking the size of the queue . 

/*

public class createAwsInstance
{
//    private static final AWSCredentials AWS_CREDENTIALS;

  //  static {
    //    // Your accesskey and secretkey to be entered here
      //  AWS_CREDENTIALS = new BasicAWSCredentials(
        //        "AKIAI62IPGPFE42FSEGA",
          //      "M2prB9Gqz/fmW1jguEQmsnl0CWzbjLD3ym+Q80fU"
        //);
    //}
*/

public class createAwsInstance  implements Runnable { 
	
	public void run(){
		// Add the Calling Function here 
		System.out.println("Starting the  Web Tier Process\n");
		startMain();
	
	}
	public void startMain()
	{
		while (true)
		{
			Controller.autoScaling();
		}
	}
    
	
	static void StartEc2Instance(String imageId, String instanceName)
    {
    	// Ideally Image Id should be the App Tier AMi Id . 
       // AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
         //       .withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
           //     .withRegion(Global.REGION)
             //   .build();
    	
    	AmazonEC2 ec2Client = ConfigureAws.configureEc2();

        // Launch EC2 Instance -- standard linux -- ami-00ddb0e5626798373 Mostly for web tier
        // App Tier = ami-0ee8cf7b8a34448a6
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(imageId)
                .withInstanceType("t2.micro") // This just tells  how many vCpus would be used .
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("ayushi_key_pair")
                .withSecurityGroupIds(Global.SECURITYGROUPID);
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
	
		public static void main(String[] args) {
			//StartEc2Instance("ami-0ee8cf7b8a34448a6", "App_Tier_Template");
			//StartEc2Instance("ami-00ddb0e5626798373", "Web-Tier");
			// The Next APi would go in the Web server Stuff 
			//S3Implemntation.getImageFromS3();
			createAwsInstance as = new createAwsInstance();
			Thread t1 = new Thread(as);
			t1.start();
			
			
		}
	}



