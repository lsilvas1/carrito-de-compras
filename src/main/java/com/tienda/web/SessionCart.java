package com.tienda.web;
import com.tienda.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
@Component
public class SessionCart {
 private final CartService carts;
 public SessionCart(CartService carts) {this.carts=carts;}
 public String id(HttpSession session) {
  synchronized(session) {
   String id=(String)session.getAttribute("cartId");
   if(id==null) {id=carts.open();session.setAttribute("cartId",id);}return id;
  }
 }
}
