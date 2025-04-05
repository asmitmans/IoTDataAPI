# Guía de Uso de la IoT Data API

Esta API permite gestionar compañías, ubicaciones, sensores y datos IoT de forma 
segura usando JWT y claves API.

---

## Autenticación de Administrador

### **Login**

**POST** `/api/auth/login`

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Respuesta:**

```json
{
  "username": "admin",
  "message": "User logged in successfully",
  "accessToken": "<jwt_token>",
  "success": true
}
```

---

## Crear Compañía

### **POST** `/api/companies`

**Header:**

```
Authorization: Bearer <jwt_token>
```

**Body:**

```json
{
  "companyName": "MineraX"
}
```

**Respuesta:**

```json
{
  "id": <company_id>,
  "companyName": "MineraX",
  "companyApiKey": "<company_api_key>"
}
```

---

## Crear Ubicación

### **POST** `/api/locations`

**Header:**

```
Authorization: Bearer <jwt_token>
```

**Body:**

```json
{
  "companyId": <company_id>,
  "locationName": "MinaW",
  "locationCountry": "Chile",
  "locationCity": "Tocopilla"
}
```

**Respuesta:**

```json
{
  "id": <location_id>,
  "company": {
    "id": <company_id>,
    "companyName": "MineraX",
    "companyApiKey": "<company_api_key>"
  },
  "locationName": "MinaW",
  "locationCountry": "Chile",
  "locationCity": "Tocopilla",
  "locationMeta": null
}
```

---

## Registrar Sensor

### **POST** `/api/sensors`

**Header:**

```
Authorization: ApiKey <company_api_key>
```

**Body:**

```json
{
  "locationId": <location_id>,
  "sensorName": "Sensor 01",
  "sensorCategory": "temperature",
  "sensorMeta": {
    "unidad": "Celsius",
    "precision": "0.1"
  }
}
```

**Respuesta:**

```json
{
  "id": <sensor_id>,
  "message": "Sensor 01",
  "sensorApiKey": "<sensor_api_key>"
}
```

---

## Enviar Datos de Sensor

### **POST** `/api/v1/sensor_data`

**Header:**

```
Authorization: ApiKey <sensor_api_key>
```

**Body: (una o varias lecturas)**

```json
{
  "api_key": "<sensor_api_key>",
  "json_data": [
    {
      "datetime": 1742860430,
      "temp": 24.4,
      "humidity": 0.5
    },
    {
      "datetime": 1742861495,
      "temp": 22.1,
      "humidity": 0.6
    }
  ]
}
```

**Respuesta:**

```json
{
  "message": "Datos guardados correctamente",
  "recordsSaved": 4
}
```

---

## Notas

- Todos los datos enviados deben ir codificados en **UTF-8** y en formato `application/json`.
- Los sensores deben **enviar la clave en el header y en el body**.
- La autenticación por roles se hace vía **JWT** (`Bearer <token>`).
- La autenticación de sensores y compañías es por **API Key** (`ApiKey <clave>` en el header).

---

## Entornos y rutas base

- **Desarrollo:**  
  Usar la ruta base `https://localhost:8080`  
  
- **Producción:**  
  Usar `https://<ip_publica>` o dominio configurado (si aplica)

---