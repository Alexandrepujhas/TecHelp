package br.com.TecHelpAPI.controllers;

import br.com.TecHelpAPI.data.dto.UserDTO;
import br.com.TecHelpAPI.data.dto.LoginDTO;
import br.com.TecHelpAPI.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")  // Versão da API
public class UserController {

    private final UserServices service;

    @Autowired
    public UserController(@Qualifier("userServices") UserServices service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(@RequestBody UserDTO user) {
        try {
            UserDTO createdUser = service.create(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header("Location", "/api/v1/users/" + createdUser.getIdUser())
                    .body(createdUser);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("USER-001", e.getMessage()));
        }
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserDTO user) {
        try {
            user.setIdUser(id); // Garante que o ID seja o mesmo da URL
            return ResponseEntity.ok(service.update(user));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("USER-002", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            boolean authenticated = service.authenticate(loginDTO.getNameUser(), loginDTO.getPassword());
            if (authenticated) {
                return ResponseEntity.ok(new AuthResponse("Login successful!"));
            }
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("AUTH-001", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("AUTH-002", "Login error"));
        }
    }

    // Classes internas para respostas padronizadas
    private static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        // Getters
    }

    private static class AuthResponse {
        private final String message;

        public AuthResponse(String message) {
            this.message = message;
        }

        // Getter
    }
}