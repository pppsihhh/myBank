package org.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Rate {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Integer id;

    private String test = "last";
    private Double usd = 39.0;
    private Double eur = 40.0;
    private Double uah = 1.0;

    public Rate () {}

    public Double getUsd() {
        return usd;
    }

    public Double getEur() {
        return eur;
    }

    public Double getUah() {
        return uah;
    }

    public void setTest(String test) {
        this.test = test;
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
