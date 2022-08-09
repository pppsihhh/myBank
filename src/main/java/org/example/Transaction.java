package org.example;

import javax.persistence.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String sender;
    private Double send;
    private String valSend;

    private String recipient;
    private Double receive;
    private String valRecipient;

    public Transaction () {}

    public Transaction(String sender, Double send, String valSend, String recipient, Double receive, String valRecipient) {
        this.sender = sender;
        this.send = send;
        this.valSend = valSend;
        this.recipient = recipient;
        this.receive = receive;
        this.valRecipient = valRecipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Double getSend() {
        return send;
    }

    public void setSend(Double send) {
        this.send = send;
    }

    public String getValSend() {
        return valSend;
    }

    public void setValSend(String valSend) {
        this.valSend = valSend;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Double getReceive() {
        return receive;
    }

    public void setReceive(Double receive) {
        this.receive = receive;
    }

    public String getValRecipient() {
        return valRecipient;
    }

    public void setValRecipient(String valRecipient) {
        this.valRecipient = valRecipient;
    }
}
