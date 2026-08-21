package com.mo.auth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception) throws IOException, ServletException {
	    String errorMessage = exception.getMessage();

	    String redirectUrl = UriComponentsBuilder.fromUriString("/api/needs")
	            .queryParam("error", errorMessage)
	            .build()
	            .encode() // 🔥 encodage important !
	            .toUriString();

	    response.sendRedirect(redirectUrl);
	}

}
