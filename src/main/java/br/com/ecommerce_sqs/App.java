package br.com.ecommerce_sqs;

import br.com.ecommerce_sqs.service.SQSService;


public class App {
    public static void main(String[] args) {
        System.out.println("Lendo mensagens ...");

        while(true){
        SQSService.messageReader();
        // Thread.sleep(1000); // Desabilitado por causa do Long Polling para economizar
        }
    }
}
