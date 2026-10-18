package com.tienda.infrastructure.access;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
public abstract class DatabaseAccessFactory {
 public record Credentials(String url,String username,String password) {}
 private final Credentials credentials;
 protected DatabaseAccessFactory(Credentials credentials) {this.credentials=credentials;}
 protected DataSource build() {return new DriverManagerDataSource(credentials.url(),credentials.username(),credentials.password());}
 public abstract DataSource createDataSource();
 public abstract boolean canRead();
 public abstract boolean canWrite();
}
