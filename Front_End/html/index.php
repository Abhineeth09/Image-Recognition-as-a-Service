<!DOCTYPE html>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
<head>
</head>

<form method='post' action='' enctype='multipart/form-data'>
 <input type="file" name="file[]" id="file" multiple>

 <input type='submit' name='submit' value='Upload Files'>
</form>

<p id="submittedoutput">

</p>

<script language="javascript" type="text/javascript">
function loadlink(){
    $.get('check_response.php',function (data) {
         content = data;
         $('#output').append(content);
         $('#output').append("<br />");
    });
}

loadlink(); // This will run on page load
setInterval(function(){
    loadlink() // this will run after every second
}, 1000);
</script>

<?php
use Aws\Sqs\SqsClient;

require '/usr/local/bin/vendor/autoload.php';

if(isset($_POST['submit'])){

 // Count number of files
 $filecount = count($_FILES['file']['name']);

 // Looping all files
 for($i=0;$i<$filecount;$i++){
  $filename = $_FILES['file']['name'][$i];

  // $unique_identifier = uniqid();
  // $filename = $unique_identifier . '_' . $filename;

  // Connect to S3

  $s3 = new Aws\S3\S3Client([
    'region'  => 'us-east-1',
    'version' => 'latest',
    'credentials' => [
            'key'    => "AKIAIABO5PPY6WPXMDHA",
            'secret' => "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",
    ]]);

  $result = $s3->putObject([
    'Bucket' => 'cloud-computing-project-input-bucket',
    'Key'    => $filename,
    'SourceFile' => $_FILES['file']['tmp_name'][$i]
  ]);

  // Deposit message with SQS
  $sqs_execute = 'php ../alt_test.php ';
  $sqs_execute .= $filename;

  shell_exec($sqs_execute);
  }
  if ($filecount > 0){
    echo "Files submitted!";
  }
}



?>
<div id="output">

</div>