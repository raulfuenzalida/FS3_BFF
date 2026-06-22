# ms-bff - Backend For Frontend

Backend For Frontend (BFF) desarrollado con Spring Boot 3.5.13, Java 17 y Maven. Actúa como gateway/proxy simple para enrutar peticiones desde los frontends (FullStack_3_FrontEnd y PanelAdmin) hacia los microservicios backend.

## Características

- **Proxy/Gateway Simple**: Redirige peticiones HTTP hacia los microservicios correspondientes
- **Passthrough JWT**: No valida tokens JWT, solo los pasa a los microservicios
- **CORS Configurado**: Habilitado para ambos frontends (PanelAdmin y FullStack_3_FrontEnd)
- **Arquitectura Limpia**: Separación de responsabilidades con ProxyService
- **Logging Básico**: Logs para tracking de peticiones proxy
- **Docker ready**: Configurado para despliegue con Docker y docker-compose

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.13**
- **Spring Web** para endpoints REST
- **RestTemplate** para hacer proxy de peticiones
- **Lombok** para reducir código boilerplate
- **Docker** para contenedorización

## Metadatos del Proyecto

- **Group**: duoc.fs3
- **Artifact**: ms-bff
- **Package**: duoc.fs3.bff
- **Version**: 0.0.1-SNAPSHOT

## Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- IDE compatible con Java (IntelliJ IDEA, Eclipse, VS Code)
- Docker y docker-compose (para despliegue con contenedores)
- Microservicios backend corriendo:
  - FS3-ms-auth (puerto 8080)
  - FS3_ms_weather (puerto 8082)
  - FS3_Ms_Sync (puerto 8083)

## Ejecución de la Aplicación

### Desde Maven

```bash
cd FS3-bff
mvn clean install
mvn spring-boot:run
```

### Desde IDE

1. Importar el proyecto como proyecto Maven
2. Ejecutar la clase `BffApplication.java`
3. La aplicación estará disponible en `http://localhost:8085`

### Despliegue con Docker

#### Construir el JAR
```bash
mvn clean package -DskipTests
```

#### Construir imagen Docker
```bash
docker build -t fs3-bff .
```

#### Ejecutar con docker-compose
```bash
# Copiar .env.example a .env y configurar variables
cp .env.example .env
# Editar .env con tus valores de MS_AUTH_URL, MS_WEATHER_URL, MS_SYNC_URL

# Iniciar contenedor
docker-compose up -d
```

#### Ejecutar contenedor directamente
```bash
docker run -d -p 8085:8085 \
  -e MS_AUTH_URL=http://ip-auth-ec2:8080 \
  -e MS_WEATHER_URL=http://ip-weather-ec2:8082 \
  -e MS_SYNC_URL=http://ip-sync-ec2:8083 \
  --name fs3-bff \
  fs3-bff
```

#### Variables de Entorno Requeridas
- `MS_AUTH_URL`: URL del microservicio auth (ej: `http://ip-auth-ec2:8080`)
- `MS_WEATHER_URL`: URL del microservicio weather (ej: `http://ip-weather-ec2:8082`)
- `MS_SYNC_URL`: URL del microservicio sync (ej: `http://ip-sync-ec2:8083`)

Ver archivo `.env.example` para template de configuración.

## Configuración

### application.properties

Configuración principal incluyendo URLs de microservicios y CORS. Ahora usa variables de entorno para las URLs de microservicios:

