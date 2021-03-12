package apptier;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.samples.ConfigureAws;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;


public class Ec2Instance {

	
	public static void terminateInstance()
	{
		// Call this function in  down scaling the instances. 
		String myInstanceId = EC2MetadataUtils.getInstanceId();
		final AmazonEC2 ec2 = ConfigureAws.configureEc2();
		AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(ConfigureAws.AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();


		System.out.println("the instance id is " + myInstanceId);
		TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest()
                .withInstanceIds(myInstanceId);
		ec2Client.terminateInstances(terminateInstancesRequest)
                .getTerminatingInstances()
                .get(0)
                .getPreviousState()
                .getName();
        System.out.println("The Instance is terminated with id: "+myInstanceId);

		
	}
	
}
