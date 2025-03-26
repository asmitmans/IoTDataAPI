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
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
);

-- Tabla sensor_data
CREATE TABLE sensor (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES location(id) ON DELETE CASCADE,
    sensor_name VARCHAR(100) NOT NULL,
    sensor_category VARCHAR(50),
    sensor_api_key VARCHAR(255) UNIQUE NOT NULL,
    sensor_meta JSONB DEFAULT '{}'
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

### Endpoints de Autenticación y Acceso

- **`POST /api/auth/login`** → Inicia sesión y devuelve un **JWT**.
   - Enviar `username` y `password` en el **body** (JSON).
   - Respuesta: `{ "accessToken": "JWT_TOKEN" }`.

- **`GET /api/companies`** → Devuelve la lista de compañías (requiere autenticación).
   - Incluir el **JWT** en el header:
     ```http
     Authorization: Bearer JWT_TOKEN
     ```
   - Respuesta: `200 OK` si el usuario tiene `ROLE_ADMIN`.

**Notas:**
- Todos los endpoints protegidos requieren autenticación vía **JWT**.
- Usar el token devuelto en el login para acceder a recursos protegidos.

---

Perfecto, aquí tienes el resumen solicitado en formato de tabla clara y profesional:

---

## Permisos, Seguridad y Endpoints

| **Agente**   | **Seguridad**                          | **Puede hacer**                                                                 | **Endpoint(s)**                                 |
|--------------|----------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------|
| **ADMIN**    | JWT (username + password)              | - Crear/editar/eliminar compañías, ubicaciones, usuarios y sensores            | `/api/companies/**`<br>`/api/locations/**`<br>`/api/users/**`<br>`/api/sensors/**` |
| **COMPANY**  | `company_api_key` (en `Authorization`) | - Registrar sensores<br>- Consultar datos históricos de sensores propios        | `POST /api/sensors`<br>`GET /api/v1/sensor_data` |
| **SENSOR**   | `sensor_api_key` (en `Authorization`)  | - Subir datos desde el sensor (una o múltiples muestras en lote)               | `POST /api/v1/sensor_data`                      |

---

### Notas de Seguridad

- Todos los endpoints están protegidos según el tipo de agente.
- Las *company_api_key* y *sensor_api_key* son generadas automáticamente.
- **HTTPS es obligatorio** para producción.  
  Asegura confidencialidad y evita exposición de los tokens.

---
---