package com.tienda;
import com.tienda.service.*;
import com.tienda.infrastructure.access.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class PatternsTest {
 @Test void allFourStrategiesAreSelectable(){var service=new PaymentService(List.of(new CashPayment(),new CreditPayment(),new DebitPayment(),new PaypalPayment()));for(String method:List.of("EFECTIVO","CREDITO","DEBITO","PAYPAL"))assertTrue(service.pay(method,BigDecimal.TEN).startsWith("DEMO-"+method));assertThrows(BusinessException.class,()->service.pay("OTRO",BigDecimal.TEN));}
 @Test void strategiesRejectNonPositiveAmounts(){assertThrows(BusinessException.class,()->new CashPayment().pay(BigDecimal.ZERO));}
 @Test void factoryProductsUseDifferentDatabaseAccounts() throws Exception {
  var admin=new ReadWriteFactory(new DatabaseAccessFactory.Credentials("jdbc:h2:mem:roles;DB_CLOSE_DELAY=-1","sa",""));
  try(var c=admin.createDataSource().getConnection();var s=c.createStatement()) {
   s.execute("CREATE TABLE demo(id INT)");s.execute("CREATE USER lector PASSWORD 'read'");s.execute("CREATE USER escritor PASSWORD 'write'");s.execute("GRANT SELECT ON demo TO lector");s.execute("GRANT INSERT, UPDATE, DELETE ON demo TO escritor");
  }
  var read=new ReadOnlyFactory(new DatabaseAccessFactory.Credentials("jdbc:h2:mem:roles","lector","read"));
  var write=new WriteOnlyFactory(new DatabaseAccessFactory.Credentials("jdbc:h2:mem:roles","escritor","write"));
  try(var c=read.createDataSource().getConnection();var s=c.createStatement()){assertDoesNotThrow(()->s.executeQuery("SELECT * FROM demo"));assertThrows(java.sql.SQLException.class,()->s.executeUpdate("INSERT INTO demo VALUES(1)"));}
  try(var c=write.createDataSource().getConnection();var s=c.createStatement()){assertDoesNotThrow(()->s.executeUpdate("INSERT INTO demo VALUES(1)"));assertThrows(java.sql.SQLException.class,()->s.executeQuery("SELECT * FROM demo"));}
  assertTrue(admin.canRead()&&admin.canWrite());assertFalse(read.canWrite());assertFalse(write.canRead());
 }
}
