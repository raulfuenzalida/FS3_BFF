package duoc.fs3.bff.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
     * Redirige una petición multipart al microservicio de sincronización.
     * 
     * @param path Ruta del endpoint (ej: /api/v1/sync/upload-image)
     * @param method Método HTTP
     * @param headers Headers de la petición original
     * @param body Cuerpo multipart de la petición
     * @return Respuesta del microservicio
     */
    public ResponseEntity<String> proxyToSyncMultipart(String path, HttpMethod method, 
                                                       HttpHeaders headers, MultiValueMap<String, Object> body) {
        String url = syncUrl + path;
        log.info("Proxying multipart to Sync: {} {}", method, url);
        return proxyMultipartRequest(url, method, headers, body);
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

    /**
     * Método para hacer proxy de peticiones multipart HTTP.
     * 
     * @param url URL completa del destino
     * @param method Método HTTP
     * @param headers Headers a preservar
     * @param body Cuerpo multipart de la petición
     * @return Respuesta del microservicio
     */
    private ResponseEntity<String> proxyMultipartRequest(String url, HttpMethod method, 
                                                         HttpHeaders headers, MultiValueMap<String, Object> body) {
        try {
            // Configurar headers para multipart
            HttpHeaders multipartHeaders = new HttpHeaders();
            multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Preservar headers importantes (como Authorization)
            if (headers.getAuthorization() != null) {
                multipartHeaders.setAuthorization(headers.getAuthorization());
            }
            
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, multipartHeaders);
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            log.info("Proxy multipart response status: {}", response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (Exception e) {
            log.error("Error proxying multipart request to {}: {}", url, e.getMessage());
            throw new RuntimeException("Error proxying multipart request: " + e.getMessage(), e);
        }
    }
}
