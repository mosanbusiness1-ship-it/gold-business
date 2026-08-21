package com.mo.auth;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin("*")
//@RequestMapping("/public")
@RestController
public class ResourceController {
	
	
	@GetMapping("/resource")
	    public ResponseEntity<String> accessResource(Principal principal) {
	        String currentUserEmail = principal.getName(); // Récupère l'email ou le username de l'utilisateur authentifié
	        return ResponseEntity.ok("Utilisateur authentifié : " + currentUserEmail);
	    }

}
