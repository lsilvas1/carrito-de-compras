package com.tienda.service;
import org.springframework.stereotype.Service;
import java.util.*;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class PaymentService {
 private final Map<String,PaymentStrategy> strategies;
 public PaymentService(List<PaymentStrategy> strategies) {this.strategies=strategies.stream().collect(Collectors.toMap(PaymentStrategy::method,Function.identity()));}
 public String pay(String method,BigDecimal amount) {
  PaymentStrategy strategy=strategies.get(method);
  if(strategy==null) throw new BusinessException(422,"Método de pago inválido");
  return strategy.pay(amount);
 }
}
