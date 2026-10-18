package com.tienda.service;
public class BusinessException extends RuntimeException {
 public final int status;
 public BusinessException(int status,String message) {super(message);this.status=status;}
}
