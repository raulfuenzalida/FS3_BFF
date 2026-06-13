package duoc.fs3.bff.controller;

import duoc.fs3.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador proxy para endpoints de autenticación.
 * 
 * Este controlador redirige todas las peticiones relacionadas con autenticación
 * hacia el microservicio FS3-ms-auth (puerto 8080).
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthProxyController {

    private final ProxyService proxyService;

    /**
     * Proxy para registro de usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody String body, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/auth/register", HttpMethod.POST, headers, body, null);
    }

    /**
     * Proxy para inicio de sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String body, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/auth/login", HttpMethod.POST, headers, body, null);
    }
}

/**
 * Controlador proxy para endpoints de administración.
 * 
 * Este controlador redirige todas las peticiones relacionadas con administración
 * hacia el microservicio FS3-ms-auth (puerto 8080).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
class AdminProxyController {

    private final ProxyService proxyService;

    /**
     * Proxy para login de administrador.
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody String body, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/admin/login", HttpMethod.POST, headers, body, null);
    }

    /**
     * Proxy para listar todos los usuarios.
     */
    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers(@RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/admin/users", HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para actualizar un usuario.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody String body, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/admin/users/" + id, HttpMethod.PUT, headers, body, null);
    }

    /**
     * Proxy para eliminar un usuario.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToAuth("/api/admin/users/" + id, HttpMethod.DELETE, headers, null, null);
    }
}
