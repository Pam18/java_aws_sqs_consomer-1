package br.com.ecommerce_sqs.service;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;

import br.com.ecommerce_sqs.dto.InfoPedidoDTO;

public class SQSService {
    public static void messageReader() {
        AwsCredentialsProvider credentialsProvider = new AwsCredentialsProvider() {
            @Override
            public AwsCredentials resolveCredentials() {
                return new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return System.getenv("AWS_ACCESS_KEY");
                    }

                    @Override
                    public String secretAccessKey() {
                        return System.getenv("AWS_SECRET_KEY");
                    }
                };
            }
        };

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();

        String awsId = System.getenv("AWS_ACCOUNT_ID");
        GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                .queueName("queue_poc_ecommerce")
                .queueOwnerAWSAccountId(awsId).build();
        GetQueueUrlResponse createResult = sqsClient.getQueueUrl(request);

        List<Message> messages = receiveMessages(sqsClient, createResult.queueUrl());

        for (Message mess : messages) {

            String jsonMess = mess.body();

            System.out.println(mess.body());

            InfoPedidoDTO jsonPedido = new Gson().fromJson(jsonMess, InfoPedidoDTO.class);

            String etapa = "transacao";
            String from = jsonPedido.getFrom();
            String to = jsonPedido.getTo();
            LocalDateTime now = LocalDateTime.now();

            System.out.println("Mensagem: " + jsonPedido.getFrom() + " - " + jsonPedido.getTo() + " - "
                                + etapa);
            
            if (from.equals("pedidos") && etapa.equals(to)) {
                System.out.println("[" + now + "]" + " {#Pedido" + jsonPedido.getNumPedido() + "}" + "TRANSAÇÃO APROVADA.");
                deleteMessages(sqsClient, createResult.queueUrl(), mess);
                
                jsonPedido.setFrom(etapa);
                jsonPedido.setTo("notaFiscal");
                String stringPedidoOut = new Gson().toJson(jsonPedido);
                sendMessage(sqsClient, createResult.queueUrl(), stringPedidoOut);
                System.out.println("MENSAGEM ENVIADA PARA A PRÓXIMA ETAPA.");

                jsonPedido.setTo("pedidos");

                String stringPedidoBack = new Gson().toJson(jsonPedido);
                sendMessage(sqsClient, createResult.queueUrl(), stringPedidoBack);
                System.out.println("MENSAGEM ENVIADA PARA A PRIMEIRA ETAPA.");
            }
        }

        sqsClient.close();
    }

    public static List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        return messages;
}

    public static void deleteMessages(SqsClient sqsClient, String queueUrl, Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    public static void sendMessage(SqsClient sqsClient, String queueUrl, String message) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }
}