package com.tienda.service;
import com.tienda.dto.Api.*;
import com.tienda.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
@Service @Transactional
public class CartService {
 private final CartRepository carts;private final ProductRepository products;private final PaymentService payments;
 public CartService(CartRepository carts,ProductRepository products,PaymentService payments){
  this.carts=carts;this.products=products;this.payments=payments;
 }
 public String open(){return UUID.randomUUID().toString();
 }
 private void check(long q){
  if(q<1||q>999)throw new BusinessException(422,"Cantidad debe estar entre 1 y 999");
 }
 private com.tienda.domain.Product product(int id){
  var p=products.lock(id).orElseThrow(()->new BusinessException(404,"Producto inexistente"));
  if(p.price()==null||p.price().signum()<=0)throw new BusinessException(409,"Producto con precio inválido");return p;
 }
 @Transactional(readOnly=true) public CartView get(String owner)
 {var items=carts.items(owner);
  return new CartView(items,items.stream().map(ItemView::subtotal).reduce(new BigDecimal("0.00"),BigDecimal::add));
 }
 public CartView add(String owner,Integer id,int quantity){
  check(quantity);product(id);
  long total=carts.quantity(owner,id)+quantity;
  check(total);
  carts.set(owner,id,(int)total);return get(owner);
 }
 public CartView update(String owner,Integer id,int quantity){check(quantity);
  product(id);
  if(carts.quantity(owner,id)==0)throw new BusinessException(404,"Producto fuera del carrito");carts.set(owner,id,quantity);
  return get(owner);
 }
 public void remove(String owner,Integer id){
  if(carts.remove(owner,id)==0)throw new BusinessException(404,"Producto fuera del carrito");
 }
 public void clear(String owner){carts.clear(owner);
 }
 public PaymentReceipt checkout(String owner,String method)
 {
  CartView cart=get(owner);if(cart.items().isEmpty())throw new BusinessException(409,"El carrito está vacío");
  // Calcular con los precios de la lectura bloqueada, evitando una instantánea anterior.
  var lockedLines=new ArrayList<ItemView>();
  for(var item:cart.items()){
   var p=product(item.productId());var price=p.price().setScale(2,java.math.RoundingMode.HALF_UP);
   lockedLines.add(new ItemView(p.id(),p.name(),price,item.quantity(),price.multiply(BigDecimal.valueOf(item.quantity()))));
  }
  cart=new CartView(List.copyOf(lockedLines),lockedLines.stream().map(ItemView::subtotal).reduce(new BigDecimal("0.00"),BigDecimal::add));
  String reference=payments.pay(method,cart.total());carts.clear(owner);
  return new PaymentReceipt(Instant.now(),cart.total(),method,reference,cart.items());
 }
}
