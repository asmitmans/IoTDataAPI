# IoT Data API

## Descripción

Este proyecto es una API para la gestión y almacenamiento de datos provenientes 
de dispositivos IoT. Utiliza *Spring Boot, **Spring Security con JWT* y 
*PostgreSQL* como base de datos.

---

## Requisitos previos

Antes de ejecutar la API asegúrate de tener instalado:

- Java 21
- PostgreSQL
- Maven
- (Opcional) ActiveMQ (si vas a utilizar los consumidores de eventos)

---

## Pasos para ejecutar la API

1. *Crear una base de datos en PostgreSQL*

   Crea una base de datos vacía para la API.  
   Por ejemplo:

```sql
   CREATE DATABASE iot_data_api;
```

2. *Ejecutar el script de creación de tablas*

   Ejecuta el siguiente script SQL manualmente:

   
   src/main/resources/sql/create_iot_schema.sql
   

   Este archivo contiene todas las sentencias CREATE TABLE y datos iniciales 
   requeridos para el funcionamiento de la aplicación.

3. *Configurar las credenciales*

   Crea una copia del archivo de ejemplo:

```bash
   cp src/main/resources/application.example.properties src/main/resources/application.properties
```

   Edita application.properties y reemplaza las credenciales de tu base de datos:

```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/iot_data_api
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_clave
```

   Si utilizas ActiveMQ, también debes configurar sus credenciales en ese archivo:

```properties
   spring.activemq.broker-url=tcp://localhost:61616
   spring.activemq.user=usuario_activemq
   spring.activemq.password=clave_activemq
```
   

4. *Ejecutar la aplicación*

   En la raíz del proyecto, compila y ejecuta:

```bash
   mvn spring-boot:run
```

   La API quedará disponible en http://localhost:8080.

---

## Contraseña del usuario admin

El script incluye un usuario admin con contraseña admin (encriptada con BCrypt)  
*solo para fines de prueba*.

*No se recomienda usarla en producción.*

Para cambiarla, genera un nuevo hash y actualiza directamente la base de datos:

```sql
UPDATE users SET password = 'nuevo_hash' WHERE username = 'admin';
```

## Configuración de `application.properties`

Este archivo **no está en el repositorio por seguridad**.

Para configurarlo:

1. Copia `application.example.properties` -> renómbralo como 
   `application.properties`
2. Ubícalo en `src/main/resources/`
3. Reemplaza los valores de base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iot_data_api
spring.datasource.username=usuario
spring.datasource.password=clave
```
---

## Preparación de la base de datos

La creación completa del esquema se encuentra en:

```bash
/src/main/resources/create_iot_schema.sql
```

Este script incluye todas las tablas y datos necesarios para que la aplicación 
funcione correctamente.

---

### Permisos requeridos para el usuario de la base de datos

Una vez creada la base de datos, debes asignar los permisos necesarios al 
usuario que la aplicación utilizará para conectarse.

> Reemplaza `user_bd` por el nombre real de tu usuario PostgreSQL.

```sql
-- Asignar permisos al usuario de base de datos
GRANT USAGE ON SCHEMA public TO user_bd;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO user_bd;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO user_bd;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO user_bd;
```

Esto asegura que la aplicación pueda leer y escribir correctamente sin requerir 
permisos administrativos.

---

## Permisos, seguridad y endpoints

| **Agente**   | **Seguridad**                          | **Puede hacer**                                                                 | **Endpoint(s)**                                 |
|--------------|----------------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------|
| **ADMIN**    | JWT (username + password)              | Crear/editar/eliminar compañías, ubicaciones, usuarios y sensores              | `/api/companies/**`, `/api/locations/**`, `/api/users/**`, `/api/sensors/**` |
| **COMPANY**  | API Key en `Authorization`             | Registrar sensores, consultar datos históricos de sus sensores                 | `POST /api/sensors`, `GET /api/v1/sensor_data` |
| **SENSOR**   | API Key en `Authorization`             | Subir datos desde sensores (individual o en lote)                              | `POST /api/v1/sensor_data`                     |

---

### Endpoints implementados

| Endpoint                        | Método   | Autenticación             | Rol requerido | Descripción                                                                 |
|--------------------------------|----------|----------------------------|---------------|-----------------------------------------------------------------------------|
| `/api/auth/login`              | POST     | Ninguna                   | N/A           | Iniciar sesión (`username/password`)                                        |
| `/api/companies/**`            | Todos    | JWT                       | `ROLE_ADMIN`  | CRUD completo de compañías                                                  |
| `/api/locations/**`            | Todos    | JWT                       | `ROLE_ADMIN`  | CRUD completo de ubicaciones                                                |
| `/api/sensors`                 | Todos     | API Key                   | `ROLE_COMPANY`| CRUD completo de sensores (`company_api_key`)                                  |
| `/api/v1/sensor_data`          | POST     | API Key (Header + Body)   | `ROLE_SENSOR` | Subir uno o varios registros de sensores (`sensor_api_key`)                |
| `/api/v1/sensor_data`          | GET     | API Key (Header)   		| `ROLE_SENSOR` | Obtener registros de sensores (`sensor_api_key`)                |

---

### Notas de seguridad

- Todos los endpoints protegidos deben usarse vía **HTTPS** en producción.
- Sensores y compañías **no usan JWT**, solo claves API en `Authorization`.
- El backend valida que el `sensor_api_key` enviado en el **header y body** coincidan.

---

## Notas para desarrolladores

Si agregas una nueva clase `@Entity`, **debes actualizar el script de base de datos** 
ubicado en:
```bash:
/src/main/create_iot_schema.sql
```
Esto es especialmente importante si se activa:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

> Si las tablas o columnas no coinciden con las entidades, la app no iniciará correctamente.

---
