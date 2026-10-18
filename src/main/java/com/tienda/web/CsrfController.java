package com.tienda.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;
@RestController
public class CsrfController {
 @GetMapping("/api/csrf") public CsrfToken csrf(CsrfToken token) {return token;}
}
