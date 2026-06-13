package duoc.fs3.bff.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Servicio de proxy para redirigir peticiones a los microservicios backend.
 * 
 * Este servicio utiliza RestTemplate para hacer forward de peticiones HTTP
 * hacia los microservicios correspondientes, preservando headers, body y
 * query parameters.
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@Service
@Slf4j
public class ProxyService {

    private final RestTemplate restTemplate;

    @Value("${ms.auth.url}")
    private String authUrl;

    @Value("${ms.weather.url}")
    private String weatherUrl;

    @Value("${ms.sync.url}")
    private String syncUrl;

    public ProxyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Redirige una petición al microservicio de autenticación.
     * 
     * @param path Ruta del endpoint (ej: /api/auth/login)
     * @param method Método HTTP
     * @param headers Headers de la petición original
     * @param body Cuerpo de la petición (puede ser null)
     * @param params Parámetros query (puede ser null)
     * @return Respuesta del microservicio
     */
    public ResponseEntity<String> proxyToAuth(String path, HttpMethod method, 
                                               HttpHeaders headers, Object body, Map<String, String> params) {
        String url = authUrl + path;
        log.info("Proxying to Auth: {} {}", method, url);
        return proxyRequest(url, method, headers, body, params);
    }

    /**
     * Redirige una petición al microservicio de clima.
     * 
     * @param path Ruta del endpoint (ej: /api/weather/city/Santiago)
     * @param method Método HTTP
     * @param headers Headers de la petición original
     * @param body Cuerpo de la petición (puede ser null)
     * @param params Parámetros query (puede ser null)
     * @return Respuesta del microservicio
     */
    public ResponseEntity<String> proxyToWeather(String path, HttpMethod method, 
                                                  HttpHeaders headers, Object body, Map<String, String> params) {
        String url = weatherUrl + path;
        log.info("Proxying to Weather: {} {}", method, url);
        return proxyRequest(url, method, headers, body, params);
    }

    /**
     * Redirige una petición al microservicio de sincronización.
     * 
     * @param path Ruta del endpoint (ej: /api/v1/sync/export)
     * @param method Método HTTP
     * @param headers Headers de la petición original
     * @param body Cuerpo de la petición (puede ser null)
     * @param params Parámetros query (puede ser null)
     * @return Respuesta del microservicio
     */
    public ResponseEntity<String> proxyToSync(String path, HttpMethod method, 
                                             HttpHeaders headers, Object body, Map<String, String> params) {
        String url = syncUrl + path;
        log.info("Proxying to Sync: {} {}", method, url);
        return proxyRequest(url, method, headers, body, params);
    }

    /**
     * Método genérico para hacer proxy de peticiones HTTP.
     * 
     * @param url URL completa del destino
     * @param method Método HTTP
     * @param headers Headers a preservar
     * @param body Cuerpo de la petición
     * @param params Parámetros query
     * @return Respuesta del microservicio
     */
    private ResponseEntity<String> proxyRequest(String url, HttpMethod method, 
                                                HttpHeaders headers, Object body, Map<String, String> params) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            log.info("Proxy response status: {}", response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (Exception e) {
            log.error("Error proxying request to {}: {}", url, e.getMessage());
            throw new RuntimeException("Error proxying request: " + e.getMessage(), e);
        }
    }
}
