---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0001. Registrar las decisiones de arquitectura

## Contexto

Este portfolio tiene un doble objetivo: mostrar mi experiencia y ser, en sí mismo, una prueba de cómo diseño y construyo software. Un recruiter o un entrevistador puede ver el código, pero el código no explica **por qué** se tomó cada decisión ni qué alternativas se descartaron.

## Decisión

Documentaremos cada decisión de arquitectura relevante como un **ADR** (*Architecture Decision Record*) en `docs/adr/`, con el formato [MADR](https://adr.github.io/madr/):

- Un fichero por decisión, numerado y en orden cronológico.
- Los ADRs no se reescriben: si una decisión cambia, se crea uno nuevo que sustituye al anterior y se actualiza el estado del antiguo.
- Se consideran relevantes las decisiones difíciles de revertir o que condicionan otras: tecnologías, estructura, infraestructura, contratos y modelo de datos.

## Consecuencias

- **Positivas:** el razonamiento queda versionado junto al código; cualquier persona puede entender el proyecto sin preguntarme; y prepara las respuestas a preguntas típicas de entrevista ("¿por qué no microservicios?").
- **Negativas:** requiere disciplina para escribirlos en el momento y no meses después.
