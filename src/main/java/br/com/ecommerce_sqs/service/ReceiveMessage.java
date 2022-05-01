package br.com.ecommerce_sqs.service;

import java.util.List;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.*;

public class ReceiveMessage {
    SqsClient sqsClient = Configurations.getSqsClient();
    GetQueueUrlResponse createResult = Configurations.getCreateResult();

    List<Message> messages = receiveMessages(sqsClient, createResult.queueUrl());

    public static List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        return messages;
    }
}
