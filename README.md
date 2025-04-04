# IoT Data API

## Descripción
                                                                                 
Este proyecto es una API para la gestión y almacenamiento de datos provenientes 
de dispositivos IoT. Utiliza **Spring Boot**, **Spring Security con JWT**, y 
**PostgreSQL** como base de datos.

## Configuración de la Base de Datos  

Este proyecto usa **PostgreSQL** como base de datos. Asegúrate de tener PostgreSQL 
instalado y configurado correctamente.

### Creación de tablas

Ejecuta el siguiente script SQL para crear las tablas necesarias para la 
autenticación y autorización de usuarios:

```sql
-- Tabla de usuarios
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de roles
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla relación entre usuarios y roles
CREATE TABLE user_roles (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insertar rol ADMIN
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- Insertar usuario ADMIN  
INSERT INTO users (username, password, enabled) 
VALUES ('admin', '$2a$12$HlJDc8.E7vkoOUfm8CK1.O3VvmCbfZ1cwdFKM59roZf6zdJljOXwi', TRUE) 
ON CONFLICT (username) DO NOTHING;

-- Asignar rol ADMIN al usuario ADMIN
INSERT INTO user_roles (user_id, role_id) 
VALUES (
    (SELECT id FROM users WHERE username = 'admin'), 
    (SELECT id FROM roles WHERE name = 'ADMIN')
) ON CONFLICT DO NOTHING;

-- Tabla company
CREATE TABLE company (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) UNIQUE NOT NULL,
    company_api_key VARCHAR(255) UNIQUE NOT NULL
);

-- Tabla location
CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    company_id INT REFERENCES company(id) ON DELETE CASCADE,
    location_name VARCHAR(100) NOT NULL,
    location_country VARCHAR(50),
    location_city VARCHAR(50),
    location_meta JSONB DEFAULT '{}'
);

-- Tabla sensor
CREATE TABLE sensor (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES location(id) ON DELETE CASCADE,
    sensor_name VARCHAR(100) NOT NULL,
    sensor_category VARCHAR(50),
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
);

-- Tabla sensor_data
CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    sensor_id INT REFERENCES sensor(id) ON DELETE CASCADE,
    timestamp BIGINT NOT NULL,
    value_name VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

```

## Nota sobre la seguridad de la contraseña del usuario `admin`

Este proyecto incluye un usuario `admin` con una contraseña predefinida (`admin`) 
encriptada con `BCrypt`. Esto se ha hecho únicamente con fines de demostración. 

Se recomienda encarecidamente cambiar esta contraseña en entornos de producción. 

### Cómo cambiar la contraseña

Para cambiar la contraseña por una más segura, sigue estos pasos:

1. Genera una nueva contraseña encriptada usando `BCryptPasswordEncoder` en Java.
2. Reemplázala en la base de datos con el siguiente comando:

   ```sql
   UPDATE users SET password = 'nueva_hash' WHERE username = 'admin';
   ```

## Configuración del archivo `application.properties`   

Este archivo contiene la configuración de la base de datos y 
**no se encuentra en el repositorio por seguridad**.

Para configurar la conexión, sigue estos pasos:

1. Copia el archivo `application.example.properties` y renómbralo como 
   `application.properties` dentro de `src/main/resources/`.
2. Edita el archivo y reemplaza `tu_bbdd`, `tu_usuario` y `tu_contraseña` con las 
   credenciales correctas de tu base de datos PostgreSQL.
3. Guarda los cambios y ejecuta el proyecto.

---

## Permisos, Seguridad y Endpoints

| **Agente**   | **Seguridad**                          | **Puede hacer**                                                                 | **Endpoint(s)**                                 |
|--------------|----------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------|
| **ADMIN**    | JWT (username + password)              | - Crear/editar/eliminar compañías, ubicaciones, usuarios y sensores            | `/api/companies/**`<br>`/api/locations/**`<br>`/api/users/**`<br>`/api/sensors/**` |
| **COMPANY**  | `company_api_key` (en `Authorization`) | - Registrar sensores<br>- Consultar datos históricos de sensores propios        | `POST /api/sensors`<br>`GET /api/v1/sensor_data` |
| **SENSOR**   | `sensor_api_key` (en `Authorization`)  | - Subir datos desde el sensor (una o múltiples muestras en lote)               | `POST /api/v1/sensor_data`                      |

---

### Tabla resumen de endpoints implementados

| Endpoint                        | Método   | Autenticación             | Rol requerido | Descripción                                                                 |
|--------------------------------|----------|----------------------------|---------------|-----------------------------------------------------------------------------|
| `/api/auth/login`              | POST     | Ninguna                   | N/A           | Iniciar sesión con credenciales de usuario (`username/password`)           |
| `/api/companies/**`            | Todos    | JWT (Authorization)       | `ROLE_ADMIN`  | CRUD completo de compañías                                                  |
| `/api/locations/**`            | Todos    | JWT (Authorization)       | `ROLE_ADMIN`  | CRUD completo de ubicaciones                                                |
| `/api/sensors`                 | POST     | ApiKey (en Header)        | `ROLE_COMPANY`| Registrar nuevo sensor. Requiere `company_api_key` en header `Authorization`|
| `/api/v1/sensor_data`          | POST     | ApiKey (en Header + Body) | `ROLE_SENSOR` | Subir uno o varios registros de datos del sensor. Requiere `sensor_api_key`|

---

### Notas importantes de seguridad

- **Todos los endpoints protegidos deben ser accedidos vía HTTPS en producción.**
- **Los sensores y compañías no usan JWT.** Se autentican con sus claves API en headers (`Authorization: ApiKey <clave>`).
- El header **`Authorization`** se interpreta por filtros personalizados (`CompanyApiKeyAuthFilter` y `SensorApiKeyAuthFilter`).
- **Sensores deben incluir `sensor_api_key` en header y en body.** Se valida que coincidan.

---

## HTTPS en desarrollo

### Paso 1: Generar un certificado autofirmado

Ejecuta este comando en terminal (modo no interactivo):  
```bash
keytool -genkeypair \
  -alias iot-api-cert \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 365 \
  -storepass password \
  -dname "CN=localhost, OU=Dev, O=Futuro, L=Santiago, S=RM, C=CL"
```  
Esto genera un archivo `keystore.p12` que puedes poner en `src/main/resources/`.

### Paso 2: Configurar el archivo `application.properties`

```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=iot-api-cert
```
---

### Notas importantes

- Todos los endpoints ahora están disponibles en:  
  `https://localhost:8443/`

- En herramientas como **Insomnia** o **Postman**, asegúrate de:
   - Usar `https://` en las URLs.
   - Desactivar la verificación estricta de SSL si da error (opcional para desarrollo).

- En producción:
   - El puerto debe ser `443`.
   - Se debe reemplazar el certificado autofirmado por uno válido (ej: Let's 
     Encrypt o CA oficial).
   - Idealmente, se usa un **proxy inverso** como Nginx.

---
