package com.tienda.web;
import com.tienda.dto.Api.*;
import com.tienda.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.function.Function;
@RestController @RequestMapping("/api/cart")
public class CartController {
 private final CartService carts;private final SessionCart sessions;
 public CartController(CartService carts,SessionCart sessions){this.carts=carts;this.sessions=sessions;}
 // Serializa solicitudes de la misma sesión, incluyendo vaciar y comprar.
 private <T>T own(HttpSession s,Function<String,T> action){synchronized(s){return action.apply(sessions.id(s));}}
 @GetMapping public CartView get(HttpSession s){return own(s,carts::get);}
 @PostMapping("/items") public CartView add(HttpSession s,@Valid @RequestBody AddItem p){return own(s,id->carts.add(id,p.productId(),p.quantity()));}
 @PutMapping("/items/{productId}") public CartView update(HttpSession s,@PathVariable Integer productId,@Valid @RequestBody Quantity p){return own(s,id->carts.update(id,productId,p.quantity()));}
 @DeleteMapping("/items/{productId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remove(HttpSession s,@PathVariable Integer productId){own(s,id->{carts.remove(id,productId);return null;});}
 @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT) public void clear(HttpSession s){own(s,id->{carts.clear(id);return null;});}
 @PostMapping("/checkout") public PaymentReceipt checkout(HttpSession s,@Valid @RequestBody Checkout p){return own(s,id->carts.checkout(id,p.method()));}
}
