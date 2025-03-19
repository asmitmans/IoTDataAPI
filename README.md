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

-- Insertar usuario ADMIN (la contraseña debe estar encriptada en producción)
INSERT INTO users (username, password, enabled) 
VALUES ('admin', '$2a$10$eBqvvLP3Iz1eN9a1HGV6peOc9FxFQG6.V1N48QEX6FgA9f2DJshVO', TRUE) 
ON CONFLICT (username) DO NOTHING;

-- Asignar rol ADMIN al usuario ADMIN
INSERT INTO user_roles (user_id, role_id) 
VALUES (
    (SELECT id FROM users WHERE username = 'admin'), 
    (SELECT id FROM roles WHERE name = 'ADMIN')
) ON CONFLICT DO NOTHING;
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