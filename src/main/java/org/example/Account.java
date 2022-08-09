package org.example;

import javax.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne (mappedBy = "account")
    private User user;
    private Double usd = 300.00;
    private Double eur = 300.00;
    private Double uah = 300.00;

    public Double getUsd() {
        return usd;
    }

    public Double getEur() {
        return eur;
    }

    public Double getUah() {
        return uah;
    }

    public Account () {

    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public void setEur(Double eur) {
        this.eur = eur;
    }

    public void setUah(Double uah) {
        this.uah = uah;
    }

}
