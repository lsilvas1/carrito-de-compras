package com.tienda.repository;
import com.tienda.dto.Api.ItemView;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.tienda.service.BusinessException;
import java.math.*;
import java.util.List;
@Repository
public class CartRepository {
 private final JdbcTemplate jdbc;
 public CartRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public List<ItemView> items(String owner){return jdbc.query("SELECT p.idProducto,p.nombre,p.precio,SUM(sc.cantidad) AS cantidad FROM shopping_cart sc JOIN producto p ON p.idProducto=sc.idProducto WHERE sc.codUsuario=? GROUP BY p.idProducto,p.nombre,p.precio ORDER BY p.idProducto",(r,n)->{
  BigDecimal price=r.getBigDecimal("precio");long quantity=r.getLong("cantidad");
  if(price==null||price.signum()<=0||quantity<1||quantity>999)throw new BusinessException(409,"Datos inválidos en el carrito original");
  price=price.setScale(2,RoundingMode.HALF_UP);return new ItemView(r.getInt("idProducto"),r.getString("nombre"),price,(int)quantity,price.multiply(BigDecimal.valueOf(quantity)));
 },owner);}
 public long quantity(String owner,int product){Long q=jdbc.queryForObject("SELECT COALESCE(SUM(cantidad),0) FROM shopping_cart WHERE codUsuario=? AND idProducto=?",Long.class,owner,product);return q==null?0:q;}
 public void set(String owner,int product,int quantity){
  // El esquema original permite duplicados: consolida las líneas de ese dueño/producto.
  jdbc.update("DELETE FROM shopping_cart WHERE codUsuario=? AND idProducto=?",owner,product);
  jdbc.update("INSERT INTO shopping_cart(idProducto,codUsuario,cantidad) VALUES(?,?,?)",product,owner,quantity);
 }
 public int remove(String owner,int product){return jdbc.update("DELETE FROM shopping_cart WHERE codUsuario=? AND idProducto=?",owner,product);}
 public void clear(String owner){jdbc.update("DELETE FROM shopping_cart WHERE codUsuario=?",owner);}
}
