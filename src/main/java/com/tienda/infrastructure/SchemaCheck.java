package com.tienda.infrastructure;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
@Component
public class SchemaCheck implements CommandLineRunner {
 private final JdbcTemplate jdbc;
 public SchemaCheck(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public void run(String... args){
  jdbc.queryForList("SELECT idProducto,nombre,codigo,imagen,precio FROM producto WHERE 1=0");
  jdbc.queryForList("SELECT idShoppingCart,idProducto,codUsuario,cantidad FROM shopping_cart WHERE 1=0");
 }
}
