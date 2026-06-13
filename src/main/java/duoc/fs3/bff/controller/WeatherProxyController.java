package duoc.fs3.bff.controller;

import duoc.fs3.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador proxy para endpoints de clima.
 * 
 * Este controlador redirige todas las peticiones relacionadas con clima
 * hacia el microservicio FS3_ms_weather (puerto 8081).
 * 
 * @author Duoc UC Fullstack III
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherProxyController {

    private final ProxyService proxyService;

    /**
     * Proxy para obtener clima por nombre de ciudad.
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<String> getWeatherByCityName(@PathVariable String cityName, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToWeather("/api/weather/city/" + cityName, HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para obtener clima por código de ciudad.
     */
    @GetMapping("/code/{cityCode}")
    public ResponseEntity<String> getWeatherByCityCode(@PathVariable String cityCode, @RequestHeader HttpHeaders headers) {
        return proxyService.proxyToWeather("/api/weather/code/" + cityCode, HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para obtener todos los climas más recientes.
     */
    @GetMapping("/all/latest")
    public ResponseEntity<String> getAllLatestWeather(@RequestHeader HttpHeaders headers) {
        return proxyService.proxyToWeather("/api/weather/all/latest", HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para obtener todos los climas.
     */
    @GetMapping("/all")
    public ResponseEntity<String> getAllWeather(@RequestHeader HttpHeaders headers) {
        return proxyService.proxyToWeather("/api/weather/all", HttpMethod.GET, headers, null, null);
    }

    /**
     * Proxy para health check del servicio de clima.
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(@RequestHeader HttpHeaders headers) {
        return proxyService.proxyToWeather("/api/weather/health", HttpMethod.GET, headers, null, null);
    }
}
