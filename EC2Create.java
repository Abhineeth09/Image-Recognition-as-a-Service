package com.amazonaws.samples;

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
public class EC2Create
{
    private static final AWSCredentials AWS_CREDENTIALS;
    
    static {
        // Your accesskey and secretkey
        AWS_CREDENTIALS = new BasicAWSCredentials(
                "Your ID",
                "Your Key"
        );
    }
    public static void main(String[] args) {
        
        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();
         
        // Launch EC2 Instance -- standard linux -- ami-00ddb0e5626798373
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId("ami-0ee8cf7b8a34448a6")
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("Your Key Pair")
                .withSecurityGroupIds("Your Security Group");
 
        RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstancesRequest);
 
        Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
        System.out.println("EC2 Instance Id: " + instanceId);
 
        // Set Tags
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance.getInstanceId())
                .withTags(new Tag("Name", "Testing1"));
        ec2Client.createTags(createTagsRequest);
 
        // Start Instance
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);
 
        ec2Client.startInstances(startInstancesRequest);

    }
}