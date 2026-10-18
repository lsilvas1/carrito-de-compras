package com.tienda.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public final class Api {
 private Api() {}
 public record ProductInput(@NotBlank @Size(max=255) String code,@NotBlank @Size(max=255) String name,
 @NotNull @DecimalMin("0.01") @DecimalMax("99999999.99") @Digits(integer=8,fraction=2) BigDecimal price,
 @NotBlank @Pattern(regexp="/?images/product/[a-zA-Z0-9._-]+") String image) {}
 public record ProductView(Integer id,String code,String name,BigDecimal price,String image) {}
 public record AddItem(@NotNull @Positive Integer productId,@Min(1) @Max(999) int quantity) {}
 public record Quantity(@Min(1) @Max(999) int quantity) {}
 public record Checkout(@NotBlank @Pattern(regexp="EFECTIVO|CREDITO|DEBITO|PAYPAL") String method) {}
 public record ItemView(Integer productId,String name,BigDecimal price,int quantity,BigDecimal subtotal) {}
 public record CartView(List<ItemView> items,BigDecimal total) {}
 public record PaymentReceipt(Instant createdAt,BigDecimal total,String paymentMethod,String reference,List<ItemView> lines) {}
}
