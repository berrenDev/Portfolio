---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0005. Hosting: backend en Render y PostgreSQL en Supabase

## Contexto

El portfolio debe estar disponible 24/7 para recruiters, con el menor coste posible y **sin administrar servidores**. Los precios de los VPS han subido, y los planes gratuitos de varias plataformas han desaparecido o se duermen.

## Opciones consideradas

| Opción | Coste aprox. | Descartada por |
|---|---|---|
| Homelab propio | Luz + dominio | Cortes de luz o red en casa; requiere administrarlo |
| VPS (Hetzner, OVH…) | ~7 €/mes | Requiere administrar el servidor |
| Google Cloud Run + GraalVM nativo | ~0 € | Más complejidad de build; arranques en frío |
| Railway | Por uso | Sin plan gratuito permanente |
| **Render + Supabase** | **~7 $/mes** | — |

## Decisión

- **Backend:** Render, servicio Docker en Frankfurt. Plan Free mientras se desarrolla; **Starter (siempre encendido) antes de compartir el enlace**, porque el Free se duerme y tarda más de 2 minutos en despertar.
- **Base de datos:** Supabase Free (PostgreSQL 17), en Frankfurt, junto al backend para minimizar la latencia.
  - Conexión por el **Session pooler**: la conexión directa es solo IPv6 y Render sale por IPv4.
  - **Data API desactivada**: todo acceso a datos pasa por el backend Spring Boot.
  - Pool de Hikari limitado a 5 conexiones por el límite del plan gratuito. El valor está en `application.yml` (`spring.datasource.hikari.maximum-pool-size`) y se puede sobrescribir sin reconstruir la imagen con la variable de entorno `SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE`.
- **Despliegue continuo:** cada merge a `main` que toque `backend/` o `api/` redespliega automáticamente.
- **Imagen Docker:** multi-stage, JRE 25, usuario no root. JVM ajustada a 512 MB (`MaxRAMPercentage=50`, `SerialGC`, `MALLOC_ARENA_MAX=2`), medida con 2.000 peticiones para que el peor caso quepa en memoria.
- **Credenciales:** solo en variables de entorno de Render; nunca en el repositorio.

## Consecuencias

- **Positivas:** coste mínimo, cero administración de servidores, despliegue automático desde GitHub.
- **Negativas:** el plan Free de Render arranca Spring Boot en unos 120 s con 0,1 CPU. Se mitigará pasando a Starter y, más adelante, con la caché AOT de Java 25 o CDS.
- **Riesgo:** dependencia de planes gratuitos que pueden cambiar. Se mitiga con Docker, Flyway y configuración por variables de entorno, que hacen la migración a otro proveedor sencilla.
