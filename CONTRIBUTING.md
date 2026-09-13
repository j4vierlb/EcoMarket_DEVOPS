# Guía de contribución y GitFlow

## 1. Estrategia de ramificación

Este proyecto utiliza GitFlow como modelo de trabajo colaborativo.

- `main`: código estable y listo para producción.
- `develop`: integración de nuevas funcionalidades y correcciones.
- `feature/<nombre>`: desarrollo de nuevas funcionalidades.
- `hotfix/<nombre>`: correcciones urgentes sobre producción.

### Ejemplos de nombres

- `feature/productos-crud`
- `feature/usuarios-validaciones`
- `hotfix/fix-application-properties`

## 2. Flujo de trabajo

1. Crear una rama `feature/*` desde `develop`.
2. Desarrollar la funcionalidad con commits pequeños y descriptivos.
3. Abrir un pull request hacia `develop`.
4. Revisar código y validar la compilación con CI.
5. Fusionar con merge commit para dejar trazabilidad.
6. Cuando se requiera una corrección urgente, crear `hotfix/*` desde `main`.
7. Fusionar primero a `main` y luego integrar el cambio a `develop`.

## 3. Convención de commits

Se recomienda usar commits en estilo convencional:

- `feat:` nueva funcionalidad
- `fix:` corrección de errores
- `docs:` cambios en documentación
- `chore:` tareas de mantenimiento
- `refactor:` refactorización sin cambio funcional
- `test:` pruebas

### Ejemplos

- `feat(productos): agregar endpoints CRUD`
- `fix(usuario): corregir validación de correo`
- `docs(readme): documentar flujo GitFlow`
- `chore(ci): configurar pipeline de GitHub Actions`

## 4. Reglas de revisión

- Todo cambio debe pasar la validación automática de CI.
- Todo pull request requiere revisión por al menos una persona.
- No se debe hacer merge directo a `main` sin aprobación.
- Los commits deben ser claros y evitar mezclas de tareas distintas.

## 5. Estrategia de merge

- `feature/*` -> `develop`: merge mediante pull request.
- `hotfix/*` -> `main`: merge mediante pull request.
- `main` -> `develop`: después de un hotfix, sincronizar cambios de producción.

## 6. Control de versiones

- Mantener ramas actualizadas antes de abrir un pull request.
- Evitar commits con mensajes ambiguos como `fix`, `changes`, `test123`.
- Mantener historial limpio y trazable.
- Resolver conflictos antes de fusionar.
