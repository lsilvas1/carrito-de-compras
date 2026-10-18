package com.tienda.repository;
import com.tienda.domain.Product;
import com.tienda.dto.Api.ProductInput;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import java.sql.Statement;
import java.util.*;
@Repository
public class ProductRepository {
 private final JdbcTemplate jdbc;
 public ProductRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
 private final RowMapper<Product> mapper=(r,n)->new Product(r.getInt("idProducto"),r.getString("codigo"),r.getString("nombre"),r.getBigDecimal("precio"),r.getString("imagen"));
 public List<Product> all(){return jdbc.query("SELECT idProducto,codigo,nombre,precio,imagen FROM producto ORDER BY idProducto",mapper);}
 public Optional<Product> get(int id){return jdbc.query("SELECT idProducto,codigo,nombre,precio,imagen FROM producto WHERE idProducto=?",mapper,id).stream().findFirst();}
 public Optional<Product> lock(int id){return jdbc.query("SELECT idProducto,codigo,nombre,precio,imagen FROM producto WHERE idProducto=? FOR UPDATE",mapper,id).stream().findFirst();}
 public int create(ProductInput p){var keys=new GeneratedKeyHolder();jdbc.update(c->{var s=c.prepareStatement("INSERT INTO producto(codigo,nombre,precio,imagen) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);s.setString(1,p.code().trim());s.setString(2,p.name().trim());s.setBigDecimal(3,p.price());s.setString(4,p.image());return s;},keys);return Objects.requireNonNull(keys.getKey()).intValue();}
 public void update(int id,ProductInput p){jdbc.update("UPDATE producto SET codigo=?,nombre=?,precio=?,imagen=? WHERE idProducto=?",p.code().trim(),p.name().trim(),p.price(),p.image(),id);}
 public void delete(int id){jdbc.update("DELETE FROM producto WHERE idProducto=?",id);}
}
