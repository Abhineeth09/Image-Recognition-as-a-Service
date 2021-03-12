package com.amazonaws.samples;
import java.util.Base64;
import java.util.List;

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
			// Get the number of  running + Pending) 
			String instanceName = "App-Tier-" + String.valueOf(getNumberOfInstances());
			String userd = "Content-Type: multipart/mixed; boundary=\"//\"\r\n"
					+ "MIME-Version: 1.0\r\n"
					+ "\r\n"
					+ "--//\r\n"
					+ "Content-Type: text/cloud-config; charset=\"us-ascii\"\r\n"
					+ "MIME-Version: 1.0\r\n"
					+ "Content-Transfer-Encoding: 7bit\r\n"
					+ "Content-Disposition: attachment; filename=\"cloud-config.txt\"\r\n"
					+ "\r\n"
					+ "#cloud-config\r\n"
					+ "cloud_final_modules:\r\n"
					+ "- [scripts-user, always]\r\n"
					+ "\r\n"
					+ "--//\r\n"
					+ "Content-Type: text/x-shellscript; charset=\"us-ascii\"\r\n"
					+ "MIME-Version: 1.0\r\n"
					+ "Content-Transfer-Encoding: 7bit\r\n"
					+ "Content-Disposition: attachment; filename=\"userdata.txt\"\r\n"
					+ "\r\n"
					+ "#!/bin/bash\r\n"
					+ "sh /home/ubuntu/RunDeepLearningModel.sh\r\n"
					+"/bin/echo \"Hello World\" >> /tmp/testfile.txt\r\n"
					+ ""
					+ "--//\r\n"
					+ "";
			System.out.println("The Name of the App Instance is " + instanceName);
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(Global.IMAGEID)
                .withInstanceType("t2.micro") // This just tells  how many vCpus would be used .
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName("ayushi_key_pair")
                //.withUserData(Base64.getEncoder().encodeToString("ls > /tmp/test.txt".getBytes()))
                .withUserData(Base64.getEncoder().encodeToString(userd.getBytes()))
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
                	
                
                	
                	
                	//stem.out.println("the State of the Instance : * " + instance.getState().getName());
                	//if((instance.getState().getName()).equalsIgnoreCase("running")  || (instance.getState().getName()).equalsIgnoreCase("pending"))
                	//{
                		//System.out.println("***** Inside the if conditon to check the number of instances ****  ");
                		//numInstances++; 
                	//}
                	System.out.printf(
                            "Found instance with id %s, " +
                            "AMI %s, " +
                            "type %s, " +
                            "state %s " +
                            "and monitoring state %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                	if(instance.getState().getName().equals("running")|| instance.getState().getName().equals("pending"))
                	{
                		numInstances++;
                	}

                	
                }
                	
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null ) { 
            	done = true; 
            }
		}
             
		System.out.println("The value for NumInstance is " + numInstances);
		return numInstances; 
	}
	/*
	public static int getNumberOfInstances()
	{
		AmazonEC2 ec2 = ConfigureAws.configureEc2();
		DescribeInstanceStatusRequest describeRequest = new DescribeInstanceStatusRequest();
		describeRequest.setIncludeAllInstances(true);
		DescribeInstanceStatusResult describeInstances = ec2.describeInstanceStatus(
                new DescribeInstanceStatusRequest());
		List<InstanceStatus> instanceStatusList = describeInstances.getInstanceStatuses();
		Integer countOfRunningInstances = 0;
		for (InstanceStatus instanceStatus : instanceStatusList) {
			InstanceState instanceState = instanceStatus.getInstanceState();
			if (instanceState.getName().equals(InstanceStateName.Running.toString()) ||instanceState.getName().equals(InstanceStateName.Pending.toString())) {
				countOfRunningInstances++;
			}
		}
	
		return countOfRunningInstances;
	}*/
	
	public static void terminateEc2Instance(String instanceId)
	{
		AmazonEC2 ec2 = ConfigureAws.configureEc2();
		String instanceName; 
		TerminateInstancesRequest ec2Request  = new TerminateInstancesRequest().withInstanceIds(instanceId);
		ec2.terminateInstances(ec2Request);
		
		
	}
	
	
}
