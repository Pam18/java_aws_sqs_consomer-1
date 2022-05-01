package br.com.ecommerce_sqs;

import com.google.gson.Gson;

import br.com.ecommerce_sqs.dto.InfoPedidoDTO;
import br.com.ecommerce_sqs.service.SQSService;



public class App {
    public static void main(String[] args) {
        System.out.println("Lendo mensagens ...");

        // String msm = "{'from':'transicao','to':'notaFisca','pedido':1}";

        // InfoPedidoDTO infoPedido = new Gson().fromJson(msm, InfoPedidoDTO.class);

        // System.out.println(infoPedido.getFrom());

        while(true){
        SQSService.messageReader();
        // Thread.sleep(1000); // Desabilitado por causa do Long Polling para economizar
        }
    }
}
