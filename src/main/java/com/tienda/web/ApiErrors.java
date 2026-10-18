package com.tienda.web;
import com.tienda.service.BusinessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(BusinessException.class) ResponseEntity<?> business(BusinessException e) {return ResponseEntity.status(e.status).body(Map.of("message",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e) {
  return ResponseEntity.badRequest().body(Map.of("message","Datos inválidos","errors",e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).toList()));
 }
 @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class}) ResponseEntity<?> malformed(Exception e) {return ResponseEntity.badRequest().body(Map.of("message","Solicitud inválida"));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(Exception e) {return ResponseEntity.status(409).body(Map.of("message","La operación incumple una restricción de la base; comprueba si el producto está en un carrito"));}
}
