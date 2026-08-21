package com.mo.auth.totp;

import com.mo.auth.JwtService;
import com.mo.auth.User;
import com.mo.core.services.QrCodeGeneratorService;
import com.mo.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Base64;
import java.util.Map;

import javax.crypto.KeyGenerator;

import com.mo.auth.LoginResponse;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/totp")
public class TotpController {

    private final TotpService totpService;
    private final QrCodeGeneratorService qrCodeGeneratorService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TotpController(TotpService totpService, QrCodeGeneratorService qrCodeGeneratorService, JwtService jwtService, UserRepository userRepository) {
        this.totpService = totpService;
        this.qrCodeGeneratorService = qrCodeGeneratorService;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
    }

    /**
     * Génère un secret TOTP pour un utilisateur donné (ou en régénère un) et le retourne.
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register TOTP",
        description = "Register a TOTP secret for the user and return the generated secret"
    )
    public ResponseEntity<Map<String, String>> register(@RequestParam Long userId) throws Exception {
        String base32Secret = totpService.registerTotpForUser(userId);
        return ResponseEntity.ok(Map.of("secret", base32Secret));
    }

    /**
     * Fournit l'URI OTPAUTH à utiliser dans une app comme Google Authenticator.
     */
    @GetMapping("/uri")
    @Operation(summary = "Get TOTP URI", description = "Return the OTPAUTH URI for a registered user, suitable for authenticator apps")
    public ResponseEntity<Map<String, String>> getTotpUri(@RequestParam Long userId,
                                                          @RequestParam String issuer,
                                                          @RequestParam String accountName) throws Exception {
        String otpAuthUri = totpService.getTotpUri(userId, issuer, accountName);
        return ResponseEntity.ok(Map.of("otpAuthUri", otpAuthUri));
    }

    /**
     * Retourne l'image QR Code encodée en base64 pour l’URI OTPAUTH de l'utilisateur.
     */
    @GetMapping("/qr")
    @Operation(summary = "Get TOTP QR code", description = "Return a base64-encoded QR code image for a user's TOTP URI")
    public ResponseEntity<Map<String, String>> getQrCodeImage(@RequestParam Long userId,
                                                              @RequestParam String issuer,
                                                              @RequestParam String accountName) throws Exception {
        String otpAuthUri = totpService.getTotpUri(userId, issuer, accountName);
        byte[] imageBytes = qrCodeGeneratorService.generateQrCode(otpAuthUri, 300, 300);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        return ResponseEntity.ok(Map.of("base64QrCode", base64Image));
    }

    /**
     * Vérifie si un code TOTP soumis est valide.
     */
    @PostMapping("/verify")
    @Operation(
        summary = "Verify TOTP code",
        description = "Verify a user's submitted TOTP code and return a JWT token when valid",
        requestBody = @RequestBody(
            description = "TOTP verification parameters",
            required = true,
            content = @Content(mediaType = "application/x-www-form-urlencoded",
                examples = @ExampleObject(value = "userId=1&code=123456")
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "TOTP verified"),
            @ApiResponse(responseCode = "401", description = "Invalid TOTP code")
        }
    )
    public ResponseEntity<?> verify(@RequestParam Long userId, @RequestParam String code) throws Exception {

        boolean valid = totpService.verifyCode(userId, code);
        
        System.out.println("codeccccccccccccccccccccccccccccccCODE :"+code);

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid code"));
        }

        // Code TOTP est valide → générer le JWT
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(360000); // durée de validité en ms

        return ResponseEntity.ok(loginResponse);
    }

}

//1   DVTRH67I25QPJCUYOA5NKAADM7EVLWNC