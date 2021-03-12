package apptier;

import java.util.List;

import com.amazonaws.samples.ConfigureAws;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SqsReadMessage {
	
	public static String readFromQueue(String QueueName, int waitTime , int visibilityTimeout)
	{
	    	System.out.println("\nReceive messages from SQS ");
	    	String test = "";
	    	AmazonSQS sqs = ConfigureAws.configureSqs();
	    	//String standardQueueUrl = sqs.getQueueUrl(QueueName).getQueueUrl();
	    	String standardQueueUrl = "https://sqs.us-east-1.amazonaws.com/414376683109/TestQ";
	    	ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(standardQueueUrl)
	    			  .withWaitTimeSeconds(waitTime)
	    			  .withVisibilityTimeout(visibilityTimeout)
	    			  .withMaxNumberOfMessages(1);
	    			ReceiveMessageResult result = null; 
	    			List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();
	    			if(sqsMessages.size() == 0 )
	    			{
	    				test = null  ; 
	    			}
	    			else
	    			{
	    				System.out.println("Before the Suspected Crash ");
	    				System.out.println("The number of Messages received : * * ***    " + sqsMessages.size());
	    				for(Message msg : sqsMessages)
	    				{
	    					System.out.println("This is the Message which is being deleted *  " + msg.getReceiptHandle());
	    					System.out.println("After the crash ");
	    					
	    					//sqs.deleteMessage(standardQueueUrl,result.getMessages().get(0).getReceiptHandle())
	    					test = sqsMessages.get(0).getBody();
	    					System.out.println("The Message is " + test );
	    					sqs.deleteMessage(standardQueueUrl, msg.getReceiptHandle());
	    				}
	    			}
					
	    		return test;
	    	
	 
	}
	static void postMessageToQueue(String image_name, final String QUEUE_NAME)
    {
    	AmazonSQS sqs = ConfigureAws.configureSqs();
    	String standardQueueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
    	.withQueueUrl(standardQueueUrl)
    	.withMessageBody(image_name)
    	.withDelaySeconds(30);
    	
    	
    	sqs.sendMessage(sendMessageStandardQueue);
    }
	
	public static int checkQueueSize(String requestQueueUrl)
	{
		AmazonSQS sqs = ConfigureAws.configureSqs();
		GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(requestQueueUrl).withAttributeNames("All");
    	GetQueueAttributesResult getQueueAttributesResult = sqs.getQueueAttributes(getQueueAttributesRequest);
    	int queueSize = Integer.parseInt(getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessages"));
    	return queueSize;
	}

}
