# Cambios manuales aplicados a la base de datos

## [2025-04-12] Asociación de usuarios a compañías

- Se añadió el campo `company_id` a la tabla `users`.
- Este campo referencia a `company(id)` con `ON DELETE SET NULL`.
- Así, si una compañía es eliminada, los usuarios no se eliminan, pero su relación se pierde de forma controlada.
- Script aplicado en la infraestructura de desarrollo.
- Script `create_iot_schema.sql` fue actualizado en el repositorio para reflejar este cambio.

```sql:
ALTER TABLE users
ADD COLUMN company_id INTEGER REFERENCES company(id) ON DELETE SET NULL;
```
---
## [2025-04-13] Ajuste de unidad temporal en tabla sensor_data

- Se renombró la columna timestamp_ms a timestamp_s en la tabla sensor_data.
- Este cambio alinea el sistema con la estructura oficial entregada, que utiliza EPOCH en segundos y no milisegundos.
- También se actualizó la entidad JPA correspondiente (SensorData) para reflejar el nuevo nombre.
- A partir de ahora, todos los timestamps enviados y almacenados deben estar expresados en segundos.

```sql
ALTER TABLE sensor_data RENAME COLUMN timestamp_ms TO timestamp_s;
```

---
