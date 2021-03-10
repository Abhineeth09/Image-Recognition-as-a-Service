package com.amazonaws.samples;
import com.amazonaws.samples.Global;

import com.amazonaws.samples.ConfigureAws;
import java.util.List;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult; 
import com.amazonaws.AmazonWebServiceClient; 


public class SqsImplementation {
	static String requestQueueUrl = "https://sqs.us-east-1.amazonaws.com/414376683109/TestQ";

	static void createSQS(final String QUEUE_NAME) 
    { 
    	AmazonSQS sqs = ConfigureAws.configureSqs();
    	CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
     // Get the URL for a queue
        String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	
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
	
	static List<Message> getMessageFromQueue(final String QUEUE_NAME)
    {
    	System.out.println("\nReceive messages from SQS ");

    	AmazonSQS sqs = ConfigureAws.configureSqs();
    	String standardQueueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
    	ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(standardQueueUrl)
    			  .withWaitTimeSeconds(10)
    			  .withMaxNumberOfMessages(10);

    			List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    			// Delete the Message after reading it from the queue.
    			
    			return sqsMessages;
    	
    	
    }
	
	// Write the API to get the number of Messages in the  Queue . 
	public static int checkSizeOfTheQueue()
	{
		AmazonSQS sqs = ConfigureAws.configureSqs();
		GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(requestQueueUrl).withAttributeNames("All");
    	GetQueueAttributesResult getQueueAttributesResult = sqs.getQueueAttributes(getQueueAttributesRequest);
    	int queueSize = Integer.parseInt(getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessages"));
    	System.out.println(String.format("The number of messages on the queue: %s", queueSize));
    	System.out.println(String.format("The number of messages in flight: %s", getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessagesNotVisible")));
    	return queueSize; 
	}
	
	
}
