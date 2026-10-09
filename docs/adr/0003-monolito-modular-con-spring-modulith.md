---
status: aceptado
date: 2026-10-09
decision-makers: Alejandro Berrendero Gómez
---

# 0003. Monolito modular con Spring Modulith (y no microservicios)

## Contexto

Las ofertas de Java en banca y consultoría piden a menudo experiencia en microservicios, y tuve la tentación de construir el portfolio así. Pero el sistema tiene un único autor, poco tráfico y un dominio pequeño (perfil, experiencia, proyectos, devlog, contacto).

## Opciones consideradas

1. **Microservicios completos:** un servicio por dominio más un API Gateway.
2. **Monolito modular con servicios satélite** asíncronos en serverless.
3. **Monolito modular** con Spring Modulith.
4. **Monolito clásico por capas** (controller / service / repository).

## Decisión

**Monolito modular con Spring Modulith (opción 3).**

- El código se organiza por módulos de negocio (`profile`, y más adelante `devlog`, `projects`, `contact`…), no por capas técnicas.
- Las fronteras entre módulos se verifican en un test (`ApplicationModules.verify()`): un módulo no puede usar las clases internas de otro.
- La comunicación entre módulos se hará con eventos de dominio.

**Por qué no microservicios:** resuelven sobre todo un problema organizativo, el de muchos equipos desplegando de forma independiente. Con un solo desarrollador casi todo serían costes: varias instancias de pago, una base de datos por servicio, un broker de mensajería y trazabilidad distribuida, sin ningún beneficio real. Sería sobreingeniería, y se notaría.

**Por qué no un monolito por capas:** no impide el acoplamiento entre dominios. Modulith añade fronteras verificables sin coste de infraestructura.

## Consecuencias

- **Positivas:** un único despliegue y un único coste; arquitectura que se mantiene ordenada al crecer; demuestra diseño por bounded contexts.
- **Negativas:** dos paquetes tuvieron que declararse como módulos `OPEN`: el del código generado (`api`), para que Modulith permitiera usar sus DTOs desde otros módulos, y `shared`, que contiene infraestructura transversal como el handler global de errores.
- **Camino de salida:** si algún módulo necesitara escalar o desplegarse por separado, las fronteras y los eventos ya preparan su extracción como microservicio.
