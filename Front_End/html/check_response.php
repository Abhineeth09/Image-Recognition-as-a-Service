<?php

require '/usr/local/bin/vendor/autoload.php';

use Aws\Sqs\SqsClient;

$client = SqsClient::factory(array(
                'credentials' => array (
                        'key' => "AKIAIABO5PPY6WPXMDHA", //use your AWS key here
                        'secret' => "XHrxzjhdUYNKwXnus27I8VFSKcE3yVfncNXzBQ/R" //use your AWS secret here
                ),

                'region' => 'us-east-1', //replace it with your region
                'version' => 'latest'
                ));
$result2 = $client->receiveMessage(array(
        'MessageAttributeNames' => ['All'],
        'QueueUrl' => 'https://sqs.us-east-1.amazonaws.com/414376683109/ResponseQ', // REQUIRED
        'WaitTimeSeconds' => 20,
        ));

if (!empty($result2->get('Messages'))) {
        echo $result2->get('Messages')[0]['Body'];
        $result2 = $client->deleteMessage([
                'QueueUrl' => 'https://sqs.us-east-1.amazonaws.com/414376683109/ResponseQ', // REQUIRED
                'ReceiptHandle' => $result2->get('Messages')[0]['ReceiptHandle'] // REQUIRED
                ]);
                echo "\r\n";
        }

?>
