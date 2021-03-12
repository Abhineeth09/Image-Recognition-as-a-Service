<?php
require '/usr/local/bin/vendor/autoload.php';

use Aws\Sqs\SqsClient;

//A function to send message to SQS queue
function send_to_sqs($message){

//Connect to SQS
$client = SqsClient::factory(array(

'credentials' => array (
'key' => "AKIAIABO5PPY6WPXMDHA", //use your AWS key here
'secret' => "XHrxzjhdUYNKwXnus27I8VFSKcE3yVfncNXzBQ/R" //use your AWS secret here
),

'region' => 'us-east-1', //replace it with your region
'version' => 'latest'
));

$client->sendMessage(array(
'QueueUrl' => 'https://sqs.us-east-1.amazonaws.com/414376683109/TestQ', //your queue url goes here
'MessageBody' => $message,
));

$filename = $argv[1];

for ($x = 2; $x <= $argc; $x+=1) {
  $filename = $filename . ' ' . $argv[$x];
}

send_to_sqs($filename);
?>