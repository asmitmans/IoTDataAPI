# IoT Data API

## Descripción

Este proyecto es una API para la gestión y almacenamiento de datos provenientes 
de dispositivos IoT. Utiliza **Spring Boot**, **Spring Security con JWT** y 
**PostgreSQL** como base de datos.

---

## Configuración de la base de datos

Este proyecto usa **PostgreSQL** como motor de base de datos.  
Asegúrate de tener PostgreSQL instalado y correctamente configurado.

> La aplicación **no crea automáticamente las tablas**.  
> Debes ejecutarlas manualmente con el script correspondiente.

### Script de creación de tablas
Ejecuta el archivo SQL ubicado en:
```bash:
/src/main/create_iot_schema.sql
```
Este archivo contiene todas las sentencias `CREATE TABLE` necesarias para 
el funcionamiento de la aplicación.

---

### Contraseña del usuario `admin`

El script incluye un usuario `admin` con una contraseña predefinida (`admin`) 
encriptada con `BCrypt`, solo con fines de prueba.

**No se recomienda usarla en producción.**

#### Cambiar la contraseña

1. Genera un nuevo hash con `BCryptPasswordEncoder` en Java.
2. Ejecuta en PostgreSQL:

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
| `/api/sensors`                 | Todos     | API Key                   | `ROLE_COMPANY`| CRUD completo de sensor (`company_api_key`)                                  |
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