```properties
# Puerto del servidor BFF
server.port=8085

# URLs de microservicios (ahora usando variables de entorno)
ms.auth.url=${MS_AUTH_URL}
ms.weather.url=${MS_WEATHER_URL}
ms.sync.url=${MS_SYNC_URL}

# CONFIGURACIÓN CORS
spring.web.cors.allowed-origins=http://localhost:8084,http://localhost:19000,http://localhost:19006,http://localhost:3000,http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

## Endpoints Proxy

El BFF expone los mismos endpoints que los microservicios, redirigiendo internamente:

### Autenticación (→ FS3-ms-auth :8080)

#### Registrar Usuario
- **POST** `/api/auth/register`

#### Iniciar Sesión
- **POST** `/api/auth/login`

#### Login Administrador
- **POST** `/api/admin/login`

#### Listar Usuarios
- **GET** `/api/admin/users`

#### Actualizar Usuario
- **PUT** `/api/admin/users/{id}`

#### Eliminar Usuario
- **DELETE** `/api/admin/users/{id}`

### Clima (→ FS3_ms_weather :8081)

#### Obtener clima por ciudad
- **GET** `/api/weather/city/{cityName}`

#### Obtener clima por código
- **GET** `/api/weather/code/{cityCode}`

#### Obtener todos los climas más recientes
- **GET** `/api/weather/all/latest`

#### Obtener todos los climas
- **GET** `/api/weather/all`

#### Health Check
- **GET** `/api/weather/health`

### Sincronización (→ FS3_Ms_Sync :8083)

#### Exportar/Sincronizar datos
- **POST** `/api/v1/sync/export`

#### Subir imagen
- **POST** `/api/v1/sync/upload-image`

#### Eliminar prenda
- **DELETE** `/api/v1/sync/item/{itemId}`

#### Descargar datos de la nube
- **GET** `/api/v1/sync/download`

#### Servir imagen
- **GET** `/api/v1/sync/images/{filename}`

## Estructura del Proyecto

```
src/main/java/duoc/fs3/bff/
|-- BffApplication.java              # Clase principal
|-- config/
|   |-- WebConfig.java                # Configuración CORS
|-- controller/
|   |-- AuthProxyController.java      # Proxy para endpoints de auth
|   |-- WeatherProxyController.java  # Proxy para endpoints de weather
|   |-- SyncProxyController.java      # Proxy para endpoints de sync
|-- service/
|   |-- ProxyService.java             # Servicio de proxy con RestTemplate
```

## Arquitectura del BFF

### Separación de Responsabilidades

El BFF sigue una arquitectura simple con separación clara de responsabilidades:

- **Controller**: Manejo de solicitudes HTTP y redirección
- **Service**: Lógica de proxy con RestTemplate
- **Config**: Configuración de CORS

### Flujo de Datos

1. **Frontend** → Solicitud al BFF (puerto 8085)
2. **BFF** → Identifica el microservicio destino según la ruta
3. **BFF** → Usa RestTemplate para forward la petición al microservicio
4. **Microservicio** → Procesa la petición y retorna respuesta
5. **BFF** → Retorna la respuesta al frontend tal cual

### Características del Proxy

- **Preservación de Headers**: Todos los headers (incluyendo Authorization) se pasan al microservicio
- **Preservación de Body**: El cuerpo de la petición se pasa tal cual
- **Preservación de Query Parameters**: Los parámetros query se pasan al microservicio
- **Passthrough de Errores**: Los errores de los microservicios se propagan tal cual
- **Sin Validación JWT**: El BFF no valida tokens, deja que cada microservicio valide

## Desarrollo

### Agregar nuevas rutas

1. Crear o modificar el controller correspondiente
2. Agregar el método que delega a ProxyService
3. Especificar la ruta destino en el microservicio
4. Actualizar este README si es necesario

### Estándares de Código

- Todo el código está documentado con JavaDoc en español
- Se siguen las convenciones de nomenclatura de Java
- Se utiliza Lombok para reducir código boilerplate

## Troubleshooting

### Problemas Comunes

#### 1. Error de conexión a microservicio
```
Error proxying request: Connection refused
```
**Solución**: Verifica que el microservicio destino esté corriendo en el puerto correcto.

#### 2. Error de CORS
```
Access to XMLHttpRequest has been blocked by CORS policy
```
**Solución**: Verifica que el origen del frontend esté en la lista de allowed-origins en application.properties.

#### 3. Error 404 en endpoints
```
404 Not Found
```
**Solución**: Verifica que la ruta del endpoint coincida con la del microservicio destino.

## Licencia

MIT License - Ver archivo LICENSE para más detalles.

## Autor

Desarrollado por Duoc UC - Fullstack III (2026)
