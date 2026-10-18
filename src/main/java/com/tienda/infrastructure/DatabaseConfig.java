package com.tienda.infrastructure;
import com.tienda.infrastructure.access.*;
import javax.sql.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
@Configuration @EnableConfigurationProperties(DataSourceProperties.class)
public class DatabaseConfig {
 @Bean DataSource dataSource(DataSourceProperties p) {
  DatabaseAccessFactory factory=new ReadWriteFactory(new DatabaseAccessFactory.Credentials(p.getUrl(),p.getUsername(),p.getPassword()));
  return factory.createDataSource();
 }
}
