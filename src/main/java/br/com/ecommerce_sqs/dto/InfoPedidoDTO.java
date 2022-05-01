package br.com.ecommerce_sqs.dto;

public class InfoPedidoDTO {
    private String from;
    private String to;
    private Integer numPedido;

    public InfoPedidoDTO() {    
    }

    public InfoPedidoDTO(String from, String to, Integer numPedido) {
        this.from = from;
        this.to = to;
        this.numPedido = numPedido;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(Integer numPedido) {
        this.numPedido = numPedido;
    }

    @Override
    public String toString() {
        return "{from: " + from + ", to: " + to + ", numPedido: " + numPedido + "}";
    }
}
