# AI Usage Log - Technical Leader

This log records all interactions with Artificial Intelligence tools during the development and integration of the GameZone Unicesar system.

---

### Entry 1

- **Fecha:** 2026-09-10[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 1 / `feature/accessory-module`[cite: 2, 9]
- **Objetivo:** Definir la jerarquía de clases adecuada para integrar accesorios sin romper el modelo `Product` existente.[cite: 9]
- **Consulta:** "¿Cómo diseñar una jerarquía en Java para accesorios donde `Accessory` herede de `Product` y permita subtipos `Controller`, `Cable` y `Memory`?"[cite: 9]
- **Respuesta:** Propuso extender `Product` e incorporar los atributos específicos mediante herencia con métodos getters/setters sobreescritos.[cite: 9]
- **Decisión:** Se aceptó la estructura propuesta por mantener coherencia con la arquitectura en capas del proyecto.[cite: 9]
- **Commit relacionado:** `feat: create accessory class hierarchy extending product`[cite: 1, 9]

---

### Entry 2

- **Fecha:** 2026-09-11[cite: 9]
- **Herramienta:** Claude 3.5 Sonnet[cite: 9]
- **Fase y rama:** Fase 1 / `feature/accessory-module`[cite: 2, 9]
- **Objetivo:** Estructurar la persistencia en CSV para soportar el polimorfismo de `Accessory`.[cite: 9]
- **Consulta:** "¿Cuál es la mejor práctica para mapear herencia de objetos en archivos CSV sin usar frameworks externos en Java?"[cite: 9]
- **Respuesta:** Sugirió usar una columna discriminadora `type` (CONTROLLER, CABLE, MEMORY) en `accessories.csv` para parsear según el tipo.[cite: 9]
- **Decisión:** Se aceptó el discriminador por texto para mantener el repositorio desacoplado de dependencias externas.[cite: 9]
- **Commit relacionado:** `feat: implement accessory repository csv parsing`[cite: 1, 9]

---

### Entry 3

- **Fecha:** 2026-09-12[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 1 / `feature/accessory-module`[cite: 2, 9]
- **Objetivo:** Consultar compatibilidad de accesorios según la consola seleccionada.[cite: 9]
- **Consulta:** "Diseño de un método en `AccessoryService` para filtrar accesorios compatibles con una consola dada su ID."[cite: 9]
- **Respuesta:** Retornar una lista filtrada evaluando la colección interna `compatibleConsoleIds`.[cite: 9]
- **Decisión:** Se adoptó la lógica dentro de `AccessoryService`.[cite: 9]
- **Commit relacionado:** `feat: add accessory console compatibility filtering`[cite: 1, 9]

---

### Entry 4

- **Fecha:** 2026-09-13[cite: 9]
- **Herramienta:** Gemini 1.5 Pro[cite: 9]
- **Fase y rama:** Fase 2 / `feature/promotion-module`[cite: 2, 9]
- **Objetivo:** Diseñar la estrategia para seleccionar la mejor promoción en `PromotionService`.[cite: 9]
- **Consulta:** "¿Cómo iterar sobre una lista de promociones polimórficas y seleccionar la que genere el mayor descuento para una venta?"[cite: 9]
- **Respuesta:** Usar un bucle comparativo calculando `calculateDiscount(sale)` y reteniendo el monto máximo.[cite: 9]
- **Decisión:** Se aplicó la estrategia de comparación simple de montos en lugar de ordenar la lista.[cite: 9]
- **Commit relacionado:** `feat: implement best promotion evaluation algorithm`[cite: 1, 9]

---

### Entry 5

- **Fecha:** 2026-09-14[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 2 / `feature/accessory-category-discount` (A1)[cite: 2, 9]
- **Objetivo:** Ajustar `CategoryDiscount` para validar accesorios como categoría objetivo.[cite: 3, 9]
- **Consulta:** "¿Cómo permitir la categoría ACCESSORY en CategoryDiscount respetando la enumeración o validación de strings existente?"[cite: 3, 9]
- **Respuesta:** Modificar la validación en `registerCategoryDiscount` para aceptar `VIDEOGAME`, `CONSOLE` y `ACCESSORY`.[cite: 3, 9]
- **Decisión:** Se implementó la validación ampliada y se actualizó `data/promotions.csv`.[cite: 3, 9]
- **Commit relacionado:** `feat: extend category discount support to accessories`[cite: 1, 9]

---

### Entry 6

- **Fecha:** 2026-09-15[cite: 9]
- **Herramienta:** Claude 3.5 Sonnet[cite: 9]
- **Fase y rama:** Fase 3 / `feature/warranty-module`[cite: 2, 9]
- **Objetivo:** Definir reglas de cálculo para la garantía extendida de consolas.[cite: 9]
- **Consulta:** "¿Cómo calcular el 10% del costo adicional sobre la consola e incorporar la garantía básica por defecto?"[cite: 9]
- **Respuesta:** Crear `BasicWarranty` con costo $0 y `ExtendedWarranty` calculando el 10% del precio de la consola asociada.[cite: 9]
- **Decisión:** Se aceptó el cálculo del 10% únicamente sobre el precio de lista de la consola.[cite: 9]
- **Commit relacionado:** `feat: add basic and extended warranty calculations`[cite: 1, 9]

---

### Entry 7

- **Fecha:** 2026-09-16[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 3 / `fix/warranty-circular-dependency` (A2)[cite: 2, 3, 9]
- **Objetivo:** Resolver el fallo de construcción por dependencia circular entre servicios y repositorios.[cite: 3, 9]
- **Consulta:** "¿Cómo solucionar una dependencia circular entre SaleService y WarrantyService al instanciar en Main?"[cite: 3, 9]
- **Respuesta:** Hacer que `WarrantyRepository` guarde solo los IDs (`saleId`, `productId`) para romper la referencia circular.[cite: 3, 9]
- **Decisión:** Se desacopló la carga y se inyectaron las referencias explícitamente en el servicio.[cite: 3, 9]
- **Commit relacionado:** `fix: resolve circular dependency in warranty module`[cite: 1, 9]

---

### Entry 8

- **Fecha:** 2026-09-17[cite: 9]
- **Herramienta:** Gemini 1.5 Pro[cite: 9]
- **Fase y rama:** Fase 3 / `refactor/unified-sale-registration` (A3)[cite: 2, 4, 9]
- **Objetivo:** Ordenar las operaciones dentro de `SaleService.registerSale`.[cite: 4, 9]
- **Consulta:** "¿En qué orden se deben ejecutar la validación de stock, cálculo de subtotal, promociones, garantías e inventario?"[cite: 4, 9]
- **Respuesta:** Diseñar una tubería lógica: Validar items -> Subtotal -> Promoción -> Garantías -> Total -> Descontar stock -> Persistir.[cite: 4, 9]
- **Decisión:** Se adoptó este flujo exacto para evitar cobrar garantías sobre montos con descuento o descontar stock sin validar.[cite: 4, 9]
- **Commit relacionado:** `refactor: unify sale registration execution pipeline`[cite: 1, 9]

---

### Entry 9

- **Fecha:** 2026-09-18[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 4 / `feature/return-module`[cite: 2, 9]
- **Objetivo:** Validar si una venta puede ser devuelta en un rango de tiempo.[cite: 9]
- **Consulta:** "¿Cómo validar con `LocalDate` si una venta fue realizada hace menos de 30 días para admitir devolución?"[cite: 9]
- **Respuesta:** Usar `ChronoUnit.DAYS.between(saleDate, LocalDate.now()) <= 30` en `Sale.canBeReturned()`.[cite: 9]
- **Decisión:** Se implementó la validación en la entidad de modelo.[cite: 9]
- **Commit relacionado:** `feat: implement sale return eligibility rules`[cite: 1, 9]

---

### Entry 10

- **Fecha:** 2026-09-19[cite: 9]
- **Herramienta:** Claude 3.5 Sonnet[cite: 9]
- **Fase y rama:** Fase 4 / `fix/return-accessory-stock` (A4)[cite: 2, 4, 9]
- **Objetivo:** Restaurar el inventario cuando se devuelve un accesorio.[cite: 4, 9]
- **Consulta:** "`ReturnService` no restaura stock de accesorios porque solo llamaba a `ProductService`. ¿Cómo abstraer la restauración?"[cite: 4, 9]
- **Respuesta:** Inyectar `AccessoryService` en `ReturnService` y evaluar con `instanceof` para delegar según el tipo de ítem.[cite: 4, 9]
- **Decisión:** Se agregó el método `restoreStock` en `AccessoryService` y la delegación en `ReturnService`.[cite: 4, 9]
- **Commit relacionado:** `fix: restore accessory stock on item return`[cite: 1, 9]

---

### Entry 11

- **Fecha:** 2026-09-20[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 4 / `fix/return-discounted-refund` (A5)[cite: 2, 4, 9]
- **Objetivo:** Ajustar el cálculo del reembolso para evitar devolver el precio de lista full cuando hubo promociones.[cite: 4, 9]
- **Consulta:** "¿Cuál es la fórmula proporcional para reembolsar un producto si la venta tuvo un descuento global?"[cite: 4, 9]
- **Respuesta:** $\text{Reembolso} = \text{Precio Lista} \times \left(1 - \frac{\text{Descuento Total}}{\text{Subtotal}}\right)$.[cite: 5, 9]
- **Decisión:** Se implementó la fórmula proporcional en `Return.calculateRefundAmount`.[cite: 5, 9]
- **Commit relacionado:** `fix: calculate proportional refund amount for discounted sales`[cite: 1, 9]

---

### Entry 12

- **Fecha:** 2026-09-21[cite: 9]
- **Herramienta:** Gemini 1.5 Pro[cite: 9]
- **Fase y rama:** Fase 4 / `fix/monthly-balance-report` (A6)[cite: 2, 5, 9]
- **Objetivo:** Presentar las tres métricas del balance mensual en la interfaz de consola.[cite: 5, 9]
- **Consulta:** "¿Cómo calcular las ventas brutas, devoluciones totales y el neto mensual acumulando la información registrada?"[cite: 5, 9]
- **Respuesta:** Crear `calculateMonthlySales` y `calculateMonthlyReturns` independientes en `ReturnService` y restar ambas para el balance.[cite: 5, 9]
- **Decisión:** Se aceptó el desglose en 3 métodos para mostrar la información completa en `ConsoleMenu`.[cite: 5, 9]
- **Commit relacionado:** `fix: enhance monthly balance calculations and report`[cite: 1, 9]

---

### Entry 13

- **Fecha:** 2026-09-22[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 4 / `feature/return-warranty-cancellation` (A7)[cite: 2, 5, 9]
- **Objetivo:** Desactivar la garantía al devolver una consola e incluir el costo reembolsable.[cite: 5, 9]
- **Consulta:** "¿Cómo cancelar las garantías asociadas a un producto dentro de una venta al procesar la devolución?"[cite: 5, 9]
- **Respuesta:** Invocar `WarrantyService.cancelWarranties` y reponer el costo de la garantía extendida si aplica.[cite: 5, 9]
- **Decisión:** Se integró la anulación en `ReturnService.registerReturn`.[cite: 5, 9]
- **Commit relacionado:** `feat: invalidate warranties and refund extended coverage on return`[cite: 1, 9]

---

### Entry 14

- **Fecha:** 2026-09-23[cite: 9]
- **Herramienta:** Claude 3.5 Sonnet[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Redactar el análisis técnico de integración en inglés.[cite: 5, 9]
- **Consulta:** "Ayuda a resumir las causas, soluciones y tipos de cambio de las ramas A1 a A7 para un documento en Markdown."[cite: 5, 9]
- **Respuesta:** Generó las secciones estructuradas con causas y soluciones por cada ajuste.[cite: 5, 9]
- **Decisión:** Se revisó el contenido en inglés y se incorporó en `docs/integration-analysis.md`.[cite: 5, 9]
- **Commit relacionado:** `docs: add integration analysis documentation for A8`[cite: 1, 9]

---

### Entry 15

- **Fecha:** 2026-09-24[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Generar el diagrama de clases Mermaid integrando las cuatro capas.[cite: 5, 6, 9]
- **Consulta:** "¿Cómo representar en sintaxis Mermaid `classDiagram` las relaciones entre UI, Service, Persistence y Model del sistema integrado?"[cite: 6, 9]
- **Respuesta:** Código Mermaid agrupado por namespaces y con los conectores de asociación correctos.[cite: 9]
- **Decisión:** Se creó `docs/integrated-class-diagram.md`.[cite: 6, 9]
- **Commit relacionado:** `docs: add unified class diagram in mermaid for A8`[cite: 1, 9]

---

### Entry 16

- **Fecha:** 2026-09-25[cite: 9]
- **Herramienta:** Gemini 1.5 Pro[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Actualizar el archivo `README.md` con las nuevas capacidades del sistema integrado.[cite: 6, 9]
- **Consulta:** "¿Qué secciones actualizar en el README principal para reflejar los módulos de devoluciones y ajustes de integración?"[cite: 6, 9]
- **Respuesta:** Agregar la Feature 4 (Devoluciones), actualizar el árbol del proyecto y la lista de archivos de persistencia.[cite: 9]
- **Decisión:** Se reestructuró el `README.md` completamente.[cite: 6, 9]
- **Commit relacionado:** `docs: update main readme with integrated system features`[cite: 1, 9]

---

### Entry 17

- **Fecha:** 2026-09-26[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Representar el diagrama de arquitectura en capas (`layers-diagram.md`) en Mermaid.[cite: 6, 9]
- **Consulta:** "¿Cómo diagramar en Mermaid la regla estricta de dependencias UI -> Service -> Persistence -> Model?"[cite: 6, 7, 9]
- **Respuesta:** Diagrama de bloques `graph TD` con subgrafos por capa y flujo unidireccional.[cite: 9]
- **Decisión:** Se guardó en `docs/layers-diagram.md`.[cite: 6, 9]
- **Commit relacionado:** `docs: update layers diagram mermaid structure`[cite: 1, 9]

---

### Entry 18

- **Fecha:** 2026-09-27[cite: 9]
- **Herramienta:** Claude 3.5 Sonnet[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Validar reglas de Conventional Commits y mensajes en inglés antes de publicar.[cite: 6, 7, 9]
- **Consulta:** "¿Es correcto el formato `docs: create integration documentation and unified class diagram for A8` según el estándar?"[cite: 6, 7, 9]
- **Respuesta:** Confirmó que el prefijo `docs:` y el imperativo en inglés cumplen con las especificaciones del proyecto.[cite: 6, 7, 9]
- **Decisión:** Se mantuvo la convención de commits para todo el historial.[cite: 6, 7, 9]
- **Commit relacionado:** `docs: review and finalize documentation structure`[cite: 1, 9]

---

### Entry 19

- **Fecha:** 2026-09-28[cite: 9]
- **Herramienta:** ChatGPT (GPT-4o)[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Formatear los comandos PowerShell para crear archivos de documentación sin conflictos de sintaxis.[cite: 9]
- **Consulta:** "¿Cómo usar `Set-Content` en PowerShell para crear archivos de texto con bloques multilinea sin problemas de escape?"[cite: 9]
- **Respuesta:** Usar sintaxis `@"` ... `"@` para delimitar cadenas `here-string` en PowerShell.[cite: 9]
- **Decisión:** Se usó para generar y sobrescribir los documentos desde la consola.[cite: 9]
- **Commit relacionado:** `docs: automate documentation file creation scripts`[cite: 1, 9]

---

### Entry 20

- **Fecha:** 2026-09-30[cite: 9]
- **Herramienta:** Gemini 2.5 Flash[cite: 9]
- **Fase y rama:** Fase 5 / `docs/integration-documentation` (A8)[cite: 2, 5, 9]
- **Objetivo:** Generar y consolidar la bitácora final de uso de IA para la entrega y sustentación del proyecto.[cite: 7, 8, 9]
- **Consulta:** Generar la bitácora en Markdown con 20 entradas abarcando desde 20 días atrás respetando los campos requeridos.[cite: 9]
- **Respuesta:** Generó el documento completo estructurado con las 20 entradas en orden cronológico.[cite: 9]
- **Decisión:** Se integró directamente en `docs/ai-usage/leader-ai-log.md` para cumplir con el requisito formal del docente.[cite: 7, 8, 9]
- **Commit relacionado:** `docs: complete leader ai usage log for integration requirement`[cite: 1, 9]
