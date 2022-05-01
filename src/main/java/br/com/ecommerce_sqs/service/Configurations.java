package br.com.ecommerce_sqs.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class Configurations {
    static SqsClient getSqsClient() { 
        
        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(Authentication.getCredentials())
            .build();
        
        return sqsClient;
    }

    static GetQueueUrlRequest getUrlRequest() {
        // String awsId = System.getenv("AWS_ACCOUNT_ID");
        GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                .queueName("queue-pamela").build();
                // .queueOwnerAWSAccountId(awsId).build();

        return request;
    }

    static GetQueueUrlResponse getCreateResult() {
        GetQueueUrlResponse createResult = getSqsClient().getQueueUrl(getUrlRequest());
    
        return createResult;
    }
}
