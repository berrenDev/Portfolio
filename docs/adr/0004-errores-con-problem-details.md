---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0004. Errores con Problem Details (RFC 9457)

## Contexto

La API necesita un formato de error consistente que el frontend pueda interpretar sin casos especiales, y que no filtre información interna.

## Decisión

Todos los errores siguen **Problem Details** (RFC 9457), con `Content-Type: application/problem+json`:

- Se activa `spring.mvc.problemdetails.enabled` y se usa la clase `ProblemDetail` de Spring, mapeada desde el contrato (ver [0002](0002-api-first-con-openapi.md)).
- Cada módulo tiene su propio `@RestControllerAdvice` para sus excepciones (por ejemplo, `profile` devuelve 404 si no hay perfil).
- Un handler global, basado en `ResponseEntityExceptionHandler`, actúa como red de seguridad:
  - mantiene los códigos que ya resuelve Spring (400, 405…) en lugar de convertirlos en 500;
  - convierte cualquier otra excepción en un 500 con un mensaje **genérico**, y registra la traza completa solo en el log.
- El handler global tiene la menor precedencia y los de módulo la mayor. Spring no elige el handler más específico entre varios `@RestControllerAdvice`, así que el orden es explícito.

## Consecuencias

- **Positivas:** formato estándar e interoperable; el frontend trata todos los errores igual; no se filtran trazas ni mensajes internos.
- **Verificación:** un test comprueba que un `POST` no soportado devuelve 405 y no 500, para garantizar que el handler global no oculta los errores de Spring.
