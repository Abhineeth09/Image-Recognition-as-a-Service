package com.amazonaws.samples;
import java.util.Base64;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.UserData;
import com.amazonaws.samples.ConfigureAws;

import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.samples.Global;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceNetworkInterfaceSpecification;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.CreateTagsRequest;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.*;
 
import com.amazonaws.services.ec2.model.Reservation;
public class createAppInstance {

	// this class would instantiate the app instances based on the load 
	// Image id can be a global variable; 
	public static void createEc2Instance(int maxInstances) 
	{
		//ring instanceName = "App-Instance";
		// MaxInstances is the number of instances that should be started. This is checked against the SQS Size .  
		AmazonEC2 ec2 = ConfigureAws.configureEc2();
		// This  Function would be called from the controller logic after checking the size of the Queue . 
		for(int i = 1 ; i <= maxInstances ; i ++ ) 
		{
			String instanceName = "App-Tier-" + String.valueOf(i);
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(Global.IMAGEID)
                .withInstanceType("t2.micro") // This just tells  how many vCpus would be used .
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("ayushi_key_pair")
                .withUserData(Base64.getEncoder().encodeToString("sh /home/ubuntu/RunDeepLearningModel.sh".getBytes()))
                .withSecurityGroupIds(Global.SECURITYGROUPID);
			RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
			Instance instance = runInstancesResult.getReservation().getInstances().get(0);
			String instanceId = instance.getInstanceId();
			System.out.println("EC2 Instance Id: " + instanceId);
        
        // Set Tag
			CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance.getInstanceId())
                .withTags(new Tag("Name", instanceName));
			ec2.createTags(createTagsRequest);
			
		}
   
	}
	public static int getNumberOfInstances()
	{
		// This returns the number of Ec2 instances  associated with the AWS Account . 
		AmazonEC2 ec2 = ConfigureAws.configureEc2();
		int numInstances = 0 ; 
		boolean done = false; 
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                	System.out.println("the State of the Insatnce is : " + instance.getState().getName() );
                	if(instance.getState().getName() == "running")
                		numInstances = numInstances+ 1;
                	
                }
                	
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null ) { 
            	done = true; 
            }
		}
                	
		return numInstances; 
	}
	public static void terminateEc2Instance(String instanceId)
	{
		AmazonEC2 ec2 = ConfigureAws.configureEc2();
		String instanceName; 
		TerminateInstancesRequest ec2Request  = new TerminateInstancesRequest().withInstanceIds(instanceId);
		ec2.terminateInstances(ec2Request);
		
		
	}
	
	
}
