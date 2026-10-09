---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0002. API First con contrato OpenAPI y generación de código

## Contexto

El backend expone una API REST que consumirá un frontend Angular. Hay que decidir qué es la fuente de verdad de esa API: el código Java o un contrato independiente. En mi trabajo diario uso el enfoque API First con contratos YAML, y quiero que el portfolio lo refleje y lo lleve un paso más allá.

## Opciones consideradas

1. **Code First:** se escriben los controladores y se genera la documentación con springdoc.
2. **API First:** se escribe primero el contrato OpenAPI y el código se genera a partir de él.

## Decisión

**API First.** El contrato `api/openapi.yaml` (OpenAPI 3.1) es la única fuente de verdad:

- `openapi-generator-maven-plugin` genera las interfaces de Spring y los DTOs (`interfaceOnly`, `useSpringBoot4`, `useJackson3`). Los controladores **implementan** esas interfaces y no redefinen rutas a mano.
- El código generado va a `target/` y nunca se commitea.
- El esquema `ProblemDetail` del contrato se mapea a la clase de Spring (`schemaMappings`) para no duplicarla.
- MapStruct usa `unmappedTargetPolicy = ERROR`: si el contrato añade un campo y no se mapea, la compilación falla.
- El contrato se valida con Spectral (`.spectral.yaml`); se integrará en la CI.
- **Futuro:** el cliente Angular se generará del mismo contrato, de modo que un cambio incompatible rompa la compilación en los dos lados.

## Consecuencias

- **Positivas:** el contrato y la implementación no pueden divergir; el diseño de la API se revisa antes de programar; frontend y backend comparten tipos.
- **Negativas:** el soporte de OpenAPI 3.1 en openapi-generator aún está en beta y produce algunos avisos. Si llegara a bloquear algo, la alternativa es bajar a 3.0.3, un cambio menor.
