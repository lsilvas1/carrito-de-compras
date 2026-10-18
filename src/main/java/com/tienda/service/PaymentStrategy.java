package com.tienda.service;
import java.math.BigDecimal;
public interface PaymentStrategy {
    String method();
    String pay(BigDecimal amount);
}
