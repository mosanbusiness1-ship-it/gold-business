package com.mo.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.mo.auth.totp.TotpService;
import com.mo.auth.dto.SignupResponseDto;
import com.mo.auth.dto.LoginResponseDto;
import com.mo.auth.dto.TwoFaResponseDto;
import com.mo.auth.dto.UserSummaryDto;
import com.mo.auth.dto.ErrorResponseDto;
import com.mo.auth.dto.ResourceAccessResponseDto;
import com.mo.auth.dto.RoleUpdateResponseDto;

import java.util.*;

@RestController
@RequestMapping("/public")
@CrossOrigin
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final TotpService totpService; // service 2FA
    private final com.mo.repositories.UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final RestTemplate restTemplate;

    private static final String EXTERNAL_SERVICE_URL = "https://example.com/api/receive-email";

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, TotpService totpService, com.mo.repositories.UserRepository userRepository, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.totpService = totpService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Create a new user account and return a JWT token with user summary",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"email\": \"user@example.com\", \"password\": \"Secret123!\", \"fullName\": \"User Example\"}"
                )
            )
        )
    )
    public ResponseEntity<SignupResponseDto> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        String jwtToken = jwtService.generateToken(registeredUser);

        UserSummaryDto userSummary = new UserSummaryDto();
        userSummary.setId(registeredUser.getId());
        userSummary.setEmail(registeredUser.getEmail());
        userSummary.setFullName(registeredUser.getFullName());

        SignupResponseDto response = new SignupResponseDto();
        response.setToken(jwtToken);
        response.setExpiresIn(360000);
        response.setUser(userSummary);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticate a user and return a JWT token or 2FA requirement response",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"email\": \"user@example.com\", \"password\": \"Secret123!\"}"
                )
            )
        )
    )
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser.isTotpEnabled()) {
            TwoFaResponseDto response = new TwoFaResponseDto();
            response.setMessage("2FA required");
            response.setUserId(authenticatedUser.getId());
            return ResponseEntity.ok(response);
        } else {
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponseDto response = new LoginResponseDto();
            response.setToken(jwtToken);
            response.setExpiresIn(360000);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/login/2fa")
    @Operation(
        summary = "Verify 2FA and login",
        description = "Verify the user's TOTP code and return a JWT token when successful",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/x-www-form-urlencoded",
                examples = @ExampleObject(
                    value = "userId=1&totpCode=123456"
                )
            )
        )
    )
    public ResponseEntity<?> verifyTotpAndLogin(@RequestParam Long userId, @RequestParam String totpCode) throws Exception {
        boolean valid = totpService.verifyCode(userId, totpCode);
        if (!valid) {
            ErrorResponseDto errorResponse = new ErrorResponseDto();
            errorResponse.setError("Invalid TOTP code");
            errorResponse.setStatus(401);
            errorResponse.setTimestamp(java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(401).body(errorResponse);
        }

        User user = authenticationService.loadUserById(userId);
        String jwtToken = jwtService.generateToken(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(jwtToken);
        response.setExpiresIn(360000);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    @Operation(summary = "Logout user", description = "Revoke the user's JWT token and log out")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/res")
    @Operation(summary = "Check resource access", description = "Verify the authenticated user's access to a secure resource")
    public ResponseEntity<ResourceAccessResponseDto> accessResource(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        ResourceAccessResponseDto response = new ResourceAccessResponseDto();
        response.setMessage("Utilisateur authentifié : " + currentUserEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/roles/{userId}")
    @Operation(summary = "Assign role to user", description = "Grant a role to the specified user")
    public ResponseEntity<RoleUpdateResponseDto> assignRole(@PathVariable Long userId, @RequestParam Role role) {
        User updatedUser = authenticationService.assignRole(userId, role);
        RoleUpdateResponseDto response = new RoleUpdateResponseDto();
        response.setUserId(updatedUser.getId());
        response.setEmail(updatedUser.getEmail());
        response.setMessage("Role assigned successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/roles/{userId}")
    @Operation(summary = "Remove role from user", description = "Revoke a role from the specified user")
    public ResponseEntity<RoleUpdateResponseDto> removeRole(@PathVariable Long userId, @RequestParam Role role) {
        User updatedUser = authenticationService.removeRole(userId, role);
        RoleUpdateResponseDto response = new RoleUpdateResponseDto();
        response.setUserId(updatedUser.getId());
        response.setEmail(updatedUser.getEmail());
        response.setMessage("Role removed successfully");
        return ResponseEntity.ok(response);
    }

    private void sendUserEmailToExternalService(String email) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            ResponseEntity<String> response = restTemplate.postForEntity(EXTERNAL_SERVICE_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email envoyé avec succès au service externe.");
            } else {
                System.err.println("Erreur lors de l'envoi de l'email au service externe : " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Exception lors de l'envoi de l'email au service externe : " + e.getMessage());
        }
    }
}
