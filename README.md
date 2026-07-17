# Código Cristóbal — Backend

API REST de la promoción **"Código Cristóbal"** de Alimentos y Bebidas Regionales, S.A.
Recibe los registros de participación (nombre, teléfono y código del empaque) desde el
frontend Angular y los guarda en MySQL.

- **Producción:** `https://api.codigocristobal.com` (Railway)
- **Frontend (repositorio aparte):** cristobal-frontend — https://codigocristobal.com

## Stack

- Java 21 · Spring Boot 3.4 (Web, Data JPA, Validation) · Lombok
- MySQL en Railway (driver `mysql-connector-j`)
- Maven

## API

### `POST /api/registros`

```json
{ "nombre": "Juan Pérez", "telefono": "88887777", "codigo": "123456789012" }
```

| Respuesta | Cuándo |
|-----------|--------|
| `201` con el registro creado | Registro exitoso |
| `400` `{"error": "..."}` | Validación fallida (nombre/teléfono/código con formato inválido) |
| `409` `{"error": "Este código ya fue registrado"}` | El código ya existe — **cada código puede activarse una sola vez**, sin importar el teléfono |
| `429` `{"error": "..."}` | Más de 10 envíos por minuto desde la misma IP |

## Estructura

```
com.cristobal.backend/
├── controller/RegistroController    POST /api/registros
├── service/RegistroService          Regla de negocio: código único
├── repository/RegistroRepository    Spring Data JPA
├── model/Registro                   Entidad `registros` (validaciones Bean Validation)
└── config/
    ├── CorsConfig                   Orígenes permitidos (app.cors.allowed-origin, lista por comas)
    ├── GlobalExceptionHandler       Errores → {"error": "mensaje"} con el status correcto
    ├── RateLimitingFilter           10 POST/minuto por IP → 429
    └── SecurityHeadersFilter        HSTS, CSP, X-Frame-Options, etc.
```

## Base de datos

Una sola instancia MySQL en Railway con dos bases:

| Base | Uso |
|------|-----|
| `railway` | **Producción** — usada por el servicio desplegado |
| `cristobal_test` | **Desarrollo/pruebas** — usada al correr localmente |

Tabla `registros`: `id`, `nombre`, `telefono`, `codigo` (con restricción única
`uk_registros_codigo`), `fecha_registro`.

> Nota: `spring.jpa.hibernate.ddl-auto=update` no agrega restricciones únicas a tablas
> ya existentes; la restricción sobre `codigo` fue creada manualmente en ambas bases.
> Cuando el esquema esté estable conviene pasar a `validate`.

## Desarrollo local

1. Copiar `src/main/resources/application.properties.template` como
   `application.properties` (está en `.gitignore` — **nunca commitear credenciales**).
2. Completar la URL y credenciales de la base `cristobal_test`
   (host público del MySQL de Railway, pestaña *Connect* del servicio).
3. Ejecutar desde IntelliJ, o con Maven: `mvn spring-boot:run` (puerto 8080).

Requiere JDK 21+. Si Maven compila con JDK 23 o superior, el procesador de anotaciones
de Lombok ya queda configurado explícitamente en el `pom.xml` (no tocar esa sección).

## Despliegue (Railway)

El servicio del backend define estas variables de entorno — la configuración de
producción **no** vive en el repositorio:

| Variable | Valor |
|----------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql.railway.internal:3306/railway` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | (password del servicio MySQL) |
| `APP_CORS_ALLOWED_ORIGIN` | `https://codigocristobal.com,https://www.codigocristobal.com` |

## Contexto de negocio

La promoción es válida del 1 de agosto al 25 de octubre de 2026. Cada semana ganan los
códigos cuyos **dos últimos dígitos** coincidan con los dos últimos números del sorteo
dominical de la Lotería Nacional (Junta de Protección Social). El reglamento completo
está publicado en el frontend (`/reglamento`).
