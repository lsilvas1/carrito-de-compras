package com.tienda;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.service.*;
import com.tienda.dto.Api.*;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @AutoConfigureMockMvc
class OriginalSchemaTest {
 @Autowired JdbcTemplate jdbc;@Autowired CartService carts;@Autowired ProductService products;@Autowired MockMvc mvc;@Autowired ObjectMapper json;
 @BeforeEach void reset(){
  jdbc.update("DELETE FROM shopping_cart");
  jdbc.update("DELETE FROM producto");
  jdbc.update("INSERT INTO producto(idProducto,nombre,codigo,imagen,precio) VALUES(1,'Cámara','CAM01','images/product/camera.jpg',671.21)");}
 @Test void usesExactlyTwoOriginalTables() {
  var names=jdbc.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'",String.class);
  assertEquals(2,names.size());
  assertTrue(names.contains("PRODUCTO"));
  assertTrue(names.contains("SHOPPING_CART"));
  assertEquals(new BigDecimal("671.21"),products.get(1).price());
 }
 @Test void cartWritesOriginalColumnsAndMergesDuplicates(){
  String id=carts.open();jdbc.update("INSERT INTO shopping_cart(idProducto,codUsuario,cantidad) VALUES(1,?,1)",id);
  jdbc.update("INSERT INTO shopping_cart(idProducto,codUsuario,cantidad) VALUES(1,?,2)",id);
  var c=carts.add(id,1,1);
  assertEquals(new BigDecimal("2684.84"),c.total());
  assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM shopping_cart WHERE codUsuario=?",Integer.class,id));
  assertEquals(4,jdbc.queryForObject("SELECT cantidad FROM shopping_cart WHERE codUsuario=?",Integer.class,id));
 }
 @Test void cartIsolatedPerOwner(){String a=carts.open(),b=carts.open();carts.add(a,1,2);
  assertTrue(carts.get(b).items().isEmpty());assertThrows(BusinessException.class,()->carts.remove(b,1));
  assertEquals(2,carts.get(a).items().get(0).quantity());
 }
 @Test void invalidPaymentLeavesCartUnchanged(){String id=carts.open();carts.add(id,1,1);
  assertThrows(BusinessException.class,()->carts.checkout(id,"INVALIDO"));assertEquals(1,carts.get(id).items().size());
 }
 @Test void checkoutIsReceiptOnlyAndClearsCart(){String id=carts.open();
  carts.add(id,1,2);var r=carts.checkout(id,"PAYPAL");
  assertEquals(new BigDecimal("1342.42"),r.total());
  assertTrue(r.reference().startsWith("DEMO-PAYPAL"));
  assertTrue(carts.get(id).items().isEmpty());
  assertThrows(BusinessException.class,()->carts.checkout(id,"EFECTIVO"));
 }
 @Test void productCrudWorksWithDoublePriceAndOriginalNames(){
  var p=products.create(new ProductInput("NUEVO","Nuevo",new BigDecimal("10.25"),"/images/product/watch.jpg"));
  products.update(p.id(),new ProductInput("NUEVO","Editado",new BigDecimal("11.50"),"/images/product/watch.jpg"));
  assertEquals("Editado",jdbc.queryForObject("SELECT nombre FROM producto WHERE idProducto=?",String.class,p.id()));
  assertEquals(new BigDecimal("11.50"),products.get(p.id()).price());
  products.delete(p.id());assertThrows(BusinessException.class,()->products.get(p.id()));
 }
 @Test void completeHttpFlowAndQuantityValidation()throws Exception{
  var s=new MockHttpSession();
  String t=json.readTree(mvc.perform(get("/api/csrf").session(s)).andReturn().getResponse().getContentAsString()).get("token").asText();
  mvc.perform(post("/api/cart/items").session(s).header("X-CSRF-TOKEN",t).contentType("application/json").content("{\"productId\":1,\"quantity\":2}")).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(1342.42));
  mvc.perform(post("/api/cart/items").session(s).header("X-CSRF-TOKEN",t).contentType("application/json").content("{\"productId\":1,\"quantity\":0}")).andExpect(status().isBadRequest());
  mvc.perform(put("/api/cart/items/1").session(s).header("X-CSRF-TOKEN",t).contentType("application/json").content("{\"quantity\":1}")).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(671.21));
  mvc.perform(post("/api/cart/checkout").session(s).header("X-CSRF-TOKEN",t).contentType("application/json").content("{\"method\":\"EFECTIVO\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.reference").exists());
  mvc.perform(get("/api/cart").session(s)).andExpect(jsonPath("$.items").isEmpty());
 }
 @Test void csrfAndAdminStillRequired()throws Exception{mvc.perform(post("/api/cart/items").contentType("application/json").content("{}")).andExpect(status().isForbidden());var s=new MockHttpSession();
  String t=json.readTree(mvc.perform(get("/api/csrf").session(s)).andReturn().getResponse().getContentAsString()).get("token").asText();
  mvc.perform(delete("/api/products/1").session(s).header("X-CSRF-TOKEN",t)).andExpect(status().isUnauthorized());}
}
