# Image Recognition as a Service

_Justin Colyar, Abhineeth Mishra, Ayushi Shekhar_

# **1. Problem statement**

The project aims to build an Elastic Web Application that provides Image Recognition as a Service by returning labels to user images. The Front-End of the application accepts images from a user and returns responses that describe the image. The Back End of the application is used to run the deep learning model that outputs the prediction for every input image. The application is built by using AWS IaaS tools such as EC2, S3 and SQS; which provide the necessary infrastructure to build and scale the application. The auto-scaling aspect of the application is to be implemented by only using the mentioned IaaS tools.

# **2. Design and implementation**

## **2.1 Architecture**

![alt text](https://github.com/Abhineeth09/Image-Recognition-as-a-Service/blob/main/architecture.png)


[Overall Design]

The front end of our application was set up on an Amazon CentOS EC2 instance to support a LAMP (Linux, Apache, MySQL, PHP) stack web server. The front-end website application was designed in HTML, PHP, and Javascript/Jquery. The website utilized a simple HTML form submission that allows the user to upload multiple images at a time. These images are then processed in PHP in &quot;index.php&quot; upon submission where the program will insert the image into our Input Bucket in S3 and put the image name in our SQS request queue. Moreover, the javascript/jquery portion of &quot;index.php&quot; is responsible for scheduling &quot;check\_response.php&quot; every few seconds so that it will query the SQS response queue and check for any available results to be displayed back to the user. In this way, the front end is asynchronous and operates independently of the application.

The Back-End (or App Tier) of the application uses EC2 instances to serve the Deep-Learning model. Once the predictions of the Deep Learning model are received the output is sent to the SQS Response Queue which will be fetched by the front end. The number of EC2 instances that serve user requests is based on the number of messages in the SQS request queue. For every 10 messages, a new EC2 instance is created to serve the users and provide elasticity. E.g. If there are 50 messages in the queue, 5 EC2 instances would serve the model in parallel. The App Tier Controller is built into the App Tier&#39;s EC2 instance. The instance shuts down if it cannot fetch any message from the request queue (the queue is empty). More EC2 instances are created by continuously monitoring the request queue size.

The SQS response and request queues are meant to decouple the web and the app tier by serving as a buffer for messages between the two. This ensures that the application as a whole is more resilient to failure as even if one of the two components were to fail, the other would be able to carry out any functionality and the messages will not be lost but rather stored. If a part was rebooted, then the system would be able to continue functioning as normal.

The S3 input bucket serves as a way of storing the client&#39;s input and make sure it persists. The app tier accesses and creates personal copies of the S3 image objects for processes, again ensuring that the original image remains unchanged and stored. Moreover, the S3 response bucket also serves as a means for storing and persisting the output as a named pair for the image and the prediction result.

## **2.2 Autoscaling**

The Architecture of the application decouples the Web Tier and the App Tier which allows for scaling just the App Tier as the load of user requests increases. We only scale the App Tier since only the computation required at the App Tier is computationally expensive. Two SQS queues connect the Web Tier and the App Tier - The Request Queue and the Response Queue.

The controller script (Java) continuously monitors the SQS Request size and creates more instances if required. Another script (Java) on the App Tier checks if there are messages on the queue that are available to be fetched, and if there are no messages in the Request Queue, the instance will shut down.

The Controller Logic is as follows -

requiredInstances = requestQueueLength

numberOfMessagesPerInstance = n (10 in our case)

if requiredInstances != 0:

requiredInstances = min(Ceil(requiredInstances/numberOfMessagesPerInstance),19)

If currentInstances \&lt; requiredInstances:

createNewInstance()

The App Tier (Back-End) Logic is as follows -

if requestQueueLength==0:

stopCurrentInstance()

Each request in the request queue consists of an image name that is input by the user. The size of this queue is used to determine the number of instances that need to be created. For every 10 messages in the queue, we have one App Instance that should be processing the SQS messages. The number 10 was determined by testing the speed of responses by trial and error. Automatic scaling of the App instances ensures consistent performance even though the number of requests keeps increasing. In our testing, 20 images took about 2.5 minutes to run and 100 images took about 3 minutes.

# **3. Testing and evaluation**

To test the front-end implementation, many image files were manually uploaded as a group and submitted. If successfully uploaded, then the SQS request queue would contain the image names and the S3 Input bucket would contain the uploaded images. Different numbers of images were uploaded at a time, multiple image submissions were completed in a row, and multiple submissions from different clients were tested to ensure front-end durability and correctness. The Controller Java script is run on the Web Tier to create more EC2 instances based on the Request Queue size. The script was manually tested to verify if new App Instances are created as more requests arrive.

The Back-End (App Tier) was manually tested for the following conditions -

- The App Tier receives messages from the SQS Request Queue, and the Visibility Timeout is set so that multiple App Tier Instances do not process the same request.
- The App Tier shuts down if there are no messages in the Request Queue (Downscaling).
- The Deep Learning model provides correct predictions to our inputs.
- The prediction result from the App Tier is added to the SQS Response Queue.
- The predictions are stored on the S3 Bucket in the specified format.

The evaluation of the application was done by repeatedly giving a different number of images to the application and timing the amount of time required to process all the inputs and return the output back to the user. The goal was to reduce this time, and parameters such as the queue visibility timeout, were tweaked to improve this time. We were able to reduce the time by 50% or more by tweaking the mentioned parameters.

#


# **4. Code**

Explain in detail the functionality of every program included in the submission zip file.

Explain in detail how to install your programs and how to run them.

&quot;EC2Instance.java&quot;: Contains utility functions for the EC2 instance such as terminating the instance.

&quot;RunDeepLearningModel.java&quot;: Contains the logic for the App Tier. It takes care of two things - Getting the predictions to the SQS Response Queue and shutting down the instance if there are no messages to fetch.

&quot;RunDeepLearningModel.sh&quot;: Is the bash script that should run automatically when the EC2 instance starts. This script invokes the Java Application that serves the Deep Learning Model.

&quot;S3Output.java&quot;: Contains utility functions that help us work with S3 data. Functions like downloadObjectFromS3, addResponseToOutputS3 and so on are in this file.

&quot;SqsReadMessage.java&quot;: Contains utility functions like checkQueueSize, readFromQueue and postMessageToQueue.

&quot;check\_response.php&quot;: This script queries the response queue for any available messages. If there are messages, the script will retrieve that message, echo it, and delete it from the response queue. This file is not meant to be run individually, but rather as a supporting php script meant to be scheduled by index.php. As such, the file must be in the same directory as index.php and PHP must be installed.

&quot;Index.php&quot;: This script is the main website file and provides the user a form to upload multiple images at a time. When the user successfully submits images, the website confirms the submission and uploads the file to the input S3 bucket. At the same time, index.php will also call alt\_test.php which will put the uploaded image names in the SQS request queue. Moreover, index.php also schedules check\_response to be run every few seconds and prints any messages that might have been received in the response queue. In order to run index.php properly, the file must be run with a LAMP stack installed on the server and be placed in /var/www/html/.

&quot;Alt\_test.php&quot;: This program takes the name of an image as a command-line argument and puts that image name in the SQS request queue. This file is not meant to be run individually, but instead to be called b index.php. In order for this file to be correctly called and run, it must be placed in /var/www/ (or the outer directory of index.php) and requires PHP to be installed.

Java 15 or later would be required to run this application.
