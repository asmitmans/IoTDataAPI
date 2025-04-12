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