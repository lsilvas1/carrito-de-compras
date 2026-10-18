package com.tienda.service;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;
/** Simulador académico: no realiza cargos ni solicita datos financieros. */
@Component
public class PaypalPayment implements PaymentStrategy {
 public String method() {return "PAYPAL";}
 public String pay(BigDecimal amount) {
  if(amount.signum()<=0) throw new BusinessException(422,"Importe inválido");
  return "DEMO-PAYPAL-"+UUID.randomUUID();
 }
}
