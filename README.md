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
/src/main/create_iot_schema.sql

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
| `/api/sensors`                 | POST     | API Key                   | `ROLE_COMPANY`| Registrar nuevo sensor (`company_api_key`)                                  |
| `/api/v1/sensor_data`          | POST     | API Key (Header + Body)   | `ROLE_SENSOR` | Subir uno o varios registros de sensores (`sensor_api_key`)                |

---

### Notas de seguridad

- Todos los endpoints protegidos deben usarse vía **HTTPS** en producción.
- Sensores y compañías **no usan JWT**, solo claves API en `Authorization`.
- El backend valida que el `sensor_api_key` enviado en el **header y body** coincidan.

---

## HTTPS en desarrollo

### Paso 1: Generar certificado autofirmado

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

Guarda `keystore.p12` en `src/main/resources/`.

### Paso 2: Configurar `application.properties`

```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=iot-api-cert
```

---

## Notas para desarrolladores

Si agregas una nueva clase `@Entity`, **debes actualizar el script de base de datos** 
ubicado en:
/src/main/create_iot_schema.sql

Esto es especialmente importante si se activa:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

> Si las tablas o columnas no coinciden con las entidades, la app no iniciará correctamente.

---
