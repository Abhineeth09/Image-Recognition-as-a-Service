package com.amazonaws.samples;
import com.amazonaws.samples.SqsImplementation;
import com.amazonaws.samples.createAppInstance;

public class Controller {

	// This Function would have the Controller Logic to Scale in/out the instances .
	private static SqsImplementation sqs;
	private static createAppInstance ec2; 
	public static void autoScaling()
	{
		float requiredInstances = SqsImplementation.checkSizeOfTheQueue(); //Return the number of msgs in the queue.
		int requiredLoad = 10 ; 
		System.out.println("The size of the queue is  = " +  requiredInstances);
		//int noOfMaxInstances = Global.MAXRUNNINGINSTANCES;  
		int noOfRunningInstances = createAppInstance.getNumberOfInstances()-3;
		System.out.println(" **** The number of instances is **  : " + noOfRunningInstances);
		//  returns the EC2 instances associated with the AWS Account. So always minus 1 to get the number of app instances; 
		//int appInstances = noOfRunningInstances - 1; 	
		//System.out.println();
		if( requiredInstances > 0 )
		{
			requiredInstances = Math.min((int)(Math.ceil(requiredInstances/requiredLoad)), Global.MAXRUNNINGINSTANCES );
			System.out.println("The Number of required Instances are *****   " + requiredInstances);
			 
			if( noOfRunningInstances < requiredInstances)
				createAppInstance.createEc2Instance((int)requiredInstances);
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
	
}
