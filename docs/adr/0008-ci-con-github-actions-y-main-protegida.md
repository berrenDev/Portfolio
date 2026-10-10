---
status: aceptado
date: 2026-10-10
decision-makers: Alejandro Berrendero Gómez
---

# 0008. CI con GitHub Actions y `main` protegida

## Contexto

Hasta ahora, la calidad de lo que llegaba a `main` dependía de que yo ejecutara `./mvnw verify` en local antes de cada commit, y Render desplegaba cualquier commit de `main` sin comprobar nada. Un fallo humano (olvidar los tests, mergear con prisa) podía llegar directamente a producción.

## Decisión

### Workflow (`.github/workflows/ci.yml`)

Se ejecuta en cada PR hacia `main` y en cada push a `main`, con tres jobs:

| Job | Qué hace | Cuándo |
|---|---|---|
| `changes` | Detecta qué partes del repo cambian (`dorny/paths-filter`) | Siempre |
| `contract` | Spectral valida el contrato y, en PRs, **oasdiff** falla si hay cambios incompatibles respecto a `main` | Si cambia `api/**` o el workflow |
| `backend` | `./mvnw -B verify` con JDK 25: generación desde el contrato, tests con Testcontainers y verificación de Modulith | Si cambia `backend/**`, `api/**` o el workflow |

### Decisiones de detalle

- **Filtro por rutas a nivel de job, no de workflow.** Si se filtra con `on.<evento>.paths`, un PR que no toca el backend nunca dispara el check, y GitHub lo deja "esperando" para siempre, lo que bloquearía el merge con la protección de `main`. Con `if:` por job, el workflow se ejecuta siempre y los jobs que no aplican aparecen como *Skipped*, que cuenta como superado.
- **Actions fijadas por SHA de commit**, con la versión en un comentario. Un tag puede moverse a otro código; un SHA no.
- **Runner fijado a `ubuntu-24.04`** en lugar de `ubuntu-latest`: los cambios de entorno se deciden en un PR propio, no llegan solos.
- **Spectral con su imagen Docker oficial** (versión exacta), porque su action no publica versiones fijables.
- **oasdiff con `review: false`** para que el contrato no se envíe a un servicio externo, y sin comentarios en el PR para no necesitar permisos de escritura.
- **Permisos mínimos:** `contents: read` en el workflow; `pull-requests: read` solo en el job `changes`, que lo necesita.
- **`concurrency` con `cancel-in-progress`** y **`timeout-minutes`** en cada job (5, 10 y 20 minutos), para no gastar minutos en ejecuciones obsoletas o colgadas.

### Protección de `main` (ruleset de GitHub)

- PR obligatorio, con merge solo por **squash** y 0 aprobaciones (trabajo solo, y GitHub no permite aprobar los PRs propios).
- Checks obligatorios: `changes`, `contract` y `backend`.
- Sin force push ni borrado de `main`.
- **Lista de bypass vacía:** la regla se aplica también al propietario del repositorio.

### Despliegue

Render pasa de *On Commit* a **After CI Checks Pass**: solo despliega un commit de `main` si la CI de ese commit pasa.

## Verificación

- **El check del contrato se comprobó fallando a propósito:** un commit temporal quitó `headline` de los campos obligatorios de `Profile` y oasdiff lo marcó como incompatible (`response-property-became-optional`). Al revertirlo, volvió a pasar.
- La primera ejecución destapó un fallo real: `mvnw` no tenía el bit de ejecución en Git (exit 126), algo que en Windows no se nota y que Docker ocultaba con su propio `chmod`.

## Consecuencias

- **Positivas:** nada llega a `main` ni a producción sin tests en verde; los cambios incompatibles en la API se detectan antes del merge; el historial de PRs y checks es público y demuestra el proceso.
- **Negativas:** cada cambio, por pequeño que sea, requiere un PR; las versiones fijadas por SHA hay que actualizarlas a mano o con una herramienta como Dependabot (pendiente).
- **A vigilar:** el soporte de OpenAPI 3.1 en oasdiff funciona con nuestro contrato, pero conviene revisarlo si el contrato usa funciones más avanzadas de 3.1.
