package org.camunda.bpm.getstarted.springbootkeycloakexample.controller;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiController {

    String keycloakLogoutUrl = "http://localhost:8080/realms/myrealm/protocol/openid-connect/logout";
    String clientId = "myclient";

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("Access granted to secure endpoint.");
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        System.out.println("here");
        return ResponseEntity.ok("Access granted to public endpoint.");
    }



    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) throws IOException {

        String redirectUri = request
                .getRequestURL()
                .toString()
                .replace(request.getRequestURI(), "");
        String logoutUrl = keycloakLogoutUrl + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        request.getSession().invalidate();
        response.sendRedirect(logoutUrl);
    }
}
