---
status: propuesto
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0007. ADRs en el repositorio, sincronizados a la base de datos por webhook

## Contexto

Los ADRs viven en `docs/adr/` como ficheros Markdown (ver [0001](0001-registrar-decisiones-de-arquitectura.md)). Uno de los objetivos del portfolio es que el proceso de construcción se pueda consultar **desde la propia web**, servido por el backend y almacenado en la base de datos.

Hay que decidir dónde está la fuente de verdad de un ADR y cómo llega hasta la web.

## Opciones consideradas

1. **Solo en base de datos:** los ADRs se escriben desde el panel de administración.
   - El ADR deja de estar junto al código que justifica y no se revisa en el mismo PR. Habría dos versiones (repo y web) que mantener a mano.
2. **Leídos del repositorio al construir el frontend:** Angular lee `docs/adr/*.md` en el build (monorepo) y genera páginas estáticas.
   - Es la opción más simple, pero no pasa por el backend ni por la base de datos, que es parte del objetivo del proyecto.
3. **El repositorio es la fuente y el backend los sincroniza a la base de datos.**

## Decisión

**Opción 3.**

1. Los ADRs se escriben en `docs/adr/` y entran en `main` mediante PR, junto al código al que afectan.
2. GitHub envía un **webhook** de `push` al backend (`POST /api/v1/webhooks/github`).
3. El backend:
   - verifica la firma HMAC del webhook (`X-Hub-Signature-256`) con un secreto compartido;
   - ignora los pushes que no sean a `main` o que no toquen `docs/adr/`;
   - lee los ADRs con la API de GitHub y los guarda en PostgreSQL de forma **idempotente** (upsert por número de ADR), de modo que recibir el mismo webhook dos veces no duplique nada;
   - extrae los metadatos del front matter (`status`, `date`) y el título del encabezado.
4. La API los expone en `GET /api/v1/adrs` y `GET /api/v1/adrs/{number}`, y el frontend los muestra.

Las **releases** de GitHub (changelog) entrarán por el mismo mecanismo.

Las **entradas del devlog** no siguen este flujo: se escriben desde el panel de administración y se guardan directamente en la base de datos, porque son contenido narrativo que no va ligado a un cambio de código concreto.

## Consecuencias

- **Positivas:**
  - Una sola fuente de verdad, versionada y revisada junto al código.
  - Los datos se sirven desde mi backend y mi base de datos, como el resto del portfolio.
  - Demuestra integración entre sistemas: webhooks, verificación de firmas, cliente de la API de GitHub y sincronización idempotente.
- **Negativas:**
  - Más piezas que la opción 2: un endpoint público, un secreto de webhook y un token de la API de GitHub, que irán en variables de entorno de Render.
  - Si un webhook se pierde (por ejemplo, con el servicio dormido o caído), la base de datos queda desactualizada. Se mitigará con una sincronización manual desde el panel de administración y, si hace falta, una tarea periódica de reconciliación.
- **Seguridad:** el endpoint del webhook rechaza cualquier petición sin firma válida y solo lee el repositorio; nunca escribe en él.
