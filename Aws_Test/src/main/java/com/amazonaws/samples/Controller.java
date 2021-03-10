package com.amazonaws.samples;
import com.amazonaws.samples.SqsImplementation;
import com.amazonaws.samples.createAppInstance;

public class Controller {

	// This Function would have the Controller Logic to Scale in/out the instances .
	private static SqsImplementation sqs;
	private static createAppInstance ec2; 
	public static void autoScaling()
	{
		int noOfMsgs = SqsImplementation.checkSizeOfTheQueue(); //Return the number of msgs in the queue. 
		int noOfMaxInstances = Global.MAXRUNNINGINSTANCES;  
		int noOfRunningInstances = createAppInstance.getNumberOfInstances();
		System.out.println("The number of instances is : " + noOfRunningInstances);
		//  returns the EC2 instances associated with the AWS Account. So always minus 1 to get the number of app instances; 
		int appInstances = noOfRunningInstances - 1; 	
		if( noOfMsgs > 0 && noOfMsgs > appInstances)
		{
			int t = Global.MAXRUNNINGINSTANCES - appInstances; 
			if( t>0 )
			{
				int t1 = noOfMsgs - appInstances; 
				if( t1 >= t ) 
				{ 
					createAppInstance.createEc2Instance(t);
				}
				else 
				{
					createAppInstance.createEc2Instance(t1); 
					
				}
				
			}
		}
			
		
	}
	
}
