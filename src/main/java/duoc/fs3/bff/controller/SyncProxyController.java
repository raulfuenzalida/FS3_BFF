package duoc.fs3.bff.controller;

import duoc.fs3.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador proxy para endpoints de sincronización.
 * 
 * Este controlador redirige todas las peticiones relacionadas con sincronización
 * hacia el microservicio FS3_Ms_Sync (puerto 8083).
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncProxyController {

    private final ProxyService proxyService;

    /**
     * Proxy para exportar/sincronizar datos a la nube.
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportAndSync(@RequestBody String body, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToSync("/api/v1/sync/export", HttpMethod.POST, headers, body, null);
    }

    /**
     * Proxy para subir una imagen individual.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file, @RequestHeader HttpHeaders headers) {
        // Para manejar multipart, necesitamos un enfoque diferente
        // Por simplicidad, pasamos el archivo como byte array
        try {
            return proxyService.proxyToSync("/api/v1/sync/upload-image", HttpMethod.POST, headers, file.getBytes(), null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error processing file\"}");
        }
    }

    /**
     * Proxy para eliminar una prenda del ropero.
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Long itemId, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToSync("/api/v1/sync/item/" + itemId, HttpMethod.DELETE, headers, null, null);
    }

    /**
     * Proxy para descargar la última versión de la nube.
     */
    @GetMapping("/download")
    public ResponseEntity<String> downloadCloudData(@RequestHeader HttpHeaders headers) {
        return proxyService.proxyToSync("/api/v1/sync/download", HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para servir una imagen guardada.
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<String> serveFile(@PathVariable String filename, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToSync("/api/v1/sync/images/" + filename, HttpMethod.GET, headers, null, null);
    }
}
