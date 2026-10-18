package com.tienda.service;
import com.tienda.domain.Product;
import com.tienda.dto.Api.*;
import com.tienda.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.RoundingMode;
@Service @Transactional
public class ProductService {
 private final ProductRepository products;
 public ProductService(ProductRepository products){this.products=products;}
 private ProductView view(Product p){return new ProductView(p.id(),p.code(),p.name(),p.price()==null?null:p.price().setScale(2,RoundingMode.HALF_UP),p.image());}
 @Transactional(readOnly=true) public List<ProductView> all(){return products.all().stream().map(this::view).toList();}
 @Transactional(readOnly=true) public ProductView get(Integer id){return view(products.get(id).orElseThrow(()->new BusinessException(404,"Producto inexistente")));}
 public ProductView create(ProductInput p){return get(products.create(p));}
 public ProductView update(Integer id,ProductInput p){products.lock(id).orElseThrow(()->new BusinessException(404,"Producto inexistente"));products.update(id,p);return get(id);}
 public void delete(Integer id){products.lock(id).orElseThrow(()->new BusinessException(404,"Producto inexistente"));products.delete(id);}
}
