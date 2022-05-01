package br.com.ecommerce_sqs.service;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import br.com.ecommerce_sqs.dto.InfoPedidoDTO;

public class SQSService {
    public static void messageReader() {
        SqsClient sqsClient = Configurations.getSqsClient();
        GetQueueUrlResponse createResult = Configurations.getCreateResult();

        List<Message> messages = ReceiveMessage.receiveMessages(sqsClient, createResult.queueUrl());


        for (Message mess : messages) {

            String jsonMess = mess.body();

            InfoPedidoDTO jsonPedido = new Gson().fromJson(jsonMess, InfoPedidoDTO.class);

            String etapa = "transacao";
            String from = jsonPedido.getFrom();
            String to = jsonPedido.getTo();
            LocalDateTime now = LocalDateTime.now();

            System.out.println("Mensagem: " + jsonPedido.getFrom() + " - " + jsonPedido.getTo() + " - "
                                + etapa);
            
            if (from.equals("pedidos") && etapa.equals(to)) {
                System.out.println("[" + now + "]" + " {#Pedido" + jsonPedido.getNumPedido() + "}" + " TRANSAÇÃO APROVADA.");
                DeleteMessage.deleteMessages(sqsClient, createResult.queueUrl(), mess);
                
                jsonPedido.setFrom(etapa);
                jsonPedido.setTo("notaFiscal");
                String stringPedidoOut = new Gson().toJson(jsonPedido);
                SendMessage.sendMessage(sqsClient, createResult.queueUrl(), stringPedidoOut);
                System.out.println("MENSAGEM ENVIADA PARA A PRIMEIRA ETAPA.");
                
                jsonPedido.setTo("pedidos");
                
                String stringPedidoBack = new Gson().toJson(jsonPedido);
                SendMessage.sendMessage(sqsClient, createResult.queueUrl(), stringPedidoBack);
                System.out.println("----------> CONFIRMAÇÃO DE RECEBIMENTO.");
            }
        }

        sqsClient.close();
    }
}