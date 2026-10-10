# Portfolio personal

Portfolio para recruiters que, además de mostrar experiencia y conocimientos, documenta su propia construcción (devlog + ADRs). Es un proyecto demostrable: la calidad del código y de las decisiones importa tanto como el resultado.

Las decisiones de arquitectura están en `docs/adr/` (índice en `docs/adr/README.md`). Léelas antes de proponer cambios de diseño; si una propuesta contradice un ADR aceptado, dilo explícitamente.

## Stack

- **Backend:** Java 25 (vía Maven Toolchains), Spring Boot 4.1, Spring Modulith, Spring Data JPA, Flyway, MapStruct.
  - El build compila y testea con un JDK 25 aunque Maven se lance con otro. Requiere `~/.m2/toolchains.xml` con una entrada `<type>jdk</type>`, `<provides><version>25</version></provides>` y `<configuration><jdkHome>` apuntando a la instalación del JDK 25; sin ella, `./mvnw verify` falla con `Cannot find matching toolchain`. Ver la [guía oficial de Maven Toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html). La imagen Docker genera su propio `toolchains.xml`.
- **API First** ([ADR 0002](docs/adr/0002-api-first-con-openapi.md)): `api/openapi.yaml` (OpenAPI 3.1) es la fuente de verdad. openapi-generator crea interfaces y DTOs en `target/`; los controladores implementan las interfaces generadas. Nunca se definen rutas a mano.
- **Monolito modular** ([ADR 0003](docs/adr/0003-monolito-modular-con-spring-modulith.md)): un paquete por módulo de negocio (`profile`, …). `api` (código generado) y `shared` son módulos `OPEN`. `ApplicationModules.verify()` debe pasar siempre.
- **Errores** ([ADR 0004](docs/adr/0004-errores-con-problem-details.md)): Problem Details (RFC 9457). Handler por módulo + handler global de menor precedencia.
- **Base de datos** ([ADR 0006](docs/adr/0006-esquema-propio-portfolio.md)): PostgreSQL 17. Las tablas viven en el esquema `portfolio`, nunca en `public`. El esquema lo gestiona solo Flyway: los cambios van siempre en una migración nueva, nunca editando una ya aplicada.
- **Tests:** JUnit 5 + Testcontainers con PostgreSQL real (nada de H2). El script `backend/docker/postgres/init/` simula los objetos de Supabase en `public`.

## Estructura del monorepo

```
api/openapi.yaml   contrato único (validado con .spectral.yaml)
backend/           Spring Boot (Dockerfile, compose.yaml para desarrollo local)
docs/adr/          decisiones de arquitectura (MADR 4, front matter con decision-makers)
.github/workflows/ci.yml  CI: contrato (Spectral + oasdiff) y backend (./mvnw verify)
frontend/          Angular (todavía no existe)
.claude/           settings.json compartido; settings.local.json es personal e ignorado
```

## Producción ([ADR 0005](docs/adr/0005-hosting-render-y-supabase.md))

- **Backend:** Render (Docker, Frankfurt, plan Free por ahora). Despliegue automático en cada merge a `main` que toque `backend/**` o `api/**`, solo cuando la CI de ese commit pasa (*After CI Checks Pass*, [ADR 0008](docs/adr/0008-ci-con-github-actions-y-main-protegida.md)).
- **Base de datos:** Supabase Free (Frankfurt), Session pooler (IPv4), Data API desactivada, pool de Hikari de 5 conexiones.
- **Configuración por variables de entorno:** `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Nunca escribas credenciales, tokens ni URLs con contraseña en el código, en los commits ni en los mensajes.
- **JVM:** ajustada a 512 MB en el Dockerfile. Si cambias dependencias o memoria, vuelve a medir con `--memory=512m`.

## Estado

**Hecho (paso 1):** `GET /api/v1/profile` en producción con el perfil real, Docker, despliegue continuo y ADRs 0001–0006 aceptados.

**Hecho (CI)** ([ADR 0008](docs/adr/0008-ci-con-github-actions-y-main-protegida.md)):
- Workflow `.github/workflows/ci.yml`: en cada PR y push a `main`, lint del contrato con Spectral, detección de cambios incompatibles con oasdiff (solo en PRs) y `./mvnw verify` (tests con Testcontainers). Filtro por rutas a nivel de job.
- `main` protegida por un ruleset: PR obligatorio con merge por squash, checks `changes`, `contract` y `backend` obligatorios, sin force push ni borrado y sin bypass.
- Render despliega con *After CI Checks Pass*: solo si la CI de ese commit pasa.

**Fase actual: resto del perfil** (experiencia, skills y certificaciones).

**Siguientes fases:**
1. Módulo `devlog`: sincronización de ADRs y releases por webhook de GitHub ([ADR 0007](docs/adr/0007-adrs-en-repositorio-sincronizados-a-bd.md), propuesto) y entradas del devlog.
2. Frontend Angular prerenderizado en Cloudflare Pages, con cliente generado del contrato.
3. Panel de administración con login de GitHub (OAuth2) y Spring Session JDBC.

No adelantes trabajo de fases futuras sin preguntar.

## Forma de trabajar

- **Antes de empezar**, enséñame el plan y espera mi OK, salvo en cambios pequeños que yo indique.
- **Verifica, no supongas:** versiones, opciones de plugins y APIs se comprueban en la documentación. Si no puedes comprobar algo, dilo.
- **Ramas y PRs:** nunca trabajes directamente en `main`. Una rama por cambio (`feat/…`, `fix/…`, `docs/…`, `chore/…`, `ci/…`), push de la rama y yo abro el PR y hago el merge (squash). No hagas merge ni push a `main`.
- **`main` está protegida:** nadie puede hacer push directo, tampoco el propietario. Todo entra por PR con la CI en verde (`changes`, `contract` y `backend`; los jobs que no aplican aparecen como *Skipped* y no bloquean).
- **Antes de cada commit:** `git status` revisado, y `./mvnw verify` en verde antes de cada commit que toque código, configuración o el contrato.
- **Un bug de producción** se corrige con un test que falle antes del arreglo y pase después.
- **Cuando una decisión cambie algo de un ADR**, avisa y propón el ADR nuevo o la actualización.

## Convenciones

- Rutas en inglés, kebab-case, versionadas con `/api/v1`.
- Esquemas del contrato en PascalCase, propiedades en camelCase.
- Commits con Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`…), en inglés.
- Saltos de línea (ver `.gitattributes`): el repositorio guarda LF; en disco, LF obligatorio para `Dockerfile`, `mvnw` y `*.sh`, CRLF para `*.cmd` y `*.bat`, y el resto según la plataforma (CRLF en Windows).
