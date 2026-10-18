package com.tienda.web;
import com.tienda.dto.Api.*;
import com.tienda.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductService products;
 public ProductController(ProductService products) {this.products=products;}
 @GetMapping public List<ProductView> all() {return products.all();}
 @GetMapping("/{id}") public ProductView get(@PathVariable Integer id) {return products.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductView create(@Valid @RequestBody ProductInput p) {return products.create(p);}
 @PutMapping("/{id}") public ProductView update(@PathVariable Integer id,@Valid @RequestBody ProductInput p) {return products.update(id,p);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id) {products.delete(id);}
}
