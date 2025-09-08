package com.eaglebank.api.dto;

public class TransactionRequest {
    private String type;
    private Double amount;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
