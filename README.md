# 🌿 EcoMarket — API REST de Mercado Ecológico

Backend de un mercado ecológico desarrollado con **Spring Boot**, que expone una API REST para la gestión de productos, usuarios y pedidos. Arquitectura en capas siguiendo buenas prácticas de desarrollo Java.

## 🧭 Modelo de ramificación: GitFlow

Este proyecto implementa **GitFlow** como estrategia de trabajo colaborativo para mantener trazabilidad, estabilidad y orden en el desarrollo.

### Ramas principales

- `main`: versión estable y lista para producción.
- `develop`: rama de integración para nuevas funcionalidades.
- `feature/<nombre>`: desarrollo de nuevas funcionalidades.
- `hotfix/<nombre>`: correcciones urgentes sobre producción.

### ¿Por qué GitFlow?

GitFlow es adecuado para este proyecto porque:

- separa claramente el código de producción del desarrollo activo;
- facilita la colaboración entre varios integrantes;
- permite trabajar en features en paralelo sin afectar la rama principal;
- ofrece un flujo definido para correcciones urgentes con `hotfix`.

Este enfoque cumple con la evaluación de DevOps, ya que demuestra control de versiones, trazabilidad y un proceso ordenado de integración.

---

## 🔀 Flujo de trabajo del equipo

1. Crear una rama `feature/<nombre>` desde `develop`.
2. Implementar la funcionalidad con commits pequeños y descriptivos.
3. Abrir un Pull Request hacia `develop`.
4. Revisar el código y dejar evidencia de aprobación.
5. Integrar a `develop` usando merge con trazabilidad.
6. Cuando haya un problema crítico en producción, crear `hotfix/<nombre>` desde `main`.
7. Corregir y fusionar a `main`, luego sincronizar a `develop`.

---

## 📝 Convenciones de commits

Se recomienda usar mensajes en estilo convencional:

- `feat:` nuevas funcionalidades
- `fix:` correcciones de errores
- `docs:` cambios en documentación
- `chore:` tareas de mantenimiento
- `refactor:` mejoras internas sin cambiar comportamiento
- `test:` cambios relacionados con pruebas

Ejemplos:

- `feat(productos): agregar endpoints CRUD`
- `fix(usuario): corregir validación de correo`
- `docs(readme): documentar flujo GitFlow`
- `chore(ci): configurar GitHub Actions`

---

## ✅ Reglas de revisión y merge

- Todo cambio debe pasar la validación automática.
- Todo Pull Request debe revisarse antes del merge.
- No se fusiona directamente a `main` sin revisión.
- Los conflictos deben resolverse antes de integrar.
- Los mensajes de commit deben ser claros y específicos.

---

## 🚀 GitHub Actions

El repositorio incluye una acción automática configurada en `.github/workflows/ci.yml`.

Se ejecuta en:

- cada push a `develop`
- cada pull request hacia `main` o `develop`

La pipeline valida el proyecto ejecutando Maven con:

```bash
./mvnw test
```

Esto permite automatizar la integración de cambios y comprobar que el microservicio sigue compilando y funcionando correctamente.

---

## 🚀 Tecnologías utilizadas

| Capa | Tecnología |
|------|-----------|
| Backend | Java + Spring Boot |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria (por defecto) / MySQL (perfil `mysql`) |
| Validación | Bean Validation (Jakarta) |
| Seguridad | Contraseñas hasheadas con BCrypt |
| API | REST con JSON |
| Build | Maven |

---

## ✨ Endpoints disponibles

### 📦 Productos — `/api/productos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| POST | `/api/productos` | Crear nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### 👤 Usuarios — `/api/usuarios`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### 🛒 Pedidos — `/api/pedidos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/pedidos` | Listar todos los pedidos |
| GET | `/api/pedidos/{id}` | Obtener pedido por ID |
| POST | `/api/pedidos` | Crear nuevo pedido |
| PUT | `/api/pedidos/{id}` | Actualizar pedido |
| DELETE | `/api/pedidos/{id}` | Eliminar pedido |

### Códigos de respuesta

| Código | Cuándo |
|--------|--------|
| `200` | Consulta o actualización correcta |
| `201` | Recurso creado (POST) |
| `204` | Recurso eliminado (DELETE) |
| `400` | Datos inválidos; el cuerpo incluye `errores` por campo |
| `404` | El recurso con ese `id` no existe |
| `409` | Ya existe un usuario con ese correo |

### Validaciones

- **Usuario**: `nombre` obligatorio (máx. 100), `correo` obligatorio, con formato válido y único, `contrasena` obligatoria (mín. 8 caracteres). La contraseña se guarda hasheada con BCrypt y nunca se devuelve en las respuestas.
- **Producto**: `nombre` obligatorio, `stock` ≥ 0, `precio` obligatorio y > 0.
- **Pedido**: `descripcion` obligatoria, `total` obligatorio y ≥ 0. Si no se envía `fecha`, se usa la fecha actual.

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en 3 capas:

```
Controller (REST)  →  Service (lógica de negocio)  →  Repository (acceso a datos)
```

```
src/
└── main/java/com/grupo3/EcoMarket/
    ├── config/           # CORS y PasswordEncoder
    ├── exception/        # Manejo global de errores
    ├── controller/       # Controladores REST
    │   ├── ProductoController.java
    │   ├── UsuarioController.java
    │   └── PedidoController.java
    ├── service/          # Lógica de negocio
    │   ├── ProductoService.java
    │   ├── UsuarioService.java
    │   └── PedidoService.java
    ├── repository/       # Acceso a datos (Spring Data JPA)
    │   ├── ProductoRepository.java
    │   ├── UsuarioRepository.java
    │   └── PedidoRepository.java
    └── model/            # Entidades JPA
        ├── Producto.java
        ├── Usuario.java
        └── Pedido.java
```

---

## ⚙️ Instalación y ejecución

### Requisitos previos

- Java 17+
- (Opcional) MySQL

No hace falta instalar Maven: el proyecto incluye el wrapper `./mvnw`.

### Configuración de base de datos

Por defecto la aplicación usa **H2 en memoria**, así que funciona sin instalar nada (los datos se pierden al reiniciar).

Para usar **MySQL**, crea la base de datos `ecomarket` y arranca con el perfil `mysql`. Las credenciales se leen de variables de entorno (ver `src/main/resources/application-mysql.properties`):

```bash
export DB_URL=jdbc:mysql://localhost:3306/ecomarket
export DB_USER=tu_usuario
export DB_PASSWORD=tu_contraseña
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Pasos para ejecutar

```bash
# 1. Clonar el repositorio
git clone https://github.com/j4vierlb/EcoMarket.git
cd EcoMarket

# 2. Compilar y ejecutar
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`

---

## 📝 Ejemplo de uso

### Crear un producto

```bash
POST /api/productos
Content-Type: application/json

{
  "nombre": "Manzanas orgánicas",
  "descripcion": "Manzanas cultivadas sin pesticidas",
  "stock": 100,
  "precio": 1500
}
```

### Crear un usuario

```bash
POST /api/usuarios
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "correo": "juan@email.com",
  "contrasena": "segura123"
}
```

---

## 👨‍💻 Autor

**Javier Lara Bustos**  
[GitHub](https://github.com/j4vierlb)

## 🔧 Hotfix: ajuste de pipeline

Se corrige la rama de hotfix para mantener la automatización y la sincronización entre main y develop.
