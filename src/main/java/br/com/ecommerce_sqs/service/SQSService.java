package br.com.ecommerce_sqs.service;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.LocalDate;
import java.util.List;

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

            String stringToObject = mess.body();
            InfoPedidoDTO pedido = new InfoPedidoDTO();

            System.out.println(stringToObject);

            stringToObject = stringToObject.replace("from", "");
            stringToObject = stringToObject.replace("to", "");
            stringToObject = stringToObject.replace("numPedido", "");
            stringToObject = stringToObject.substring(5, stringToObject.lastIndexOf("}"));

            pedido.setFrom(stringToObject.substring(1, (stringToObject.indexOf(",") - 1)));
            pedido.setTo(stringToObject.substring((stringToObject.indexOf(":") + 2),
                    (stringToObject.lastIndexOf(",") - 1)));
            pedido.setNumPedido(Integer.valueOf(stringToObject.substring(stringToObject.lastIndexOf(":") + 1)));

            System.out.println(pedido.getFrom());

            String etapa = "transacao";
            String from = pedido.getFrom();
            String to = pedido.getTo();

            // // String etapa = "transicao";
            // // String from = "messg";

            // // String msm = "{'from':'transicao','to':'notaFisca','pedido':1}";

            // System.out.println(mess.body());

            // InfoPedidoDTO infoPedido = new Gson().fromJson(mess.body(),
            // InfoPedidoDTO.class);

            // System.out.println(infoPedido.getFrom());

            System.out.println("Mensagem: " + from + to + etapa);
            if (from.equals("pedidos") && etapa.equals(to)) {
                System.out.println("O pedido foi enviado para aprovação de crédito");
                deleteMessages(sqsClient, createResult.queueUrl(), mess);

                InfoPedidoDTO pedidoSaida = new InfoPedidoDTO(etapa, "notaFiscal", pedido.getNumPedido());
                sendMessage(sqsClient, createResult.queueUrl(), pedidoSaida.toString());

                pedidoSaida.setTo("pedidos");
                sendMessage(sqsClient, createResult.queueUrl(), pedidoSaida.toString());
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