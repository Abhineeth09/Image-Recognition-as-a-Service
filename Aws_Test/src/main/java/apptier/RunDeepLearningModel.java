package apptier;
import apptier.SqsReadMessage;

import com.amazonaws.services.sqs.model.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import apptier.*;
import apptier.S3Output;


// Add the the queue name and url in some constant file. 
public class RunDeepLearningModel { /* implements Runnable { 
	
	public void run(){
		// Add the Calling Function here 
		System.out.println("Starting the Process\n");
		startMain();
	}
	
	public void startMain()
	{
		while(true) 
		{
			
			int queueSize = SqsReadMessage.checkQueueSize("https://sqs.us-east-1.amazonaws.com/414376683109/TestQ");
			if( queueSize == 0 ) 
			{
				Ec2Instance.terminateInstance();
				
			}
			else
			{
				
				String sqsMessage = SqsReadMessage.readFromQueue("TestQ", 15 , 30 ) ; 
				if(sqsMessage == null ) 
				{
					continue; 
				}
				else
				{
					
				
				// Check for the queue size , if sizes 0 , then terminate the ec2 instance . 
					String predictedValue = deepLearningModel(sqsMessage);
					if(predictedValue == null) 
					{
						predictedValue = "No Prediction";
					}
					System.out.println("The Predicted value is " + predictedValue);
					// Add this predicted value to output queue and to S3
					String output = sqsMessage+ "  " + predictedValue;
					System.out.println("The output including image name and output " + output);
					S3Output.addResponseToOutputS3(sqsMessage, output );
				}
			} 
		
		}
	}*/
	public static String deepLearningModel(String imageName)
	{
		String s = null;
		String str = null; 
		imageName = imageName.replaceAll("\\s+","");
		S3Output.downloadObjectFromS3(imageName , "cloud-computing-project-input-bucket");
        try {
    
            Process p = Runtime.getRuntime().exec("python3 /home/ubuntu/classifier/image_classification.py  /home/ubuntu/classifier/" +imageName );
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                str = s; 
            }
            
            // read any errors from the attempted command
            //System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
           
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
        return str;
	}
	
	
	
	public class MyThread extends Thread {

	    public void run(){
	       System.out.println("MyThread running");
	    }
	  }
	
	
	public static void main (String [] args) 
	{
		Ec2Instance.terminateInstance();
		//RunDeepLearningModel dp = new RunDeepLearningModel();
		//Thread t1 = new Thread(dp);
		//t1.start();
		
		
		
		
	}

	
}
