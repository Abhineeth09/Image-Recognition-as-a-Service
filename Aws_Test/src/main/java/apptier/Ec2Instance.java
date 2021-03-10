package apptier;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.util.EC2MetadataUtils;

public class Ec2Instance {

	public static void terminateInstance()
	{
		// Call this function in  down scaling the instances. 
		String myInstanceId = EC2MetadataUtils.getInstanceId();
		TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(myInstanceId);
		
	}
}
