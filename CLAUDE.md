# Portfolio personal

Portfolio para recruiters que, además de mostrar experiencia y conocimientos, documenta su propia construcción (devlog + ADRs). Es un proyecto demostrable: la calidad del código y de las decisiones importa tanto como el resultado.

## Stack decidido

- **Backend:** Java 25, Spring Boot 4, Spring Modulith (monolito modular), Spring Data JPA, Flyway.
- **Enfoque API First:** el contrato `api/openapi.yaml` (OpenAPI 3.1) es la fuente de verdad. Las interfaces de Spring se generan con openapi-generator (`interfaceOnly`, `useSpringBoot3`/equivalente actual). Más adelante, el cliente Angular también se generará del mismo contrato.
- **Base de datos:** PostgreSQL en Supabase (conexión por el pooler, IPv4).
- **Hosting:** backend en Render (Docker), frontend Angular prerenderizado en Cloudflare Pages (fase posterior).
- **Tests:** JUnit 5 + Testcontainers (PostgreSQL real, nada de H2).

## Estructura del monorepo

```
api/openapi.yaml   contrato único
backend/           Spring Boot
frontend/          Angular (todavía no existe)
docs/adr/          decisiones de arquitectura (formato MADR)
```

## Fase actual: paso 1

Objetivo: una API desplegada que devuelva el perfil desde la base de datos. Solo un endpoint:
`GET /api/v1/profile`. No añadir nada fuera de este alcance sin preguntar.

## Convenciones

- Errores con Problem Details (RFC 9457, `application/problem+json`).
- Rutas en inglés, kebab-case, versionadas con `/api/v1`.
- Esquemas del contrato en PascalCase, propiedades en camelCase.
- Commits con Conventional Commits (`feat:`, `fix:`, `docs:`…).
- Cada decisión de arquitectura relevante se documenta como ADR en `docs/adr/`.
