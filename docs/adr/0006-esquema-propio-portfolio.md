---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0006. Esquema propio `portfolio` en PostgreSQL

## Contexto

El primer despliegue en Render falló al arrancar:

```
Found non-empty schema(s) "public" but no schema history table.
```

Supabase crea sus propios objetos en el esquema `public`, así que nunca está vacío. Flyway se niega a migrar un esquema no vacío que no tiene su tabla de historial.

## Opciones consideradas

1. **`baselineOnMigrate=true`**, como sugiere el propio error.
2. **Esquema propio** para las tablas de la aplicación.

## Decisión

**Esquema propio `portfolio`.**

- Flyway: `default-schema` y `schemas` = `portfolio`. Solo gestiona ese esquema, lo crea si no existe y guarda en él `flyway_schema_history`. Las migraciones no cambian: Flyway fija el `search_path` durante la migración.
- Hibernate: `default_schema` = `portfolio`. Cada consulta incluye el esquema, así que no depende del `search_path` de la conexión, que un pooler en modo transacción no garantiza.

**Por qué no `baselineOnMigrate`:** con la configuración por defecto, Flyway registraría la V1 como ya aplicada **sin ejecutarla**, y las tablas nunca se crearían en producción.

## Consecuencias

- **Positivas:** las tablas de la aplicación quedan aisladas de todo lo que gestione Supabase; el despliegue no depende del contenido de `public`; y el esquema no está expuesto por la Data API de Supabase.
- **Verificación:** un script de init compartido simula Supabase creando un objeto en `public`, tanto en Docker Compose como en Testcontainers. El test de integración **falla con la configuración anterior y pasa con la nueva**, así que el fallo de producción queda cubierto como test de regresión.
