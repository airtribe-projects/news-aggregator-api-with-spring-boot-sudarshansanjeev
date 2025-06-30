package org.airtribe.controller;

import org.airtribe.model.request.TokenRequest;
import org.airtribe.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
public class JwtDebugController {

    private final JwtService jwtService;

    public JwtDebugController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Endpoint to decode and verify token.
     * Example: POST /api/jwt/verify with raw token in body.
     */
    @PostMapping("/verify")
    public String verifyToken(@RequestBody TokenRequest request) {
        String token = request.getToken();
        // Remove possible quotes
        token = token.replace("\"", "").trim();

        boolean isValid = jwtService.validateToken(token);

        if (!isValid) {
            return "Invalid token!";
        }

        String username = jwtService.extractUserName(token);
        var claims = jwtService.extractAllClaims(token);

        StringBuilder sb = new StringBuilder();
        sb.append("✅ Token is valid!\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("All Claims: ").append(claims.toString());

        return sb.toString();
    }
}